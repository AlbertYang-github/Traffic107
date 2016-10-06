package com.yohann.traffic107.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.utils.TipSearchService;

import java.util.ArrayList;
import java.util.List;

public class DestActivity extends AppCompatActivity {
    private EditText etDest;
    private ListView lvPoi;
    private PoiAdapter adapter;
    private TipSearchService tipSearchService;
    private List<TipSearchService.KeyTip> poiList;
    private LatLonPoint startLatLonPoint;
    private LatLonPoint endLatLonPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dest);
        init();
    }

    private void init() {
        etDest = (EditText) findViewById(R.id.et_dest);
        lvPoi = (ListView) findViewById(R.id.lv_poi);
        adapter = new PoiAdapter();
        tipSearchService = new TipSearchService(this);
        poiList = new ArrayList<>();

        //监听输入
        etDest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lvPoi.setAdapter(adapter);
                //数据改变
                tipSearchService.getKeyTipList(s.toString(), new TipSearchService.OnInputtipsListener() {
                    @Override
                    public void onInputtipsListener(List<TipSearchService.KeyTip> keyTips) {
                        poiList = keyTips;
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //选择目的地
        lvPoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //确定起始点和终止点
                startLatLonPoint = new LatLonPoint(Variable.myLatitude, Variable.myLongitude);
                endLatLonPoint = poiList.get(position).getLatLonPoint();

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("startLatLonPoint", startLatLonPoint);
                bundle.putParcelable("endLatLonPoint", endLatLonPoint);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private class PoiAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return poiList.size();
        }

        @Override
        public Object getItem(int position) {
            return poiList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(DestActivity.this, R.layout.item_poi, null);
            TextView tvPoi = (TextView) view.findViewById(R.id.tv_poi);
            tvPoi.setText(poiList.get(position).getPlaceName());

            return view;
        }
    }
}
