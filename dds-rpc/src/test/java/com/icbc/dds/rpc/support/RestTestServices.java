package com.icbc.dds.rpc.support;

import com.icbc.dds.rpc.pojo.DataObject;
import com.icbc.dds.rpc.pojo.DetailsObject;
import com.icbc.dds.rpc.pojo.ReturnObject;
import com.icbc.dds.springboot.annotation.DDSService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public Response getServiceConsumesStringProducesString(@QueryParam("param1") String param1, @QueryParam("param2") String param2) {
        return Response.ok(param1 + " " + param2).build();
    }

    @GET
    @Path("getServiceConsumesStringProducesJson")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceConsumesStringProducesJson(@QueryParam("param1") String param1, @QueryParam("param2") int param2) {
        DataObject dataObject = new DataObject();
        dataObject.setStringValue(param1);
        dataObject.setIntValue(param2);
        DetailsObject detailsObject = new DetailsObject(param1, new int[]{1, 2, 3});
        dataObject.setDeftailsObject(detailsObject);
        List<DetailsObject> detailsObjects = Arrays.asList(new DetailsObject("d1", new int[] {1}), new DetailsObject("d2", new int[] {2}));
        dataObject.setDetailsObjectList(detailsObjects);
        return Response.ok(dataObject).build();
    }

    @POST
    @Path("postServiceConsumesStringProducesString")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response postServiceConsumesStringProducesString(@QueryParam("param1") String param1, @QueryParam("param2") String param2) {
        return Response.ok(param1 + " " + param2).build();
    }

    @POST
    @Path("postServiceConsumesFormProducesJson")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postServiceConsumesFormProducesJson(@FormParam("param1") String param1, @FormParam("param2") boolean param2) {
        return Response.ok(new ReturnObject(param2, param1)).build();
    }

    @POST
    @Path("postServiceConsumesJsonProducesJson")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postServiceConsumesJsonProducesJson(DataObject data) {
        return Response.ok(new DetailsObject(data.getStringValue(), new int[] {1,2,3})).build();
    }

    @POST
    @Path("postServiceConsumesMapProducesJson")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postServiceConsumesMapProducesJson(Map<String, Boolean> dataMap) {
        return Response.ok(new ReturnObject(dataMap.get("param1"), "中文")).build();
    }
}
