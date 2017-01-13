package com.icbc.dds.registry.server;

import com.icbc.dds.registry.server.pojo.Applications;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Created by kfzx-wengxj on 2017/1/13.
 */
@Ignore("Playground，手工运行")
public class RegistryServerTest {
    @BeforeClass
    public static void setUpRegistryServer() throws ExecutionException, InterruptedException {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                RegistryServer.main(new String[] {});
            }
        }).get();
    }

    @Test
    public void getAppsFirstTimeShouldReturnVersionsDelta1() {
        // 初始化Client是expensive operation，所以需要将Client保留下来供后续调用，Client是线程安全的
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(clientConfig);
        client.addFilter(new GZIPContentEncodingFilter());

        // 发起Get请求
        Applications response = client.resource("http://localhost:8761/eureka/apps")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get(Applications.class);

        // Assert
        assertEquals("1", response.getApplications().getVersions__delta());
    }
}