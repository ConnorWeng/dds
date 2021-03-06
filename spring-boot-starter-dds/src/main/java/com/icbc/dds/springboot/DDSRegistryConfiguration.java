package com.icbc.dds.springboot;

import com.icbc.dds.registry.client.DDSClient;
import com.icbc.dds.springboot.annotation.DisableDDSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@Configuration
public class DDSRegistryConfiguration implements SmartLifecycle, ApplicationContextAware {
    private final static Logger logger = LoggerFactory.getLogger(DDSRegistryConfiguration.class);
    private boolean isRunning = false;

    @Autowired
    private DDSClient ddsClient;
    private boolean enabled = true;

    @Override
    public boolean isAutoStartup() {
        return enabled;
    }

    @Override
    public void stop(Runnable callback) {
        ddsClient.deRegister();
        isRunning = false;
    }

    @Override
    public void start() {
        ddsClient.register();
        isRunning = true;
    }

    @Override
    public void stop() {
        ddsClient.deRegister();
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithDisableDDSClient = applicationContext.getBeansWithAnnotation(DisableDDSClient.class);
        if (beansWithDisableDDSClient.size() > 0) {
            this.enabled = false;
        }
    }
}
