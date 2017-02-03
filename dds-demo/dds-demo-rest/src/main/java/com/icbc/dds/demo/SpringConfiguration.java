package com.icbc.dds.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kfzx-wengxj on 04/02/2017.
 */
@Configuration
public class SpringConfiguration {
    @Bean
    public BusinessService businessService() {
        return new BusinessService();
    }
}
