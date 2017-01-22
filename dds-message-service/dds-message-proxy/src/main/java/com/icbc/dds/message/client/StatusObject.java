package com.icbc.dds.message.client;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class StatusObject {
	private boolean success;
	private String message;
	
	@JsonCreator
	public StatusObject(@JsonProperty("success") boolean success, @JsonProperty("message") String message) {
		this.success = success;
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
