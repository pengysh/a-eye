package com.a.eye.collector.protocol.common;

/**
 * Created by wusheng on 16/7/4.
 */
public interface ISerializable {
    byte[] convert2Bytes();

    NullableClass convert2Object(byte[] data);

}
