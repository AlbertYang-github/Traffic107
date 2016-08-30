package com.yohann.traffic107.user.im;

import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;

/**
 * Created by Yohann on 2016/8/30.
 */
public class MessageHandler extends BmobIMMessageHandler {
    public MessageHandler(BmobIMApplication bmobIMApplication) {

    }

    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        //当接收到服务器发来的消息时，此方法被调用
    }

    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
    }
}
