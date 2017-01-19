package com.icbc.dds.api.exception;

public class DDSRegistryException extends RuntimeException {

	public DDSRegistryException(String msg) {
		super(msg);
	}

	public DDSRegistryException(Throwable cause) {
		super(cause);
	}

	public DDSRegistryException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
