package com.icbc.dds.registry.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icbc.dds.registry.client.common.Constants;
import com.icbc.dds.registry.client.pojo.Application;
import com.icbc.dds.registry.client.pojo.Instance;
import com.icbc.dds.registry.client.pojo.InstanceInfo;
import com.icbc.dds.registry.client.transport.EurekaClient;

public class Cache {
	private static final Logger logger = LoggerFactory.getLogger(Cache.class);

	private Map<String, Long> appUpdateTimeMap = new HashMap<String, Long>(); // 记录每个服务缓存信息的更新时间
	private Map<String, List<InstanceInfo>> cache = new HashMap<String, List<InstanceInfo>>(); // 缓存
	private ThreadLocal<Long> threadExpireMap = new ThreadLocal<Long>() { // 线程园区优先超时时间
		@Override
		protected Long initialValue() {
			return -1l;
		}
	};
	private long expireTime = Constants.DEFAULT_EXPIRE_TIME;
	private long threadExpireTime = Constants.DEFAULT_THREAD_EXPIRE_TIME;
	private EurekaClient eurekaClient;
	private String localZone;
	private String remoteZone = "other";

	public Cache(EurekaClient eurekaClient, List<String> apps, String localZone) {
		this.eurekaClient = eurekaClient;
		this.localZone = localZone;
		for (String app : apps) {
			appUpdateTimeMap.put(app, Constants.DEFAULT_UPDATE_TIME);
		}
	}

	private synchronized void refeshCache(String app, long currentTime) {
		if (currentTime - appUpdateTimeMap.get(app) > expireTime) {
			try {
				Application application = this.eurekaClient.getApp(app);
				if (application == null) {
					throw new Exception();
				}
				List<InstanceInfo> instances = application.getInstance();
				for (InstanceInfo instance : instances) {
					String key = (instance.getApp() + instance.getVipAddress()).toLowerCase();
					if (!localZone.equalsIgnoreCase(instance.getVipAddress())) {
						key = (instance.getApp() + remoteZone).toLowerCase();
					}
					if (!cache.containsKey(key)) {
						cache.put(key, new ArrayList<InstanceInfo>());
					}
					cache.get(key).add(instance);
				}
				appUpdateTimeMap.put(app, currentTime); // 调整更新时间
				logger.info("服务{}缓存更新成功", app);
			} catch (Throwable e) {
				logger.info("本次服务{}缓存更新失败", app);
			}
		}
	}

	public InstanceInfo getNextInstance(String app) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - appUpdateTimeMap.get(app) > expireTime) { // 缓存过时
			refeshCache(app, currentTime); // 更新缓存
		}

		if (threadExpireMap.get() == -1l) { // 线程首次调用
			logger.info("线程{}首次调用", Thread.currentThread().getName());
			threadExpireMap.set(currentTime);
		}
		logger.info("current:{}, thread:{}, div:{}, expire:{}", currentTime, threadExpireMap.get(), currentTime - threadExpireMap.get(), threadExpireTime);
		if (currentTime - threadExpireMap.get() < threadExpireTime && cache.containsKey((app + localZone).toLowerCase())) { // 本园区优先
			logger.info("返回本园区实例");
			return getRandomInstanceFromCache((app + localZone).toLowerCase());
		} else { // 返回其他园区
			threadExpireMap.set(currentTime);
			if (cache.containsKey((app + remoteZone).toLowerCase())) {
				logger.info("返回其它园区实例");
				return getRandomInstanceFromCache((app + remoteZone).toLowerCase());
			} else {
				logger.info("服务未发现已注册实例");
				return null;
			}
		}
	}

	private InstanceInfo getRandomInstanceFromCache(String key) {
		List<InstanceInfo> sameZoneInstances = cache.get(key);
		Random random = new Random();
		return sameZoneInstances.get(random.nextInt(sameZoneInstances.size()));
	}
}
