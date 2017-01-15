package com.icbc.dds.rpc.template;

import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.AvailableInstanceNotFoundException;
import com.icbc.dds.api.pojo.InstanceInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import static org.mockito.Mockito.*;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class RestTemplateTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void retry3TimesThenThrowException() throws AvailableInstanceNotFoundException {
        RegistryClient mockedRegistryClient = mock(RegistryClient.class);
        when(mockedRegistryClient.getInstanceByAppName("ServiceWithoutInstances")).thenReturn(null);

        try {
            thrown.expect(AvailableInstanceNotFoundException.class);
            new RestTemplate(Client.create(), mockedRegistryClient).get("ServiceWithoutInstances", "/", MediaType.APPLICATION_JSON_TYPE, String.class);
        } finally {
            verify(mockedRegistryClient, times(3)).getInstanceByAppName("ServiceWithoutInstances");
        }
    }

    @Test
    public void buildRequestWithRightParams() throws AvailableInstanceNotFoundException {
        RegistryClient mockedRegistryClient = mock(RegistryClient.class);
        when(mockedRegistryClient.getInstanceByAppName("Service")).thenReturn(new InstanceInfo("127.0.0.1", 8080));
        Client mockedClient = mock(Client.class);

        WebResource mockedWebResource = mock(WebResource.class);
        when(mockedClient.resource(anyString())).thenReturn(mockedWebResource);
        when(mockedWebResource.path(anyString())).thenReturn(mockedWebResource);
        when(mockedWebResource.queryParams(any(MultivaluedMap.class))).thenReturn(mockedWebResource);
        when(mockedWebResource.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(Client.create().resource("http://localhost:8080").getRequestBuilder());

        try {
            thrown.expect(Exception.class);
            new RestTemplate(mockedClient, mockedRegistryClient).get("Service", "/", MediaType.APPLICATION_JSON_TYPE, String.class);
        } finally {
            verify(mockedClient).resource("http://127.0.0.1:8080");
            verify(mockedWebResource).path("/").accept(MediaType.APPLICATION_JSON_TYPE);
        }
    }
}