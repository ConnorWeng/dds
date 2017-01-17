package com.icbc.dds.demo.rest;

import org.codehaus.jackson.annotate.JsonAutoDetect;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
@JsonAutoDetect
public class HelloObject {
    private String name;

    public HelloObject() {}

    public HelloObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "hello, " + name;
    }
}
