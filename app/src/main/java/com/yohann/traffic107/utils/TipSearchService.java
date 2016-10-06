package com.yohann.traffic107.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * TipSearchService
 *
 * @author: lenovo
 * @time: 2016/10/6 9:13
 */
public class TipSearchService {
    private Context mContext;
    private OnInputtipsListener mOnInputtipsListener;

    /**
     * 关键字搜索服务构造器
     *
     * @param context
     */
    public TipSearchService(@NonNull Context context) {
        this.mContext = context;
    }

    /**
     * 获取输入地名的提示信息
     *
     * @param input
     * @param listener
     */
    public void getKeyTipList(@NonNull String input, @NonNull OnInputtipsListener listener) {
        this.mOnInputtipsListener = listener;
        String newText = input.trim();
        if (!TextUtils.isEmpty(newText)) {
            //设置查询的城市
            InputtipsQuery inputtipsQuery = new InputtipsQuery(newText, "太原");
            Inputtips inputtips = new Inputtips(mContext, inputtipsQuery);
            inputtips.setInputtipsListener(new InputtipsListener());
            inputtips.requestInputtipsAsyn();
        }
    }

    /**
     * 关键字解析成功后回调
     */
    public interface OnInputtipsListener {
        void onInputtipsListener(List<KeyTip> keyTips);
    }

    /**
     * 地名关键字解析返回的对象
     */
    public static class KeyTip {
        private String placeName;
        private LatLonPoint latLonPoint;

        public KeyTip(String placeName, LatLonPoint latLonPoint) {
            this.placeName = placeName;
            this.latLonPoint = latLonPoint;
        }

        /**
         * 获取地名
         *
         * @return
         */
        public String getPlaceName() {
            return placeName;
        }

        /**
         * 获取经纬度
         *
         * @return
         */
        public LatLonPoint getLatLonPoint() {
            return latLonPoint;
        }
    }

    private class InputtipsListener implements Inputtips.InputtipsListener {

        @Override
        public void onGetInputtips(List<Tip> tips, int i) {
            //当进行输入字符的时候进行提示的信息
            if (i == 1000) {// 正确返回
                List<KeyTip> keyTips = new ArrayList<>();
                for (int j = 0; j < tips.size(); j++) {
                    KeyTip keyTip = new KeyTip(tips.get(j).getName(), tips.get(j).getPoint());
                    keyTips.add(keyTip);
                }
                mOnInputtipsListener.onInputtipsListener(keyTips);
            } else {
                Toast.makeText(mContext, "没有查询到结果", Toast.LENGTH_SHORT).show();
                new RuntimeException("GetInputtips  失败,错误码：" + i);
            }
        }
    }
}
