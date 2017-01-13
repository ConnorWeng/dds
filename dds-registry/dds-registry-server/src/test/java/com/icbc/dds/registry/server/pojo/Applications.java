package com.icbc.dds.registry.server.pojo;

import java.util.List;

/**
 * Created by ConnorWeng on 2017/1/13.
 */
public class Applications {
    private ApplicationsInner applications;

    public Applications() {}

    public void setApplications(ApplicationsInner applications) {
        this.applications = applications;
    }

    public ApplicationsInner getApplications() {
        return applications;
    }
}
