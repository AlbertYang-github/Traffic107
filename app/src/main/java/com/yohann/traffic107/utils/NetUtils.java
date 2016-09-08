package com.yohann.traffic107.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.bean.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Yohann on 2016/8/28.
 */
public class NetUtils {
    private static final String TAG = "NetUtilsInfo";
    private Context context;
    private AMap aMap;

    public NetUtils(Context context, AMap aMap) {
        this.context = context;
        this.aMap = aMap;
    }

    public void loadMarker() {
        //向服务器获取路况数据
        new Thread() {
            @Override
            public void run() {
                Variable.eventMap.clear();
                BmobQuery<Event> query = new BmobQuery<>();
                query.addWhereEqualTo("isFinished", false);
                query.findObjects(new FindListener<Event>() {
                    @Override
                    public void done(List<Event> list, BmobException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                ViewUtils.show(context, "没有数据可加载");
                            } else {
                                for (Event event : list) {
                                    Variable.eventMap.put(event.getObjectId(), event);
                                }
                                ViewUtils.show(context, "加载了" + Variable.eventMap.size() + "条数据");
                                drawPath();
                            }
                        } else {
                            ViewUtils.show(context, "数据加载失败 " + e.getErrorCode());
                        }
                    }
                });
            }
        }.start();
    }

    public void drawPath() {
        Iterator<Map.Entry<String, Event>> it = Variable.eventMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Event> entry = it.next();
            Event event = entry.getValue();

            Double latitude = event.getLatitude();
            Double longitude = event.getLongitude();

            Double startLatitude = event.getStartLatitude();
            Double startLongitude = event.getStartLongitude();
            Double endLatitude = event.getEndLatitude();
            Double endLongitude = event.getEndLongitude();

            if (latitude != null && longitude != null) {
                MarkerOptions options = new MarkerOptions();
                showMarker(new LatLng(latitude, longitude), R.layout.marker_layout_show, options, event.getTitle());
                aMap.addMarker(options);
            } else {
                new MyRouteSearch(context, aMap).planPath(
                        new LatLonPoint(startLatitude, startLongitude),
                        new LatLonPoint(endLatitude, endLongitude), null);
                MarkerOptions startOptions = new MarkerOptions();
                MarkerOptions endOptions = new MarkerOptions();
                showMarker(new LatLng(startLatitude, startLongitude), R.layout.marker_layout_show, startOptions, event.getTitle());
                showMarker(new LatLng(endLatitude, endLongitude), R.layout.marker_layout_show, endOptions, event.getTitle());
                aMap.addMarker(startOptions);
                aMap.addMarker(endOptions);
            }
        }
    }

    /**
     * 添加时显示的marker
     *
     * @param latLng
     * @param markerOptions
     */
    public void initMarker(LatLng latLng, int layout, MarkerOptions markerOptions) {
        View view = View.inflate(context, layout, null);
        TextView tvLongitude = (TextView) view.findViewById(R.id.tv_longitude);
        TextView tvLatitude = (TextView) view.findViewById(R.id.tv_latitude);

        tvLongitude.setText("经度：" + latLng.longitude);
        tvLatitude.setText("纬度：" + latLng.latitude);

        markerOptions.position(latLng).icon(BitmapDescriptorFactory.fromView(view)).anchor(0, 1).visible(true);
    }

    /**
     * 显示的marker
     *
     * @param latLng
     * @param layout
     * @param markerOptions
     */
    public void showMarker(LatLng latLng, int layout, MarkerOptions markerOptions, String title) {
        View view = View.inflate(context, layout, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_marker);
        tvTitle.setText(title);
        markerOptions.position(latLng).icon(BitmapDescriptorFactory.fromView(view)).anchor(0, 1).visible(true);
    }

    public void addFlagMarker(ArrayList<LatLng> list) {
        for (LatLng latLng : list) {
            Log.i(TAG, "addFlagMarker: ");
            View view = View.inflate(context, R.layout.marker_flag, null);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng).icon(BitmapDescriptorFactory.fromView(view)).anchor(0.5f, 0.5f).visible(true);
            aMap.addMarker(markerOptions);
        }
    }
}
