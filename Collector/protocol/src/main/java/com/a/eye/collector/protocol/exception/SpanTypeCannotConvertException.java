package com.a.eye.collector.protocol.exception;

public class SpanTypeCannotConvertException extends RuntimeException {
    public SpanTypeCannotConvertException(int spanTypeValue) {
        super("Can not convert SpanTypeValue[" + spanTypeValue + "]");
    }
}
