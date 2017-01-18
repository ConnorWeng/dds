package com.icbc.dds.rpc.support;

import com.icbc.dds.api.Metrics;
import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.api.pojo.InstanceInfo;
import com.icbc.dds.rpc.factory.SupportFactory;
import com.icbc.dds.rpc.pojo.DataObject;
import com.icbc.dds.rpc.pojo.DetailsObject;
import com.icbc.dds.rpc.pojo.ReturnObject;
import com.icbc.dds.rpc.template.RestTemplate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(mockedRegistryClient.getInstanceByAppName("RestTestServices")).thenReturn(new InstanceInfo("localhost", 8081));
        restSupport = SupportFactory.getRestSupport(RestSupportIntegrationTest.class);
        restSupport.setRestTemplate(new RestTemplate(mockedRegistryClient, mockedMetrics));
    }

    @Test
    public void getServiceConsumesStringProducesString() throws DDSRestRPCException {
        String result = restSupport.getRestTemplate().get("RestTestServices", "/getServiceConsumesStringProducesString", MediaType.TEXT_PLAIN_TYPE, String.class, "param1", "ok", "param2", "中文");
        assertEquals("ok 中文", result);
    }

    @Test
    public void getServiceConsumesStringProducesJson() throws DDSRestRPCException {
        DataObject dataObject = restSupport.getRestTemplate().get("RestTestServices", "/getServiceConsumesStringProducesJson", MediaType.APPLICATION_JSON_TYPE, DataObject.class, "param1", "中文", "param2", "100");
        assertEquals("中文", dataObject.getStringValue());
        assertEquals(100, dataObject.getIntValue());
        assertEquals(new DetailsObject("中文", new int[] {1, 2, 3}), dataObject.getDeftailsObject());
        assertEquals(new DetailsObject("d2", new int[] {2}), dataObject.getDetailsObjectList().get(1));
    }

    @Test
    public void postServiceConsumesStringProducesString() throws DDSRestRPCException {
        String result = restSupport.getRestTemplate().post("RestTestServices", "/postServiceConsumesStringProducesString", MediaType.TEXT_PLAIN_TYPE, String.class, null, "param1", "ok", "param2", "中文");
        assertEquals("ok 中文", result);
    }

    @Test
    public void postServiceConsumesFormProducesJson() throws DDSRestRPCException {
        Map<String, String> formMap = new HashMap<String, String>();
        formMap.put("param1", "中文");
        formMap.put("param2", "false");
        ReturnObject result = restSupport.getRestTemplate().post("RestTestServices", "/postServiceConsumesFormProducesJson", MediaType.APPLICATION_JSON_TYPE, ReturnObject.class, formMap);
        assertEquals(result.getMessage(), "中文");
        assertFalse(result.isError());
    }

    @AfterClass
    public static void tearDown() {
        executorService.shutdown();
    }
}
