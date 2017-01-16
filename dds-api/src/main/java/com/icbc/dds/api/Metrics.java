package com.icbc.dds.api;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public interface Metrics {
    void tickStart(String name);
    void tickStop(String name, boolean isSuccess);
}
