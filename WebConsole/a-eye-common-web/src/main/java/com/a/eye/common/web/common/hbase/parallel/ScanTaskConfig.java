package com.a.eye.common.web.common.hbase.parallel;

import com.sematext.hbase.wd.AbstractRowKeyDistributor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.springframework.data.hadoop.hbase.HbaseAccessor;

import java.nio.charset.Charset;

/**
 * @author HyunGil Jeong
 */
public class ScanTaskConfig {

    private final String tableName;
    private final Configuration configuration;
    private final Charset charset;
    private final HTableInterfaceFactory tableFactory;

    private final AbstractRowKeyDistributor rowKeyDistributor;
    private final int scanTaskQueueSize;

    public ScanTaskConfig(String tableName, HbaseAccessor hbaseAccessor, AbstractRowKeyDistributor rowKeyDistributor, int scanCaching) {
        this(tableName, hbaseAccessor.getConfiguration(), hbaseAccessor.getCharset(), hbaseAccessor.getTableFactory(), rowKeyDistributor, scanCaching);
    }

    public ScanTaskConfig(String tableName, Configuration configuration, Charset charset, HTableInterfaceFactory tableFactory, AbstractRowKeyDistributor rowKeyDistributor, int scanCaching) {
        if (tableName == null) {
            throw new NullPointerException("No table specified");
        }
        if (rowKeyDistributor == null) {
            throw new NullPointerException("rowKeyDistributor must not be null");
        }
        this.tableName = tableName;
        this.configuration = configuration;
        this.charset = charset;
        this.tableFactory = tableFactory;
        this.rowKeyDistributor = rowKeyDistributor;
        if (scanCaching > 0) {
            this.scanTaskQueueSize = scanCaching;
        } else {
            this.scanTaskQueueSize = configuration.getInt(
                    HConstants.HBASE_CLIENT_SCANNER_CACHING,
                    HConstants.DEFAULT_HBASE_CLIENT_SCANNER_CACHING);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Charset getCharset() {
        return charset;
    }

    public HTableInterfaceFactory getTableFactory() {
        return tableFactory;
    }

    public AbstractRowKeyDistributor getRowKeyDistributor() {
        return rowKeyDistributor;
    }

    public int getScanTaskQueueSize() {
        return scanTaskQueueSize;
    }
}
