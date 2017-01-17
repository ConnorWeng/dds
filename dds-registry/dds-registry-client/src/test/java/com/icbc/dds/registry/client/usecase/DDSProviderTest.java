package com.icbc.dds.registry.client.usecase;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;

import com.icbc.dds.registry.client.DDSClient;
import com.icbc.dds.registry.server.RegistryServer;

public class DDSProviderTest {

	public static void main(String[] args) {
		try {
			init();
			Executors.newSingleThreadExecutor().submit(new Runnable() {
				@Override
				public void run() {
					RegistryServer.main(new String[] {});
				}
			}).get();
			DDSClient provider = new DDSClient("E:\\work\\workspace\\dds\\dds-registry\\dds-registry-client\\src\\test\\java\\com\\icbc\\dds\\registry\\client\\usecase\\dds-provider.conf");
			provider.register();

			TimeUnit.SECONDS.sleep(6000);

			provider.deRegister();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static void init() throws IOException {
		if (System.getProperty("log.dir") == null) {
			System.setProperty("log.dir", System.getProperty("user.dir"));
		}

		System.out.println(DDSConsumerTest.class.getClassLoader().getResource("log4j.xml"));
		PropertyConfigurator.configure(DDSConsumerTest.class.getResource("log4j.xml"));
	}

}
