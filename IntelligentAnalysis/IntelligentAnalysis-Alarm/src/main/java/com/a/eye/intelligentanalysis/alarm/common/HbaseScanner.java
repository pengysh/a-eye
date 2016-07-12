package com.a.eye.intelligentanalysis.alarm.common;

import com.a.eye.collector.protocol.AckSpan;
import com.a.eye.collector.protocol.RequestSpan;
import com.google.common.base.Joiner;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
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
import org.apache.storm.tuple.Values;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 *  @author gaohongtao.
 */
public class HbaseScanner implements Runnable{
    
    private static final Logger LOG = LogManager.getLogger(HbaseScanner.class);
    
    private static final int HBASE_STREAM_DATA_START_SEC = 3 * 60 * 1000;
    
    private static final String TABLE_NAME = "sw-call-chain-new";
    
    public static final byte[] FAMILY_NAME = Bytes.toBytes("span");
    
    public static final RequestSpan REQUEST_SPAN =  new RequestSpan();
    
    public static final AckSpan ACK_SPAN =  new AckSpan();
    
    private final BlockingQueue<Values> queue;
    
    private final Table table;
    
    private final int shardingKey;
    
    private final CountMetric spoutCountMetric;
    
    private final AssignableMetric rowKeyMetric;
    
    private byte[] startKey = Bytes.toBytes("1.0b.1461060884538");
    
    public HbaseScanner(final int shardingKey, final BlockingQueue<Values> queue, final CountMetric spoutCountMetric, final AssignableMetric rowKeyMetric) {
        try {
            table = ConnectionFactory.createConnection().getTable(TableName.valueOf(TABLE_NAME));
        } catch (IOException e) {
            LOG.error("init connection error", e);
            throw new RuntimeException(e);
        }
        this.queue = queue;
        this.shardingKey = shardingKey;
        this.spoutCountMetric = spoutCountMetric;
        this.rowKeyMetric = rowKeyMetric;
        
    }
    
    @Override
    public void run() {
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
            try {
                
                Cell[] cells = rr.rawCells();
                if (rr.isEmpty() || rr.isPartial() || rr.isStale() || cells.length < 1) {
                    LOG.warn("stale data");
                }
                String qualifier = new String(CellUtil.cloneQualifier(cells[0]));
                String[] qualifierItems = qualifier.split("-");
                if (qualifierItems.length < 2) {
                    LOG.warn("column name ");
                    continue;
                }
                String traceId;
                SpanSpec spanSpec;
                byte[] spanValue = CellUtil.cloneValue(cells[0]);
                switch (SpanSpec.valueOf(qualifierItems[0])) {
                    case REQ:
                        traceId = ((RequestSpan) REQUEST_SPAN.convertData(spanValue)).getTraceId();
                        spanSpec = SpanSpec.REQ;
                        break;
                    case ACK:
                        traceId = ((AckSpan) ACK_SPAN.convertData(spanValue)).getTraceId();
                        spanSpec = SpanSpec.ACK;
                        break;
                    default:
                        continue;
                }
                queue.put(new Values(traceId, qualifierItems[1], spanSpec.name(), spanValue));
                spoutCountMetric.incr();
            } catch (InterruptedException e) {
                LOG.error(e);
            }
        }
        rs.close();
        if (rowKey != null){
            startKey = rowKey;
            rowKeyMetric.setValue(new String(startKey));
        }
    }
    
    private ResultScanner scanTable(byte[] startKey) {
        Scan scan = new Scan();
        scan.addFamily(FAMILY_NAME);
        
        // Initialize startKey when the first scanning table request
        if (null == startKey) {
            long startTs =  makeTimestamp(HBASE_STREAM_DATA_START_SEC);
            startKey = Bytes.toBytes(Joiner.on(".").join(shardingKey, startTs));
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
