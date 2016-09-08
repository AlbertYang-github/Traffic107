package com.yohann.traffic107.user.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mingle.widget.LoadingView;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.activity.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PersonDetailActivity extends BaseActivity {

    private static final String TAG = "PersonDetailActivityInfo";
    private TextView tvTime;
    private TextView tvLoc;
    private ImageView ivPic;
    private ImageView ivVoice;
    private String picUrl;
    private Bitmap bitmap;
    private String voiceUrl;
    private MediaPlayer mediaPlayer;
    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        init();
        loadData();
    }

    private void init() {
        tvTime = (TextView) findViewById(R.id.tv_time_user_msg);
        tvLoc = (TextView) findViewById(R.id.tv_loc_user_msg);
        ivPic = (ImageView) findViewById(R.id.iv_pic_user_msg);
        ivVoice = (ImageView) findViewById(R.id.iv_voice_user_msg);
        loadingView = (LoadingView) findViewById(R.id.loadingView);

        ivVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(voiceUrl);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    private void loadData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        tvTime.setText(bundle.getString("time"));
        tvLoc.setText(bundle.getString("loc"));

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

        voiceUrl = bundle.getString("voice");
        if (voiceUrl != null) {
            ivVoice.setVisibility(View.VISIBLE);
        }
    }
}
