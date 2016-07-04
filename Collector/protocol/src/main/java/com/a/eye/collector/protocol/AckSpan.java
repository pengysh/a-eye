package com.a.eye.collector.protocol;

import com.a.eye.collector.protocol.common.AbstractDataSerializable;

/**
 * Created by wusheng on 16/7/4.
 */
public class AckSpan extends AbstractDataSerializable {
    /**
     * tid，调用链的全局唯一标识
     */
    protected String traceId;
    /**
     * 当前调用链的上级描述<br/>
     * 如当前序号为：0.1.0时，parentLevel=0.1
     */
    protected String parentLevel;
    /**
     * 当前调用链的本机描述<br/>
     * 如当前序号为：0.1.0时，levelId=0
     */
    protected int levelId = 0;
    /**
     * 节点调用花费时间
     */
    protected long cost = 0L;
    /**
     * 节点调用的状态<br/>
     * 0：成功<br/>
     * 1：异常<br/>
     * 异常判断原则：代码产生exception，并且此exception不在忽略列表中
     */
    protected byte statusCode = 0;
    /**
     * 节点调用的错误堆栈<br/>
     * 堆栈以JAVA的exception为主要判断依据
     */
    protected String exceptionStack;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentLevel() {
        return parentLevel;
    }

    public void setParentLevel(String parentLevel) {
        this.parentLevel = parentLevel;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public byte getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(byte statusCode) {
        this.statusCode = statusCode;
    }

    public String getExceptionStack() {
        return exceptionStack;
    }

    public void setExceptionStack(String exceptionStack) {
        this.exceptionStack = exceptionStack;
    }

    @Override
    public int getDataType() {
        return 2;
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
