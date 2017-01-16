package com.icbc.dds.rpc.template;

import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.AvailableInstanceNotFoundException;
import com.icbc.dds.api.pojo.InstanceInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public class RestTemplate {
    private static final int RETRY_TIMES = 3;

    private Client client;
    private RegistryClient registryClient;

    public RestTemplate(Client client, RegistryClient registryClient) {
        this.client = client;
        this.registryClient = registryClient;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public <T> T get(String appName, String path, MediaType mediaType, Class<T> responseType, String... query) throws AvailableInstanceNotFoundException {
        return this.get(appName, path, mediaType, prepareParams(query), responseType);
    }

    public <T> T get(String appName, String path, MediaType mediaType, MultivaluedMap params, Class<T> responseType) throws AvailableInstanceNotFoundException {
        for (int i = 0; i < RETRY_TIMES; i++) {
            InstanceInfo instanceInfo = registryClient.getInstanceByAppName(appName);
            if (instanceInfo != null) {
                return this.get(instanceInfo.getIpAddr(), instanceInfo.getPort(), path, mediaType, params, responseType);
            }
        }
        throw new AvailableInstanceNotFoundException(String.format("No available instance of app %s", appName));
    }

    public <T> T get(String ipAddr, int port, String path, MediaType mediaType, Class<T> responseType, String... query) {
        return get(ipAddr, port, path, mediaType, prepareParams(query), responseType);
    }

    public <T> T get(String ipAddr, int port, String path, MediaType mediaType, MultivaluedMap params, Class<T> responseType) {
        ClientResponse response = client.resource("http://" + ipAddr + ":" + port)
                .path(path)
                .queryParams(params)
                .accept(mediaType)
                .get(ClientResponse.class);
        int status = response.getStatus();
        return response.getEntity(responseType);
    }

    private MultivaluedMap prepareParams(String... query) {
        if (query.length % 2 != 0) {
            throw new IllegalArgumentException("query length must be even");
        }
        MultivaluedMapImpl params = new MultivaluedMapImpl();
        for (int i = 0; i < query.length; i += 2) {
            params.add(query[i], query[i+1]);
        }
        return params;
    }
}
