package com.icbc.dds.rpc.pojo;

import java.util.List;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
public class DataObject {
    private int intValue;
    private String stringValue;
    private boolean boolValue;
    private List<String> stringValueList;
    private DetailsObject deftailsObject;
    private List<DetailsObject> detailsObjectList;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public boolean isBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }

    public List<String> getStringValueList() {
        return stringValueList;
    }

    public void setStringValueList(List<String> stringValueList) {
        this.stringValueList = stringValueList;
    }

    public DetailsObject getDeftailsObject() {
        return deftailsObject;
    }

    public void setDeftailsObject(DetailsObject deftailsObject) {
        this.deftailsObject = deftailsObject;
    }

    public List<DetailsObject> getDetailsObjectList() {
        return detailsObjectList;
    }

    public void setDetailsObjectList(List<DetailsObject> detailsObjectList) {
        this.detailsObjectList = detailsObjectList;
    }
}
