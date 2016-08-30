package com.yohann.traffic107.user.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.user.fragment.AddFragment;
import com.yohann.traffic107.user.fragment.AskFragment;
import com.yohann.traffic107.user.fragment.MapFragment;
import com.yohann.traffic107.user.fragment.StatisticsFragment;
import com.yohann.traffic107.utils.ViewUtils;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.exception.BmobException;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivityInfo";

    private FrameLayout flContent;
    private DrawerLayout dlDrawer;
    private NavigationView navigationView;
    private MapFragment mapFragment;
    private FragmentManager fragmentManager;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init() {
        flContent = (FrameLayout) findViewById(R.id.fl_content);
        dlDrawer = (DrawerLayout) findViewById(R.id.dl_drawer);
        navigationView = (NavigationView) findViewById(R.id.nv_slidingmenu);
        View headerView = navigationView.getHeaderView(0);
        tvUsername = (TextView) headerView.findViewById(R.id.tv_username_sliding);
        tvUsername.setText(Variable.userName);

        //创建Fragment
        mapFragment = new MapFragment();

        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fl_content, mapFragment).commit();

        //使用当前用户id连接IM服务器
        BmobIM.connect(Variable.userId, new ConnectListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    ViewUtils.show(HomeActivity.this, Variable.userName + "IM连接成功");
                } else {
                    ViewUtils.show(HomeActivity.this, "异常 " + e.getErrorCode());
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Log.i(TAG, item.getTitle() + "");

                switch (item.getTitle().toString()) {
                    case "路况首页":
                        fragmentManager.beginTransaction().replace(R.id.fl_content, new MapFragment()).commit();
                        break;

                    case "添加路况":
                        fragmentManager.beginTransaction().replace(R.id.fl_content, new AddFragment()).commit();
                        break;

                    case "询问附近":
                        fragmentManager.beginTransaction().replace(R.id.fl_content, new AskFragment()).commit();
                        break;

                    case "数据统计":
                        fragmentManager.beginTransaction().replace(R.id.fl_content, new StatisticsFragment()).commit();
                        break;
                }

                dlDrawer.closeDrawers();
                return true;
            }
        });
    }

    //对外获取DrawerLayout的方法
    public DrawerLayout getDrawerLayout() {
        return dlDrawer;
    }

    public void skipMine(View view) {
        startActivity(new Intent(this, PersonalActivity.class));
        dlDrawer.closeDrawers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BmobIM.getInstance().disConnect();
    }
}
