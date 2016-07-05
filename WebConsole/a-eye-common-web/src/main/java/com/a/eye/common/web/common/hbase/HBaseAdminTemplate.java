package com.a.eye.common.web.common.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.data.hadoop.hbase.HbaseSystemException;

/**
 * @author emeroad
 */
public class HBaseAdminTemplate {

    private final Admin admin;
    private final Connection connection;

    public HBaseAdminTemplate(Configuration configuration) {
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (Exception e) {
            throw new HbaseSystemException(e);
        }
    }

    public boolean createTableIfNotExist(HTableDescriptor htd) {
        try {
            if (!admin.tableExists(htd.getTableName())) {
                this.admin.createTable(htd);
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new HbaseSystemException(e);
        }
    }

    public boolean tableExists(String tableName) {
        try {
            return admin.tableExists(TableName.valueOf(tableName));
        } catch (IOException e) {
            throw new HbaseSystemException(e);
        }
    }

    public boolean dropTableIfExist(String tableName) {
        TableName tn = TableName.valueOf(tableName);
        try {
            if (admin.tableExists(tn)) {
                this.admin.disableTable(tn);
                this.admin.deleteTable(tn);
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new HbaseSystemException(e);
        }
    }

    public void dropTable(String tableName) {
        TableName tn = TableName.valueOf(tableName);
        try {
            this.admin.disableTable(tn);
            this.admin.deleteTable(tn);
        } catch (IOException e) {
            throw new HbaseSystemException(e);
        }
    }

    public void close() {
        try {
            this.admin.close();
            this.connection.close();
        } catch (IOException e) {
            throw new HbaseSystemException(e);
        }
    }
}
