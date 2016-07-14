package com.a.eye.intelligentanalysis.alarm.bolt;

import com.a.eye.intelligentanalysis.alarm.common.VolatilePolicy;
import org.apache.storm.shade.org.joda.time.DateTime;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.utils.RotatingMap;

import java.util.Map;

/**
 *  @author gaohongtao.
 */
public abstract class AbstractRotatingMapBolt<K,V> extends BaseRichBolt {
    
    private static final int BUCKET_NUM = 3;
    
    private final long rotatingInterval;
    
    private final int maxCacheSize;
    
    private final VolatilePolicy volatilePolicy;
    
    private RotatingMap<K, V> pending;
    
    private int cacheSize;
    
    private long latestRotatingTime;
    
    public AbstractRotatingMapBolt(final long ttl, final int maxCacheSize, final VolatilePolicy volatilePolicy){
        this.rotatingInterval = ttl / (BUCKET_NUM - 1);
        this.maxCacheSize = maxCacheSize;
        this.volatilePolicy = volatilePolicy;
    }
    
    protected RotatingMap.ExpiredCallback<K,V> supplyExpiredCallback() {
        return null;
    }
    
    protected boolean put(final K key, final V value) {
        if (!canPut()) {
            return false;
        }
        pending.put(key, value);
        cacheSize++;
        return true;
    }
    
    private boolean canPut() {
        if (cacheSize < maxCacheSize) {
            return true;
        }
        switch (volatilePolicy) {
            case TTL: rotate(1); return canPut();
            case FORBIDDEN: return false;
            default: return true;
        }
    }
    
    protected boolean containsKey(final K key) {
        return pending.containsKey(key);
    }
    
    protected V get(final K key) {
        return pending.get(key);
    }
    
    protected Object remove(final K key) {
        cacheSize--;
        return pending.remove(key);
    }
    
    protected int size() {
        return cacheSize;
    }
    
    @Override
    public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        pending = new RotatingMap<>(BUCKET_NUM, supplyExpiredCallback());
        latestRotatingTime = DateTime.now().getMillis();
        prepareBolt(stormConf, context, collector);
    }
    
    protected abstract void prepareBolt(final Map stormConf, final TopologyContext context, final OutputCollector collector);
    
    @Override
    public void execute(final Tuple input) {
        long interval = DateTime.now().getMillis() - latestRotatingTime;
        if (interval > rotatingInterval) {
            rotate(interval / rotatingInterval);
        }
        executeBolt(input);
    }
    
    protected abstract void executeBolt(final Tuple input);
    
    private void rotate(final long rotatingTimes) {
        for (int i = 0; i < rotatingTimes; i++) {
            Map expiredMap = pending.rotate();
            cacheSize -= expiredMap.size();
        }
    }
}
