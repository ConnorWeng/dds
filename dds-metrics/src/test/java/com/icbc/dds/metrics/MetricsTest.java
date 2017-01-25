package com.icbc.dds.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.Timer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.SortedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void oneThreadTicks1000Times() throws InterruptedException {
        Thread thread = new Thread(r1);
        thread.start();
        thread.join();

        SortedMap<String, Timer> timers = metrics.getRegistry().getTimers();
        assertEquals(1000, timers.get("test-timer").getCount());
    }

    @Test
    public void twoThreadsTickPer1000Times() throws InterruptedException {
        Thread thread1 = new Thread(r1);
        thread1.start();
        Thread thread2 = new Thread(r1);
        thread2.start();

        thread1.join();
        thread2.join();

        SortedMap<String, Timer> timers = metrics.getRegistry().getTimers();
        assertEquals(2000, timers.get("test-timer").getCount());
    }

    @Test
    public void oneThreadFails100Times() throws InterruptedException {
        Thread thread = new Thread(r2);
        thread.start();
        thread.join();

        SortedMap<String, Meter> meters = metrics.getRegistry().getMeters();
        assertEquals(100, meters.get("test-timer#fail").getCount());
    }

    @Test
    public void threeThreadsFail100TimesTick3000Times() throws InterruptedException {
        Thread thread1 = new Thread(r1);
        thread1.start();
        Thread thread2 = new Thread(r1);
        thread2.start();
        Thread thread3 = new Thread(r2);
        thread3.start();

        thread1.join();
        thread2.join();
        thread3.join();

        SortedMap<String, Timer> timers = metrics.getRegistry().getTimers();
        assertEquals(3000, timers.get("test-timer").getCount());
        SortedMap<String, Meter> meters = metrics.getRegistry().getMeters();
        assertEquals(100, meters.get("test-timer#fail").getCount());
    }

    @Test
    @Ignore
    public void oneThreadFailsNearly100TimesPerMinute() throws InterruptedException {
        Thread thread = new Thread(r3);
        thread.start();

        thread.join();

        SortedMap<String, Meter> meters = metrics.getRegistry().getMeters();
        double oneMinuteRate = meters.get("test-timer#fail").getOneMinuteRate();
        System.out.println(String.format("one minute fail rate: %s", oneMinuteRate));
        assertTrue(oneMinuteRate > 70 && oneMinuteRate < 100);
    }

    private Runnable r1 = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                metrics.tickStart("test-timer");
                metrics.tickStop("test-timer", true);
            }
        }
    };

    private Runnable r2 = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                metrics.tickStart("test-timer");
                if (i % 10 == 0) {
                    metrics.tickStop("test-timer", false);
                } else {
                    metrics.tickStop("test-timer", true);
                }
            }
        }
    };

    private Runnable r3 = new Runnable() {
        @Override
        public void run() {
            try {
                for (int i = 0; i < 10000; i++) {
                    metrics.tickStart("test-timer");

                    Thread.sleep(1);

                    if (i % 10 == 0) {
                        metrics.tickStop("test-timer", false);
                    } else {
                        metrics.tickStop("test-timer", true);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}