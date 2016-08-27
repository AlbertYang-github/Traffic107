package com.yohann.traffic107.root;

import android.os.Bundle;

import com.yohann.traffic107.R;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.utils.BmobUtils;

public class CommitActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_root);
        BmobUtils.init(this);

    }
}
