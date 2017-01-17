package com.icbc.dds.registry.client.pojo;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.annotate.JsonTypeName;

@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
//@JsonTypeName(value = "instance")
public class InstanceInfo {
	private String hostName;
	private String instanceId;
	private String app;
	private String ipAddr;
	private String vipAddress;
	private String secureVipAddress;
	private String status;
	private Port port;
	private Port securePort;
	private String homePageUrl;
	private String statusPageUrl;
	private String healthCheckUrl;
	private DataCenterInfo dataCenterInfo;
	@JsonIgnore
	private Map<String, String> metadata;

	@JsonCreator
	public InstanceInfo(@JsonProperty("instanceId") String instanceId, @JsonProperty("ipAddr") String ipAddr, @JsonProperty("port") Port port) {
		this.instanceId = instanceId;
		this.ipAddr = ipAddr;
		this.port = port;
	}

	public InstanceInfo(String hostName, String app, String ipAddr, String zone, int port) {
		this.hostName = hostName;
		this.app = app;
		this.ipAddr = ipAddr;
		this.vipAddress = zone;
		this.port = new Port(port, true);
		this.instanceId = this.hostName;
		this.secureVipAddress = this.vipAddress;
		this.status = "up";
		this.homePageUrl = "";
		this.statusPageUrl = "";
		this.healthCheckUrl = "";
		this.securePort = new Port();
		this.dataCenterInfo = new DataCenterInfo();
		this.metadata = new HashMap<String, String>();
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public String getVipAddress() {
		return vipAddress;
	}

	public void setVipAddress(String vipAddress) {
		this.vipAddress = vipAddress;
	}

	public String getSecureVipAddress() {
		return secureVipAddress;
	}

	public void setSecureVipAddress(String secureVipAddress) {
		this.secureVipAddress = secureVipAddress;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Port getPort() {
		return port;
	}

	public void setPort(Port port) {
		this.port = port;
	}

	public Port getSecurePort() {
		return securePort;
	}

	public void setSecurePort(Port securePort) {
		this.securePort = securePort;
	}

	public String getHomePageUrl() {
		return homePageUrl;
	}

	public void setHomePageUrl(String homePageUrl) {
		this.homePageUrl = homePageUrl;
	}

	public String getStatusPageUrl() {
		return statusPageUrl;
	}

	public void setStatusPageUrl(String statusPageUrl) {
		this.statusPageUrl = statusPageUrl;
	}

	public String getHealthCheckUrl() {
		return healthCheckUrl;
	}

	public void setHealthCheckUrl(String healthCheckUrl) {
		this.healthCheckUrl = healthCheckUrl;
	}

	public DataCenterInfo getDataCenterInfo() {
		return dataCenterInfo;
	}

	public void setDataCenterInfo(DataCenterInfo dataCenterInfo) {
		this.dataCenterInfo = dataCenterInfo;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
}
