package com.icbc.dds.metrics;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Created by kfzx-wengxj on 24/01/2017.
 */
public class Snapshot {
    private final long[] values;
    private long failTimes;
    private long window;

    public Snapshot(Collection<Long> values, long failTimes, long window, TimeUnit timeUnit) {
        this.failTimes = failTimes;
        this.window = timeUnit.toSeconds(window) + 1;
        final Object[] copy = values.toArray();
        this.values = new long[copy.length];
        for (int i = 0; i < copy.length; i++) {
            this.values[i] = (Long) copy[i];
        }
        Arrays.sort(this.values);
    }

    public int count() {
        return values.length;
    }

    public double tps() {
        if (window == 0L) {
            return 0;
        }
        return count() / window;
    }

    public double max() {
        return TimeUnit.NANOSECONDS.toMillis(values[values.length - 1]);
    }

    public double mean() {
        if (values.length == 0) {
            return 0;
        }
        long total = 0;
        for (int i = 0; i < values.length; i++) {
            total += values[i];
        }
        return (total / values.length) * 1000 * 1000;
    }

    public long min() {
        return TimeUnit.NANOSECONDS.toMillis(values[0]);
    }

    public long fail() {
        return failTimes;
    }

    public long success() {
        return values.length - failTimes;
    }

    @Override
    public String toString() {
        return String.format("count: %s, tps: %s, max: %s ms, mean: %s ms, min: %s ms, fail: %s, success: %s", count(), tps(), max(), mean(), min(), fail(), success());
    }
}
