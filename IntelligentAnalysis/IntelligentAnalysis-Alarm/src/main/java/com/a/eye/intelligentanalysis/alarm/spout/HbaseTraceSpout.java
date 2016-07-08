package com.a.eye.intelligentanalysis.alarm.spout;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.storm.shade.org.joda.time.DateTime;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 *  @author gaohongtao.
 */
public class HbaseTraceSpout extends BaseRichSpout {
    
    private static final Logger LOG = LoggerFactory.getLogger(HbaseTraceSpout.class);
    
    private static final int HBASE_STREAM_DATA_START_SEC = 3 * 60;
    
    private static final String TABLE_NAME = "sw-call-chain-new";
    
    public static final byte[] FAMILY_NAME = Bytes.toBytes("call-chain");
    
    private Table table;
    
    private byte[] startKey;
    
    private SpoutOutputCollector collector;
    
    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
    }
    
    @Override
    public void open(final Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
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
        ResultScanner rs = scanTable();
        Result rr = new Result();
        if (null == rs) {
            return;
        }
        byte[] rowkey = null;
        while (rr != null) {
            try {
                rr = rs.next();
            } catch (IOException e) {
                LOG.error("Catch exception: ", e);
            }
            if (rr != null && !rr.isEmpty()) {
                rowkey = rr.getRow();
                Values tuple = new Values(rowkey);
                for (Cell each : rr.rawCells()) {
                    addTuple(tuple, each.getQualifierArray(), each.getQualifierLength(), each.getQualifierOffset());
                    addTuple(tuple, each.getValueArray(), each.getValueLength(), each.getValueOffset());
                }
                collector.emit(tuple);
            }
        }
        rs.close();
        if (rowkey != null)
            startKey = rowkey;
    }
    
    private void addTuple(final Values tuple, final byte[] data, final int length, final int offset) {
        byte[] result = new byte[length];
        System.arraycopy(data, offset, result, 0, length);
        tuple.add(result);
    }
    
    private ResultScanner scanTable() {
        
        Scan scan = new Scan();
        scan.addFamily(FAMILY_NAME);
        
        // Initialize startKey when the first scanning table request
        if (null == startKey) {
            startKey = new byte[5];
            int startTs = makeTimestamp(HBASE_STREAM_DATA_START_SEC);
            //TODO start key
            Bytes.putInt(startKey, 1, startTs);
        }
        
        // Initialize stopKey
        byte[] stopKey = new byte[5];
        //TODO stop key
        Bytes.putInt(stopKey, 1, 0);
        
        // Set the range of scanning table
        scan.setStartRow(startKey);
        scan.setMaxResultSize(100);
        scan.setStopRow(stopKey);
        
        // get result of this scanning process
        ResultScanner rs = null;
        try {
            rs = table.getScanner(scan);
        } catch (IOException e) {
            LOG.error("Catch exception: ", e);
        }
        return rs;
    }
    
    private int makeTimestamp(int secInterval) {
        long now = new DateTime().getMillis();
        int nowTs = (int)(now /1000);
        return (nowTs - secInterval);
    }
}
