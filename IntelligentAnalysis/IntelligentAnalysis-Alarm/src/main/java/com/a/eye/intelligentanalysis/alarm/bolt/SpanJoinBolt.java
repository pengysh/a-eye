package com.a.eye.intelligentanalysis.alarm.bolt;

import com.a.eye.intelligentanalysis.alarm.common.SpanSpec;
import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.shade.com.google.common.base.Joiner;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.RotatingMap;
import org.apache.storm.utils.Time;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  @author gaohongtao.
 */
public class SpanJoinBolt extends BaseRichBolt {
    
    private final int timeout;
    
    private OutputCollector collector;
    
    private Fields idFields;
    
    private static final int BUCKET_NUM = 3;
    
    private final RotatingMap<List<Object>, Tuple> pending = new RotatingMap<>(BUCKET_NUM, (RotatingMap.ExpiredCallback<List<Object>, Tuple>) (key, val) -> {
        collector.emit("timeout", val.getValues());
    });
    
    private final Object _lock = new Object();
    
    private ScheduledExecutorService es;
    
    public SpanJoinBolt(final int timeout) {
        this.timeout = timeout;
    }
    
    @Override
    public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        this.collector = collector;
        GlobalStreamId source = context.getThisSources().keySet().iterator().next();
        idFields = context.getComponentOutputFields(source.get_componentId(), source.get_streamId());
        
        final long expirationMillis = timeout * 1000L;
        final long sleepTime = expirationMillis / (BUCKET_NUM - 1);
        SecurityManager s = System.getSecurityManager();
        final ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        es = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(group, r, Joiner.on("-").join("Thread-cleanSpanJoin", context.getThisTaskId()), 0);
            if (!t.isDaemon())
                t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        });
        es.scheduleAtFixedRate(() -> {
            synchronized (_lock) {
                pending.rotate();
            }
        }, 100, sleepTime, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void execute(final Tuple input) {
        List<Object> key = input.select(idFields);
        try {
            if (!pending.containsKey(key)) {
                pending.put(key, input);
                return;
            }
            Tuple target = pending.get(key);
            input.getByteByField("span");
            pending.remove(key);
            String thisSpanSpec = input.getString(2);
            String existsSpanSpec = target.getString(2);
            if (thisSpanSpec.equals(existsSpanSpec)) {
                //duplicate data
                pending.put(key, input);
                return;
            }
            byte[] requestSpan = getRequestSpan(target, input);
            if (null == requestSpan) {
                return;
            }
            byte[] ackSpan = getAckSpan(target, input);
            if (null == ackSpan) {
                return;
            }
            collector.emit("forward", new Values(input.getString(0), input.getString(1), requestSpan, ackSpan));
        } finally {
            collector.ack(input);
        }
    }
    
    private byte[] getAckSpan(Tuple... tuples) {
        for (Tuple each : tuples) {
            if (SpanSpec.valueOf(each.getString(2)).equals(SpanSpec.ACK)) {
                return each.getBinary(3);
            }
        }
        return null;
    }
    
    private byte[] getRequestSpan(Tuple... tuples) {
        for (Tuple each : tuples) {
            if (SpanSpec.valueOf(each.getString(2)).equals(SpanSpec.REQ)) {
                return each.getBinary(3);
            }
        }
        return null;
    }
    
    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declareStream("forward", new Fields("trace_id", "level_id", "request", "ack"));
        declarer.declareStream("timeout", new Fields("trace_id", "level_id", "span_spec", "span"));
    }
}
