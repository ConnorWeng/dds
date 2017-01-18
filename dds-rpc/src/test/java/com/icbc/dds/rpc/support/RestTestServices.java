package com.icbc.dds.rpc.support;

import com.icbc.dds.springboot.annotation.DDSService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by kfzx-wengxj on 18/01/2017.
 */
@DDSService
@Path("/")
public class RestTestServices {
    @GET
    @Path("getServiceConsumesStringProducesString")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceConsumesStringProducesString(@QueryParam("param1") String param1, @QueryParam("param2") String param2) {
        return param1 + " " + param2;
    }

    @POST
    @Path("postServiceConsumesStringProducesString")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postServiceConsumesStringProducesString(@QueryParam("param1") String param1, @QueryParam("param2") String param2) {
        return param1 + " " + param2;
    }
}
