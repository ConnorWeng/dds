package com.icbc.dds.metrics;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public class Metrics implements com.icbc.dds.api.Metrics {
    // TODO: 25/01/2017 配置化，单位为秒
    private static final int REPORT_INTERVAL = 60;

    private static ConcurrentHashMap<String, SingleServiceMetric> registry = new ConcurrentHashMap<String, SingleServiceMetric>();
    private static Reporter reporter = new Reporter();

    private ThreadLocal<HashMap<String, Long>> contextMap =
            new ThreadLocal<HashMap<String, Long>>() {
                @Override
                protected HashMap<String, Long> initialValue() {
                    return new HashMap<String, Long>();
                }
            };

    public void startReport() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                reporter.report(registry);
            }
        }, 0, 1000 * REPORT_INTERVAL);
    }

    @Override
    public void tickStart(String name) {
        contextMap.get().put(name, System.nanoTime());
    }

    @Override
    public void tickStop(String name, boolean success) {
        Long start = contextMap.get().get(name);
        Long elapsed = System.nanoTime() - start;
        if (!registry.containsKey(name)) {
            makeSureKeyExist(name);
        }
        registry.get(name).update(elapsed, success);
    }

    private synchronized void makeSureKeyExist(String key) {
        if (!registry.containsKey(key)) {
            registry.put(key, new SingleServiceMetric());
        }
    }

    public static ConcurrentHashMap<String, SingleServiceMetric> getRegistry() {
        return registry;
    }

    public void clear() {
        registry.clear();
    }
}
