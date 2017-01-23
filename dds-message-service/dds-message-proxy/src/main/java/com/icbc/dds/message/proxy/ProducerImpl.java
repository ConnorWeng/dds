package com.icbc.dds.message.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import com.icbc.dds.message.pojo.StatusObject;
import com.icbc.dds.message.common.HeartbeatScanner;
import com.icbc.dds.springboot.annotation.DDSService;

@DDSService
@Path("/producer")
public class ProducerImpl {
    
	private final Logger logger = Logger.getLogger(ProducerImpl.class);
	private static final String propertyLocation = "spring_producer_proxy.properties";
	private static Map<String, KafkaProducer<Integer, String>> sessions = new ConcurrentHashMap<String, KafkaProducer<Integer, String>>();
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
		props.put("client.id", props.getProperty("client.id") + "_" + Math.random());
		
		//test
		System.out.println("param-" + propMap);
		for(Entry<String, String> e : propMap.entrySet()) {
			props.put(e.getKey(), e.getValue());
		}
		KafkaProducer<Integer, String> producer = new KafkaProducer<Integer, String>(props);
		
		String uuid = UUID.randomUUID().toString();
		sessions.put(uuid, producer);
		logger.info("将" + uuid + "所对应的Producer对象放入缓存！");
		logger.info("session---" + sessions); //测试

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
	public Response close(@PathParam("uuid") String uuid) throws Exception {
		if (!sessionExists(uuid)) {
			logger.info("报异常：缓存中未有" + uuid + "所对应的Producer对象存在！");
			return Response.status(Response.Status.BAD_REQUEST).entity(new StatusObject(false, "session-" + uuid + " does not exist, cannot close")).build();
		}
		KafkaProducer<Integer, String> producer = getProducer(uuid);
		producer.close();
		producer = null;
		heartbeats.remove(uuid.toString());
		sessions.remove(uuid.toString());
		logger.info("将" + uuid + "所对应的Producer对象从缓存删除！");
		return Response.ok(new StatusObject(true, "session-" + uuid + " succesfully close")).build();
	}

	@POST
    @Path("messages/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response sendMessages(@PathParam("uuid") String uuid, @FormParam("topic") String topic, @FormParam("key") int key, @FormParam("message") String message) {
		if (topic == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new StatusObject(false, "topic is null")).build();
		}
/*		if (key == 0) {
			return Response.ok(new StatusObject(false, "key is null")).build();
		}*/
		if (message == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new StatusObject(false, "message is null")).build();
		}
		
		if (!sessionExists(uuid)) {
			logger.info("报异常：缓存中未有" + uuid + "所对应的Producer对象存在！");
			return Response.status(Response.Status.BAD_REQUEST).entity(new StatusObject(false, "session-" + uuid + " does not exist, cannot send message")).build();
		}
		KafkaProducer<Integer, String> producer = getProducer(uuid);
		ProducerRecord<Integer, String> record = new ProducerRecord<Integer, String>(topic, key, message);
		producer.send(record);
		producer.flush();
		logger.info("finish sending"); //测试
		return Response.ok(new StatusObject(true, "message is successfully sent")).build();
	}

	private boolean sessionExists(String uuid) {
		return sessions.keySet().contains(uuid);
	}
	
	private KafkaProducer<Integer, String> getProducer(String uuid) {
		KafkaProducer<Integer, String> producer = sessions.get(uuid.toString());
		return producer;
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
