package com.icbc.dds.registry.client.usecase;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import com.icbc.dds.api.pojo.InstanceInfo;
import com.icbc.dds.registry.client.DDSClient;

public class DDSConsumerTest {

	public static void main(String[] args) {
		try {
			init();
			DDSClient ddsConsumer = new DDSClient("E:\\work\\workspace\\dds\\dds-registry\\dds-registry-client\\src\\test\\java\\com\\icbc\\dds\\registry\\client\\usecase\\dds-consumer.conf");
			InstanceInfo instance = ddsConsumer.getInstanceByAppName("cms");
			System.out.println(instance.getIpAddr() + " ::: " + instance.getPort());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static void init() throws IOException {
		if (System.getProperty("log.dir") == null) {
			System.setProperty("log.dir", System.getProperty("user.dir"));
		}

		PropertyConfigurator.configure(DDSConsumerTest.class.getResource("log4j.xml"));
	}

}
