package com.icbc.dds.message.client;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.rpc.factory.SupportFactory;
import com.icbc.dds.rpc.support.RestSupport;

public class RpcClient extends RestSupport {
/*	public String getString() {
		return this.getRestTemplate().get("localhost", 8081, "/producer/sessions", MediaType.TEXT_PLAIN_TYPE, String.class, "params", "value1");
	}*/
	
	public StatusObject getProducerInit() throws DDSRestRPCException {
		Map<String, String> formMap = new HashMap<String, String>();
        formMap.put("param1", "中文");
        formMap.put("param2", "false");
        
		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/producer/sessions/")
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(formMap)
				.post(StatusObject.class);
		System.out.println(statusObject.getMessage());
		return statusObject;
//        return this.getRestTemplate().post("localhost", 8081, "/producer/sessions", MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_FORM_URLENCODED_TYPE, StatusObject.class, formMap);
	}
	
	public String testProducerHeartbeat(String uuid) throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/producer/sessions/" + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.put(StatusObject.class);
		System.out.println(statusObject.getMessage());
		return statusObject.getMessage();
		//return this.getRestTemplate().put("localhost", 8081, "/producer/session", MediaType.TEXT_PLAIN_TYPE, MediaType.TEXT_PLAIN_TYPE,  String.class, "uuid", uuid);
	}

	public void testProducerSendMessage(String uuid) throws DDSRestRPCException {
		Map<String, String> formMap = new HashMap<String, String>();
        formMap.put("topic", "kafka1");
        formMap.put("key", "1");
        formMap.put("message", "message11111");

		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/producer/messages/" + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.entity(formMap)
				.post(StatusObject.class);
		System.out.println(statusObject.getMessage());
	}
	
	public String testProducerClose(String uuid) throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/producer/sessions/" + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.delete(StatusObject.class);
		System.out.println(statusObject.getMessage());
		return statusObject.getMessage();
	}
	
	public String getConsumerInit() throws DDSRestRPCException {		
		Map<String, String> formMap = new HashMap<String, String>();
        formMap.put("topic", "kafka1");
        formMap.put("max.poll.records", "2");
        
		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/consumer/sessions/")
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.entity(formMap)
				.post(StatusObject.class);
		System.out.println(statusObject.getMessage());
		return statusObject.getMessage();
	}
	
	public String testConsumerHeartbeat(String uuid) throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/consumer/sessions/" + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.put(StatusObject.class);
		System.out.println(statusObject.getMessage());
		return statusObject.getMessage();
	}
	
	public String testConsumerClose(String uuid) throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/consumer/sessions/" + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.delete(StatusObject.class);
		System.out.println(statusObject.getMessage());
		return statusObject.getMessage();
	}
	
	public DataObject  testConsumerGetMessage(String uuid, long timeout) throws DDSRestRPCException {
		DataObject dataObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/consumer/messages/" + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.TEXT_PLAIN_TYPE)
				.query("timeout", timeout+"")
				.get(DataObject.class);
		System.out.println(dataObject.getMessage()+","+dataObject.getDataList());
		return dataObject;
	}
	
	public String testConsumerCommit(String uuid) throws DDSRestRPCException {
		StatusObject statusObject = this.getRestTemplate()
				.service("localhost", 8081)
				.path("/consumer/messages/" + uuid)
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.TEXT_PLAIN_TYPE)
				.delete(StatusObject.class);
		System.out.println(statusObject.getMessage());
		return statusObject.getMessage();
	}
	
	public static void main(String[] args) throws DDSRestRPCException {
		RpcClient client = SupportFactory.getRestSupport(RpcClient.class);
		
		String consumerUuid = client.getConsumerInit(); 
		client.testConsumerGetMessage(consumerUuid, 1000);
		client.testConsumerGetMessage(consumerUuid, 1000);
		client.testConsumerGetMessage(consumerUuid, 1000);
		client.testConsumerCommit(consumerUuid);
		client.testConsumerHeartbeat(consumerUuid);
		client.testConsumerClose(consumerUuid);
		consumerUuid = "wrong-uuid";
		client.testConsumerGetMessage(consumerUuid, 1000);
		client.testConsumerGetMessage(consumerUuid, 1000);
		client.testConsumerGetMessage(consumerUuid, 1000);
		client.testConsumerCommit(consumerUuid);
		client.testConsumerHeartbeat(consumerUuid);
		client.testConsumerClose(consumerUuid);
		
		String producerUuid = client.getProducerInit().getMessage();
		client.testProducerSendMessage(producerUuid);
		client.testProducerHeartbeat(producerUuid);
		client.testProducerClose(producerUuid);
		producerUuid = "wrong-uuid";
		client.testProducerSendMessage(producerUuid);
		client.testProducerHeartbeat(producerUuid);
		client.testProducerClose(producerUuid);
		
	}
}
