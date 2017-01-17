package com.icbc.dds.registry.client.transport;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icbc.dds.registry.client.common.Constants;
import com.icbc.dds.registry.client.pojo.Application;
import com.icbc.dds.registry.client.pojo.Instance;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

public class JerseyEurekaClient implements EurekaClient {
	private static final Logger logger = LoggerFactory.getLogger(JerseyEurekaClient.class);

	private int retry = Constants.DEFAULT_RETRY_TIMES;
	private long interval = Constants.DAFAULT_SLEEP_INTERVAL;
	private Client client;
	private String addr;

	public JerseyEurekaClient(String addr) {
		this.addr = addr;
		DefaultClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		clientConfig.getClasses().add(JacksonJsonProvider.class);
		this.client = Client.create(clientConfig);
		// client.addFilter(new GZIPContentEncodingFilter());
	}

	@Override
	public boolean register(Instance instance) {
		return register(instance, 0);
	}

	@Override
	public boolean register(Instance instance, int times) {
		while (true) {
			try {
				ClientResponse response = client.resource(getRequestBase() + instance.getApp()).accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE).entity(instance).post(ClientResponse.class);
				if (response.getStatus() == 204) { // 204代表注册成功
					logger.info("服务注册成功，状态码：{}，服务名：{}，宿主机：{}", response.getStatus(), instance.getApp(), instance.getHostName());
					return true;
				} else {
					logger.error("服务注册失败，状态码：{}，服务名：{}，宿主机：{}，失败信息：{}", response.getStatus(), instance.getApp(), instance.getHostName(), response.getEntity(String.class));
					return false;
				}
			} catch (Exception e) { // 连接错误
				logger.error("服务注册失败，服务名：{}，宿主机：{}，失败信息：{}", instance.getApp(), instance.getHostName(), e);
				if (++times >= retry) { // 连接错误导致注册失败的情况下重试
					return false;
				}
				logger.info("重试服务注册，服务名：{}，宿主机：{}", instance.getApp(), instance.getHostName());
				try {
					TimeUnit.SECONDS.sleep(interval);
				} catch (Exception interrupt) {
					// do nothing
				}
			}
		}
	}

	@Override
	public void deRegister(String app, String instance) {
		try {
			client.resource(getRequestBase() + app + "/" + instance).delete();
		} catch (Exception e) {
			logger.error("服务注销失败，服务名：{}，宿主机：{}，失败信息：{}", app, instance, e);
		}
	}

	@Override
	public boolean renew(String app, String instance) {
		ClientResponse response = client.resource(getRequestBase() + app + "/" + instance).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
		if (response.getStatus() == 404) { // 服务已注销
			return false;
		} else {
			return true;
		}
	}

	@Override
	public int updateMetrics(String app, String instance, String metrics) {
		return 0;
	}

	@Override
	public Application getApp(String app) {
		try {
			ClientResponse response = client.resource(getRequestBase() + app).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
			if (response.getStatus() == 200) {
				Application application = response.getEntity(Application.class);
				logger.info("########## {}", application.getName());
				return application;
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("########## {}", e);
			return null;
		}
	}

	private String getRequestBase() {
		return "http://" + addr + "/eureka/apps/";
	}

}
