package com.icbc.dds.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
@Configuration
public class DDSAutoConfiguration {
    @Bean
    public DDSServerRunner ddsServerRunner() {
        return new DDSServerRunner();
    }
}
