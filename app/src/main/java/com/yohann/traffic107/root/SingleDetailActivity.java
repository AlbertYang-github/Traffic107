package com.yohann.traffic107.root;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.common.bean.Event;
import com.yohann.traffic107.utils.StringUtils;
import com.yohann.traffic107.utils.ViewUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.gujun.android.taggroup.TagGroup;

public class SingleDetailActivity extends BaseActivity {
    private TextView tvTime;
    private TextView tvLoc;
    private TagGroup labelGroup;
    private TextView tvTitle;
    private TextView tvDesc;
    private ImageView ivPic;
    private Button btnRemove;
    private String picUrl;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_single);
        init();
        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvLoc = (TextView) findViewById(R.id.tv_loc);
        labelGroup = (TagGroup) findViewById(R.id.label_group);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        btnRemove = (Button) findViewById(R.id.btn_remove);
        ivPic = (ImageView) findViewById(R.id.iv_pic);

        //删除该Marker
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SingleDetailActivity.this);
                builder.setTitle("确认删除");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(Variable.eventId)) {
                        } else {
                            Event event = new Event();
                            event.setFinished(true);
                            event.setEndTime(new Date(System.currentTimeMillis()));

                            event.update(Variable.eventId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ViewUtils.show(SingleDetailActivity.this, "删除成功");
                                        Variable.eventMap.remove(Variable.eventMap);
                                        setResult(RESULT_OK, null);
                                        finish();
                                    } else {
                                        ViewUtils.show(SingleDetailActivity.this, "删除失败 " + e.getErrorCode());
                                    }
                                }
                            });
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
    }

    private void loadData() throws IOException {
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
                        }
                    });
                }
            }.start();
        }
        String[] labels = StringUtils.getArrayFromString(bundle.getString("labels"));
        labelGroup.setTags(labels);
    }
}
