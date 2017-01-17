package com.icbc.dds.message.proxy;

import com.icbc.dds.springboot.annotation.DDSService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@DDSService
@Path("/")
public class ProducerImpl {
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello world";
    }
}
