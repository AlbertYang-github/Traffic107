package com.yohann.traffic107.user.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.yohann.traffic107.utils.APPPath;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.MediaPlayerManger;
import com.yohann.traffic107.utils.RecordManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotiMsgActivity extends BaseActivity implements View.OnClickListener {

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
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
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
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            bitmap = BitmapFactory.decodeFile(imagePath);
            ivPic.setVisibility(View.VISIBLE);
            ivPic.setImageBitmap(bitmap);
            c.close();
        }
    }
}
