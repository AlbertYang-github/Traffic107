package com.yohann.traffic107.user.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.LatLonPoint;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Constants;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.bean.Event;
import com.yohann.traffic107.user.activity.DestActivity;
import com.yohann.traffic107.user.activity.DetailActivity;
import com.yohann.traffic107.user.activity.HomeActivity;
import com.yohann.traffic107.user.activity.NotiMsgActivity;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.LocUtils;
import com.yohann.traffic107.utils.LocationInit;
import com.yohann.traffic107.utils.NavService;
import com.yohann.traffic107.utils.NetUtils;
import com.yohann.traffic107.utils.ViewUtils;

import java.text.SimpleDateFormat;
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
public class MapFragment extends Fragment implements AMap.OnMarkerClickListener {
    private static final String TAG = "MapFragmentInfo";
    private HomeActivity activity;
    private ImageView ivMenu;
    private MapView mapView;
    private AMap aMap;
    private NetUtils netUtils;
    private LocationInit locationInit;
    private ImageView ivFlush;
    private boolean status = true;
    private ImageView ivNotification;
    private Animation notiMsgAnim;
    private ImageView ivPhone;
    private ImageView ivNavi;
    private LatLonPoint mStartLatLonPoint;
    private LatLonPoint mEndLatLonPoint;
    private Button btnCancelNavi;

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
        ivFlush = (ImageView) view.findViewById(R.id.iv_flush_user);
        ivPhone = (ImageView) view.findViewById(R.id.iv_phone);
        ivNavi = (ImageView) view.findViewById(R.id.iv_navi);
        btnCancelNavi = (Button) view.findViewById(R.id.btn_cancel_navi);
        aMap = mapView.getMap();
        netUtils = new NetUtils(activity, aMap);
        locationInit = new LocationInit(activity, aMap);
        aMap.setOnMarkerClickListener(this);
        ivNotification = (ImageView) view.findViewById(R.id.iv_notification);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        new LocUtils(activity, aMap).getLoc();
        notiMsgAnim = AnimationUtils.loadAnimation(activity, R.anim.plus_zoom_in);

        //向107端提交信息
        ivNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivNotification.startAnimation(notiMsgAnim);
                startActivity(new Intent(activity, NotiMsgActivity.class));
            }
        });

        //刷新
        ivFlush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netUtils.loadMarker();
            }
        });

        //导航
        ivNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到选择目的地Activity
                startActivityForResult(new Intent(activity, DestActivity.class), 1);
            }
        });

        //取消导航
        btnCancelNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aMap.clear();
                netUtils.loadMarker();
                btnCancelNavi.setVisibility(View.INVISIBLE);
                Variable.isNaving = false;
            }
        });

        //拨打电话
        ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivPhone.startAnimation(notiMsgAnim);
                String phoneNumber = "0351-5678107";
                Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                startActivity(intentPhone);
            }
        });

        //启动一个线程轮询（Event数据库的数）
        new Thread() {
            @Override
            public void run() {

                BmobQuery<Event> query = new BmobQuery<>();
                query.addWhereEqualTo("isFinished", false);
                while (status) {
                    query.findObjects(new FindListener<Event>() {
                        @Override
                        public void done(List<Event> list, BmobException e) {
                            if (e == null) {
                                if (list.size() == 0) {
                                } else {
                                    if (Variable.eventMap.size() != list.size()) {
                                        ViewUtils.show(activity, "数据有更新");
                                        aMap.clear();
                                        netUtils.loadMarker();
                                        if (Variable.isNaving) {
                                            startNavi();
                                        }
                                    }
                                }
                            } else {
                            }
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latLng = marker.getPosition();

        Iterator<Map.Entry<String, Event>> it = Variable.eventMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Event> entry = it.next();
            Variable.eventId = entry.getKey();
            Event event = entry.getValue();

            Double latitude = event.getLatitude();
            Double longitude = event.getLongitude();
            if (latitude != null && longitude != null) {
                if (latitude == latLng.latitude && latLng.longitude == longitude) {
                    Intent intent = new Intent(activity, com.yohann.traffic107.user.activity.SingleDetailActivity.class);
                    Bundle bundle = new Bundle();
                    String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(event.getStartTime());
                    bundle.putString("startTime", startTime);
                    bundle.putString("loc", event.getLocation());
                    bundle.putString("labels", event.getLabels());
                    bundle.putString("title", event.getTitle());
                    bundle.putString("desc", event.getDesc());
                    bundle.putString("pic", event.getFileUrl());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, Constants.WATCH);
                    break;
                }
            }

            Double startLatitude = event.getStartLatitude();
            Double startLongitude = event.getStartLongitude();
            Double endLatitude = event.getEndLatitude();
            Double endLongitude = event.getEndLongitude();

            if (startLatitude != null && startLongitude != null && endLatitude != null && endLongitude != null) {
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
                    bundle.putString("pic", event.getFileUrl());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, Constants.WATCH);
                    break;
                }
            }
        }

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        status = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == activity.RESULT_OK) {

            //清屏，从服务器拉取数据
            aMap.clear();
            netUtils.loadMarker();

            Bundle latLonPoint = data.getExtras();
            mStartLatLonPoint = (LatLonPoint) latLonPoint.get("startLatLonPoint");
            mEndLatLonPoint = (LatLonPoint) latLonPoint.get("endLatLonPoint");

            startNavi();
        }
    }

    /**
     * 开始导航
     */
    public void startNavi() {
        //设置已导航的标记
        Variable.isNaving = true;
        btnCancelNavi.setVisibility(View.VISIBLE);

        //服务器查询躲避区域
        new Thread() {
            @Override
            public void run() {
                BmobQuery<Event> query = new BmobQuery<>();
                query.addWhereEqualTo("isFinished", false);
                query.findObjects(new FindListener<Event>() {
                    @Override
                    public void done(List<Event> list, BmobException e) {
                        //创建导航避让区域
                        List<List<LatLonPoint>> avoidAreas = new ArrayList<>();

                        for (Event event : list) {
                            List<LatLonPoint> area = new ArrayList<>();
                            area.add(new LatLonPoint(event.getLatitude1(), event.getLongitude1()));
                            area.add(new LatLonPoint(event.getLatitude2(), event.getLongitude2()));
                            area.add(new LatLonPoint(event.getLatitude3(), event.getLongitude3()));
                            area.add(new LatLonPoint(event.getLatitude4(), event.getLongitude4()));

                            avoidAreas.add(area);
                        }

                        //创建NaviService对象
                        NavService navService = new NavService(activity, aMap, new NavService.OverLay(15, Color.parseColor("#62a90b")));
                        navService.driveRoutePlan(mStartLatLonPoint, mEndLatLonPoint, navService.MODE_DEFAULT, avoidAreas);
                    }
                });
            }
        }.start();
    }
}
