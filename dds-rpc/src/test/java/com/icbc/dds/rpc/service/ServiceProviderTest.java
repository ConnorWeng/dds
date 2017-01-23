package com.icbc.dds.rpc.service;

import com.icbc.dds.api.Metrics;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class ServiceProviderTest {
    @Before
    public void clearCache() {
        ServiceProvider.reset();
    }

    @Test
    public void metricsServiceNotFoundThenReturnDefaultMetrics() throws InstantiationException, IllegalAccessException {
        Metrics metrics = ServiceProvider.get(Metrics.class, DefaultMetrics.class);
        assertTrue(DefaultMetrics.class.isInstance(metrics));
    }

    @Test
    public void exampleServiceShouldReturnExampleServiceProvider() throws InstantiationException, IllegalAccessException {
        ExampleService exampleService = ServiceProvider.get(ExampleService.class, DefaultExampleServiceProvider.class);
        assertTrue(ExampleServiceProvider.class.isInstance(exampleService));
    }

    @Test
    public void getOneServiceTwiceShouldReturnTheSameInstance() throws InstantiationException, IllegalAccessException {
        ExampleService exampleService1 = ServiceProvider.get(ExampleService.class, DefaultExampleServiceProvider.class);
        ExampleService exampleService2 = ServiceProvider.get(ExampleService.class, DefaultExampleServiceProvider.class);
        assertSame(exampleService1, exampleService2);
    }
}