package com.icbc.dds.registry.client.transport;

import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.icbc.dds.registry.client.common.Constants;
import com.icbc.dds.registry.client.pojo.ApplicationInfo;
import com.icbc.dds.registry.client.pojo.ApplicationWrapper;
import com.icbc.dds.registry.client.pojo.InstanceInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.json.JSONConfiguration;

public class JerseyEurekaClient implements EurekaClient {

	private final String requestBase;
	private final Client client;

	public JerseyEurekaClient(String addr) {
		this.requestBase=Constants.DEFAULT_PROTOCOL+addr+Constants.DEFAULT_EUREKA_CONTEXT;
		DefaultClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		clientConfig.getClasses().add(JacksonJsonProvider.class);
		this.client = Client.create(clientConfig);
//		client.addFilter(new GZIPContentEncodingFilter());
	}

	@Override
	public void register(InstanceInfo instanceInfo) {
		ClientResponse response = client.resource(requestBase)
				.path(instanceInfo.getInstanceInfo().getApp())
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(instanceInfo)
				.post(ClientResponse.class);
		if (response.getStatus() != 204) { // 204代表注册成功
			throw new DDSResponseException("注册中心返回状态码：" + response.getEntity(String.class));
		}
	}

	@Override
	public void deRegister(String app, String instanceId) {
		client.resource(requestBase).path(app).path(instanceId).delete();
	}

	@Override
	public void renew(String app, String instanceId) {
		ClientResponse response = client.resource(requestBase).path(app).path(instanceId).accept(MediaType.APPLICATION_JSON_TYPE).put(ClientResponse.class);
		if (response.getStatus() != 200) { // 200代表心跳成功
			throw new DDSResponseException(String.valueOf(response.getStatus()));
		}
	}

	@Override
	public ApplicationWrapper getApp(String app) {
		ClientResponse response = client.resource(requestBase).path(app).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
		if (response.getStatus() == 200) {
			ApplicationInfo application = response.getEntity(ApplicationInfo.class);
			return application.getApplication();
		} else {
			throw new DDSResponseException(String.valueOf(response.getStatus()));
		}
	}

	@Override
	public int updateMetrics(String app, String instanceId, String metrics) {
		return 0;
	}

}
