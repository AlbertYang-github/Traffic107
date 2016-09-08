package com.yohann.traffic107.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.victor.loading.newton.NewtonCradleLoading;
import com.yohann.traffic107.R;

/**
 * Created by Yohann on 2016/9/8.
 */
public class UploadingView {

    private AlertDialog dialog;

    public UploadingView(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        View uploadView = View.inflate(context, R.layout.upload_dialog, null);
        NewtonCradleLoading loading = (NewtonCradleLoading) uploadView.findViewById(R.id.uploadView);
        loading.start();
        dialog.setView(uploadView);
    }

    public void open() {
        dialog.show();
    }

    public void close() {
        dialog.dismiss();
    }
}
