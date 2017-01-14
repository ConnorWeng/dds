package com.icbc.dds.api;

import com.icbc.dds.api.pojo.InstanceInfo;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public abstract class RegistryClient {
    public abstract InstanceInfo getInstanceByAppName(String appName);

    public InstanceInfo getInstance(String ipAddr, int port) {
        return new InstanceInfo(ipAddr, port);
    }
}
