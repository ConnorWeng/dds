package com.icbc.dds.api.exception;

/**
 * Created by kfzx-wengxj on 18/01/2017.
 */
public class DDSInstantiationException extends RuntimeException {
    public DDSInstantiationException() {

    }

    public DDSInstantiationException(Throwable t) {
        super(t);
    }

    public DDSInstantiationException(String msg) {
        super(msg);
    }

    public DDSInstantiationException(String msg, Throwable t) {
        super(msg, t);
    }
}
