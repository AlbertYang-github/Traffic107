package com.yohann.traffic107.root;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yohann.traffic107.R;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.common.bean.DoublePoiEvent;
import com.yohann.traffic107.utils.StringUtils;
import com.yohann.traffic107.utils.ViewUtils;

import java.text.SimpleDateFormat;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.gujun.android.taggroup.TagGroup;

public class DetailCommitActivity extends BaseActivity {
    private TextView tvTime;
    private TextView tvStartLoc;
    private TextView tvEndLoc;
    private TagGroup labelGroup;
    private TextView tvTitle;
    private TextView tvDesc;
    private Button btnApply;
    private Button btnRefuse;
    private TextView tvCommitStatus;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commit_details_root);
        init();
        loadData();
    }

    private void init() {
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvStartLoc = (TextView) findViewById(R.id.tv_start_Loc);
        tvEndLoc = (TextView) findViewById(R.id.tv_end_loc);
        labelGroup = (TagGroup) findViewById(R.id.label_group);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        btnApply = (Button) findViewById(R.id.btn_apply);
        btnRefuse = (Button) findViewById(R.id.btn_refuse);
        tvCommitStatus = (TextView) findViewById(R.id.tv_commit_status_root_msg);

        MyOnClickListener listener = new MyOnClickListener();
        btnRefuse.setOnClickListener(listener);
        btnApply.setOnClickListener(listener);
    }

    private void loadData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        objectId = bundle.getString("objectId");
        DoublePoiEvent doublePoiEvent = (DoublePoiEvent) bundle.getSerializable("doublePoiEvent");
        String[] labels = StringUtils.getArrayFromString(doublePoiEvent.getLabels());
        labelGroup.setTags(labels);
        tvTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(doublePoiEvent.getStartTime()));
        tvStartLoc.setText(doublePoiEvent.getStartLocation());
        tvEndLoc.setText(doublePoiEvent.getEndLocation());
        tvTitle.setText(doublePoiEvent.getTitle());
        tvDesc.setText(doublePoiEvent.getDesc());
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_apply:
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailCommitActivity.this);
                    builder.setTitle("确定应用");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DoublePoiEvent doublePoiEvent = new DoublePoiEvent();

                            doublePoiEvent.update(objectId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ViewUtils.show(DetailCommitActivity.this, "应用成功");
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        ViewUtils.show(DetailCommitActivity.this, "应用失败 " + e.getErrorCode());
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                    break;

                case R.id.btn_refuse:
                    AlertDialog.Builder builderR = new AlertDialog.Builder(DetailCommitActivity.this);
                    builderR.setTitle("确定应用");
                    builderR.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DoublePoiEvent doublePoiEvent = new DoublePoiEvent();
                            doublePoiEvent.update(objectId, new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        ViewUtils.show(DetailCommitActivity.this, "拒绝成功");
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        ViewUtils.show(DetailCommitActivity.this, "拒绝失败 " + e.getErrorCode());
                                    }
                                }
                            });
                        }
                    });
                    builderR.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builderR.show();
                    break;
            }

        }
    }
}
