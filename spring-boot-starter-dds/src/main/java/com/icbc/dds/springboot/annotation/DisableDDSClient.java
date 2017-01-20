package com.icbc.dds.springboot.annotation;

import java.lang.annotation.*;

/**
 * Created by ConnorWeng on 2017/1/20.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DisableDDSClient {
}
