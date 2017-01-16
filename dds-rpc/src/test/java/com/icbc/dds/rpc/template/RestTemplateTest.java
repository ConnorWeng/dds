package com.icbc.dds.rpc.template;

import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.DDSRestRPCException;
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
    public void retry3TimesToCallServiceWithProblemThenThrowException() throws DDSRestRPCException {
        RegistryClient mockedRegistryClient = mock(RegistryClient.class);
        when(mockedRegistryClient.getInstanceByAppName("ServiceWithProblem")).thenReturn(new InstanceInfo("127.0.0.1", 55555));
        Client mockedClient = mock(Client.class);

        WebResource mockedWebResource = mock(WebResource.class);
        when(mockedClient.resource(anyString())).thenReturn(mockedWebResource);
        when(mockedWebResource.path(anyString())).thenReturn(mockedWebResource);
        when(mockedWebResource.queryParams(any(MultivaluedMap.class))).thenReturn(mockedWebResource);
        when(mockedWebResource.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(Client.create().resource("http://127.0.0.1:55555").getRequestBuilder());

        try {
            thrown.expect(DDSRestRPCException.class);
            new RestTemplate(mockedClient, mockedRegistryClient).get("ServiceWithProblem", "/", MediaType.APPLICATION_JSON_TYPE, String.class);
        } finally {
            verify(mockedRegistryClient, times(3)).getInstanceByAppName("ServiceWithProblem");
            verify(mockedClient, times(3)).resource("http://127.0.0.1:55555");
            verify(mockedWebResource, times(3)).path("/");
            verify(mockedWebResource, times(3)).accept(MediaType.APPLICATION_JSON_TYPE);
        }
    }
}