package com.icbc.dds.registry.client.pojo;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Port {
	@JsonIgnore
	private static final int DEFAULT_SECURY_PORT = 443;
	@JsonIgnore
	private static final boolean DEFAULT_SECURY_ENABLED = false;
	@JsonProperty("$")
	private int port;
	@JsonProperty("@enabled")
	private boolean enabled;

	public Port() {
		this.port = DEFAULT_SECURY_PORT;
		this.enabled = DEFAULT_SECURY_ENABLED;
	}

	@JsonCreator
	public Port(@JsonProperty("$") int port, @JsonProperty("@enabled") boolean enabled) {
		this.port = port;
		this.enabled = enabled;
	}

	@JsonIgnore
	public int getPort() {
		return port;
	}

	@JsonIgnore
	public boolean isEnabled() {
		return enabled;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
