package com.icbc.dds.registry.client.pojo;

import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstanceWrapper {
	@JsonIgnore
	private static final String DEFAULT_INSTANCE_STATUS = "up";
	@JsonIgnore
	private static final int DEFAULT_INSTANCE_PORT = 443;
	private String instanceId;
	private String hostName;
	private String app;
	private String ipAddr;
	private String vipAddress;
	private String secureVipAddress;
	private String status;
	private PortWrapper port;
	private PortWrapper securePort;
	private String homePageUrl;
	private String statusPageUrl;
	private String healthCheckUrl;
	private DCWrapper dataCenterInfo;

	public InstanceWrapper(String hostName, String app, String ipAddr, String zone, int port) {
		this.hostName = hostName;
		this.app = app;
		this.ipAddr = ipAddr;
		this.vipAddress = zone;
		this.port = new PortWrapper(port, true);
		this.instanceId = UUID.randomUUID().toString().replace("-", "");
		this.secureVipAddress = zone;
		this.status = DEFAULT_INSTANCE_STATUS;
		this.homePageUrl = "";
		this.statusPageUrl = "";
		this.healthCheckUrl = "";
		this.securePort = new PortWrapper(DEFAULT_INSTANCE_PORT, false);
		this.dataCenterInfo = new DCWrapper();
	}

	@JsonCreator
	public InstanceWrapper(@JsonProperty("vipAddress") String vipAddress, @JsonProperty("ipAddr") String ipAddr, @JsonProperty("port") PortWrapper port) {
		this.vipAddress = vipAddress;
		this.ipAddr = ipAddr;
		this.port = port;
	}

	public static class DCWrapper {
		private String clazz = "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo";
		private String name = "MyOwn";

		@JsonProperty("name")
		public String getName() {
			return name;
		}

		@JsonProperty("@class")
		public String getClazz() {
			return clazz;
		}
	}

	public static class PortWrapper {
		private final int port;
		private final boolean enabled;

		@JsonCreator
		public PortWrapper(@JsonProperty("$") int port, @JsonProperty("@enabled") boolean enabled) {
			this.port = port;
			this.enabled = enabled;
		}

		@JsonProperty("$")
		public int getPort() {
			return port;
		}

		@JsonProperty("@enabled")
		public boolean isEnabled() {
			return enabled;
		}
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getHostName() {
		return hostName;
	}

	public String getApp() {
		return app;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public String getVipAddress() {
		return vipAddress;
	}

	public String getSecureVipAddress() {
		return secureVipAddress;
	}

	public String getStatus() {
		return status;
	}

	public PortWrapper getPort() {
		return port;
	}

	public PortWrapper getSecurePort() {
		return securePort;
	}

	public String getHomePageUrl() {
		return homePageUrl;
	}

	public String getStatusPageUrl() {
		return statusPageUrl;
	}

	public String getHealthCheckUrl() {
		return healthCheckUrl;
	}

	public DCWrapper getDataCenterInfo() {
		return dataCenterInfo;
	}

}
