package com.yohann.traffic107.user.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.common.bean.UserEvent;
import com.yohann.traffic107.utils.APPPath;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.MediaPlayerManger;
import com.yohann.traffic107.utils.RecordManager;
import com.yohann.traffic107.utils.UploadingView;
import com.yohann.traffic107.utils.ViewUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class NotiMsgActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "NotiMsgActivityInfo";
    private TextView tvTime;
    private EditText etLoc;
    private String address;
    private GeocodeSearch geocodeSearch;
    private ImageView ivAddPic;
    private ImageView ivPic;
    private Bitmap bitmap;
    private String roadMediaOutDir;
    private String recordOutputPath;
    private File recordOutputFile;
    private ImageView ivRecord;
    private RecordManager recordManager;
    private boolean isRecord;
    private Animation recordAnimOpen;
    private Animation recordAnimClose;
    private TextView tvVoice;
    private ImageView ivVoice;
    private MediaPlayerManger mediaPlayerManger;
    private Button btnFinish;
    private String pictureOutputPath;
    private File pictureOutputFile;
    private String picUrl;
    private String picRmUrl;
    private String voiceUrl;
    private String voiceRmUrl;
    private String time;
    private UploadingView uploadingView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            uploadingView.close();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti_msg);
        BmobUtils.init(this);
        init();
        initMediaPath();
    }

    private void init() {
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(new AddressListener());
        tvTime = (TextView) findViewById(R.id.tv_time);
        time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        tvTime.setText(time);
        etLoc = (EditText) findViewById(R.id.et_loc);
        getAddress(Variable.myLatitude, Variable.myLongitude);
        ivAddPic = (ImageView) findViewById(R.id.iv_add_pic);
        ivPic = (ImageView) findViewById(R.id.iv_pic);
        ivAddPic.setOnClickListener(this);
        ivPic.setOnClickListener(this);
        ivRecord = (ImageView) findViewById(R.id.iv_record);
        ivRecord.setOnClickListener(this);
        tvVoice = (TextView) findViewById(R.id.tv_voice);
        ivVoice = (ImageView) findViewById(R.id.iv_voice);
        ivVoice.setOnClickListener(this);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(this);

        recordAnimOpen = AnimationUtils.loadAnimation(this, R.anim.record_rotate_open);
        recordAnimClose = AnimationUtils.loadAnimation(this, R.anim.record_rotate_close);

        //长按删除图片
        ivPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bitmap = null;
                ivPic.setVisibility(View.GONE);
                return true;
            }
        });
    }

    /**
     * 初始化媒体文件的路径
     */
    private void initMediaPath() {
        //创建该路对应的媒体文件夹
        roadMediaOutDir = APPPath.APP_SD_PATH + File.separator + System.currentTimeMillis();
        File roadMediaOutFile = new File(roadMediaOutDir);
        if (roadMediaOutFile.exists()) {
            roadMediaOutFile.delete();
        }
        roadMediaOutFile.mkdir();


        recordOutputPath = roadMediaOutDir + File.separator + System.currentTimeMillis() + ".amr";
        recordOutputFile = new File(recordOutputPath);
    }

    private void saveAddress() {
        etLoc.setText(address);
    }

    public void getAddress(double latitude, double longitude) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 100, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_pic:
                pictureOutputPath = roadMediaOutDir + File.separator + System.currentTimeMillis() + ".jpg";
                pictureOutputFile = new File(pictureOutputPath);

                try {
                    if (pictureOutputFile.exists()) {
                        pictureOutputFile.delete();
                    }
                    pictureOutputFile.createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureOutputFile));
                startActivityForResult(intent, 1);
                break;

            case R.id.iv_record:

                if (isRecord) {
                    ivRecord.startAnimation(recordAnimClose);
                    tvVoice.setVisibility(View.INVISIBLE);
                    ivVoice.setVisibility(View.VISIBLE);
                    if (recordManager != null) {
                        recordManager.stopRecord();
                    }
                    isRecord = false;
                } else {
                    ivRecord.startAnimation(recordAnimOpen);
                    tvVoice.setText("正在录音...");
                    isRecord = true;
                    try {
                        if (recordOutputFile.exists()) {
                            recordOutputFile.delete();
                        }
                        recordOutputFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (recordManager == null) {
                        recordManager = new RecordManager();
                    }

                    recordManager.startRecord(recordOutputFile);
                }
                break;

            case R.id.iv_voice:
                if (!recordOutputFile.exists()) {
                    return;
                }
                if (mediaPlayerManger == null) {
                    mediaPlayerManger = new MediaPlayerManger();
                }
                try {
                    mediaPlayerManger.startPlay(recordOutputPath);
                } catch (IOException e) {
                    throw new RuntimeException("该录音不存在！");
                }
                break;

            case R.id.btn_finish:
                uploadingView = new UploadingView(NotiMsgActivity.this);
                uploadingView.open();
                final UserEvent userEvent = new UserEvent();
                userEvent.setLocation(address);
                userEvent.setLatitude(Variable.myLatitude);
                userEvent.setLongitude(Variable.myLongitude);
                userEvent.setTime(new Date(System.currentTimeMillis()));
                userEvent.setUsername(Variable.userName);
                new Thread() {
                    @Override
                    public void run() {
                        //上传文本信息
                        userEvent.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    Variable.objectId = userEvent.getObjectId();
                                    Log.i(TAG, "done: 基本信息上传完成");
                                }
                            }
                        });

                        //批量上传文件
                        final String[] filePaths = new String[2];
                        filePaths[0] = pictureOutputPath;
                        filePaths[1] = recordOutputPath;
                        Log.i(TAG, "run: picPath = " + pictureOutputPath + "  voicePath = " + recordOutputPath);
                        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                            @Override
                            public void onSuccess(List<BmobFile> list, List<String> list1) {
                                if (list.size() == filePaths.length) {
                                    Log.i(TAG, "onSuccess: 文件上传完成");
                                    Log.i(TAG, "onSuccess: " + list.get(0).getFileUrl());
                                    Log.i(TAG, "onSuccess: " + list.get(0).getUrl());
                                    Log.i(TAG, "onSuccess: " + list.get(1).getFileUrl());
                                    Log.i(TAG, "onSuccess: " + list.get(1).getUrl());

                                    //添加Url
                                    UserEvent userEventUrl = new UserEvent();
                                    userEventUrl.setPicUrl(list.get(0).getFileUrl());
                                    userEventUrl.setPicRmUrl(list.get(0).getUrl());
                                    userEventUrl.setVoiceUrl(list.get(1).getFileUrl());
                                    userEventUrl.setVoiceRmUrl(list.get(1).getUrl());
                                    Log.i(TAG, "run: Variable.objectId = " + Variable.objectId);
                                    userEventUrl.update(Variable.objectId, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                            if (e == null) {
                                                Log.i(TAG, "done: Url添加完成");
                                                handler.sendEmptyMessage(0);
                                                ViewUtils.show(NotiMsgActivity.this, "上传成功");
                                                setResult(RESULT_OK);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onProgress(int i, int i1, int i2, int i3) {

                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    }
                }.start();
                break;
        }
    }

    class AddressListener implements GeocodeSearch.OnGeocodeSearchListener {

        @Override
        public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
            address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
            saveAddress();
        }

        @Override
        public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            bitmap = BitmapFactory.decodeFile(pictureOutputPath);
            ivPic.setVisibility(View.VISIBLE);
            ivPic.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }
}
