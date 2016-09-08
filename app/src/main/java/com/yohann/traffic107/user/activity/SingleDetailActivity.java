package com.yohann.traffic107.user.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingle.widget.LoadingView;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.gujun.android.taggroup.TagGroup;

public class SingleDetailActivity extends BaseActivity {
    private TextView tvTime;
    private TextView tvLoc;
    private TagGroup labelGroup;
    private TextView tvTitle;
    private TextView tvDesc;
    private ImageView ivPic;
    private String picUrl;
    private Bitmap bitmap;
    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user_single);
        init();
        loadData();
    }

    private void init() {
        tvTime = (TextView) findViewById(R.id.tv_time_commit_user);
        tvLoc = (TextView) findViewById(R.id.tv_loc_user);
        labelGroup = (TagGroup) findViewById(R.id.label_group_user);
        tvTitle = (TextView) findViewById(R.id.tv_title_user);
        tvDesc = (TextView) findViewById(R.id.tv_desc_user);
        ivPic = (ImageView) findViewById(R.id.iv_pic);
        loadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    private void loadData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        tvTime.setText(bundle.getString("startTime"));
        tvLoc.setText(bundle.getString("loc"));
        tvTitle.setText(bundle.getString("title"));
        tvDesc.setText(bundle.getString("desc"));
        picUrl = bundle.getString("pic");
        if (picUrl != null) {
            new Thread() {
                @Override
                public void run() {
                    ivPic.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.VISIBLE);
                    URL url = null;
                    try {
                        url = new URL(picUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ivPic.post(new Runnable() {
                        @Override
                        public void run() {
                            ivPic.setImageBitmap(bitmap);
                            loadingView.setVisibility(View.GONE);
                        }
                    });
                }
            }.start();
        }
        String[] labels = StringUtils.getArrayFromString(bundle.getString("labels"));
        labelGroup.setTags(labels);
    }
}
