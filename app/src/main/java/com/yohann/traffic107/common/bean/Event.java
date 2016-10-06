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
    private String fileUrl;
    private String rmUrl;

    private Double latitude1;
    private Double longitude1;
    private Double latitude2;
    private Double longitude2;
    private Double latitude3;
    private Double longitude3;
    private Double latitude4;
    private Double longitude4;

    public Double getLatitude1() {
        return latitude1;
    }

    public void setLatitude1(Double latitude1) {
        this.latitude1 = latitude1;
    }

    public Double getLongitude1() {
        return longitude1;
    }

    public void setLongitude1(Double longitude1) {
        this.longitude1 = longitude1;
    }

    public Double getLatitude2() {
        return latitude2;
    }

    public void setLatitude2(Double latitude2) {
        this.latitude2 = latitude2;
    }

    public Double getLongitude2() {
        return longitude2;
    }

    public void setLongitude2(Double longitude2) {
        this.longitude2 = longitude2;
    }

    public Double getLatitude3() {
        return latitude3;
    }

    public void setLatitude3(Double latitude3) {
        this.latitude3 = latitude3;
    }

    public Double getLongitude3() {
        return longitude3;
    }

    public void setLongitude3(Double longitude3) {
        this.longitude3 = longitude3;
    }

    public Double getLatitude4() {
        return latitude4;
    }

    public void setLatitude4(Double latitude4) {
        this.latitude4 = latitude4;
    }

    public Double getLongitude4() {
        return longitude4;
    }

    public void setLongitude4(Double longitude4) {
        this.longitude4 = longitude4;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public String getRmUrl() {
        return rmUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setRmUrl(String rmUrl) {
        this.rmUrl = rmUrl;
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
