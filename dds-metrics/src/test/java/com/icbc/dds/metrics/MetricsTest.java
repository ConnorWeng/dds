package com.icbc.dds.metrics;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ConnorWeng on 2017/1/26.
 */
public class MetricsTest {
    private Metrics metrics = new Metrics();

    @Before
    public void setUp() {
        metrics.clear();
    }

    @Test
    public void tickSuccessOnce() {
        metrics.tickStart("test-service");
        metrics.tickStop("test-service", true);
        Snapshot snapshot = Metrics.getRegistry().get("test-service").getSnapshot();
        assertEquals(1, snapshot.count());
        assertEquals(1, snapshot.success());
        assertEquals(0, snapshot.fail());
    }

    @Test
    public void tickFailOnce() {
        metrics.tickStart("test-service");
        metrics.tickStop("test-service", false);
        Snapshot snapshot = Metrics.getRegistry().get("test-service").getSnapshot();
        assertEquals(1, snapshot.count());
        assertEquals(0, snapshot.success());
        assertEquals(1, snapshot.fail());
    }
}
