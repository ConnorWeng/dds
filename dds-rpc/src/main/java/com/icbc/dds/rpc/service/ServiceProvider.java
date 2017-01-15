package com.icbc.dds.rpc.service;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public class ServiceProvider {
    private static ConcurrentMap<Class, Object> instanceMap = new ConcurrentHashMap<Class, Object>();

    // FIXME: 16/01/2017 不能保证单例
    public static <T> T get(Class<T> clazz, Class<? extends T> defaultClass) throws IllegalAccessException, InstantiationException {
        Object instance = instanceMap.get(clazz);
        if (instance != null) return (T) instance;
        try {
            ServiceLoader<T> loader = ServiceLoader.load(clazz);
            Iterator<T> iterator = loader.iterator();
            instance = iterator.next();
        } catch (ServiceConfigurationError e) {
            // TODO: 15/01/2017 记录日志
            instance = defaultClass.newInstance();
        }
        instanceMap.put(clazz, instance);
        return (T) instance;
    }

    public static void reset() {
        instanceMap.clear();
    }
}
