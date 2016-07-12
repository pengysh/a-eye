package com.a.eye.intelligentanalysis.alarm;

import com.a.eye.intelligentanalysis.alarm.bolt.InvokeRelationshipBolt;
import com.a.eye.intelligentanalysis.alarm.bolt.SpanJoinBolt;
import com.a.eye.intelligentanalysis.alarm.bolt.TimeoutSpanAlarmBolt;
import com.a.eye.intelligentanalysis.alarm.bolt.TraceInfoBolt;
import com.a.eye.intelligentanalysis.alarm.spout.HbaseTraceSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

/**
 *  分析调用链关系拓扑.
 *  
 *  @author gaohongtao.
 */
public class AnalysisTraceTopology {
    
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("hbaseTraceSpout", new HbaseTraceSpout(32), 8);
        
        //汇聚REQ和ACK
        builder.setBolt("spanJoinBolt", new SpanJoinBolt(30), 8).fieldsGrouping("hbaseTraceSpout", new Fields("trace_id", "level_id"));
        //汇聚REQ和ACK,超时后告警
        builder.setBolt("timeoutAlarmSpanBolt", new TimeoutSpanAlarmBolt(), 4).shuffleGrouping("spanJoinBolt");
        
        //计算A->B直接关系
        builder.setBolt("invokeRelationshipBolt", new InvokeRelationshipBolt(), 4).fieldsGrouping("spanJoinBolt", "forward", new Fields("trace_id"));
        //计算调用链信息,用第一个调用点代表整个调用链信息
        builder.setBolt("traceInfoBolt", new TraceInfoBolt(), 4).fieldsGrouping("spanJoinBolt", "forward", new Fields("trace_id"));
        //TODO 根据需要再增加新的BOLT
        
        Config conf = new Config();
        
        if (args != null && args.length > 0) {
            conf.setDebug(false);
            conf.setNumWorkers(1);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        } else {
            conf.setDebug(true);
            conf.setMaxTaskParallelism(2);
            conf.registerMetricsConsumer(org.apache.storm.metric.LoggingMetricsConsumer.class, 1);
            LocalCluster cluster = new LocalCluster();
            StormTopology topology = builder.createTopology();
            cluster.submitTopology("test", conf, topology);
            Utils.sleep(60000);
            cluster.killTopology("test");
            cluster.shutdown();
        }
    }
}
