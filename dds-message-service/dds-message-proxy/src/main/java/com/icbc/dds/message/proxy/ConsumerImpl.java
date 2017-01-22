package com.icbc.dds.message.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import com.icbc.dds.message.client.DataObject;
import com.icbc.dds.message.client.StatusObject;
import com.icbc.dds.message.common.HeartbeatScanner;
import com.icbc.dds.springboot.annotation.DDSService;

@DDSService
@Path("/consumer")
public class ConsumerImpl {
    
	private final Logger logger = Logger.getLogger(ConsumerImpl.class);
	private static final String propertyLocation = "spring_consumer_proxy.properties";
	private static Map<String, KafkaConsumer<Integer, String>> sessions = new ConcurrentHashMap<String, KafkaConsumer<Integer, String>>();
	private static Map<String, Long> heartbeats = new ConcurrentHashMap<String, Long>();
	private static HeartbeatScanner scanner = new HeartbeatScanner(sessions, heartbeats);
	
	static {
		Thread scannerThread = new Thread(scanner);
		//测试时注释 正式运行后将注释去掉
		scannerThread.start(); 
	}
	
	@POST
    @Path("sessions")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
    public Response init(Map<String, String> propMap) {
		Properties props = loadProperties();
		
		if (!propMap.containsKey("topic")) {
			return Response.noContent().entity("error: topic is null").build();
		}
		String topic = propMap.get("topic");
		propMap.remove("topic");
		for(Entry<String, String> e : propMap.entrySet()) {
			props.put(e.getKey(), e.getValue());
		}

		KafkaConsumer<Integer, String> consumer = new KafkaConsumer<Integer, String>(props);
		Collection<String> singletonList = Collections.singletonList(topic);
		consumer.subscribe(singletonList);

		String uuid = UUID.randomUUID().toString();
		sessions.put(uuid, consumer);
		logger.info("将" + uuid + "所对应的Consumer对象放入缓存！");

		return Response.ok(new StatusObject(true, uuid)).build();
    }
	
	@PUT
	@Path("sessions/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response heartbeat(@PathParam("uuid") String uuid) {
		if (!sessionExists(uuid)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new StatusObject(false, "session-" + uuid + " does not exist, cannot get heartbeat")).build();
		}
		heartbeats.put(uuid, System.currentTimeMillis());
		return Response.ok(new StatusObject(true, "get heartbeat from session-" + uuid)).build();
	}

	@DELETE
	@Path("sessions/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response close(@PathParam("uuid") String uuid) {
		if (!sessionExists(uuid)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new StatusObject(false, "session-" + uuid + " does not exist, cannot close")).build();
		}
		KafkaConsumer<Integer, String> consumer = getConsumer(uuid);		
		consumer.close();
		consumer = null;
		heartbeats.remove(uuid.toString());
		sessions.remove(uuid.toString());
		logger.info("将" + uuid + "所对应的Consumer对象从缓存删除！");
		return Response.ok(new StatusObject(true, "session-" + uuid + " succesfully close")).build();
	}
	
	@GET
    @Path("messages/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.TEXT_PLAIN)
	public Response getMessages(@PathParam("uuid") String uuid, @QueryParam("timeout") long timeout) {
		if (timeout <= 0) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new DataObject(false, "error: timeout is wrong", null)).build();
		}
		if (!sessionExists(uuid)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new DataObject(false,  "session-" + uuid + " does not exist, cannot get messages", null)).build();
		}
		KafkaConsumer<Integer, String> consumer = getConsumer(uuid);		
		ConsumerRecords<Integer, String> records = consumer.poll(timeout);
		List<String> messages = new ArrayList<String>();

		for (ConsumerRecord<Integer, String> record : records) {
			messages.add(record.value());
		}
		return Response.ok(new DataObject(true, null, messages)).build();
	}
	
	@DELETE
	@Path("messages/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response commit(@PathParam("uuid") String uuid) {
		if (!sessionExists(uuid)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new StatusObject(false, "session-" + uuid + " does not exist, cannot commit")).build();
		}
		KafkaConsumer<Integer, String> consumer = getConsumer(uuid);
		consumer.commitSync();
		return Response.ok(new StatusObject(true, "session-" + uuid + " successfully commit")).build();
	}
	
	private boolean sessionExists(String uuid) {
		return sessions.keySet().contains(uuid);
	}
	
	private KafkaConsumer<Integer, String> getConsumer(String uuid) {
		KafkaConsumer<Integer, String> consumer = sessions.get(uuid);
		return consumer;
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
