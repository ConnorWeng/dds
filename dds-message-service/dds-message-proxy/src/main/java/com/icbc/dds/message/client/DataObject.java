package com.icbc.dds.message.client;

import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
public class DataObject {
	private boolean success;
	private String message;
    private List<String> dataList;
    
	@JsonCreator
	public DataObject(@JsonProperty("success") boolean success, @JsonProperty("message") String message, @JsonProperty("dataList") List<String> dataList) {
		this.success = success;
		this.message = message;
		this.dataList = dataList;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getDataList() {
		return dataList;
	}

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}
}
