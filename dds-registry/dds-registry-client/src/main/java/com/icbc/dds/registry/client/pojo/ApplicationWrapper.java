package com.icbc.dds.registry.client.pojo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ApplicationWrapper {
	private String name;
	private List<InstanceWrapper> instanceInfos;

	@JsonCreator
	public ApplicationWrapper(@JsonProperty("name") String name, @JsonProperty("instance") List<InstanceWrapper> instanceInfos) {
		this.name = name;
		this.instanceInfos = instanceInfos;
	}

	public String getName() {
		return name;
	}

	public List<InstanceWrapper> getInstanceInfos() {
		return instanceInfos;
	}

}
