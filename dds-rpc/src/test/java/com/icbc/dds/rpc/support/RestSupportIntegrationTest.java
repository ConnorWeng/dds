package com.icbc.dds.rpc.support;

import com.icbc.dds.api.Metrics;
import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.rpc.factory.SupportFactory;
import com.icbc.dds.rpc.pojo.DataObject;
import com.icbc.dds.rpc.pojo.DetailsObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@SpringBootApplication
public class RestSupportIntegrationTest extends RestSupport {
    RegistryClient mockedRegistryClient = mock(RegistryClient.class);
    Metrics mockedMetrics = mock(Metrics.class);
    static ExecutorService executorService;
    RestSupportIntegrationTest restSupport;

    @BeforeClass
    public static void setUp() throws ExecutionException, InterruptedException {
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                SpringApplication.run(RestSupportIntegrationTest.class);
            }
        });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Before
    public void setUpRestSupport() {
        restSupport = SupportFactory.getRestSupport(RestSupportIntegrationTest.class);
    }

    @Test
    public void getServiceConsumesStringProducesString() {
        String result = restSupport.getRestTemplate().get("localhost", 8081, "/getServiceConsumesStringProducesString", MediaType.TEXT_PLAIN_TYPE, String.class, "param1", "ok", "param2", "中文");
        assertEquals("ok 中文", result);
    }

    @Test
    public void getServiceConsumesStringProducesJson() {
        DataObject dataObject = restSupport.getRestTemplate().get("localhost", 8081, "/getServiceConsumesStringProducesJson", MediaType.APPLICATION_JSON_TYPE, DataObject.class, "param1", "中文", "param2", "100");
        assertEquals("中文", dataObject.getStringValue());
        assertEquals(100, dataObject.getIntValue());
        assertEquals(new DetailsObject("中文", new int[] {1, 2, 3}), dataObject.getDeftailsObject());
        assertEquals(new DetailsObject("d2", new int[] {2}), dataObject.getDetailsObjectList().get(1));
    }

    @Test
    public void postServiceConsumesStringProducesString() {
        String result = restSupport.getRestTemplate().post("localhost", 8081, "/postServiceConsumesStringProducesString", MediaType.TEXT_PLAIN_TYPE, String.class, "param1", "ok", "param2", "中文");
        assertEquals("ok 中文", result);
    }

    @AfterClass
    public static void tearDown() {
        executorService.shutdown();
    }
}
