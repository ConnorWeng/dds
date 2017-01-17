package com.icbc.dds.demo.rest;

import com.icbc.dds.rpc.factory.SupportFactory;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public class RestClient {
    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        LocalRestSupport restSupport = SupportFactory.getRestSupport(LocalRestSupport.class);
        System.out.println(restSupport.hello());
        System.out.println(restSupport.helloJSON());
    }
}
