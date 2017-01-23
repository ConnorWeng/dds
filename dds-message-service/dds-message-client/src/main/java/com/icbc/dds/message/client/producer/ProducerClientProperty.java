package com.icbc.dds.message.client.producer;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class ProducerClientProperty {

	private static final String propertyLocation = "spring_producer_client.properties";
	
	public static Map<String, String> getInitProps() {
		Properties props = loadProperties();
		Map<String, String> propMap = new HashMap<String, String>();
		for (Entry<Object, Object> e : props.entrySet()) {
			propMap.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
		}
		return propMap;
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
