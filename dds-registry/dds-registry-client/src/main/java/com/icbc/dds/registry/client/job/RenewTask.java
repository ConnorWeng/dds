package com.icbc.dds.registry.client.job;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icbc.dds.registry.client.pojo.InstanceInfo;
import com.icbc.dds.registry.client.pojo.InstanceWrapper;
import com.icbc.dds.registry.client.transport.DDSResponseException;
import com.icbc.dds.registry.client.transport.EurekaClient;

public class RenewTask extends TimerTask {
	private static final Logger logger = LoggerFactory.getLogger(RenewTask.class);
	private EurekaClient eurekaClient;
	private InstanceInfo instanceInfo;

	public RenewTask(EurekaClient eurekaClient, InstanceInfo instanceInfo) {
		this.eurekaClient = eurekaClient;
		this.instanceInfo = instanceInfo;
	}

	@Override
	public void run() {
		InstanceWrapper instance = instanceInfo.getInstanceInfo();
		try {
			eurekaClient.renew(instance.getApp(), instance.getInstanceId()); // 心跳
			logger.info("心跳发送成功，服务名：{}，宿主机：{}", instance.getApp(), instance.getInstanceId());
		} catch (Exception e) {
			logger.error("心跳发送失败，服务名：{}，宿主机：{}，失败信息：{}", instance.getApp(), instance.getInstanceId(), e);
			if (e instanceof DDSResponseException && "404".equals(e.getMessage())) {
				logger.info("检测到宿主机{}上服务{}已取消注册，尝试重新注册服务", instance.getInstanceId(), instance.getApp());
				try {
					eurekaClient.register(instanceInfo);
				} catch (Exception e1) {
					logger.error("注册失败，服务名：{}，宿主机：{}，失败信息：{}", instance.getApp(), instance.getInstanceId(), e);
				}
			}
		}
	}

}
