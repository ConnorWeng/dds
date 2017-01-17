package com.icbc.dds.registry.client.job;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icbc.dds.registry.client.common.Constants;
import com.icbc.dds.registry.client.pojo.Instance;
import com.icbc.dds.registry.client.transport.EurekaClient;
import com.sun.jersey.api.client.ClientHandlerException;

public class RenewTask extends TimerTask {
	private static final Logger logger = LoggerFactory.getLogger(RenewTask.class);
	private EurekaClient eurekaClient;
	private Instance instance;
	private int retry = Constants.DEFAULT_RETRY_TIMES;

	public RenewTask(EurekaClient eurekaClient, Instance instance) {
		this.eurekaClient = eurekaClient;
		this.instance = instance;
	}

	@Override
	public void run() {
		try {
			boolean status = eurekaClient.renew(instance.getApp(), instance.getInstanceId()); // 心跳
			if (status) {
				logger.info("心跳发送成功，服务名：{}，宿主机：{}", instance.getApp(), instance.getInstanceId());
			} else {
				logger.info("检测到宿主机{}上服务{}已取消注册，尝试重新注册服务", instance.getInstanceId(), instance.getApp());
				eurekaClient.register(instance, retry);
			}
		} catch (ClientHandlerException e) {
			logger.error("心跳发送失败，服务名：{}，宿主机：{}，失败信息：{}", instance.getApp(), instance.getInstanceId(), e);
		}
	}

}
