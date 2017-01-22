package com.icbc.dds.message.common;

import java.util.concurrent.TimeUnit;

import com.icbc.dds.api.exception.DDSRestRPCException;


public class HeartbeatTask implements Runnable {

	protected static final int HEARTBEAT_INTERVAL_MS = Constants.HEARTBEAT_INTERVAL_MS;
	
	private RpcClient rpcClient;
	private Thread monitoredThread;

	public HeartbeatTask(RpcClient rpcClient, Thread thread) {
		this.rpcClient = rpcClient;
		this.monitoredThread = thread;
	}

	@Override
	public void run() {
		while (true) {
			if (monitoredThread.isAlive()) {
				try {
					rpcClient.heartbeat();
				} catch (DDSRestRPCException e) {
					e.printStackTrace();
				}
			}
			
			try {
				TimeUnit.MILLISECONDS.sleep(HEARTBEAT_INTERVAL_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}