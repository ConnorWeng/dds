package com.icbc.dds.registry.client.common;

public class Constants {
	public static final String DEFAULT_CONF_FILE_NAME = "dds-client.conf";
	
	public static final String DEFAULT_PROTOCOL = "http://";
	public static final String DEFAULT_EUREKA_CONTEXT = "/eureka/v2/apps/";

	public static final String DEFAULT_SEPARATOR = ","; // 默认分隔符
	public static final int DEFAULT_RETRY_TIMES = 6; // 注册重试次数
	public static final long DAFAULT_SLEEP_INTERVAL = 30 * 1000; // 注册重试间隔时长(s)

	public static final long DEFAULT_DELAY = 20; // 心跳首次发送延迟时间(s)
	public static final long DEFAULT_PERIOD = 20; // 心跳发送间隔(s)
	public static final String DEFAULT_JOB_NAME = "renew"; // 心跳线程名

	public static final long DEFAULT_UPDATE_TIME = -1; // 缓存更新时间(默认未更新)
	public static final long DEFAULT_EXPIRE_TIME = 20 * 1000; // 缓存超时时间(s)
	public static final long DEFAULT_THREAD_EXPIRE_TIME = 40 * 1000; // 线程园区优先超时时间(s)
}
