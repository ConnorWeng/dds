package com.icbc.dds.registry.client.transport;

import com.icbc.dds.registry.client.pojo.ApplicationWrapper;
import com.icbc.dds.registry.client.pojo.InstanceInfo;

public interface EurekaClient {

	/**
	 * 注册
	 * 
	 * @param instanceInfo
	 * @return
	 */
	public boolean register(InstanceInfo instanceInfo);

	/**
	 * 注销
	 * 
	 * @param app
	 * @param instance
	 */
	public boolean deRegister(String app, String instanceId);

	/**
	 * 心跳
	 * 
	 * @param app
	 * @param instance
	 * @return
	 */
	public boolean renew(String app, String instanceId);

	/**
	 * 上传metrics
	 * 
	 * @param app
	 * @param instance
	 * @param metrics
	 * @return
	 */
	public boolean updateMetrics(String app, String instanceId, String metrics);

	/**
	 * 根据服务名查询实例信息
	 * 
	 * @param app
	 * @return
	 */
	public ApplicationWrapper getApp(String app);
}
