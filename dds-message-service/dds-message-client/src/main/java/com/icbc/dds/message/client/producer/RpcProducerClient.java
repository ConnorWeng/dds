package com.icbc.dds.message.client.producer;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.common.Message;
import com.icbc.dds.message.common.RpcClient;
import com.icbc.dds.message.pojo.StatusObject;

public class RpcProducerClient extends RpcClient {

	private static final  Logger logger = Logger.getLogger(RpcProducerClient.class);
	private String messagePath = "/producer/messages/";
	
	public void init(String serverAddr, int serverPort) {
		super.init(serverAddr, serverPort, "producer");
	}
	
	public synchronized void initSession() throws DDSRestRPCException {
		Map<String, String> propMap = ProducerClientProperty.getInitProps();
		super.initSession(propMap);
	}
	
	public synchronized void heartbeat() throws DDSRestRPCException {
		super.heartbeat();
	}
	
	public synchronized void releaseSession() throws DDSRestRPCException {
		super.releaseSession();
	}	
	
	public synchronized void sendMessage(String topic, int key, Message message) throws DDSRestRPCException {
		String data = extractFromMessage(message);

		Map<String, String> formMap = new HashMap<String, String>();
        formMap.put("topic", topic);
        formMap.put("key", key + "");
        formMap.put("message", data);
        
		StatusObject statusObject = this.getRestTemplate()
				.service(serverAddr, serverPort)
				.path(messagePath + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.entity(formMap)
				.post(StatusObject.class);
		if (statusObject.isSuccess()) {
	        logger.info("成功发数--" + message.getMessage());			
		}
	}
	
	public synchronized void sendMessage(String topic, int key, String message) throws DDSRestRPCException {
		long start = System.currentTimeMillis();
		Map<String, String> formMap = new HashMap<String, String>();
        formMap.put("topic", topic);
        formMap.put("key", key + "");
        formMap.put("message", message);
        
        StatusObject statusObject = this.getRestTemplate()
				.service(serverAddr, serverPort)
				.path(messagePath + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.entity(formMap)
				.post(StatusObject.class);
		if (statusObject.isSuccess()) {
			logger.info("成功发数--" + message);
		}
		long durTime = System.currentTimeMillis() - start;
		totalTime += durTime;
		sendTimes ++;
		System.out.println("call sendMessage() in RpcClient---Average time (s) " + (totalTime / (double)sendTimes)/1000);
		System.out.println("call sendMessage() in RpcClient---TPS " + (sendTimes / (double)totalTime)*1000);
	}	

	private String extractFromMessage(Message message) {
		return message.getMessage();
	}
}
