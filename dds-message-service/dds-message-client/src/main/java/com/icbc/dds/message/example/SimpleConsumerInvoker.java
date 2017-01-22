package com.icbc.dds.message.example;

import java.util.concurrent.TimeUnit;

import com.icbc.dds.message.client.consumer.SimpleConsumerClient;

public class SimpleConsumerInvoker {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException(
					"[usage] java client.consumer.outer.SimpleClientInvoker <ip> <port>");
		}
		
		String serverAddr = args[0];
		int serverPort = Integer.parseInt(args[1]);
		SimpleConsumerClient rpcClient = new SimpleConsumerClient(serverAddr, serverPort, new NullProcessor());
		
		
		long totalTime = 0;
		long start = System.currentTimeMillis();
		long i = 0;
		for (;;) {
			i += rpcClient.getAndProcessMessage();
			totalTime = System.currentTimeMillis() - start;
			System.out.println(i + ", TPS" + i * 1000.0 / totalTime );
			TimeUnit.SECONDS.sleep(10);
		}
		
/*		rpcClient.close();
		System.exit(0);*/
	}
}
