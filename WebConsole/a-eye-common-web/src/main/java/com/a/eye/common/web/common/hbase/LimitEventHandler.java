package com.a.eye.common.web.common.hbase;

import org.apache.hadoop.hbase.client.Result;

/**
 * @author emeroad
 */
public interface LimitEventHandler {
    void handleLastResult(Result lastResult);
}
