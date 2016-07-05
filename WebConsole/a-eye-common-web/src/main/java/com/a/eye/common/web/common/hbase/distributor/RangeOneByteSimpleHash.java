package com.a.eye.common.web.common.hbase.distributor;


import java.util.Arrays;

import com.a.eye.common.web.common.util.MathUtils;
import com.sematext.hbase.wd.RowKeyDistributorByHashPrefix;

/**
 * Provides handy methods to distribute
 *
 * pinpoint copy and modify : https://github.com/sematext/HBaseWD/blob/master/src/main/java/com/sematext/hbase/wd/RowKeyDistributorByHashPrefix.java
 * @author Alex Baranau
 * @author emeroad
 */
public class RangeOneByteSimpleHash implements RowKeyDistributorByHashPrefix.Hasher {
    private final int start;
    private final int end;
    private int mod;

    // Used to minimize # of created object instances
    // Should not be changed. TODO: secure that
    private static final byte[][] PREFIXES;

    static {
        PREFIXES = new byte[256][];
        for (int i = 0; i < 256; i++) {
            PREFIXES[i] = new byte[] {(byte) i};
        }
    }


    public RangeOneByteSimpleHash(int start, int end, int maxBuckets) {
        if (maxBuckets < 1 || maxBuckets > 256) {
            throw new IllegalArgumentException("maxBuckets should be in 1..256 range");
        }
        this.start = start;
        this.end = end;
        // i.e. "real" maxBuckets value = maxBuckets or maxBuckets-1
        this.mod = maxBuckets;
    }

    @Override
    public byte[] getHashPrefix(byte[] originalKey) {
        long hash = MathUtils.fastAbs(hashBytes(originalKey));
        return new byte[] {(byte) (hash % mod)};
    }

    /** Compute hash for binary data. */
    private int hashBytes(byte[] bytes) {
        int min = Math.min(bytes.length, end);
        int hash = 1;
        for (int i = start; i < min; i++)
            hash = (31 * hash) + (int) bytes[i];
        return hash;
    }

    @Override
    public byte[][] getAllPossiblePrefixes() {
        return Arrays.copyOfRange(PREFIXES, 0, mod);
    }

    @Override
    public int getPrefixLength(byte[] adjustedKey) {
        return 1;
    }

    @Override
    public String getParamsToStore() {
        return String.valueOf(mod);
    }

    @Override
    public void init(String storedParams) {
        this.mod = Integer.parseInt(storedParams);
    }

}
