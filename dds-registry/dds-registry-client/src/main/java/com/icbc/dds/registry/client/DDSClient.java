package com.icbc.dds.registry.client;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.pojo.InstanceInfo;
import com.icbc.dds.registry.client.cache.Cache;
import com.icbc.dds.registry.client.common.Constants;
import com.icbc.dds.registry.client.job.RenewJob;
import com.icbc.dds.registry.client.job.RenewTask;
import com.icbc.dds.registry.client.pojo.Instance;
import com.icbc.dds.registry.client.transport.EurekaClient;
import com.icbc.dds.registry.client.transport.JerseyEurekaClient;

public class DDSClient implements RegistryClient {
	private Properties properties = new Properties();
	private EurekaClient eurekaClient;
	private Instance instance;
	private RenewJob renewJob = null;
	private Cache cache;
	private String separator = Constants.DEFAULT_SEPARATOR;

	public DDSClient(String conf) throws Exception {
		properties.load(new FileInputStream(conf));
		this.eurekaClient = new JerseyEurekaClient(properties.getProperty("eurekaServer"));
		String hostName = properties.getProperty("hostName");
		String app = properties.getProperty("app");
		String ipAddr = properties.getProperty("ipAddr");
		String port = properties.getProperty("port");
		String localZone = properties.getProperty("localZone");

		if((hostName != null && !"".equals(hostName)) 
				&& (app != null && !"".equals(app)) 
				&& (ipAddr != null && !"".equals(ipAddr)) 
				&& (port != null && !"".equals(port)) 
				&& (localZone != null && !"".equals(localZone))) {
			this.instance = new Instance(hostName, app, ipAddr, localZone, Integer.parseInt(port));
		}

		String consumApps = properties.getProperty("consumApps");
		if (consumApps != null && !"".equals(consumApps)) {
			List<String> apps = Arrays.asList(consumApps.split(this.separator));
			this.cache = new Cache(this.eurekaClient, apps, localZone);
		}
	}

	@Override
	public InstanceInfo getInstanceByAppName(String appName) {
		com.icbc.dds.registry.client.pojo.InstanceInfo nextInstance = cache.getNextInstance(appName);
		if (nextInstance != null) {
			return new InstanceInfo(nextInstance.getIpAddr(), nextInstance.getPort().getPort());
		} else {
			return null;
		}
	}

	@Override
	public void register() {
		boolean isRegisted = this.eurekaClient.register(instance); // 注册
		if (isRegisted) { // 注册成功后启动定时心跳任务
			renewJob = new RenewJob(new RenewTask(eurekaClient, instance));
			renewJob.start();
		}
	}

	@Override
	public void deRegister() {
		// 关闭定时心跳
		if (this.renewJob != null) {
			this.renewJob.close();
		}
		// 注销服务
		this.eurekaClient.deRegister(instance.getApp(), instance.getInstanceId());
	}

}
