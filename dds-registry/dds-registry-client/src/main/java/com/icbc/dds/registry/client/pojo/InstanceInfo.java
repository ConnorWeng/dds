package com.icbc.dds.registry.client.pojo;

import org.codehaus.jackson.annotate.JsonProperty;

public class InstanceInfo {
	private InstanceWrapper instanceInfo;

	public InstanceInfo(InstanceWrapper instanceInfo) {
		this.instanceInfo = instanceInfo;
	}

	@JsonProperty("instance")
	public InstanceWrapper getInstanceInfo() {
		return instanceInfo;
	}

}
