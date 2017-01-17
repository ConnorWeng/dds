package com.icbc.dds.springboot;

import com.icbc.dds.api.RegistryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@Configuration
public class DDSRegistryConfiguration implements SmartLifecycle {
    private boolean isRunning = false;

    @Autowired
    private RegistryClient registryClient;

    @Bean
    public RegistryClient registryClient() {
        // TODO: 17/01/2017 返回具体实现
        return null;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        isRunning = false;
    }

    @Override
    public void start() {
        isRunning = true;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
