package com.a.eye.common.web.common.hbase;

/**
 * @author emeroad
 */
public interface ValueMapper<T> {
    byte[] mapValue(T value);
}
