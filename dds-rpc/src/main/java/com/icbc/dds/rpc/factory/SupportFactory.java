package com.icbc.dds.rpc.factory;

import com.icbc.dds.api.Metrics;
import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.DDSInstantiationException;
import com.icbc.dds.rpc.service.DefaultMetrics;
import com.icbc.dds.rpc.service.DefaultRegistryClient;
import com.icbc.dds.rpc.service.ServiceProvider;
import com.icbc.dds.rpc.support.RestSupport;
import com.icbc.dds.rpc.template.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class SupportFactory {
    private static final Logger logger = LoggerFactory.getLogger(SupportFactory.class);
    private static ConcurrentMap<Class, Object> instanceMap = new ConcurrentHashMap<Class, Object>();

    public static <T extends RestSupport> T getRestSupport(Class<T> clazz) {
        Object instance = instanceMap.get(clazz);
        if (instance != null) return (T) instance;
        return createNewRestSupport(clazz);
    }

    private synchronized static <T extends RestSupport> T createNewRestSupport(Class<T> clazz) {
        Object instance = instanceMap.get(clazz);
        if (instance != null) return (T) instance;
        try {
            RegistryClient registryClient = ServiceProvider.get(RegistryClient.class, DefaultRegistryClient.class);
            Metrics metrics = ServiceProvider.get(Metrics.class, DefaultMetrics.class);

            RestSupport restSupport = clazz.newInstance();
            restSupport.setRestTemplate(new RestTemplate(registryClient, metrics));

            instanceMap.put(clazz, restSupport);
            return (T) restSupport;
        } catch (IllegalAccessException e) {
            logger.error("fail to get spi provider", e);
            throw new DDSInstantiationException(e);
        } catch (InstantiationException e) {
            logger.error("fail to create an instance of spi provider", e);
            throw new DDSInstantiationException(e);
        }
    }

    public static void reset() {
        instanceMap.clear();
    }
}
