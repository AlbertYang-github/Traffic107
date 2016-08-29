package com.yohann.traffic107.user.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Constants;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.bean.Event;
import com.yohann.traffic107.user.activity.DetailActivity;
import com.yohann.traffic107.user.activity.HomeActivity;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.LocationInit;
import com.yohann.traffic107.utils.NetUtils;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Yohann on 2016/8/28.
 */
public class MapFragment extends Fragment implements AMap.OnMarkerClickListener {
    private HomeActivity activity;
    private ImageView ivMenu;
    private MapView mapView;
    private AMap aMap;
    private NetUtils netUtils;
    private LocationInit locationInit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (HomeActivity) getActivity();
        BmobUtils.init(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        init(view);
        mapView.onCreate(savedInstanceState);
        netUtils.loadMarker();
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

    /**
     * 初始化View
     *
     * @param view
     */
    private void init(View view) {
        ivMenu = (ImageView) view.findViewById(R.id.iv_menu_home);
        mapView = (MapView) view.findViewById(R.id.map_home);
        aMap = mapView.getMap();
        netUtils = new NetUtils(activity, aMap);
        locationInit = new LocationInit(activity, aMap);
        aMap.setOnMarkerClickListener(this);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latLng = marker.getPosition();

        Iterator<Map.Entry<String, Event>> it = Variable.eventMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Event> entry = it.next();
            Variable.eventId = entry.getKey();
            Event event = entry.getValue();
            Double startLatitude = event.getStartLatitude();
            Double startLongitude = event.getStartLongitude();
            Double endLatitude = event.getEndLatitude();
            Double endLongitude = event.getEndLongitude();

            if ((latLng.latitude == startLatitude && latLng.longitude == startLongitude)
                    || (latLng.latitude == endLatitude && latLng.longitude == endLongitude)) {

                Intent intent = new Intent(activity, DetailActivity.class);
                Bundle bundle = new Bundle();
                String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getStartTime());
                bundle.putString("startTime", startTime);
                bundle.putString("startLoc", event.getStartLocation());
                bundle.putString("endLoc", event.getEndLocation());
                bundle.putString("labels", event.getLabels());
                bundle.putString("title", event.getTitle());
                bundle.putString("desc", event.getDesc());
                intent.putExtras(bundle);
                startActivityForResult(intent, Constants.WATCH);
                break;
            }
        }

        return true;
    }
}
