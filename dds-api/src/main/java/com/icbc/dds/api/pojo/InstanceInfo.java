package com.icbc.dds.api.pojo;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class InstanceInfo {
    private String ipAddr;
    private int port;

    public InstanceInfo(String ipAddr, int port) {
        this.ipAddr = ipAddr;
        this.port = port;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
