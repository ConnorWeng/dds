package com.icbc.dds.springboot.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface DDSService {
}
