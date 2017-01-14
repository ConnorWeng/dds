package com.icbc.dds.rpc.service;

import com.icbc.dds.api.RegistryClient;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class ServiceProviderTest {
    @Test
    public void registryClientServiceNotFoundThenReturnDefaultRegistryClient() throws InstantiationException, IllegalAccessException {
        RegistryClient client = ServiceProvider.get(RegistryClient.class, DefaultRegistryClient.class);
        assertTrue(DefaultRegistryClient.class.isInstance(client));
    }

    @Test
    public void exampleServiceShouldReturnExampleServiceProvider() throws InstantiationException, IllegalAccessException {
        ExampleService exampleService = ServiceProvider.get(ExampleService.class, DefaultExampleServiceProvider.class);
        assertTrue(ExampleServiceProvider.class.isInstance(exampleService));
    }
}