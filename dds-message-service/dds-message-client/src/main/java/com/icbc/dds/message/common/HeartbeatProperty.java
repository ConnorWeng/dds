package com.icbc.dds.message.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HeartbeatProperty {

	private static final String propertyLocation = "spring_heartbeat.properties";
	private static Properties props = loadProperties();
	
	public static int getHeartbeatIntervalMs() {
		return getProperty("heartbeat.interval.ms", 3000);
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
