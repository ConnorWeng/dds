package com.icbc.dds.metrics;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ConnorWeng on 2017/1/26.
 */
public class SingleServiceMetricTest {
    private SingleServiceMetric singleServiceMetric;

    @Before
    public void setUp() {
        singleServiceMetric = new SingleServiceMetric();
    }

    @Test
    public void updateTwiceIn1sThenTpsShouldBe2() throws InterruptedException {
        singleServiceMetric.update(1 * 1000 * 1000, true); // 1ms
        singleServiceMetric.update(2 * 1000 * 1000, true); // 2ms
        Snapshot snapshot = singleServiceMetric.getSnapshot();
        assertEquals(2, Math.round(snapshot.tps()));
    }

    @Test
    public void updateTwiceIn2sThenTpsShouldBe1() throws InterruptedException {
        singleServiceMetric.update(1 * 1000 * 1000, true); // 1ms
        Thread.sleep(1500L);
        singleServiceMetric.update(2 * 1000 * 1000, true); // 2ms
        Snapshot snapshot = singleServiceMetric.getSnapshot();
        assertEquals(1, Math.round(snapshot.tps()));
    }
}
