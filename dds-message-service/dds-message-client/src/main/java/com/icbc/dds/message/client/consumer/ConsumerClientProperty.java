package com.icbc.dds.message.client.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class ConsumerClientProperty {

	private static final String propertyLocation = "spring_consumer_client.properties";
	private static Properties props = loadProperties();
	
	public static Map<String, String> getInitProps() {
		Properties props = loadProperties();
		props.remove("poll.records.timeout.ms");
		Map<String, String> propMap = new HashMap<String, String>();
		for (Entry<Object, Object> e : props.entrySet()) {
			propMap.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
		}
		return propMap;
	}
	
	public static int getPollRecordsTimeoutMs() {
		return getProperty("poll.records.timeout.ms", 1000);
	}
	
	private static int getProperty(String key, int defaultVal) {
		return Integer.parseInt((String) props.getOrDefault(key, defaultVal + ""));
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
