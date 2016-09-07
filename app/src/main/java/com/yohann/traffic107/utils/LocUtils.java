package com.yohann.traffic107.utils;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.yohann.traffic107.common.Constants.Variable;

/**
 * Created by Yohann on 2016/8/29.
 */
public class LocUtils implements AMapLocationListener {
    private static final String TAG = "LocUtilsInfo";
    private Context context;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption;


    public LocUtils(Context context, AMap aMap) {
        this.context = context;
        this.aMap = aMap;
    }

    public void getLoc() {
        locationClientOption = new AMapLocationClientOption();
        locationClient = new AMapLocationClient(context);
        locationClient.setLocationListener(this);
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setInterval(10000);
        locationClientOption.setOnceLocation(false);
        locationClientOption.setNeedAddress(true);
        locationClientOption.setWifiActiveScan(true);
        locationClientOption.setMockEnable(false);
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Variable.myLatitude = aMapLocation.getLatitude();
        Variable.myLongitude = aMapLocation.getLongitude();
    }
}
