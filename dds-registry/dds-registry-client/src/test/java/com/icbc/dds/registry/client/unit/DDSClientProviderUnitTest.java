package com.icbc.dds.registry.client.unit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.icbc.dds.api.exception.DDSRegistryException;
import com.icbc.dds.registry.newclient.DDSClient;
import com.icbc.dds.registry.newclient.pojo.InstanceInfo;

public class DDSClientProviderUnitTest {
	private String providerConf = "provider.conf";
	private String consumerConf = "consumer.conf";
	private String wrongParamConf = "wrong-params.conf";
	private DDSClient provider;

	@Test(expected = DDSRegistryException.class)
	public void wrongParamTest() {
		new DDSClient(wrongParamConf);
	}

	@Test
	public void registerSuccessTest() {
		provider.register();
	}

	@Test(expected = DDSRegistryException.class)
	public void registerFailTest() {
		provider.setInstanceInfo(new InstanceInfo(null));
		provider.register();
	}

	@Test
	public void getInstanceByAppNameTest() {
		provider.register();
		DDSClient consumer = new DDSClient(consumerConf);
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {

		}
		com.icbc.dds.api.pojo.InstanceInfo instance = consumer.getInstanceByAppName("cms");
		assertEquals("localhost", instance.getIpAddr());
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
		// Executors.newSingleThreadExecutor().submit(new Runnable() {
		// @Override
		// public void run() {
		// RegistryServer.main(new String[] {});
		// }
		// }).get();
	}

	@Before
	public void setUp() throws Exception {
		this.provider = new DDSClient(providerConf);
		provider.setInterval(1000);
		provider.setRetry(1);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		provider.deRegister();
	}

}
