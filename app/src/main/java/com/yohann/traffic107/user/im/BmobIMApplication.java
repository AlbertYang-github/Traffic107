package com.yohann.traffic107.user.im;

import android.app.Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;

/**
 * Created by Yohann on 2016/8/30.
 */
public class BmobIMApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            //NewIM初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new MessageHandler(this));
        }
    }

    public String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
