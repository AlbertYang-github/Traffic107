package com.yohann.traffic107.root;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import com.yohann.traffic107.common.bean.Event;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.StringUtils;
import com.yohann.traffic107.utils.UploadingView;
import com.yohann.traffic107.utils.ViewUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.gujun.android.taggroup.TagGroup;

public class SingleEventEditActivity extends BaseActivity {
    private static final String TAG = "SingleEventActivityInfo";
    private static int counter = 0;

    private EditText etLoc;
    private EditText etTitle;
    private EditText etDesc;
    private ImageView ivAddLabels;
    private TagGroup tagGroup;
    private TextView tvLabelHint;
    private TextView tvTime;
    private Button btnFinish;
    private ArrayList<String> labelList;
    private GeocodeSearch geocodeSearch;
    private String address;
    private Double longitude;
    private Double latitude;
    private String loc;
    private Date startDate;
    private Event event;
    private ImageView ivAddPic;
    private ImageView ivPic;
    private Bitmap bitmap;
    private String imagePath;
    private String fileUrl;
    private String rmUrl;
    private UploadingView uploadingView;

    private Double latitude1;
    private Double longitude1;
    private Double latitude2;
    private Double longitude2;
    private Double latitude3;
    private Double longitude3;
    private Double latitude4;
    private Double longitude4;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            uploadingView.close();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event);
        BmobUtils.init(this);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        etLoc = (EditText) findViewById(R.id.et_loc);
        ivAddLabels = (ImageView) findViewById(R.id.iv_add_labels);
        tagGroup = (TagGroup) findViewById(R.id.label_group);
        tvLabelHint = (TextView) findViewById(R.id.tv_label_hint);
        tvTime = (TextView) findViewById(R.id.tv_time);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        etTitle = (EditText) findViewById(R.id.et_title);
        etDesc = (EditText) findViewById(R.id.et_desc);
        ivAddPic = (ImageView) findViewById(R.id.iv_add_pic);
        ivPic = (ImageView) findViewById(R.id.iv_pic);

        ivPic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bitmap = null;
                ivPic.setVisibility(View.GONE);
                return true;
            }
        });

        geocodeSearch = new GeocodeSearch(this);

        //装载标签
        labelList = new ArrayList<>();

        //点击删除标签
        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                labelList.remove(tag);

                if (labelList.size() == 0) {
                    tvLabelHint.setVisibility(View.VISIBLE);
                }

                tagGroup.setTags(labelList);
            }
        });


        //添加监听
        MyOnClickListener listener = new MyOnClickListener();
        ivAddLabels.setOnClickListener(listener);
        btnFinish.setOnClickListener(listener);
        ivAddPic.setOnClickListener(listener);
        geocodeSearch.setOnGeocodeSearchListener(new AddressListener());

        //获取当前时间
        startDate = new Date();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate);
        tvTime.setText(time);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        longitude = bundle.getDouble("longitude");
        latitude = bundle.getDouble("latitude");

        latitude1 = bundle.getDouble("latitude1");
        longitude1 = bundle.getDouble("longitude1");
        latitude2 = bundle.getDouble("latitude2");
        longitude2 = bundle.getDouble("longitude2");
        latitude3 = bundle.getDouble("latitude3");
        longitude3 = bundle.getDouble("longitude3");
        latitude4 = bundle.getDouble("latitude4");
        longitude4 = bundle.getDouble("longitude4");

        getAddress(latitude, longitude);
    }

    public void getAddress(double latitude, double longitude) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 100, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }

    public void saveAddress() {
        etLoc.setText(address);
        loc = address;
    }

    /**
     * 按钮监听
     */
    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {

            switch (v.getId()) {
                //填写标签内容
                case R.id.iv_add_labels:
                    AlertDialog.Builder builder = new AlertDialog.Builder(SingleEventEditActivity.this);
                    final AlertDialog dialog = builder.create();
                    //将自定义布局设置给dialog
                    View view = View.inflate(SingleEventEditActivity.this, R.layout.labels_input, null);

                    final EditText etLabel = (EditText) view.findViewById(R.id.et_label);
                    Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
                    Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

                    dialog.setView(view);
                    dialog.show();

                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "确定标签");
                            labelList.add(etLabel.getText().toString());
                            dialog.dismiss();
                            if (labelList.size() != 0) {
                                tvLabelHint.setVisibility(View.GONE);
                            }
                            tagGroup.setTags(labelList);
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "取消");
                            if (labelList.size() != 0) {
                                tvLabelHint.setVisibility(View.GONE);
                            }
                            dialog.dismiss();
                        }
                    });
                    break;

                case R.id.iv_add_pic:
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                    break;

                case R.id.btn_finish:
                    uploadingView = new UploadingView(SingleEventEditActivity.this);
                    uploadingView.open();
                    String labels = StringUtils.getStringFromArrayList(labelList);

                    event = new Event();
                    event.setLocation(loc);
                    event.setLatitude(latitude);
                    event.setLongitude(longitude);
                    event.setLabels(labels);
                    event.setTitle(etTitle.getText().toString());
                    event.setDesc(etDesc.getText().toString());
                    event.setStartTime(startDate);

                    event.setLatitude1(latitude1);
                    event.setLongitude1(longitude1);
                    event.setLatitude2(latitude2);
                    event.setLongitude2(longitude2);
                    event.setLatitude3(latitude3);
                    event.setLongitude3(longitude3);
                    event.setLatitude4(latitude4);
                    event.setLongitude4(longitude4);

                    //上传
                    new Thread() {

                        @Override
                        public void run() {

                            //上传文本信息
                            event.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        Variable.objectId = event.getObjectId();
                                        Log.i(TAG, "done: 基本信息上传完成");
                                    }
                                }
                            });

                            //上传文件
                            final BmobFile bmobFile = new BmobFile(new File(imagePath));
                            bmobFile.upload(new UploadFileListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        fileUrl = bmobFile.getFileUrl();
                                        rmUrl = bmobFile.getUrl();
                                        Log.i(TAG, "done: 文件上传完成");

                                        //添加Url
                                        Event eventUrl = new Event();
                                        eventUrl.setFileUrl(fileUrl);
                                        eventUrl.setRmUrl(rmUrl);
                                        eventUrl.setFinished(false);
                                        Log.i(TAG, "run: Variable.objectId = " + Variable.objectId);
                                        eventUrl.update(Variable.objectId, new UpdateListener() {
                                            @Override
                                            public void done(BmobException e) {
                                                if (e == null) {
                                                    Log.i(TAG, "done: Url添加完成");
                                                    handler.sendEmptyMessage(0);
                                                    ViewUtils.show(SingleEventEditActivity.this, "上传成功");
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    }.start();
                    break;
            }
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
            imagePath = c.getString(columnIndex);
            Log.i(TAG, "onActivityResult: " + imagePath);
            bitmap = BitmapFactory.decodeFile(imagePath);
            ivPic.setVisibility(View.VISIBLE);
            ivPic.setImageBitmap(bitmap);
            c.close();
        }
    }
}
