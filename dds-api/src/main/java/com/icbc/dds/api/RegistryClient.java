package com.icbc.dds.api;

import com.icbc.dds.api.pojo.InstanceInfo;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public interface RegistryClient {
    InstanceInfo getInstanceByAppName(String appName);
}
