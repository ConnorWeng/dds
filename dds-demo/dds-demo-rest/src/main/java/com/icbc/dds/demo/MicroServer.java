package com.icbc.dds.demo;

import com.icbc.dds.springboot.annotation.DisableDDSClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@SpringBootApplication
public class MicroServer {
    public static void main(String[] args) {
        SpringApplication.run(MicroServer.class, args);
    }
}
