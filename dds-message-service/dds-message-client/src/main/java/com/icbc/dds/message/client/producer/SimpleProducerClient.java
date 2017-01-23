package com.icbc.dds.message.client.producer;

import java.util.HashMap;
import java.util.Map;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.common.HeartbeatTask;
import com.icbc.dds.message.common.Message;
import com.icbc.dds.rpc.factory.SupportFactory;

public class SimpleProducerClient {

	private RpcProducerClient rpcClient;
	Map<String, String> props = new HashMap<String, String>();
	
	public SimpleProducerClient(String serviceName) {
		this.rpcClient = SupportFactory.getRestSupport(RpcProducerClient.class);
		rpcClient.init(serviceName);
		
		try {
			this.rpcClient.initSession();
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
}
