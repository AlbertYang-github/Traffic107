package com.yohann.traffic107.common.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Yohann on 2016/9/4.
 */
public class EventPoi extends BmobObject {
    private Double latitude;
    private Double longitude;

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
