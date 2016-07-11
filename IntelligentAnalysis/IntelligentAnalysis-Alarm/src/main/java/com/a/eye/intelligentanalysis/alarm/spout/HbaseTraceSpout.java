package com.a.eye.intelligentanalysis.alarm.spout;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.storm.metric.api.AssignableMetric;
import org.apache.storm.metric.api.CountMetric;
import org.apache.storm.shade.org.joda.time.DateTime;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Values;

import java.io.IOException;
import java.util.Map;

/**
 *  @author gaohongtao.
 */
public class HbaseTraceSpout extends BaseRichSpout {
    
    private static final Logger LOG = LogManager.getLogger(HbaseTraceSpout.class);
    
    private static final int HBASE_STREAM_DATA_START_SEC = 3 * 60 * 1000;
    
    private static final String TABLE_NAME = "sw-call-chain-new";
    
    public static final byte[] FAMILY_NAME = Bytes.toBytes("call-chain");
    
    public static final String VERSION = "1.0b.";
    
    private Table table;
    
    private byte[] startKey = Bytes.toBytes("1.0b.1461060884538");
    
    private SpoutOutputCollector collector;
    
    private transient CountMetric spoutCountMetric;
    
    private transient AssignableMetric rowKeyMetric;
      
    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
    }
    
    @Override
    public void open(final Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
        spoutCountMetric = new CountMetric();
        context.registerMetric("hbase_read_count", spoutCountMetric, 20);
        rowKeyMetric = new AssignableMetric(new String(startKey));
        context.registerMetric("current_row_key", rowKeyMetric, 20);
        try {
            table = ConnectionFactory.createConnection().getTable(TableName.valueOf(TABLE_NAME));
        } catch (IOException e) {
            LOG.error("init connection error", e);
            throw new RuntimeException(e);
        }
        this.collector = collector;
    }
    
    @Override
    public void nextTuple() {
        ResultScanner rs = scanTable(startKey);
        Result rr = new Result();
        if (null == rs) {
            return;
        }
        byte[] rowKey = null;
        while (rr != null) {
            try {
                rr = rs.next();
            } catch (IOException e) {
                LOG.error("Catch exception: ", e);
            }
            if (rr == null || rr.isEmpty()) {
                continue;
            }
            rowKey = rr.getRow();
            Values tuple = new Values(rowKey);
            for (Cell each : rr.rawCells()) {
                addTuple(tuple, each.getQualifierArray(), each.getQualifierLength(), each.getQualifierOffset());
                addTuple(tuple, each.getValueArray(), each.getValueLength(), each.getValueOffset());
            }
            spoutCountMetric.incr();
            collector.emit(tuple);
        }
        rs.close();
        if (rowKey != null){
            startKey = rowKey;
            rowKeyMetric.setValue(new String(startKey));
        }
            
    }
    
    private void addTuple(final Values tuple, final byte[] data, final int length, final int offset) {
        byte[] result = new byte[length];
        System.arraycopy(data, offset, result, 0, length);
        tuple.add(result);
    }
    
    ResultScanner scanTable(byte[] startKey) {
        
        Scan scan = new Scan();
        scan.addFamily(FAMILY_NAME);
        
        // Initialize startKey when the first scanning table request
        if (null == startKey) {
            long startTs =  makeTimestamp(HBASE_STREAM_DATA_START_SEC);
            startKey = Bytes.toBytes(VERSION + startTs);
        }
        // Set the range of scanning table
        byte[] startKeyExcludeFirst = Bytes.add(startKey ,Bytes.toBytes("0"));
        scan.setStartRow(startKeyExcludeFirst);
        scan.setMaxResultSize(100);
        
        // get result of this scanning process
        ResultScanner rs = null;
        try {
            rs = table.getScanner(scan);
        } catch (IOException e) {
            LOG.error("Catch exception: ", e);
        }
        return rs;
    }
    
    private long makeTimestamp(int secInterval) {
        long now = new DateTime().getMillis();
        return (now - secInterval);
    }
}
