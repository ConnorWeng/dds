package com.icbc.dds.demo;

import com.icbc.dds.api.exception.DDSRestRPCException;
import com.icbc.dds.demo.support.MicroServiceSupport;
import com.icbc.dds.rpc.factory.SupportFactory;

/**
 * Created by kfzx-wengxj on 25/01/2017.
 */
public class ServiceConsumer {
    public static void main(String[] args) throws DDSRestRPCException {
        MicroServiceSupport microService = SupportFactory.getRestSupport(MicroServiceSupport.class);
        System.out.println(microService.hello("tom"));
    }
}