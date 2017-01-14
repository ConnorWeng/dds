package com.icbc.dds.rpc.service;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public class ServiceProvider {
    public static <T> T get(Class<T> clazz, Class<? extends T> defaultClass) throws IllegalAccessException, InstantiationException {
        try {
            ServiceLoader<T> loader = ServiceLoader.load(clazz);
            Iterator<T> iterator = loader.iterator();
            return iterator.next();
        } catch (ServiceConfigurationError e) {
            // TODO: 15/01/2017 记录日志
            return defaultClass.newInstance();
        }
    }
}
