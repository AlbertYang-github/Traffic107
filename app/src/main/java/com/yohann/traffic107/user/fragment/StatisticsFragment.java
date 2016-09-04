package com.yohann.traffic107.user.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.bean.Event;
import com.yohann.traffic107.user.activity.HomeActivity;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.LocationInit;
import com.yohann.traffic107.utils.NetUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Yohann on 2016/8/28.
 */
public class StatisticsFragment extends Fragment {
    private static final String TAG = "StatisticsFragmentInfo";
    private HomeActivity activity;
    private ImageView ivMenu;
    private MapView mapView;
    private AMap aMap;
    private LocationInit locationInit;
    private ArrayList<LatLng> poiList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (HomeActivity) getActivity();
        BmobUtils.init(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, null);
        init(view);
        mapView.onCreate(savedInstanceState);
        locationInit.init();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void init(View view) {
        ivMenu = (ImageView) view.findViewById(R.id.iv_menu_statistics);
        mapView = (MapView) view.findViewById(R.id.map_statistics);
        aMap = mapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_NIGHT);
        locationInit = new LocationInit(activity, aMap);
        poiList = new ArrayList<>();
        //查询数据库中的所有经纬度，以点的形式显示在地图上
        new Thread() {
            @Override
            public void run() {
                BmobQuery<Event> query = new BmobQuery<>();
                query.findObjects(new FindListener<Event>() {
                    @Override
                    public void done(List<Event> list, BmobException e) {
                        for (Event event : list) {
                            if (event.getLatitude() != null) {
                                poiList.add(new LatLng(event.getLatitude(), event.getLongitude()));
                            } else {
                                poiList.add(new LatLng(event.getStartLatitude(), event.getStartLongitude()));
                                poiList.add(new LatLng(event.getEndLatitude(), event.getEndLatitude()));
                            }
                        }
                        Log.i(TAG, "done: " + poiList.size());
                        NetUtils netUtils = new NetUtils(activity, aMap);
                        netUtils.addFlagMarker(poiList);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //给ivMenu添加点击事件
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getDrawerLayout().openDrawer(Gravity.LEFT);
            }
        });
    }
}
