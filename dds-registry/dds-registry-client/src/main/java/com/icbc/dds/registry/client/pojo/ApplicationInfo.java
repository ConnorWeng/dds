package com.icbc.dds.registry.client.pojo;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ApplicationInfo {
	private ApplicationWrapper application;

	@JsonCreator
	public ApplicationInfo(@JsonProperty("application") ApplicationWrapper application) {
		this.application = application;
	}

	public ApplicationWrapper getApplication() {
		return application;
	}
}
