package com.icbc.dds.demo;

import com.icbc.dds.springboot.annotation.DDSService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@DDSService
@Path("/")
public class MicroService {
    @GET
    @Path("hello")
    @Produces("text/plain")
    public String hello(@QueryParam("user") String user) {
        return String.format("hello, %s", user);
    }
}
