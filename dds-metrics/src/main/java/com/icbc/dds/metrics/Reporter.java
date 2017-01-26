package com.icbc.dds.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kfzx-wengxj on 24/01/2017.
 */
public class Reporter {
    private static final Logger logger = LoggerFactory.getLogger(Reporter.class);

    public void report(ConcurrentMap<String, SingleServiceMetric> registry) {
        Set<String> keys = registry.keySet();
        for (String key : keys) {
            String serviceName = key;
            Snapshot snapshot = registry.get(serviceName).getSnapshot();
            logger.debug(snapshot.toString());
            // TODO: 26/01/2017 将snapshot数据往服务端发
        }
    }
}
