package com.icbc.dds.demo.support;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.rpc.support.RestSupport;

import javax.ws.rs.core.MediaType;

/**
 * Created by kfzx-wengxj on 25/01/2017.
 */
public class MicroServiceSupport extends RestSupport {
    public String hello(String user) throws DDSRestRPCException {
        String response = this.getRestTemplate()
                .service("micro-service")
                .accept(MediaType.TEXT_PLAIN_TYPE)
                .path("/hello")
                .query("user", "tom")
                .get(String.class);
        return response;
    }
}
