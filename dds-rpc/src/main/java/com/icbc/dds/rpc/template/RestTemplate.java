package com.icbc.dds.rpc.template;

import com.icbc.dds.api.Metrics;
import com.icbc.dds.api.RegistryClient;
import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.api.pojo.InstanceInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kfzx-wengxj on 14/01/2017.
 */
public class RestTemplate {
    private static final int RETRY_TIMES = 3;
    private static final int RETRY_INTERVAL = 1000;

    private Client client;
    private RegistryClient registryClient;
    private Metrics metrics;

    // FIXME: 02/02/2017 什么时候清理？
    private static ConcurrentHashMap<String, InstanceInfo> instances = new ConcurrentHashMap<String, InstanceInfo>();

    public static void reset() {
        instances.clear();
    }

    private InstanceInfo getInstanceByAppName(String appName, int retryTimes) {
        if (retryTimes == 0) {
            InstanceInfo instance = instances.get(appName);
            if (instance != null) return instance;
        }
        return getNextInstanceByAppName(appName);
    }

    private synchronized InstanceInfo getNextInstanceByAppName(String appName) {
        InstanceInfo instanceInfo = registryClient.getInstanceByAppName(appName);
        // TODO: 02/02/2017 null检查
        instances.put(appName, instanceInfo);
        return instanceInfo;
    }

    public RestTemplate(RegistryClient registryClient, Metrics metrics) {
        this.registryClient = registryClient;
        this.metrics = metrics;
    }

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
                InstanceInfo instanceInfo = getInstanceByAppName(appName, i);
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
        // TODO: 18/01/2017 需要捕获ClientHandlerException再封装成DDSRestRPCException吗？
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

    public <T> T post(String appName, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) throws DDSRestRPCException {
        return this.cud("POST", appName, path, acceptMediaType, sendMediaType, responseType, entity, query);
    }

    public <T> T post(String ipAddr, int port, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) {
        return this.cud("POST", ipAddr, port, path, acceptMediaType, sendMediaType, responseType, entity, query);
    }

    public <T> T put(String appName, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) throws DDSRestRPCException {
        return this.cud("PUT", appName, path, acceptMediaType, sendMediaType, responseType, entity, query);
    }

    public <T> T put(String ipAddr, int port, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) {
        return this.cud("PUT", ipAddr, port, path, acceptMediaType, sendMediaType, responseType, entity, query);
    }

    public <T> T delete(String appName, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) throws DDSRestRPCException {
        return this.cud("DELETE", appName, path, acceptMediaType, sendMediaType, responseType, entity, query);
    }

    public <T> T delete(String ipAddr, int port, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) {
        return this.cud("DELETE", ipAddr, port, path, acceptMediaType, sendMediaType, responseType, entity, query);
    }

    public <T> T cud(String method, String appName, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) throws DDSRestRPCException {
        return this.cud(method, appName, path, acceptMediaType, sendMediaType, prepareParams(query), entity, responseType);
    }

