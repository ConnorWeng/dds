package com.icbc.dds.registry.client.pojo;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCenterInfo {
	@JsonIgnore
	private static final String DEFAULT_DC_INFO_CLASS = "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo";
	@JsonIgnore
	private static final String DEFAULT_DC_NAME = "MyOwn";
	@JsonProperty("@class")
	private String clazz;
	private String name;

	public DataCenterInfo() {
		this.clazz = DEFAULT_DC_INFO_CLASS;
		this.name = DEFAULT_DC_NAME;
	}

	@JsonCreator
	public DataCenterInfo(@JsonProperty("@class") String clazz, @JsonProperty("name") String name) {
		this.clazz = clazz;
		this.name = name;
	}

	@JsonIgnore
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
