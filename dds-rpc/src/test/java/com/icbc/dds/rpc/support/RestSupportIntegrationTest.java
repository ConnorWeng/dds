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
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        String result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/getServiceConsumesStringProducesString")
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .query("param1", "ok", "param2", "中文")
                .get(String.class);
        assertEquals("ok 中文", result);
    }

    @Test
    public void getServiceConsumesStringProducesJson() throws DDSRestRPCException {
        DataObject dataObject = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/getServiceConsumesStringProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .query("param1", "中文", "param2", "100")
                .get(DataObject.class);
        assertEquals("中文", dataObject.getStringValue());
        assertEquals(100, dataObject.getIntValue());
        assertEquals(new DetailsObject("中文", new int[] {1, 2, 3}), dataObject.getDeftailsObject());
        assertEquals(new DetailsObject("d2", new int[] {2}), dataObject.getDetailsObjectList().get(1));
    }

    @Test
    public void postServiceConsumesStringProducesString() throws DDSRestRPCException {
        String result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesStringProducesString")
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .type(MediaType.TEXT_PLAIN_TYPE)
                .query("param1", "ok", "param2", "中文")
                .post(String.class);
        assertEquals("ok 中文", result);
    }

    @Test
    public void postServiceConsumesFormProducesJson() throws DDSRestRPCException {
        Map<String, String> formMap = new HashMap<String, String>();
        formMap.put("param1", "中文");
        formMap.put("param2", "false");
        ReturnObject result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesFormProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .entity(formMap)
                .post(ReturnObject.class);
        assertEquals(result.getMessage(), "中文");
        assertFalse(result.isError());
    }

    @Test
    public void postServiceConsumesJsonProducesJson() throws DDSRestRPCException {
        DataObject dataObject = new DataObject();
        dataObject.setStringValue("中文");
        DetailsObject result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesJsonProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(dataObject)
                .post(DetailsObject.class);
        assertEquals(dataObject.getStringValue(), result.getName());
    }

    @Test
    public void postServiceConsumesMapProducesJson() throws DDSRestRPCException {
        Map<String, Boolean> dataMap = new HashMap<String, Boolean>();
        dataMap.put("param1", false);
        ReturnObject result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesMapProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(dataMap)
                .post(ReturnObject.class);
        assertEquals(false, result.isError());
        assertEquals("中文", result.getMessage());
    }

    @Test
    public void postServiceConsumesListProducesJson() throws DDSRestRPCException {
        List<DataObject> dataObjects = new ArrayList<DataObject>();
        DataObject dataObject1 = new DataObject();
        dataObjects.add(dataObject1);
        DataObject dataObject2 = new DataObject();
        dataObject2.setDeftailsObject(new DetailsObject("中文", new int[] {}));
        dataObjects.add(dataObject2);
        ReturnObject result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesListProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(dataObjects)
                .post(ReturnObject.class);
        assertEquals("中文", result.getMessage());
    }

    @Test
    public void postServiceConsumesFormProducesStream() throws DDSRestRPCException, IOException {
        Map<String, String> form = new HashMap<String, String>();
        form.put("param1", "中文");
        form.put("param2", "55555555");
        InputStream inputStream = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesFormProducesStream")
                .accept(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .entity(form)
                .post(InputStream.class);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        bufferedReader.close();
        assertEquals("中文! I am a stream. It's amazing!", line);
    }

    @Test
    public void postServiceConsumesStreamWithFieldProducesStreamWithField() throws DDSRestRPCException, IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("中文! I am a stream. It's amazing!\n".getBytes());
        ClientResponse response = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesStreamWithFieldProducesStreamWithField/Smile")
                .accept(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .type(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .entity(byteArrayInputStream)
                .post(ClientResponse.class);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntityInputStream()));
        String line = bufferedReader.readLine();
        assertEquals("Smile! 中文! I am a stream. It's amazing!", line);
        String field = response.getHeaders().getFirst("custom-header-field");
        assertEquals("looks fine!", field);
    }

    @Test
    public void postServiceConsumesMultiPartProducesJson() throws DDSRestRPCException {
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("field1", "value1")
                .field("field2", "value2");
        ReturnObject result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesMultiPartProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .entity(formDataMultiPart)
                .post(ReturnObject.class);
        assertEquals("value1", result.getMessage());
    }

    @Test
    public void postServiceConsumesMultiPartWithStreamProducesJson() throws DDSRestRPCException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream("中文! I am a stream. It's amazing!\n".getBytes());
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart("stream", byteArrayInputStream, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("field", "value");
        formDataMultiPart.bodyPart(formDataBodyPart);
        ReturnObject result = restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("/postServiceConsumesMultiPartWithStreamProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .entity(formDataMultiPart)
                .post(ReturnObject.class);
        assertEquals("中文! I am a stream. It's amazing!", result.getMessage());
    }

    @Test
    public void putServiceConsumesJsonProducesJson() throws DDSRestRPCException {
        DataObject dataObject = new DataObject();
        dataObject.setStringValue("中文");
        ReturnObject returnObject = this.restSupport.getRestTemplate()
                .service("RestTestServices")
                .path("putServiceConsumesJsonProducesJson")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(dataObject)
                .put(ReturnObject.class);
        assertEquals("中文", returnObject.getMessage());
    }

    @AfterClass
    public static void tearDown() {
        executorService.shutdown();
    }
}
