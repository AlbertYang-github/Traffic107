package com.yohann.traffic107.utils;

import android.os.Environment;

import java.io.File;

/**
 * APPPath
 *
 * @author: lenovo
 * @time: 2016/8/13 9:03
 */
public class APPPath {
    public static final String APP_SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "navFor107";
    public static final String SD_TAKEPHOTO_PATH = APP_SD_PATH + File.separator + "photo";
    public static final String SD_RECORD_PATH = APP_SD_PATH + File.separator + "record";
    public static final String SD_TAKEVIDEO_PATH = APP_SD_PATH + File.separator + "video";
}
