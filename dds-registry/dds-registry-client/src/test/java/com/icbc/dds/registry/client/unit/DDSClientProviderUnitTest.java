package com.icbc.dds.registry.client.unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.icbc.dds.api.exception.DDSRegistryException;
import com.icbc.dds.registry.client.DDSClient;
import com.icbc.dds.registry.client.pojo.InstanceInfo;
import com.icbc.dds.registry.server.RegistryServer;

public class DDSClientProviderUnitTest {
	private final static ExecutorService service = Executors.newSingleThreadExecutor();
	private String providerConf = "provider.conf";
	private String consumerConf = "consumer.conf";
	private String wrongParamConf = "wrong-params.conf";
	private DDSClient provider;

	@Test(expected = DDSRegistryException.class)
	public void wrongParamTest() {
		new DDSClient(wrongParamConf);
	}

	@Test(expected = DDSRegistryException.class)
	public void registerFailTest() {
		provider.setInstanceInfo(new InstanceInfo(null));
		provider.register();
	}

	@Test
	public void getInstanceByAppNameTest() {
		provider.register();

		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {

		}
		DDSClient consumer = new DDSClient(consumerConf);
		com.icbc.dds.api.pojo.InstanceInfo instance = consumer.getInstanceByAppName("cms");
		assertEquals(8080, instance.getPort());
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
		service.submit(new Runnable() {
			@Override
			public void run() {
				RegistryServer.main(new String[] {});
			}
		}).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		service.shutdown();
	}

	@Before
	public void setUp() throws Exception {
		this.provider = new DDSClient(providerConf);
		provider.setRetry(1);
	}

	@After
	public void tearDown() throws Exception {
		provider.deRegister();
	}

}
