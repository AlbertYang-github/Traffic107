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
    private String username;
    private Date time;
    private String picUrl;
    private String picRmUrl;
    private String voiceUrl;
    private String voiceRmUrl;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setPicRmUrl(String picRmUrl) {
        this.picRmUrl = picRmUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public void setVoiceRmUrl(String voiceRmUrl) {
        this.voiceRmUrl = voiceRmUrl;
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

    public String getPicUrl() {
        return picUrl;
    }

    public String getPicRmUrl() {
        return picRmUrl;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public String getVoiceRmUrl() {
        return voiceRmUrl;
    }
}
