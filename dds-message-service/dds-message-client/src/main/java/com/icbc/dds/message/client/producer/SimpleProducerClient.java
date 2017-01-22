package com.icbc.dds.message.client.producer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.common.HeartbeatTask;
import com.icbc.dds.message.common.Message;
import com.icbc.dds.rpc.factory.SupportFactory;

public class SimpleProducerClient {

	private static final String propertyLocation = "spring_producer_client.properties";
	private RpcProducerClient rpcClient;
	Map<String, String> props = new HashMap<String, String>();
	
	public SimpleProducerClient(String serverAddr, int serverPort) {
		this.rpcClient = SupportFactory.getRestSupport(RpcProducerClient.class);
		rpcClient.init(serverAddr, serverPort);
		
		Map<String, String> propMap = new HashMap<String, String>();
		Properties props = loadProperties();
		for (Entry<Object, Object> e : props.entrySet()) {
			propMap.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
		}
		
		try {
			this.rpcClient.initSession(propMap);
		} catch (DDSRestRPCException e) {
			e.printStackTrace();
		}
		
		Thread heartbeatThread = new Thread(new HeartbeatTask(rpcClient, Thread.currentThread()));
		heartbeatThread.start();
	}
	
	public void sendMessage(String topic, int key, Message message) {
		try {
			rpcClient.sendMessage(topic, key, message);
		} catch (DDSRestRPCException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String topic, int key, String message) {
		try {
			rpcClient.sendMessage(topic, key, message);
		} catch (DDSRestRPCException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			this.rpcClient.releaseSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Properties loadProperties() {
		Properties props = new Properties();
		
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream is = classLoader.getResourceAsStream(propertyLocation);
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return props;
	}
}
