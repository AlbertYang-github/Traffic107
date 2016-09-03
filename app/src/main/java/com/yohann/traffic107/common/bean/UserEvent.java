package com.yohann.traffic107.common.bean;

import java.io.Serializable;
import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by Yohann on 2016/8/25.
 */
public class UserEvent extends BmobObject implements Serializable {
    private String location;
    private Double longitude;
    private Double latitude;
    private String labels;
    private String title;
    private String desc;
    private Date startTime;
    private Date endTime;
    private String username;
    private Boolean commStatus;

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCommStatus(Boolean commStatus) {
        this.commStatus = commStatus;
    }

    public String getLabels() {
        return labels;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getCommStatus() {
        return commStatus;
    }
}
