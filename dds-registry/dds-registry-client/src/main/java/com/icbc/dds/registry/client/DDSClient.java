package com.icbc.dds.registry.client;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.DDSRegistryException;
import com.icbc.dds.registry.client.cache.Cache;
import com.icbc.dds.registry.client.common.Constants;
import com.icbc.dds.registry.client.job.RenewJob;
import com.icbc.dds.registry.client.job.RenewTask;
import com.icbc.dds.registry.client.pojo.InstanceInfo;
import com.icbc.dds.registry.client.pojo.InstanceWrapper;
import com.icbc.dds.registry.client.transport.DDSResponseException;
import com.icbc.dds.registry.client.transport.JerseyEurekaClient;

public class DDSClient implements RegistryClient {
	private static final Logger logger = LoggerFactory.getLogger(DDSClient.class);

	private int retry = Constants.DEFAULT_RETRY_TIMES;
	private long interval = Constants.DAFAULT_SLEEP_INTERVAL;
	private final Properties prop = new Properties();
	private JerseyEurekaClient eurekaClient;
	private InstanceInfo instanceInfo = null;
	private RenewJob renewJob = null;
	private Cache cache;

	public DDSClient() {
		init(Constants.DEFAULT_CONF_FILE_NAME);
	}
	
	public DDSClient(String fileName) {
		init(fileName);
	}
	
	private void init(String fileName) {
		try {
			prop.load(DDSClient.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			throw new DDSRegistryException(e);
		}
		if (!prop.containsKey("eureka_servers") || "".equals(prop.getProperty("eureka_servers")) 
				|| !prop.containsKey("zone") || "".equals(prop.getProperty("zone"))) { // 通用配置必须配置
			throw new DDSRegistryException("缺少配置");
		}

		this.eurekaClient = new JerseyEurekaClient(prop.getProperty("eureka_servers"));

		if (prop.containsKey("service_name") && !"".equals(prop.getProperty("service_name")) 
				&& prop.containsKey("port") && !"".equals(prop.getProperty("port"))) { // 服务端配置
			// 获取本机ip
			String ip = getIp();
			this.instanceInfo = new InstanceInfo(new InstanceWrapper(ip, prop.getProperty("service_name"), ip, prop.getProperty("zone"), Integer.parseInt(prop.getProperty("port"))));
			renewJob = new RenewJob(new RenewTask(eurekaClient, instanceInfo));
		} else {
			this.cache = new Cache(eurekaClient, prop.getProperty("zone"));
		}
	}

	@Override
	public com.icbc.dds.api.pojo.InstanceInfo getInstanceByAppName(String appName) {
		InstanceWrapper instance = cache.getNextInstance(appName);
		if (instance != null) {
			return new com.icbc.dds.api.pojo.InstanceInfo(instance.getIpAddr(), instance.getPort().getPort());
		} else {
			return null;
		}
	}

	@Override
	public void register() {
		if (instanceInfo != null) {
			int times = 0;
			while (true) { // 注册
				try {
					eurekaClient.register(instanceInfo);
					renewJob.start();
					break;
				} catch (Exception e) {
					if (e instanceof DDSResponseException || ++times >= retry) {
						throw new DDSRegistryException("服务注册失败", e);
					}
					logger.error("第{}次服务注册失败，失败信息：{}", times, e);
					try {
						TimeUnit.MILLISECONDS.sleep(interval);
					} catch (Exception interrupt) {
						// do nothing
					}
				}
			}
			logger.info("服务注册成功，instanceId：{}", instanceInfo.getInstanceInfo().getInstanceId());
		}
	}

	@Override
	public void deRegister() {
		if (instanceInfo != null) {
			renewJob.close();
			InstanceWrapper instance = instanceInfo.getInstanceInfo();
			try {
				eurekaClient.deRegister(instance.getApp(), instance.getInstanceId());
			} catch (Exception e) {
				
			}
		}
	}
	
	private String getIp() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
					continue;
				}
				
				List<InterfaceAddress> addresses = networkInterface.getInterfaceAddresses();
				for (InterfaceAddress addr : addresses) {
					if (addr.getAddress() instanceof Inet4Address) {
						return addr.getAddress().getHostAddress();
					}
				}
			}
			throw new NullPointerException();
		} catch (Exception e) {
			throw new DDSRegistryException("获取ip地址失败", e);
		}
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void setInstanceInfo(InstanceInfo instanceInfo) {
		this.instanceInfo = instanceInfo;
	}

	public int getPort() {
		if (prop.containsKey("port")) {
			return Integer.parseInt(prop.getProperty("port"));
		} else {
			throw new DDSRegistryException("服务端口未配置");
		}
	}
}
