package com.icbc.dds.message.common;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.pojo.StatusObject;
import com.icbc.dds.rpc.support.RestSupport;


public class RpcClient extends RestSupport {

	private final static Logger logger = Logger.getLogger(RpcClient.class);
	protected String serviceName;
	private String sessionPath;
	protected String uuid;

	protected long totalTime = 0; //test
	protected long sendTimes = 0; //test
	
	public void init(String serviceName, String kafkaType) {
		this.serviceName = serviceName;
		this.sessionPath = "/" + kafkaType + "/sessions/";
	}
	
	public void initSession(Map<String, String> props) throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service(serviceName)
				.path(sessionPath)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(props)
				.post(StatusObject.class);
		uuid = statusObject.getMessage();
		logger.info("启动会话，获得uuid--" + uuid);
	}
	
	public void releaseSession() throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service(serviceName)
				.path(sessionPath + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.delete(StatusObject.class);
		if (statusObject.isSuccess()) {
			logger.info("会话关闭，uuid--" + uuid);
		}
		System.out.println("call sendMessage() in RpcClient---Average time (s) " + (totalTime / (double)sendTimes)/1000);
		System.out.println("call sendMessage() in RpcClient---TPS " + (sendTimes / (double)totalTime)*1000);
	}
	
	public void heartbeat() throws DDSRestRPCException {
		if (uuid != null) {
			StatusObject statusObject = this.getRestTemplate()
					.service(serviceName)
					.path(sessionPath + uuid)
					.accept(MediaType.APPLICATION_JSON_TYPE)
					.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.put(StatusObject.class);
			if (statusObject.isSuccess()) {
				logger.info("发送心跳，uuid--" + uuid);
			}
		}
	}
}
