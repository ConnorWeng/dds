package com.icbc.dds.message.client.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.common.HeartbeatTask;
import com.icbc.dds.message.common.Message;
import com.icbc.dds.rpc.factory.SupportFactory;

public class SimpleConsumerClient {

	private static final String propertyLocation = "spring_consumer_client.properties";
	private final RpcConsumerClient rpcClient;
	private IProcessor processor;
	
	public SimpleConsumerClient(String serverAddr, int serverPort, IProcessor processor) {
		this.rpcClient = SupportFactory.getRestSupport(RpcConsumerClient.class);
		rpcClient.init(serverAddr, serverPort);
		
		this.processor = processor;

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
		this.processor.init();
		
		Thread heartbeatThread = new Thread(new HeartbeatTask(rpcClient, Thread.currentThread()));
		heartbeatThread.start();
	}
	
	public long getAndProcessMessage() {
		List<Message> messages = null;
		try {
			messages = rpcClient.getMessage();
		} catch (DDSRestRPCException e) {
			e.printStackTrace();
		}
		return messages.size();
	}
	
	public void close() {
		try {
			this.rpcClient.releaseSession();
		} catch (DDSRestRPCException e) {
			e.printStackTrace();
		}
		this.processor.close();
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
