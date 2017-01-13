package com.icbc.dds.registry.server.pojo;

import java.util.List;

/**
 * Created by ConnorWeng on 2017/1/13.
 */
public class ApplicationsInner {
    private String versions__delta;
    private String apps__hashcode;
    private List<Object> application;

    public ApplicationsInner() {
    }

    public String getVersions__delta() {
        return versions__delta;
    }

    public void setVersions__delta(String versions__delta) {
        this.versions__delta = versions__delta;
    }

    public String getApps__hashcode() {
        return apps__hashcode;
    }

    public void setApps__hashcode(String apps__hashcode) {
        this.apps__hashcode = apps__hashcode;
    }

    public List<Object> getApplication() {
        return application;
    }

    public void setApplication(List<Object> application) {
        this.application = application;
    }
}