    public <T> T cud(String method, String appName, String path, MediaType acceptMediaType, MediaType sendMediaType, MultivaluedMap params, Object entity, Class<T> responseType) throws DDSRestRPCException {
        ClientHandlerException clientHandlerException = null;
        for (int i = 0; i < RETRY_TIMES; i++) {
            try {
                InstanceInfo instanceInfo = getInstanceByAppName(appName, i);
                return this.cud(method, instanceInfo.getIpAddr(), instanceInfo.getPort(), path, acceptMediaType, sendMediaType, params, entity, responseType);
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

    public <T> T cud(String method, String ipAddr, int port, String path, MediaType acceptMediaType, MediaType sendMediaType, Class<T> responseType, Object entity, String... query) {
        return this.cud(method, ipAddr, port, path, acceptMediaType, sendMediaType, prepareParams(query), entity, responseType);
    }

    public <T> T cud(String method, String ipAddr, int port, String path, MediaType acceptMediaType, MediaType sendMediaType, MultivaluedMap params, Object entity, Class<T> responseType) {
        String metricsName = method + "://" + ipAddr + ":" + port + path;
        metrics.tickStart(metricsName);
        WebResource.Builder builder = client.resource("http://" + ipAddr + ":" + port)
                .path(path)
                .queryParams(params)
                .accept(acceptMediaType);
        ClientResponse response;
        if (Map.class.isInstance(entity) && sendMediaType.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) {
            builder.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
            Form form = new Form();
            Map<String, String> entityMap = (Map) entity;
            for (String key: entityMap.keySet()) {
                form.add(key, entityMap.get(key));
            }
            response = builder.method(method, ClientResponse.class, form);
        } else if (entity != null) {
            // TODO: 18/01/2017 应该对type做合法性校验
            builder.type(sendMediaType);
            builder.entity(entity);
            response = builder.method(method, ClientResponse.class);
        } else {
            response = builder.method(method, ClientResponse.class);
        }
        int status = response.getStatus();
        metrics.tickStop(metricsName, status < 400);
        if (responseType.equals(ClientResponse.class)) {
            return (T) response;
        } else {
            return response.getEntity(responseType);
        }
    }

    public Builder service(String appName) {
        return new Builder(this, appName);
    }

    public Builder service(String ip, int port) {
        return new Builder(this, ip, port);
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

    public final class Builder {
        private RestTemplate restTemplate;
        private String appName;
        private String ipAddr;
        private int port = 0;
        private String path;
        private MediaType acceptMediaType;
        private MediaType sendMediaType;
        private String[] query = new String[]{};
        private Object entity;

        public Builder(RestTemplate restTemplate, String appName) {
            this.restTemplate = restTemplate;
            this.appName = appName;
        }

        public Builder(RestTemplate restTemplate, String ip, int port) {
            this.restTemplate = restTemplate;
            this.ipAddr = ip;
            this.port = port;
        }

        public Builder ip(String ipAddr) {
            this.ipAddr = ipAddr;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder accept(MediaType mediaType) {
            this.acceptMediaType = mediaType;
            return this;
        }

        public Builder type(MediaType mediaType) {
            this.sendMediaType = mediaType;
            return this;
        }

        public Builder query(String... query) {
            this.query = query;
            return this;
        }

        public Builder entity(Object entity) {
            this.entity = entity;
            return this;
        }

        public <T> T get(Class<T> responseType) throws DDSRestRPCException {
            validate("GET");
            if (appName != null) {
                return this.restTemplate.get(appName, path, acceptMediaType, responseType, query);
            } else {
                return this.restTemplate.get(ipAddr, port, path, acceptMediaType, responseType, query);
            }
        }

        public <T> T post(Class<T> responseType) throws DDSRestRPCException {
            validate("POST");
            if (appName != null) {
                return this.restTemplate.post(appName, path, acceptMediaType, sendMediaType, responseType, entity, query);
            } else {
                return this.restTemplate.post(ipAddr, port, path, acceptMediaType, sendMediaType, responseType, entity, query);
            }
        }

        public <T> T put(Class<T> responseType) throws DDSRestRPCException {
            validate("PUT");
            if (appName != null) {
                return this.restTemplate.put(appName, path, acceptMediaType, sendMediaType, responseType, entity, query);
            } else {
                return this.restTemplate.put(ipAddr, port, path, acceptMediaType, sendMediaType, responseType, entity, query);
            }
        }

        public <T> T delete(Class<T> responseType) throws DDSRestRPCException {
            validate("DELETE");
            if (appName != null) {
                return this.restTemplate.delete(appName, path, acceptMediaType, sendMediaType, responseType, entity, query);
            } else {
                return this.restTemplate.delete(ipAddr, port, path, acceptMediaType, sendMediaType, responseType, entity, query);
            }
        }

        private void validate(String method) {
            if (this.appName == null && this.ipAddr == null) {
                throw new IllegalArgumentException("服务名和ip地址不能同时为空");
            }
            if (this.ipAddr != null && this.port == 0) {
                throw new IllegalArgumentException("端口不能为空");
            }
            if (this.path == null) {
                throw new IllegalArgumentException("path不能为空");
            }
            if (this.acceptMediaType == null) {
                throw new IllegalArgumentException("accept media type不能为空");
            }
            if (method != "GET" && this.sendMediaType == null) {
                throw new IllegalArgumentException(String.format("使用%s方法时必须指定type", method));
            }
        }
    }
}
