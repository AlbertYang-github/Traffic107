package com.yohann.traffic107.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yohann.traffic107.R;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.utils.StringUtils;

import me.gujun.android.taggroup.TagGroup;

public class DetailActivity extends BaseActivity {
    private TextView tvTime;
    private TextView tvStartLoc;
    private TextView tvEndLoc;
    private TagGroup labelGroup;
    private TextView tvTitle;
    private TextView tvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        init();
        loadData();
    }

    private void init() {
        tvTime = (TextView) findViewById(R.id.tv_time_user);
        tvStartLoc = (TextView) findViewById(R.id.tv_start_Loc_user);
        tvEndLoc = (TextView) findViewById(R.id.tv_end_loc_user);
        labelGroup = (TagGroup) findViewById(R.id.label_group_user);
        tvTitle = (TextView) findViewById(R.id.tv_title_user);
        tvDesc = (TextView) findViewById(R.id.tv_desc_user);
    }

    private void loadData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        bundle.getString("startLoc");
        bundle.getString("endLoc");
        bundle.getString("labels");
        bundle.getString("title");
        bundle.getString("desc");

        tvTime.setText(bundle.getString("startTime"));
        tvStartLoc.setText(bundle.getString("endLoc"));
        tvEndLoc.setText(bundle.getString("endLoc"));
        tvTitle.setText(bundle.getString("title"));
        tvDesc.setText(bundle.getString("desc"));

        String[] labels = StringUtils.getArrayFromString(bundle.getString("labels"));
        labelGroup.setTags(labels);
    }
}
