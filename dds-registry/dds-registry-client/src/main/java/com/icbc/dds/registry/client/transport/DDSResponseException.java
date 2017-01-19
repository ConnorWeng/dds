package com.icbc.dds.registry.client.transport;

public class DDSResponseException extends RuntimeException {
	public DDSResponseException(String msg) {
		super(msg);
	}

	public DDSResponseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
