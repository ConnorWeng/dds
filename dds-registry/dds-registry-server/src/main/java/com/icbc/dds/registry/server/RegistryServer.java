package com.icbc.dds.registry.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by Connor on 07/01/2017.
 */
@SpringBootApplication
@EnableEurekaServer
public class RegistryServer {
    public static void main(String[] args) {
        SpringApplication.run(RegistryServer.class, args);
    }
}
