package com.yohann.traffic107.common.bean;

import java.io.Serializable;
import java.util.Date;

import cn.bmob.v3.BmobObject;

/**
 * Created by Yohann on 2016/8/25.
 */
public class Event extends BmobObject implements Serializable {
    private String startLocation;
    private String endLocation;
    private String location;
    private Double longitude;
    private Double latitude;
    private Double startLongitude;
    private Double endLongitude;
    private Double startLatitude;
    private Double endLatitude;
    private String labels;
    private String title;
    private String desc;
    private Date startTime;
    private Date endTime;
    private String username;
    private Boolean isFinished;
    private String commStatus;

    public String getCommStatus() {
        return commStatus;
    }

    public void setCommStatus(String commStatus) {
        this.commStatus = commStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getLocation() {
        return location;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public void setEndLongitude(Double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public void setEndLatitude(Double endLatitude) {
        this.endLatitude = endLatitude;
    }

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

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public Double getEndLongitude() {
        return endLongitude;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public Double getEndLatitude() {
        return endLatitude;
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

    public Boolean getFinished() {
        return isFinished;
    }
}
