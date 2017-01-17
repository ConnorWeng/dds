package com.icbc.dds.springboot;

import com.icbc.dds.springboot.annotation.DDSService;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.BeansException;
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

    @Override
    public void run(String... strings) throws Exception {
        Tomcat tomcat = new Tomcat();
        // TODO: 16/01/2017 应该如何获取端口配置?
        String port = System.getenv("port");
        if (port == null || port.isEmpty()) {
            port = "8081";
        }
        tomcat.setPort(Integer.valueOf(port));
        Context context = tomcat.addContext("", null);
        tomcat.addServlet(context, "jersey-container-servlet", resourceConfig());
        context.addServletMapping("/", "jersey-container-servlet");

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
        return new ServletContainer(defaultResourceConfig);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
