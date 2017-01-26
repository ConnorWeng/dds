package com.icbc.dds.metrics;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by kfzx-wengxj on 24/01/2017.
 */
public class SingleServiceMetric {
    private final static int COLLISION_BUFFER = 256;

    private ConcurrentSkipListMap<Long, Long> measurements;
    private AtomicLong lastTick;
    private AtomicLong failTimes;

    public SingleServiceMetric() {
        this.measurements = new ConcurrentSkipListMap<Long, Long>();
        this.lastTick = new AtomicLong(System.nanoTime() * COLLISION_BUFFER);
        this.failTimes = new AtomicLong(0L);
    }

    public void update(long value, boolean success) {
        measurements.put(getTick(), value);
        if (!success) {
            failTimes.incrementAndGet();
        }
    }

    public Snapshot getSnapshot() {
        long lastKey = measurements.lastKey();
        long firstKey = measurements.firstKey();
        long window = (lastKey - firstKey) / COLLISION_BUFFER;
        ConcurrentNavigableMap<Long, Long> headMap = measurements.headMap(measurements.lastKey(), true);
        Snapshot snapshot = new Snapshot(headMap.values(), failTimes.get(), window, TimeUnit.NANOSECONDS);
        failTimes.set(0L);
        headMap.clear();
        return snapshot;
    }

    private long getTick() {
        while (true) {
            long oldTick = lastTick.get();
            long tick = System.nanoTime() * COLLISION_BUFFER;
            /*
             * 引入COLLISION_BUFFER是为了让这里的+1操作仅等于+1/256个纳秒，而不是+1纳秒，
             * 从而之后在snapshot计算时间窗口不会出现大误差。
             *
             * 举例：
             * 如果没有COLLISION_BUFFER，假设1纳秒内，单线程调用了本方法256次，那么每次都是
             *  tick - oldTick <= 0，所以每一次调用导致lastTick增加1纳秒，256次调用等于
             * lastTick增加了256纳秒，然而实际时间还是在1纳秒内。后续计算tps时，如果使用(
             * 调用次数/256纳秒)肯定是错的，对的应该是(调用次数/1纳秒)。通过COLLISION_BU
             * FFER，256次调用等于lastTick增加了256/256 = 1纳秒，计算tps，使用(调用次数
             * /1纳秒)是正确的。只要1纳秒内，调用本方法次数不要超过256次，那么计算都是没有问
             * 题的。
             */
            long newTick = tick - oldTick > 0 ? tick : oldTick + 1;
            if (lastTick.compareAndSet(oldTick, newTick)) {
                return newTick;
            }
        }
    }

    private void trim(long key) {
        measurements.headMap(key).clear();
    }
}
