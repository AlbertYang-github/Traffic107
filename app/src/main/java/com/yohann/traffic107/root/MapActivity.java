package com.yohann.traffic107.root;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Constants;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.common.bean.Event;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.LocationInit;
import com.yohann.traffic107.utils.NetUtils;
import com.yohann.traffic107.utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MapActivity extends BaseActivity implements AMap.OnMarkerClickListener {

    private static final String TAG = "MapActivityInfo";

    private MapView mapView;
    private RelativeLayout rlBtnDouble;
    private RelativeLayout rlBtnSingle;
    private Animation animOpen;
    private AMap aMap;
    private ImageView ivFlush;
    private String selectionMode;

    private ImageView ivPlus;
    private TextView tvUserCommit;
    private TextView tvAddSingle;
    private TextView tvAddDouble;
    private RadioButton rbtnStart;
    private RadioButton rbtnEnd;
    private ImageView ivBackSingle;
    private ImageView ivBackDouble;
    private ImageView ivFinishSingle;
    private ImageView ivFinishDouble;

    private String location;
    private Double longitude;
    private Double latitude;

    private boolean btnStatus;
    private boolean menuStatus;

    private Double startLongitude;
    private Double endLongitude;
    private Double startLatitude;
    private Double endLatitude;

    ArrayList<Marker> markerStartList = new ArrayList<>();
    ArrayList<Marker> markerEndList = new ArrayList<>();
    ArrayList<Marker> markerArea = new ArrayList<>();
    private Marker marker;
    private Marker startMarker;
    private Marker endMarker;
    private NetUtils netUtils;
    private LocationInit locationInit;
    private Animation animEditOpen;
    private Animation animEditClose;
    private Animation animMenuOpen;
    private Animation animMenuClose;
    private Animation animMenuShowOpen;
    private Animation animMenuShowClose;
    private Animation animEditSingleOpen;
    private Animation animEditSingleClose;
    private String editflag;
    private boolean isDouble;
    private CheckBox cbArea;
    private RadioButton rbtnArea;

    private Double latitude1;
    private Double longitude1;
    private Double latitude2;
    private Double longitude2;
    private Double latitude3;
    private Double longitude3;
    private Double latitude4;
    private Double longitude4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_root);
        BmobUtils.init(this);
        init();
        mapView.onCreate(savedInstanceState);
        netUtils.loadMarker();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Variable.eventMap.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 初始化View
     */
    private void init() {
        mapView = (MapView) findViewById(R.id.map);
        ivPlus = (ImageView) findViewById(R.id.iv_plus);
        rbtnStart = (RadioButton) findViewById(R.id.rbtn_start);
        rbtnEnd = (RadioButton) findViewById(R.id.rbtn_end);
        ivFinishSingle = (ImageView) findViewById(R.id.iv_finish_single);
        ivFinishDouble = (ImageView) findViewById(R.id.iv_finish_double);
        ivBackSingle = (ImageView) findViewById(R.id.iv_back_add_single);
        ivBackDouble = (ImageView) findViewById(R.id.iv_back_add_double);
        rlBtnSingle = (RelativeLayout) findViewById(R.id.rl_btn_single);
        rlBtnDouble = (RelativeLayout) findViewById(R.id.rl_btn_double);
        cbArea = (CheckBox) findViewById(R.id.cb_area);
        rbtnArea = (RadioButton) findViewById(R.id.rbtn_area);

        aMap = mapView.getMap();
        animOpen = AnimationUtils.loadAnimation(this, R.anim.plus_open_anim);
        animEditOpen = AnimationUtils.loadAnimation(this, R.anim.edit_double_open);
        animEditClose = AnimationUtils.loadAnimation(this, R.anim.edit_double_close);
        animMenuOpen = AnimationUtils.loadAnimation(this, R.anim.menu_rotate_open);
        animMenuClose = AnimationUtils.loadAnimation(this, R.anim.menu_rotate_close);
        animMenuShowOpen = AnimationUtils.loadAnimation(this, R.anim.menu_show_open);
        animMenuShowClose = AnimationUtils.loadAnimation(this, R.anim.menu_show_close);
        animEditSingleOpen = AnimationUtils.loadAnimation(this, R.anim.edit_single_open);
        animEditSingleClose = AnimationUtils.loadAnimation(this, R.anim.edit_single_close);
        ivFlush = (ImageView) findViewById(R.id.iv_flush_root);
        tvUserCommit = (TextView) findViewById(R.id.tv_user_commit);
        tvAddSingle = (TextView) findViewById(R.id.tv_add_single);
        tvAddDouble = (TextView) findViewById(R.id.tv_add_double);

        MyOnClickListener listener = new MyOnClickListener();
        ivPlus.setOnClickListener(listener);
        rbtnStart.setOnClickListener(listener);
        rbtnEnd.setOnClickListener(listener);
        ivFinishSingle.setOnClickListener(listener);
        ivFinishDouble.setOnClickListener(listener);
        ivFinishDouble.setOnClickListener(listener);
        ivFlush.setOnClickListener(listener);
        tvUserCommit.setOnClickListener(listener);
        tvAddSingle.setOnClickListener(listener);
        tvAddDouble.setOnClickListener(listener);
        ivBackSingle.setOnClickListener(listener);
        ivBackDouble.setOnClickListener(listener);

        aMap.setOnMarkerClickListener(this);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        netUtils = new NetUtils(this, aMap);
        locationInit = new LocationInit(this, aMap);
        locationInit.init();

        //地图长按监听
        aMap.setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                if ("double".equals(selectionMode)) {

                    //起始点
                    if (rbtnStart.isChecked()) {
                        startLongitude = latLng.longitude;
                        startLatitude = latLng.latitude;

                        netUtils.initMarker(latLng, R.layout.marker_start_layout, markerOptions);
                        if (markerStartList.size() == 0) {
                            //第一次添加
                            startMarker = aMap.addMarker(markerOptions);
                            markerStartList.add(startMarker);
                        } else {
                            //非第一次添加
                            startMarker = aMap.addMarker(markerOptions);
                            Marker markerOld = markerStartList.get(0);
                            markerOld.remove();
                            markerStartList.clear();
                            markerStartList.add(startMarker);
                        }
                    }

                    //终止点
                    if (rbtnEnd.isChecked()) {
                        endLongitude = latLng.longitude;
                        endLatitude = latLng.latitude;

                        netUtils.initMarker(latLng, R.layout.marker_end_layout, markerOptions);
                        if (markerEndList.size() == 0) {
                            endMarker = aMap.addMarker(markerOptions);
                            markerEndList.add(startMarker);
                        } else {
                            //非第一次添加
                            endMarker = aMap.addMarker(markerOptions);
                            Marker markerOld = markerEndList.get(0);
                            markerOld.remove();
                            markerEndList.clear();
                            markerEndList.add(endMarker);
                        }
                    }

                }

                if ("single".equals(selectionMode)) {
                    longitude = latLng.longitude;
                    latitude = latLng.latitude;
                    if (marker == null) {
                        netUtils.initMarker(latLng, R.layout.marker_single_layout, markerOptions);
                        marker = aMap.addMarker(markerOptions);
                    } else {
                        marker.remove();
                        netUtils.initMarker(latLng, R.layout.marker_single_layout, markerOptions);
                        marker = aMap.addMarker(markerOptions);
                    }
                }
            }
        });

        //点击地图添加导航时的避让区域的矩形的四个顶点
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (cbArea.isChecked() || rbtnArea.isChecked()) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    Marker marker = null;
                    switch (markerArea.size()) {
                        case 0:
                            markerOptions.position(latLng);
                            marker = aMap.addMarker(markerOptions);
                            markerArea.add(marker);
                            latitude1 = latLng.latitude;
                            longitude1 = latLng.longitude;
                            break;

                        case 1:
                            markerOptions.position(latLng);
                            marker = aMap.addMarker(markerOptions);
                            markerArea.add(marker);
                            latitude2 = latLng.latitude;
                            longitude2 = latLng.longitude;
                            break;

                        case 2:
                            markerOptions.position(latLng);
                            marker = aMap.addMarker(markerOptions);
                            markerArea.add(marker);
                            latitude3 = latLng.latitude;
                            longitude3 = latLng.longitude;
                            break;

                        case 3:
                            markerOptions.position(latLng);
                            marker = aMap.addMarker(markerOptions);
                            markerArea.add(marker);
                            latitude4 = latLng.latitude;
                            longitude4 = latLng.longitude;
                            break;

                        default:
                            break;
                    }

                    Log.i(TAG, "latitude1=" + latitude1 + "  longitude1=" + longitude1);
                    Log.i(TAG, "latitude2=" + latitude2 + "  longitude2=" + longitude2);
                    Log.i(TAG, "latitude3=" + latitude3 + "  longitude3=" + longitude3);
                    Log.i(TAG, "latitude4=" + latitude4 + "  longitude4=" + longitude4);
                }
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
                    Log.i(TAG, "onMarkerClick: single");
                    Intent intent = new Intent(MapActivity.this, SingleDetailActivity.class);
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
                    Log.i(TAG, "onMarkerClick: double");
                    Intent intent = new Intent(MapActivity.this, DoubleDetailActivity.class);
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

    /**
     * 按钮监听
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.iv_plus:
                    if (menuStatus) {
                        //执行关闭
                        ivPlus.startAnimation(animMenuClose);
                        tvUserCommit.setVisibility(View.INVISIBLE);
                        tvAddSingle.setVisibility(View.INVISIBLE);
                        tvAddDouble.setVisibility(View.INVISIBLE);
                        tvUserCommit.startAnimation(animMenuShowClose);
                        tvAddSingle.startAnimation(animMenuShowClose);
                        tvAddDouble.startAnimation(animMenuShowClose);
                        mapView.setAlpha(1);
                        menuStatus = false;
                    } else {
                        //执行打开
                        ivPlus.startAnimation(animMenuOpen);
                        tvUserCommit.setVisibility(View.VISIBLE);
                        tvAddSingle.setVisibility(View.VISIBLE);
                        tvAddDouble.setVisibility(View.VISIBLE);
                        tvUserCommit.startAnimation(animMenuShowOpen);
                        tvAddSingle.startAnimation(animMenuShowOpen);
                        tvAddDouble.startAnimation(animMenuShowOpen);
                        mapView.setAlpha(0.5f);
                        menuStatus = true;
                    }
                    break;

                case R.id.tv_user_commit:
                    Log.i(TAG, "跳转用户提交数据菜单");
                    //执行关闭
                    ivPlus.startAnimation(animMenuClose);
                    tvUserCommit.setVisibility(View.INVISIBLE);
                    tvAddSingle.setVisibility(View.INVISIBLE);
                    tvAddDouble.setVisibility(View.INVISIBLE);
                    tvUserCommit.startAnimation(animMenuShowClose);
                    tvAddSingle.startAnimation(animMenuShowClose);
                    tvAddDouble.startAnimation(animMenuShowClose);
                    mapView.setAlpha(1);
                    menuStatus = false;
                    startActivity(new Intent(MapActivity.this, CommitActivity.class));
                    break;

                case R.id.tv_add_single:
                    //执行关闭
                    ivPlus.startAnimation(animMenuClose);
                    tvUserCommit.setVisibility(View.INVISIBLE);
                    tvAddSingle.setVisibility(View.INVISIBLE);
                    tvAddDouble.setVisibility(View.INVISIBLE);
                    tvUserCommit.startAnimation(animMenuShowClose);
                    tvAddSingle.startAnimation(animMenuShowClose);
                    tvAddDouble.startAnimation(animMenuShowClose);
                    mapView.setAlpha(1);
                    menuStatus = false;

                    rlBtnSingle.startAnimation(animEditSingleOpen);
                    rlBtnSingle.setVisibility(View.VISIBLE);

                    //设置选择模式
                    selectionMode = "single";
                    break;

                case R.id.tv_add_double:
                    //执行关闭
                    ivPlus.startAnimation(animMenuClose);
                    tvUserCommit.setVisibility(View.INVISIBLE);
                    tvAddSingle.setVisibility(View.INVISIBLE);
                    tvAddDouble.setVisibility(View.INVISIBLE);
                    tvUserCommit.startAnimation(animMenuShowClose);
                    tvAddSingle.startAnimation(animMenuShowClose);
                    tvAddDouble.startAnimation(animMenuShowClose);
                    mapView.setAlpha(1);
                    menuStatus = false;

                    rlBtnDouble.startAnimation(animEditOpen);
                    rlBtnDouble.setVisibility(View.VISIBLE);
                    //清屏
                    if (markerStartList.size() > 0 || markerEndList.size() > 0) {
                        markerStartList.get(0).remove();
                        markerStartList.clear();
                        markerEndList.get(0).remove();
                        markerEndList.clear();

                        startLongitude = null;
                        endLongitude = null;
                        startLatitude = null;
                        endLatitude = null;

                        //刷新地图
                        mapView.invalidate();
                    }

                    selectionMode = "double";
                    break;

                case R.id.iv_back_add_single:
                    rlBtnSingle.startAnimation(animEditSingleClose);
                    rlBtnSingle.setVisibility(View.INVISIBLE);
                    selectionMode = null;
                    break;

                case R.id.iv_back_add_double:
                    rlBtnDouble.startAnimation(animEditClose);
                    rlBtnDouble.setVisibility(View.INVISIBLE);
                    selectionMode = null;
                    break;

                case R.id.rbtn_start:
                    //添加起始点
                    editflag = "start";
                    break;

                case R.id.rbtn_end:
                    //添加终止点
                    editflag = "end";
                    break;

                case R.id.iv_finish_single:
                    if (longitude == null || latitude == null) {
                        ViewUtils.show(MapActivity.this, "请添加一个位置");
                    } else {
                        rlBtnSingle.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(MapActivity.this, SingleEventEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("longitude", longitude);
                        bundle.putDouble("latitude", latitude);

                        bundle.putDouble("latitude1", latitude1);
                        bundle.putDouble("longitude1", longitude1);
                        bundle.putDouble("latitude2", latitude2);
                        bundle.putDouble("longitude2", longitude2);
                        bundle.putDouble("latitude3", latitude3);
                        bundle.putDouble("longitude3", longitude3);
                        bundle.putDouble("latitude4", latitude4);
                        bundle.putDouble("longitude4", longitude4);

                        intent.putExtras(bundle);
                        startActivityForResult(intent, Constants.D_EDIT);
                    }
                    break;

                case R.id.iv_finish_double:
                    if (startLongitude == null || startLatitude == null || endLongitude == null || endLatitude == null) {
                        ViewUtils.show(MapActivity.this, "请选择起始点和终止点");
                    } else {
                        rlBtnDouble.setVisibility(View.INVISIBLE);
                        btnStatus = false;

                        Intent intent = new Intent(MapActivity.this, DoubleEventEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("startLongitude", startLongitude);
                        bundle.putDouble("startLatitude", startLatitude);
                        bundle.putDouble("endLongitude", endLongitude);
                        bundle.putDouble("endLatitude", endLatitude);

                        bundle.putDouble("latitude1", latitude1);
                        bundle.putDouble("longitude1", longitude1);
                        bundle.putDouble("latitude2", latitude2);
                        bundle.putDouble("longitude2", longitude2);
                        bundle.putDouble("latitude3", latitude3);
                        bundle.putDouble("longitude3", longitude3);
                        bundle.putDouble("latitude4", latitude4);
                        bundle.putDouble("longitude4", longitude4);

                        intent.putExtras(bundle);
                        startActivityForResult(intent, Constants.EDIT);
                    }
                    break;

                case R.id.iv_flush_root:
                    aMap.clear();
                    netUtils.loadMarker();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.EDIT:
                if (resultCode == RESULT_OK) {
                    aMap.clear();
                    netUtils.loadMarker();
                }
                break;

            case Constants.WATCH:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "onActivityResult:");
                    aMap.clear();
                    netUtils.loadMarker();
                }
                break;

            case Constants.D_EDIT:
                if (resultCode == RESULT_OK) {
                    aMap.clear();
                    netUtils.loadMarker();
                }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("温馨提示：")
                .setMessage("您确定要退出？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MapActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("取消", null);
        builder.create().show();
    }
}

