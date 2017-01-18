package com.icbc.dds.rpc.pojo;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
public class ReturnObject {
    private boolean isError;
    private String message;

    @JsonCreator
    public ReturnObject(@JsonProperty("isError") boolean isError, @JsonProperty("message") String message) {
        this.isError = isError;
        this.message = message;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
