package com.icbc.dds.demo.rest;

import com.icbc.dds.springboot.annotation.DDSService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@DDSService
@Path("/")
public class SampleResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "hello, everyone";
    }

    @GET
    @Path("/y")
    @Produces("text/plain")
    public String yes() {
        return "yes";
    }
}
