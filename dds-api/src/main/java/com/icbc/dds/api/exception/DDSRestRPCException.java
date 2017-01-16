package com.icbc.dds.api.exception;

/**
 * Created by kfzx-wengxj on 15/01/2017.
 */
public class DDSRestRPCException extends Exception {
    public DDSRestRPCException() {

    }

    public DDSRestRPCException(Throwable t) {
        super(t);
    }

    public DDSRestRPCException(String msg) {
        super(msg);
    }

    public DDSRestRPCException(String msg, Throwable t) {
        super(msg, t);
    }
}
