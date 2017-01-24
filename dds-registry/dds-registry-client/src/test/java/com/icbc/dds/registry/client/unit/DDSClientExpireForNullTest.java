package com.icbc.dds.registry.client.unit;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.icbc.dds.registry.client.DDSClient;
import com.icbc.dds.registry.server.RegistryServer;

public class DDSClientExpireForNullTest {
	private String providerConf = "provider.conf";
	private String consumerConf = "consumer.conf";
	private DDSClient aProvider;

	@Test
	public void expireForNullTest() {
		DDSClient consumer = new DDSClient(consumerConf);
		long start = System.currentTimeMillis();
		while (true) {
			com.icbc.dds.api.pojo.InstanceInfo instance = consumer.getInstanceByAppName("cms");
			if (instance == null) {
				break;
			}
		}
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);

		assertTrue(stop - start >= 40 * 1000);
	}

	private static void init() throws IOException {
		if (System.getProperty("log.dir") == null) {
			System.setProperty("log.dir", System.getProperty("user.dir"));
		}

		PropertyConfigurator.configure(DDSClientProviderUnitTest.class.getClassLoader().getResource("log4j.xml"));
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		init();
		Executors.newSingleThreadExecutor().submit(new Runnable() {
			@Override
			public void run() {
				RegistryServer.main(new String[] {});
			}
		}).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.aProvider = new DDSClient(providerConf);
		aProvider.setRetry(1);
		aProvider.register();

		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {

		}
	}

	@After
	public void tearDown() throws Exception {
		aProvider.deRegister();
	}

}
