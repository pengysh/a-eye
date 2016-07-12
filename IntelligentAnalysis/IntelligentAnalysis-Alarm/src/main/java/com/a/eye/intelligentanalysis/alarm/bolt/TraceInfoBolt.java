package com.a.eye.intelligentanalysis.alarm.bolt;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 *  @author gaohongtao.
 */
public class TraceInfoBolt extends BaseRichBolt {
    
    @Override
    public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        
    }
    
    @Override
    public void execute(final Tuple input) {
        
    }
    
    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        
    }
}
