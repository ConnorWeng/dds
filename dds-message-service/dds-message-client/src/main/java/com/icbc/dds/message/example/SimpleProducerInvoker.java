package com.icbc.dds.message.example;

import org.apache.log4j.Logger;

import com.icbc.dds.message.client.producer.SimpleProducerClient;

public class SimpleProducerInvoker {

	private final static Logger logger = Logger.getLogger(SimpleProducerInvoker.class);
	
	public static void main(String[] args) throws Exception {
		byte[] bytes = new byte[1024];
		for (int i = 0; i < 1024; i++) {
			bytes[i] = "A".getBytes()[0];
		}
		String data = new String(bytes);
		
		if (args.length != 3) {
			throw new IllegalArgumentException(
					"[usage] java client.producer.outer.SimpleClientInvoker <ip> <port> <totalNum>");
		}
		
		String serverAddr = args[0];
		int serverPort = Integer.parseInt(args[1]);
		long totalNum = Long.parseLong(args[2]);

		SimpleProducerClient rpcClient = new SimpleProducerClient(serverAddr, serverPort);
		
		long totalTime = 0;
		long start = System.currentTimeMillis();
		for (int i = 0; i < totalNum; i++) {
			logger.info("第" + i + "次发数");
			rpcClient.sendMessage("kafka1", i, data);
			totalTime = System.currentTimeMillis() - start;
			System.out.println("TPS" + (i+1) * 1000.0 / totalTime );
		}
		
		rpcClient.close();
		System.exit(0);
	}
}
