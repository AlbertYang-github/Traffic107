package com.yohann.traffic107.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.root.EditActivity;
import com.yohann.traffic107.utils.BmobUtils;


public class MapActivity extends BaseActivity {

    private static final String TAG = "MapActivityInfo";

    private MapView mapView;
    private RadioGroup rg;
    private RadioButton rbtnStart;
    private RadioButton rbtnEnd;
    private TextView tvFinish;
    private RelativeLayout rlBtn;
    private ImageView ivMenu;
    private ImageView ivBack;
    private ImageView ivEdit;
    private Animation animOpen;
    private AMap aMap;

    private boolean editStatus;
    private boolean menuStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_user);
        BmobUtils.init(this);
        init();
        mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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

    /**
     * 初始化View
     */
    private void init() {
        mapView = (MapView) findViewById(R.id.map);
        rg = (RadioGroup) findViewById(R.id.rg);
        rbtnStart = (RadioButton) findViewById(R.id.rbtn_start);
        rbtnEnd = (RadioButton) findViewById(R.id.rbtn_end);
        tvFinish = (TextView) findViewById(R.id.tv_finish);
        rlBtn = (RelativeLayout) findViewById(R.id.rl_btn);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivEdit = (ImageView) findViewById(R.id.iv_edit);

        animOpen = AnimationUtils.loadAnimation(this, R.anim.plus_open_anim);

        MyOnClickListener listener = new MyOnClickListener();
        rbtnStart.setOnClickListener(listener);
        rbtnEnd.setOnClickListener(listener);
        tvFinish.setOnClickListener(listener);
        ivMenu.setOnClickListener(listener);
        ivBack.setOnClickListener(listener);
        ivEdit.setOnClickListener(listener);

        aMap = mapView.getMap();
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
    }

    /**
     * 按钮监听
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.iv_menu:
                    ivMenu.startAnimation(animOpen);
                    if (menuStatus == false) {
                        //弹出菜单
                        ivEdit.setVisibility(View.VISIBLE);
                        menuStatus = true;
                    } else {
                        ivEdit.setVisibility(View.INVISIBLE);
                        menuStatus = false;
                    }

                    break;

                case R.id.iv_edit:
                    //编辑路况信息
                    ivMenu.startAnimation(animOpen);
                    rlBtn.setVisibility(View.VISIBLE);
                    editStatus = true;
                    ivEdit.setVisibility(View.INVISIBLE);
                    break;

                case R.id.rbtn_start:
                    break;

                case R.id.rbtn_end:
                    break;

                case R.id.tv_finish:
                    startActivity(new Intent(MapActivity.this, EditActivity.class));
                    rlBtn.setVisibility(View.INVISIBLE);
                    editStatus = false;
                    break;

                case R.id.iv_back:
                    rlBtn.setVisibility(View.INVISIBLE);
                    editStatus = false;
                    break;
            }
        }
    }
}
