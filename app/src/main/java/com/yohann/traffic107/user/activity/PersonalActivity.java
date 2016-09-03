package com.yohann.traffic107.user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.activity.BaseActivity;
import com.yohann.traffic107.common.bean.DoublePoiEvent;
import com.yohann.traffic107.utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PersonalActivity extends BaseActivity {
    private static final String TAG = "PersonalActivityInfo";
    private static final int SHOW = 1;

    private ListView lvCommitData;
    private List<DoublePoiEvent> commitList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    lvCommitData.setAdapter(new MyAdapter());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        init();
    }

    private void init() {
        lvCommitData = (ListView) findViewById(R.id.lv_commit_data);
        commitList = new ArrayList<>();
        lvCommitData.addHeaderView(View.inflate(this, R.layout.commit_header, null));
        query(Variable.userName);

        lvCommitData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: position = " + position);
                DoublePoiEvent doublePoiEvent = commitList.get(position - 1);
                Intent intent = new Intent(PersonalActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(doublePoiEvent.getStartTime());
                bundle.putString("startTime", startTime);
                bundle.putString("startLoc", doublePoiEvent.getStartLocation());
                bundle.putString("endLoc", doublePoiEvent.getEndLocation());
                bundle.putString("labels", doublePoiEvent.getLabels());
                bundle.putString("title", doublePoiEvent.getTitle());
                bundle.putString("desc", doublePoiEvent.getDesc());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return commitList.size();
        }

        @Override
        public Object getItem(int position) {
            return commitList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(PersonalActivity.this, R.layout.item_commit_mine_user, null);

            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title_commit);
            TextView tvTime = (TextView) view.findViewById(R.id.tv_time_commit);
            TextView tvStatus = (TextView) view.findViewById(R.id.tv_status_commit);

            DoublePoiEvent doublePoiEvent = commitList.get(position);
            tvTitle.setText(doublePoiEvent.getTitle());
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(doublePoiEvent.getStartTime());
            tvTime.setText(time);
            return view;
        }
    }

    public void query(final String username) {
        //向服务器获取路况数据
        new Thread() {
            @Override
            public void run() {
                BmobQuery<DoublePoiEvent> query = new BmobQuery<>();
                query.addWhereEqualTo("username", username);

                query.findObjects(new FindListener<DoublePoiEvent>() {
                    @Override
                    public void done(List<DoublePoiEvent> list, BmobException e) {
                        if (e == null) {
                            if (list.size() == 0) {
                                ViewUtils.show(PersonalActivity.this, "没有数据可加载");
                            } else {
                                for (DoublePoiEvent doublePoiEvent : list) {
                                    commitList.add(doublePoiEvent);
                                }
                                handler.sendEmptyMessage(SHOW);
                                ViewUtils.show(PersonalActivity.this, "加载了" + list.size() + "条数据");
                            }
                        } else {
                            ViewUtils.show(PersonalActivity.this, "数据加载失败 " + e.getErrorCode());
                        }
                    }
                });
            }


        }.start();
    }

    /**
     * 后退
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }
}
