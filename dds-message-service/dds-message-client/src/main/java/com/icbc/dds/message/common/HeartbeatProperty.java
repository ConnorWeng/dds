package com.icbc.dds.message.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HeartbeatProperty {

	private static final String propertyLocation = "spring_heartbeat.properties";
	private static Properties props = loadProperties();
	
	public static long getHeartbeatIntervalMs() {
		return getProperty("heartbeat.interval.ms", 3000);
	}

	private static long getProperty(String key, long defaultVal) {
		return Long.parseLong((String) props.getOrDefault(key, defaultVal + ""));
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
