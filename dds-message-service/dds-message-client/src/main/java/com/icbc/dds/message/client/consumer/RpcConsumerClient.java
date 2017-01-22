package com.icbc.dds.message.client.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.message.common.Message;
import com.icbc.dds.message.common.RpcClient;
import com.icbc.dds.message.pojo.DataObject;
import com.icbc.dds.message.pojo.StatusObject;


public class RpcConsumerClient extends RpcClient {

	private final static Logger logger = Logger.getLogger(RpcConsumerClient.class);

	private String messagePath = "/consumer/messages/";
	private long getMessageTimeoutMs = 1000;
	
	public synchronized void init(String serverAddr, int serverPort) {
		super.init(serverAddr, serverPort, "consumer");
	}
	
	public synchronized void initSession(Map<String, String> props) throws DDSRestRPCException {
		super.initSession(props);
	}
	
	public synchronized void releaseSession() throws DDSRestRPCException {
		super.releaseSession();
	}

	public synchronized void heartbeat() throws DDSRestRPCException {
		super.heartbeat();
	}	

	public synchronized List<Message> getMessage() throws DDSRestRPCException {
		DataObject dataObject = this.getRestTemplate()
				.service(serverAddr, serverPort)
				.path(messagePath + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.TEXT_PLAIN_TYPE)
				.query("timeout", getMessageTimeoutMs+"")
				.get(DataObject.class);
		List<String> response = new ArrayList<String> ();
		if (dataObject.isSuccess()) {
			response = dataObject.getDataList();
			logger.info(response); 
		}
		return extractMessageFromResponse(response);
	}

	public synchronized void commit() throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service(serverAddr, serverPort)
				.path(messagePath + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.TEXT_PLAIN_TYPE)
				.delete(StatusObject.class);
		if (statusObject.isSuccess()) {
			logger.info("commit offset，uuid--" + uuid);
		}
	}
	
	private List<Message> extractMessageFromResponse(List<String> response) {
		List<Message> messages = new ArrayList<Message>();
		
		for (String str : response) {
			Message msg = new Message();
			msg.setMessage(str.toString());
			messages.add(msg);
		}
		
		return messages;
	}
}
