package com.icbc.dds.rpc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public class ServiceProvider {
    private static final Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

    private static ConcurrentMap<Class, Object> instanceMap = new ConcurrentHashMap<Class, Object>();

    public static <T> T get(Class<T> clazz, Class<? extends T> defaultClass) throws IllegalAccessException, InstantiationException {
        Object instance = instanceMap.get(clazz);
        if (instance != null) return (T) instance;
        return loadService(clazz, defaultClass);
    }

    private static synchronized <T> T loadService(Class<T> clazz, Class<? extends T> defaultClass) throws IllegalAccessException, InstantiationException {
        Object instance = instanceMap.get(clazz);
        if (instance != null) return (T) instance;
        try {
            ServiceLoader<T> loader = ServiceLoader.load(clazz);
            Iterator<T> iterator = loader.iterator();
            instance = iterator.next();
        } catch (ServiceConfigurationError e) {
            logger.info(String.format("load service [%s] failed, so use default service [%s] instead", clazz.getName(), defaultClass.getName()));
            instance = defaultClass.newInstance();
        }
        instanceMap.put(clazz, instance);
        return (T) instance;
    }

    public static void reset() {
        instanceMap.clear();
    }
}
