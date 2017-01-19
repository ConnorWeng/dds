package com.icbc.dds.registry.client.usecase;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;

import com.icbc.dds.api.pojo.InstanceInfo;
import com.icbc.dds.registry.client.DDSClient;
import com.icbc.dds.registry.server.RegistryServer;

public class DDSClientTest {

	private static void init() throws IOException {
		if (System.getProperty("log.dir") == null) {
			System.setProperty("log.dir", System.getProperty("user.dir"));
		}

		PropertyConfigurator.configure(DDSClientTest.class.getClassLoader().getResource("log4j.xml"));
	}

	public static void main(String[] args) {
		try {
			init();
			Executors.newSingleThreadExecutor().submit(new Runnable() {
				@Override
				public void run() {
					RegistryServer.main(new String[] {});
				}
			}).get();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// 服务端使用
		DDSClient provider = new DDSClient("provider.conf");
		provider.register();

		try {
			TimeUnit.SECONDS.sleep(25);
		} catch (InterruptedException e) {

		}

		// 客户端使用
		DDSClient consumer = new DDSClient("consumer.conf");
		InstanceInfo instanceInfo = consumer.getInstanceByAppName("cms");
		System.out.println(instanceInfo.getIpAddr() + " ::: " + instanceInfo.getPort());

		provider.deRegister();
	}

}
