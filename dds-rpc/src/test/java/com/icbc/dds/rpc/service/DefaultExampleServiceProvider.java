package com.icbc.dds.rpc.service;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class DefaultExampleServiceProvider extends ExampleService {
    @Override
    public String exampleMethod() {
        return "default example method";
    }
}
