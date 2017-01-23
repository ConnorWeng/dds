package com.icbc.dds.message.client.consumer;

import java.util.List;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.common.HeartbeatTask;
import com.icbc.dds.message.common.Message;
import com.icbc.dds.rpc.factory.SupportFactory;

public class SimpleConsumerClient {

	private final RpcConsumerClient rpcClient;
	private IProcessor processor;
	
	public SimpleConsumerClient(String serviceName, IProcessor processor) {
		this.rpcClient = SupportFactory.getRestSupport(RpcConsumerClient.class);
		rpcClient.init(serviceName);
		
		this.processor = processor;
		
		try {
			this.rpcClient.initSession();
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
			processor.process(messages);
			rpcClient.commit();
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
}
