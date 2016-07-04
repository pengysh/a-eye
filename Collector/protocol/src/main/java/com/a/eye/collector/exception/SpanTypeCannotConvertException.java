package com.a.eye.collector.exception;

public class SpanTypeCannotConvertException extends RuntimeException {
    public SpanTypeCannotConvertException(String spanTypeValue) {
        super("Can not convert SpanTypeValue[" + spanTypeValue + "]");
    }
}
