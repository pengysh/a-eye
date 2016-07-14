package com.a.eye.intelligentanalysis.alarm.bolt;

import com.a.eye.intelligentanalysis.alarm.common.SpanSpec;
import com.a.eye.intelligentanalysis.alarm.common.VolatilePolicy;
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
public class SpanJoinBolt extends AbstractRotatingMapBolt<List<Object>, Tuple> {
    
    private OutputCollector collector;
    
    private Fields idFields;
    
    public SpanJoinBolt(final long ttl, final int maxCacheSize, final VolatilePolicy volatilePolicy) {
        super(ttl, maxCacheSize, volatilePolicy);
    }
    
    @Override
    protected RotatingMap.ExpiredCallback<List<Object>, Tuple> supplyExpiredCallback() {
        return (key, val) -> collector.emit("timeout", val.getValues());
    }
    
    @Override
    protected void prepareBolt(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        this.collector = collector;
        GlobalStreamId source = context.getThisSources().keySet().iterator().next();
        idFields = context.getComponentOutputFields(source.get_componentId(), source.get_streamId());
    }
    
    @Override
    protected void executeBolt(final Tuple input) {
        List<Object> key = input.select(idFields);
        try {
            if (!containsKey(key)) {
                if (!put(key, input)) {
                    throw new RuntimeException("max cache");
                }
                return;
            }
            Tuple target = get(key);
            input.getByteByField("span");
            remove(key);
            String thisSpanSpec = input.getString(2);
            String existsSpanSpec = target.getString(2);
            if (thisSpanSpec.equals(existsSpanSpec)) {
                //duplicate data
                put(key, input);
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
        } catch (final Exception exp){
            collector.fail(input);
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
