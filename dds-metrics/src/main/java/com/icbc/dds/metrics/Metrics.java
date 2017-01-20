package com.icbc.dds.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

import java.util.HashMap;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public final class Metrics implements com.icbc.dds.api.Metrics {
    private final MetricRegistry registry = new MetricRegistry();

    public Metrics() {
    }

    private final ThreadLocal<HashMap<String, Context>> contextMap =
            new ThreadLocal<HashMap<String, Context>>() {
                @Override
                protected HashMap<String, Context> initialValue() {
                    return new HashMap<String, Context>();
                }
            };

    @Override
    public void tickStart(String name) {
        contextMap.get().put(name, registry.timer(name).time());
    }

    @Override
    public void tickStop(String name, boolean isSuccess) {
        Timer.Context context = contextMap.get().get(name);
        context.stop();
        if (!isSuccess) {
            // FIXME: 16/01/2017 按分钟取平均数，非绝对准确
            registry.meter(name + "#fail").mark();
        }
    }

    public MetricRegistry getRegistry() {
        return registry;
    }
}
