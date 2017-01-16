package com.icbc.dds.metrics;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.Timer;
import org.junit.Before;
import org.junit.Test;

import java.util.SortedMap;

import static org.junit.Assert.*;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public class MetricsTest {
    private final Metrics metrics = new Metrics();

    @Before
    public void setUp() {
        metrics.getRegistry().removeMatching(MetricFilter.ALL);
    }

    @Test
    public void oneThreadTick1000Times() throws InterruptedException {
        Thread thread = new Thread(r);
        thread.run();
        thread.join();

        SortedMap<String, Timer> timers = metrics.getRegistry().getTimers();
        assertEquals(1000, timers.get("test-timer").getCount());
    }

    @Test
    public void twoThreadsTickPer1000Times() throws InterruptedException {
        Thread thread1 = new Thread(r);
        thread1.run();
        Thread thread2 = new Thread(r);
        thread2.run();

        thread1.join();
        thread2.join();

        SortedMap<String, Timer> timers = metrics.getRegistry().getTimers();
        assertEquals(2000, timers.get("test-timer").getCount());
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 1000; i++) {
                    metrics.tickStart("test-timer");
                    Thread.sleep(1);
                    metrics.tickStop("test-timer", true);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}