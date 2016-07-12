package com.a.eye.intelligentanalysis.alarm.spout;

import com.a.eye.intelligentanalysis.alarm.common.HbaseScanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.storm.metric.api.AssignableMetric;
import org.apache.storm.metric.api.MultiCountMetric;
import org.apache.storm.shade.com.google.common.base.Joiner;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *  @author gaohongtao.
 */
public class HbaseTraceSpout extends BaseRichSpout {
    
    private static final Logger LOG = LogManager.getLogger(HbaseTraceSpout.class);
    
    private final BlockingQueue<Values> queue = new LinkedBlockingQueue<>(1000);
    
    private final int shardingNum;
    
    private SpoutOutputCollector collector;
    
    public HbaseTraceSpout(final int shardingNum) {
        this.shardingNum = shardingNum;
    }
    
    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("trace_id", "level_id", "span_spec", "span"));
    }
    
    @Override
    public void open(final Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
        this.collector = collector;
        
        // According to the task id, decide which sharding partitions this task will deal with.
        // And create a thread for every sharding partition.
        int taskId = context.getThisTaskId();
        String componentId = context.getComponentId(taskId);
        int taskNum = context.getComponentTasks(componentId).size();
        int rem = taskId % taskNum;
    
        MultiCountMetric spoutCountMetric = new MultiCountMetric();
        context.registerMetric("hbase_read_count", spoutCountMetric, 20);
        int threadNum = 0;
        try {
            for (short shardingKey = 0; shardingKey < shardingNum; shardingKey++) {
                if (shardingKey % taskNum == rem) {
                    String shardingName = Joiner.on("-").join("partition-", shardingKey);
                    AssignableMetric rowKeyMetric = new AssignableMetric("");
                    context.registerMetric(Joiner.on("-").join("rowkey", shardingName), rowKeyMetric, 20);
                    Thread scanThread = new Thread(new HbaseScanner(shardingKey, queue, spoutCountMetric.scope(shardingName), rowKeyMetric));
                    scanThread.setName(Joiner.on("-").join("Thread", "scanThread", taskNum, ++threadNum));
                    scanThread.setDaemon(true);
                    scanThread.start();
                }
            }
        } catch (Exception e) {
            LOG.error("failed to create scan thread", e);
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void nextTuple() {
        Values tuple;
        try {
            while ((tuple = queue.take()) != null) {
                collector.emit(tuple);
            }
        } catch (final InterruptedException ignored) {
        }
    }
}
