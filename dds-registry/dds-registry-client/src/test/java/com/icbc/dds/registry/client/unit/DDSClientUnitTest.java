package com.icbc.dds.registry.client.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.icbc.dds.registry.client.DDSClient;
import com.icbc.dds.registry.client.pojo.ApplicationWrapper;
import com.icbc.dds.registry.client.pojo.InstanceInfo;
import com.icbc.dds.registry.client.pojo.InstanceWrapper;
import com.icbc.dds.registry.client.pojo.InstanceWrapper.PortWrapper;
import com.icbc.dds.registry.client.transport.EurekaClient;

public class DDSClientUnitTest {
	private String consumerConf = "consumer.conf";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		init();
	}

	@Test
	public void getAppTest() {
		DDSClient consumer = new DDSClient(consumerConf, new MockedEurekaClient());
		com.icbc.dds.api.pojo.InstanceInfo instance = consumer.getInstanceByAppName("cms");
		assertEquals(8080, instance.getPort());
	}

	@Test
	public void getExpireTest() {
		DDSClient consumer = new DDSClient(consumerConf, new MockedEurekaClient());
		long start = System.currentTimeMillis();
		while (true) {
			com.icbc.dds.api.pojo.InstanceInfo instance = consumer.getInstanceByAppName("cms");
			if (instance == null || instance.getPort() == 8888) {
				break;
			}
		}
		long stop = System.currentTimeMillis();

		assertTrue(stop - start >= 5 * 1000);
	}

	private static void init() throws IOException {
		if (System.getProperty("log.dir") == null) {
			System.setProperty("log.dir", System.getProperty("user.dir"));
		}

		PropertyConfigurator.configure(DDSClientProviderUnitTest.class.getClassLoader().getResource("log4j.xml"));
	}

	private static class MockedEurekaClient implements EurekaClient {
		private final AtomicInteger counter = new AtomicInteger();

		@Override
		public ApplicationWrapper getApp(String app) {
			List<InstanceWrapper> instanceInfos = new ArrayList<InstanceWrapper>();
			InstanceWrapper instanceWrapper = new InstanceWrapper("A", "localhost", new PortWrapper(8080, true));
			instanceInfos.add(instanceWrapper);
			ApplicationWrapper applicationWrapper = new ApplicationWrapper("cms", instanceInfos);

			List<InstanceWrapper> instanceInfos1 = new ArrayList<InstanceWrapper>();
			InstanceWrapper instanceWrapper1 = new InstanceWrapper("A", "localhost", new PortWrapper(8080, true));
			InstanceWrapper instanceWrapper2 = new InstanceWrapper("B", "localhost", new PortWrapper(8888, true));
			instanceInfos1.add(instanceWrapper1);
			instanceInfos1.add(instanceWrapper2);
			ApplicationWrapper applicationWrapper1 = new ApplicationWrapper("cms", instanceInfos1);

			ArrayList<ApplicationWrapper> list = new ArrayList<ApplicationWrapper>();
			list.add(applicationWrapper);
			list.add(applicationWrapper1);

			if (counter.get() == 0) {
				counter.incrementAndGet();
				return list.get(0);
			} else {
				Random random = new Random();
				int idx = random.nextInt(2);
				return list.get(idx);
			}
		}

		@Override
		public boolean register(InstanceInfo instanceInfo) {
			return true;
		}

		@Override
		public boolean deRegister(String app, String instanceId) {
			return true;
		}

		@Override
		public boolean renew(String app, String instanceId) {
			return true;
		}

		@Override
		public boolean updateMetrics(String app, String instanceId, String metrics) {
			return true;
		}

	}
}
