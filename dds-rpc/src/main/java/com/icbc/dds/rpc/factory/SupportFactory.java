package com.icbc.dds.rpc.factory;

import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.rpc.service.DefaultRegistryClient;
import com.icbc.dds.rpc.service.ServiceProvider;
import com.icbc.dds.rpc.support.RestSupport;
import com.icbc.dds.rpc.template.RestTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class SupportFactory {
    private static ConcurrentMap<Class, Object> instanceMap = new ConcurrentHashMap<Class, Object>();

    // FIXME: 16/01/2017 不能保证单例
    public static <T extends RestSupport> T getRestSupport(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        Object instance = instanceMap.get(clazz);
        if (instance != null) return (T) instance;

        RegistryClient registryClient = ServiceProvider.get(RegistryClient.class, DefaultRegistryClient.class);

        RestSupport restSupport = clazz.newInstance();
        restSupport.setRestTemplate(new RestTemplate(null, registryClient));

        instanceMap.put(clazz, restSupport);
        return (T) restSupport;
    }
}
