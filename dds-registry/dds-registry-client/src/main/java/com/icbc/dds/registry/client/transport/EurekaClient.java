package com.icbc.dds.registry.client.transport;

import com.icbc.dds.registry.client.pojo.Application;
import com.icbc.dds.registry.client.pojo.Instance;

public interface EurekaClient {

	public boolean register(Instance instance);

	public boolean register(Instance instance, int times);

	public void deRegister(String app, String instance);

	public boolean renew(String app, String instance);

	public int updateMetrics(String app, String instance, String metrics);

	public Application getApp(String app);
}
