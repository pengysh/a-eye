package com.a.eye.common.web.common.hbase;

import org.apache.hadoop.hbase.client.Result;

/**
 * @author emeroad
 */
public class EmptyLimitEventHandler implements LimitEventHandler{

    @Override
    public void handleLastResult(Result lastResult) {
    }
}
