package com.icbc.dds.rpc.support;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.ext.ContextResolver;

/**
 * Created by kfzx-wengxj on 18/01/2017.
 */
public class DDSObjectMapperProvider implements ContextResolver<ObjectMapper> {
    @Override
    public ObjectMapper getContext(Class<?> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper;
    }
}
