package com.icbc.dds.rpc.template;

import com.icbc.dds.api.Metrics;
import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.api.pojo.InstanceInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public class RestTemplate {
    private static final int RETRY_TIMES = 3;
    private static final int RETRY_INTERVAL = 1000;

    private Client client;
    private RegistryClient registryClient;
    private Metrics metrics;

    public RestTemplate(Client client, RegistryClient registryClient, Metrics metrics) {
        this.client = client;
        this.registryClient = registryClient;
        this.metrics = metrics;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public <T> T get(String appName, String path, MediaType mediaType, Class<T> responseType, String... query) throws DDSRestRPCException {
        return this.get(appName, path, mediaType, prepareParams(query), responseType);
    }

    public <T> T get(String appName, String path, MediaType mediaType, MultivaluedMap params, Class<T> responseType) throws DDSRestRPCException {
        ClientHandlerException clientHandlerException = null;
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                InstanceInfo instanceInfo = registryClient.getInstanceByAppName(appName);
                return this.get(instanceInfo.getIpAddr(), instanceInfo.getPort(), path, mediaType, params, responseType);
            } catch (ClientHandlerException e) {
                // TODO: 16/01/2017 记录日志
                if (i == RETRY_TIMES - 1) {
                    clientHandlerException = e;
                }
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e1) {
                    // TODO: 17/01/2017 重新设置中断状态是否有必要？
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new DDSRestRPCException(clientHandlerException);
    }

    public <T> T get(String ipAddr, int port, String path, MediaType mediaType, Class<T> responseType, String... query) {
        return get(ipAddr, port, path, mediaType, prepareParams(query), responseType);
    }

    public <T> T get(String ipAddr, int port, String path, MediaType mediaType, MultivaluedMap params, Class<T> responseType) {
        String metricsName = "GET://" + ipAddr + ":" + port + path;
        metrics.tickStart(metricsName);
        ClientResponse response = client.resource("http://" + ipAddr + ":" + port)
                .path(path)
                .queryParams(params)
                .accept(mediaType)
                .get(ClientResponse.class);
        int status = response.getStatus();
        metrics.tickStop(metricsName, status < 400);
        return response.getEntity(responseType);
    }

    public <T> T post(String appName, String path, MediaType mediaType, Object entity, Class<T> responseType) throws DDSRestRPCException {
        ClientHandlerException clientHandlerException = null;
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                InstanceInfo instanceInfo = registryClient.getInstanceByAppName(appName);
                return this.post(instanceInfo.getIpAddr(), instanceInfo.getPort(), path, mediaType, entity, responseType);
            } catch (ClientHandlerException e) {
                // TODO: 16/01/2017 记录日志
                if (i == RETRY_TIMES - 1) {
                    clientHandlerException = e;
                }
                try {
                    Thread.sleep(RETRY_INTERVAL);
                } catch (InterruptedException e1) {
                    // TODO: 17/01/2017 重新设置中断状态是否有必要？
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new DDSRestRPCException(clientHandlerException);
    }

    public <T> T post(String ipAddr, int port, String path, MediaType mediaType, Object entity, Class<T> responseType) {
        String metricsName = "POST://" + ipAddr + ":" + port + path;
        metrics.tickStart(metricsName);
        ClientResponse response = client.resource("http://" + ipAddr + ":" + port)
                .path(path)
                .entity(entity)
                .accept(mediaType)
                .post(ClientResponse.class);
        int status = response.getStatus();
        metrics.tickStop(metricsName, status < 400);
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
