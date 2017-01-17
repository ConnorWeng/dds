package com.icbc.dds.demo.rest;

import com.icbc.dds.springboot.annotation.DDSService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@DDSService
@Path("/json")
public class AnotherResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HelloObject hello() {
        return new HelloObject("json");
    }
}
