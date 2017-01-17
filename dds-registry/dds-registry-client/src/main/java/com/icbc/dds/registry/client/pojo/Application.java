package com.icbc.dds.registry.client.pojo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonTypeName(value = "application")
public class Application {
	private String name;
	private List<InstanceInfo> instance;

//	@JsonCreator
//	public Application(@JsonProperty("name") String name, @JsonProperty("instance") List<Instance> instances) {
//		this.name = name;
//		this.instances = instances;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<InstanceInfo> getInstance() {
		return instance;
	}

	public void setInstance(List<InstanceInfo> instance) {
		this.instance = instance;
	}
}
