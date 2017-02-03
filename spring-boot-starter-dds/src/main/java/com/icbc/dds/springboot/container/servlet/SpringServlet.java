package com.icbc.dds.springboot.container.servlet;

import com.icbc.dds.springboot.container.SpringComponentProviderFactory;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.ws.rs.core.Application;

public class SpringServlet extends ServletContainer {

    private static final long serialVersionUID = 5686655395748077999L;

    private static final Logger logger = LoggerFactory.getLogger(SpringServlet.class);

    private ApplicationContext applicationContext;

    public SpringServlet(Application appClass, ApplicationContext applicationContext) {
        super(appClass);
        this.applicationContext = applicationContext;
    }

    @Override
    protected void initiate(ResourceConfig rc, WebApplication wa) {
        try {
            wa.initiate(rc, new SpringComponentProviderFactory(rc, getContext()));
        } catch (RuntimeException e) {
            logger.error("Exception occurred when intialization", e);
            throw e;
        }
    }

    protected ApplicationContext getContext() {
        return applicationContext;
    }

 }