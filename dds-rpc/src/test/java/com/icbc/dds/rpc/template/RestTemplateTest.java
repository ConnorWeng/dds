package com.icbc.dds.rpc.template;

import com.icbc.dds.api.Metrics;
import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.api.pojo.InstanceInfo;
import com.icbc.dds.rpc.service.DefaultMetrics;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class RestTemplateTest {
    @Before
    public void clearCache() {
        RestTemplate.reset();
    }

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    @Ignore("不同的jdk下行为不一致，待调整")
    public void retry3TimesToCallServiceWithProblemThenThrowException() throws DDSRestRPCException {
        Metrics mockedMetrics = mock(Metrics.class);

        RegistryClient mockedRegistryClient = mock(RegistryClient.class);
        when(mockedRegistryClient.getInstanceByAppName("ServiceWithProblem")).thenReturn(new InstanceInfo("127.0.0.1", 55555));
        Client mockedClient = mock(Client.class);

        // TODO 不同的jdk下行为不一致，待调整
        WebResource mockedWebResource = mock(WebResource.class);
        when(mockedClient.resource(anyString())).thenReturn(mockedWebResource);
        when(mockedWebResource.path(anyString())).thenReturn(mockedWebResource);
        when(mockedWebResource.queryParams(any(MultivaluedMap.class))).thenReturn(mockedWebResource);
        when(mockedWebResource.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(Client.create().resource("http://127.0.0.1:55555").getRequestBuilder());

        try {
            thrown.expect(DDSRestRPCException.class);
            new RestTemplate(mockedClient, mockedRegistryClient, mockedMetrics).get("ServiceWithProblem", "/", MediaType.APPLICATION_JSON_TYPE, String.class);
        } finally {
            verify(mockedMetrics, times(3)).tickStart("GET://127.0.0.1:55555/");
            verify(mockedRegistryClient, times(3)).getInstanceByAppName("ServiceWithProblem");
            verify(mockedClient, times(3)).resource("http://127.0.0.1:55555");
            verify(mockedWebResource, times(3)).path("/");
            verify(mockedWebResource, times(3)).accept(MediaType.APPLICATION_JSON_TYPE);
        }
    }

    @Test
    public void getOneAppTwiceShouldReturnTheSameInstance() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RegistryClient mockedRegistryClient = mock(RegistryClient.class);
        when(mockedRegistryClient.getInstanceByAppName("ExampleService")).thenReturn(new InstanceInfo("127.0.0.1", 55555));
        RestTemplate restTemplate = new RestTemplate(mockedRegistryClient, new DefaultMetrics());
        Method getInstanceByAppName = RestTemplate.class.getDeclaredMethod("getInstanceByAppName", String.class, int.class);
        getInstanceByAppName.setAccessible(true);
        InstanceInfo instance1 = (InstanceInfo) getInstanceByAppName.invoke(restTemplate, "ExampleService", 0);
        InstanceInfo instance2 = (InstanceInfo) getInstanceByAppName.invoke(restTemplate, "ExampleService", 0);
        assertNotNull(instance1);
        assertSame(instance1, instance2);
        verify(mockedRegistryClient, times(1)).getInstanceByAppName("ExampleService");
    }

    @Test
    public void getOneAppTwiceWithDifferentRetryTimesShouldReturnDifferentInstances() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RegistryClient mockedRegistryClient = mock(RegistryClient.class);
        when(mockedRegistryClient.getInstanceByAppName("ExampleService")).thenReturn(new InstanceInfo("127.0.0.1", 55555));
        RestTemplate restTemplate = new RestTemplate(mockedRegistryClient, new DefaultMetrics());
        Method getInstanceByAppName = RestTemplate.class.getDeclaredMethod("getInstanceByAppName", String.class, int.class);
        getInstanceByAppName.setAccessible(true);
        InstanceInfo instance1 = (InstanceInfo) getInstanceByAppName.invoke(restTemplate, "ExampleService", 0);
        InstanceInfo instance2 = (InstanceInfo) getInstanceByAppName.invoke(restTemplate, "ExampleService", 1);
        verify(mockedRegistryClient, times(2)).getInstanceByAppName("ExampleService");
    }
}