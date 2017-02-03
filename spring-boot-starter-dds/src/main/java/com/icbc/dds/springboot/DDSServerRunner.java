package com.icbc.dds.springboot;

import com.icbc.dds.registry.client.DDSClient;
import com.icbc.dds.springboot.annotation.DDSService;
import com.icbc.dds.springboot.container.servlet.SpringServlet;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by kfzx-wengxj on 16/01/2017.
 */
public class DDSServerRunner implements ApplicationContextAware, CommandLineRunner {
    private ApplicationContext applicationContext;

    @Autowired
    private DDSClient ddsClient;

    @Override
    public void run(String... strings) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(ddsClient.getPort());
        Context context = tomcat.addContext("", null);
        tomcat.addServlet(context, "jersey-container-servlet", resourceConfig());
        context.addServletMapping("/*", "jersey-container-servlet");

        tomcat.start();
        tomcat.getServer().await();
    }

    private ServletContainer resourceConfig() {
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(DDSService.class);
        Set<Class<?>> beans = new HashSet<Class<?>>();
        for (String key : beansMap.keySet()) {
            beans.add(beansMap.get(key).getClass());
        }
        DefaultResourceConfig defaultResourceConfig = new DefaultResourceConfig(beans);
        defaultResourceConfig.getClasses().add(JacksonJsonProvider.class);
        return new SpringServlet(defaultResourceConfig, applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
