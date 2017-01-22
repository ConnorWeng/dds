package com.icbc.dds.message.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LoginUtil {
	
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	public static final String JAAS_POSTFIX = ".jaas.conf";
	public static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");
	public static final String IBM_LOGIN_MODULE = "com.ibm.security.auth.module.Krb5LoginModule required";
	public static final String SUN_LOGIN_MODULE = "com.sun.security.auth.module.Krb5LoginModule required";
	public static final String ZOOKEEPER_AUTH_PRINCIPAL = "zookeeper.server.principal";
	public static final String JAVA_SECURITY_KRB5_CONF = "java.security.krb5.conf";
	public static final String JAVA_SECURITY_LOGIN_CONF = "java.security.auth.login.config";
	
	public enum Module {
		
		STORM("StormClient"), KAFKA("KafkaClient"), ZOOKEEPER("Client");

		private String name;

		private Module(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static void setJaasFile(String principal, String keytabPath) throws IOException {
		
		String jaasPath = new File(System.getProperty("java.io.tmpdir")) + File.separator
				+ System.getProperty("user.name") + JAAS_POSTFIX;

		// Windows·���·ָ����滻
		jaasPath = jaasPath.replace("\\", "\\\\");
		deleteJaasFile(jaasPath);
		writeJaasFile(jaasPath, principal, keytabPath);
		System.setProperty(JAVA_SECURITY_LOGIN_CONF, jaasPath);
	}

	public static void setZookeeperServerPrincipal(String zkServerPrincipal) throws IOException {
		System.setProperty(ZOOKEEPER_AUTH_PRINCIPAL, zkServerPrincipal);
		String ret = System.getProperty(ZOOKEEPER_AUTH_PRINCIPAL);
		if (ret == null) {
			throw new IOException(ZOOKEEPER_AUTH_PRINCIPAL + " is null.");
		}
		if (!ret.equals(zkServerPrincipal)) {
			throw new IOException(ZOOKEEPER_AUTH_PRINCIPAL + " is " + ret + " is not " + zkServerPrincipal + ".");
		}
	}

	public static void setKrb5Config(String krb5ConfFile) throws IOException {
		System.setProperty(JAVA_SECURITY_KRB5_CONF, krb5ConfFile);
		String ret = System.getProperty(JAVA_SECURITY_KRB5_CONF);
		if (ret == null) {
			throw new IOException(JAVA_SECURITY_KRB5_CONF + " is null.");
		}
		if (!ret.equals(krb5ConfFile)) {
			throw new IOException(JAVA_SECURITY_KRB5_CONF + " is " + ret + " is not " + krb5ConfFile + ".");
		}
	}

	public static void securityPrepare(String userKeyFile, String userPrincipal) throws IOException {
		String configPath = System.getProperty("user.dir") + File.separator + "conf" + File.separator;
		String krbFile = configPath + "krb5.conf";
		String userKeyTableFile = configPath + userKeyFile;

		// Windows·���·ָ����滻
		userKeyTableFile = userKeyTableFile.replace("\\", "\\\\");
		krbFile = krbFile.replace("\\", "\\\\");

		LoginUtil.setKrb5Config(krbFile);
		LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
		LoginUtil.setJaasFile(userPrincipal, userKeyTableFile);
	}
	
	private static void writeJaasFile(String jaasPath, String principal, String keytabPath) throws IOException {
		FileWriter writer = new FileWriter(new File(jaasPath));
		try {
			writer.write(getJaasConfContext(principal, keytabPath));
			writer.flush();
		} catch (IOException e) {
			throw new IOException("Failed to create jaas.conf File");
		} finally {
			writer.close();
		}
	}

	private static void deleteJaasFile(String jaasPath) throws IOException {
		File jaasFile = new File(jaasPath);
		if (jaasFile.exists()) {
			if (!jaasFile.delete()) {
				throw new IOException("Failed to delete exists jaas file.");
			}
		}
	}

	private static String getJaasConfContext(String principal, String keytabPath) {
		Module[] allModule = Module.values();
		StringBuilder builder = new StringBuilder();
		for (Module modlue : allModule) {
			builder.append(getModuleContext(principal, keytabPath, modlue));
		}
		return builder.toString();
	}

	private static String getModuleContext(String userPrincipal, String keyTabPath, Module module) {
		StringBuilder builder = new StringBuilder();
		if (IS_IBM_JDK) {
			builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
			builder.append(IBM_LOGIN_MODULE).append(LINE_SEPARATOR);
			builder.append("credsType=both").append(LINE_SEPARATOR);
			builder.append("principal=\"" + userPrincipal + "\"").append(LINE_SEPARATOR);
			builder.append("useKeytab=\"" + keyTabPath + "\"").append(LINE_SEPARATOR);
			builder.append("debug=true;").append(LINE_SEPARATOR);
			builder.append("};").append(LINE_SEPARATOR);
		} else {
			builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
			builder.append(SUN_LOGIN_MODULE).append(LINE_SEPARATOR);
			builder.append("useKeyTab=true").append(LINE_SEPARATOR);
			builder.append("keyTab=\"" + keyTabPath + "\"").append(LINE_SEPARATOR);
			builder.append("principal=\"" + userPrincipal + "\"").append(LINE_SEPARATOR);
			builder.append("useTicketCache=false").append(LINE_SEPARATOR);
			builder.append("storeKey=true").append(LINE_SEPARATOR);
			builder.append("debug=true;").append(LINE_SEPARATOR);
			builder.append("};").append(LINE_SEPARATOR);
		}

		return builder.toString();
	}
}
