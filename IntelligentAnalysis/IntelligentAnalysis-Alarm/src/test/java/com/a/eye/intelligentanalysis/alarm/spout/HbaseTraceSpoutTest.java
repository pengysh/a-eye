package com.a.eye.intelligentanalysis.alarm.spout;

import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *  @author gaohongtao.
 */
public class HbaseTraceSpoutTest {
    
    @Test
    public void testScan() throws IOException {
        HbaseTraceSpout spout = new HbaseTraceSpout();
        spout.open(null, null, null);
        try (ResultScanner rs = spout.scanTable(Bytes.toBytes("1.0b.1461060884538"))) {
            assertNotNull(rs.next());
        }
    
        try (ResultScanner rs = spout.scanTable(Bytes.toBytes("1.0b.1461060884539"))) {
            assertNull(rs.next());
        }
    }
}