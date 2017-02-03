package com.icbc.dds.demo;

import com.icbc.dds.springboot.annotation.DDSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@DDSService
@Path("/")
@Scope("prototype")
public class MicroService {
    @Autowired
    private BusinessService businessService;

    @GET
    @Path("hello")
    @Produces("text/plain")
    public String hello(@QueryParam("user") String user) {
        return String.format("hello, %s", user);
    }

    @GET
    @Path("business")
    @Produces("text/plain")
    public String business() {
        System.out.println(businessService);
        System.out.println(this);
        return BusinessService.doSth();
    }
}
