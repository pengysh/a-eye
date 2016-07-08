package com.a.eye.intelligentanalysis.alarm;

import com.a.eye.intelligentanalysis.alarm.spout.HbaseTraceSpout;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.utils.Utils;

/**
 *  分析调用链关系拓扑.
 *  
 *  @author gaohongtao.
 */
public class AnalysisTraceTopology {
    
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("hbaseTraceSpout", new HbaseTraceSpout());
        Config conf = new Config();
        
        
        if (args != null && args.length > 0) {
            conf.setDebug(false);
            conf.setNumWorkers(1);
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        } else {
            conf.setDebug(true);
            LocalCluster cluster = new LocalCluster();
            StormTopology topology = builder.createTopology();
            cluster.submitTopology("test", conf, topology);
            Utils.sleep(40000);
            cluster.killTopology("test");
            cluster.shutdown();
        }
    }
}
