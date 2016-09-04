package com.yohann.traffic107.root;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.yohann.traffic107.common.bean.Event;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.StringUtils;
import com.yohann.traffic107.utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import me.gujun.android.taggroup.TagGroup;

public class SingleEventActivity extends AppCompatActivity {
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
        geocodeSearch.setOnGeocodeSearchListener(new AddressListener());

        //获取当前时间
        startDate = new Date();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate);
        tvTime.setText(time);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        longitude = bundle.getDouble("longitude");
        latitude = bundle.getDouble("latitude");

        getAddress(latitude, longitude);
    }

    public void getAddress(double latitude, double longitude) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 100, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(query);
    }

    public void saveAddress() {
        etLoc.setText(address);
    }

    /**
     * 按钮监听
     */
    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                //填写标签内容
                case R.id.iv_add_labels:
                    AlertDialog.Builder builder = new AlertDialog.Builder(SingleEventActivity.this);
                    final AlertDialog dialog = builder.create();
                    //将自定义布局设置给dialog
                    View view = View.inflate(SingleEventActivity.this, R.layout.labels_input, null);

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

                case R.id.btn_finish:
                    String labels = StringUtils.getStringFromArrayList(labelList);

                    event = new Event();
                    event.setLocation(loc);
                    event.setLatitude(latitude);
                    event.setLongitude(longitude);
                    event.setLabels(labels);
                    event.setTitle(etTitle.getText().toString());
                    event.setDesc(etDesc.getText().toString());
                    event.setStartTime(startDate);
                    event.setFinished(false);

                    //上传
                    new Thread() {
                        @Override
                        public void run() {

                            event.save(new SaveListener<String>() {
                                @Override
                                public void done(String s, BmobException e) {
                                    if (e == null) {
                                        ViewUtils.show(SingleEventActivity.this, "上传成功");
                                        setResult(RESULT_OK, null);
                                        finish();
                                    } else {
                                        ViewUtils.show(SingleEventActivity.this, "上传失败" + e.getErrorCode());
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
}
