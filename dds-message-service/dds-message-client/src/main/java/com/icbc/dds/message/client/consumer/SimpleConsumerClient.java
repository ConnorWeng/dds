package com.icbc.dds.message.client.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.common.Constants;
import com.icbc.dds.message.common.HeartbeatTask;
import com.icbc.dds.message.common.Message;
import com.icbc.dds.rpc.factory.SupportFactory;

public class SimpleConsumerClient {
	
	private final RpcConsumerClient rpcClient;
	Map<String, String> prop = new HashMap<String, String>();
	private IProcessor processor;
	
	public SimpleConsumerClient(String serverAddr, int serverPort, String topic, IProcessor processor) {
		this.rpcClient = SupportFactory.getRestSupport(RpcConsumerClient.class);
		rpcClient.init(serverAddr, serverPort);
		
		this.processor = processor;
		
		prop.put("max.poll.records", Constants.MAX_POLL_RECORDS + ""); 
		prop.put("topic", topic);
		
		try {
			this.rpcClient.initSession(prop);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.processor.close();
	}
}
