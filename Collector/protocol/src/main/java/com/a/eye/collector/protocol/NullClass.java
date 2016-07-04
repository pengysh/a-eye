package com.a.eye.collector.protocol;

import com.a.eye.collector.protocol.common.NullableClass;

/**
 * Created by wusheng on 16/7/4.
 */
public class NullClass implements NullableClass {
    @Override
    public boolean isNull() {
        return true;
    }
}
