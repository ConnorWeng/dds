package com.icbc.dds.rpc.support;

import com.icbc.dds.rpc.template.RestTemplate;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public abstract class RestSupport {
    private RestTemplate restTemplate;

    protected Client initClient() {
        DefaultClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(clientConfig);
        client.addFilter(new GZIPContentEncodingFilter());
        return client;
    };

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        if (this.restTemplate.getClient() == null) {
            this.restTemplate.setClient(initClient());
        }
    }
}
