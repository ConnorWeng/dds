package com.icbc.dds.demo.rest;

import com.icbc.dds.rpc.support.RestSupport;

import javax.ws.rs.core.MediaType;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public class LocalRestSupport extends RestSupport {
    public String hello() {
        return this.getRestTemplate().get("localhost", 80, "/", MediaType.TEXT_HTML_TYPE, String.class);
    }

    public HelloObject helloJSON() {
        return this.getRestTemplate().get("localhost", 80, "/hello.json", MediaType.APPLICATION_JSON_TYPE, HelloObject.class);
    }
}
