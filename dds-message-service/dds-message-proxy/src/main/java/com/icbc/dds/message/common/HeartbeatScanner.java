package com.icbc.dds.message.common;

import java.io.Closeable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;


public class HeartbeatScanner implements Runnable {
	
	protected static final long CHECK_SPAN_MS = HeartbeatProperty.getCheckSpanMs();
	public static final long HEARTBEAT_INTERVAL_MS = HeartbeatProperty.getHeartbeatIntervalMs();
	protected static final long HEARTBEAT_EXPIRE_TIMES = HeartbeatProperty.getHeartbeatExpireTimes();
	
	private static final Logger logger = Logger.getLogger(HeartbeatScanner.class);
	
	private Map<String, ? extends Closeable> sessions;
	private Map<String, Long> heartbeats;
	
	public HeartbeatScanner(Map<String, ? extends Closeable> sessions, Map<String, Long> heartbeats) {
		this.sessions = sessions;
		this.heartbeats = heartbeats;
	}

	@Override
	public void run() {
		while (true) {
			checkHeartbeat();
			try {
				TimeUnit.MILLISECONDS.sleep(CHECK_SPAN_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void checkHeartbeat() {
		try {
			logger.info("启动心跳扫描");
			long currentTime = System.currentTimeMillis();
			for (Entry<String, Long> e : heartbeats.entrySet()) {
				if ((currentTime - e.getValue()) > HEARTBEAT_INTERVAL_MS * HEARTBEAT_EXPIRE_TIMES) {
					try {
						Closeable producer = sessions.get(e.getKey());
						if (producer != null) {
							producer.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						sessions.remove(e.getKey());
						heartbeats.remove(e.getKey());
						logger.info(e.getKey() + "所对应的Consumer对象心跳超时，从缓存删除！");
					}							
				} 
			}
			logger.info("心跳扫描结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
