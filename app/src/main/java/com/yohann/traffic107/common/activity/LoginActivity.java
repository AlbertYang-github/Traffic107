package com.yohann.traffic107.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yohann.traffic107.R;
import com.yohann.traffic107.common.Constants.Variable;
import com.yohann.traffic107.common.bean.Root;
import com.yohann.traffic107.common.bean.User;
import com.yohann.traffic107.user.activity.HomeActivity;
import com.yohann.traffic107.user.activity.RegisterActivity;
import com.yohann.traffic107.utils.BmobUtils;
import com.yohann.traffic107.utils.ViewUtils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 选择登录
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivityInfo";

    private EditText etUsername;
    private EditText etPassword;
    private CheckBox cbUserType;
    private ImageView ivLogin;
    private TextView tvRegister;
    private String username;
    private String password;
    private ProgressBar pbLogin;
    private User user;
    private Root root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        BmobUtils.init(this);
        init();
    }

    private void init() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        cbUserType = (CheckBox) findViewById(R.id.cb_user_type);
        ivLogin = (ImageView) findViewById(R.id.iv_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        pbLogin = (ProgressBar) findViewById(R.id.pb_login);

        //注册
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        //登录
        ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbUserType.isChecked()) {
                    //登录工作端
                    rootLogin();
                } else {
                    //登录普通用户端
                    userLogin();
                }
            }
        });

        cbUserType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvRegister.setVisibility(View.INVISIBLE);
                } else {
                    tvRegister.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void userLogin() {

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return;
        } else {
            pbLogin.setVisibility(View.VISIBLE);
            user = new User();
            user.setUsername(username);
            user.setPassword(password);

            new Thread() {
                @Override
                public void run() {
                    BmobQuery<User> query = new BmobQuery<>();

                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e == null) {
                                boolean isSuccessful = false;
                                for (User user : list) {
                                    if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                                        //登录成功
                                        Variable.userId = user.getObjectId();
                                        Variable.userName = user.getUsername();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                        ViewUtils.show(LoginActivity.this, "登录成功");
                                        isSuccessful = true;
                                        break;
                                    }
                                }
                                if (isSuccessful) {
                                } else {
                                    ViewUtils.show(LoginActivity.this, "登录失败");
                                }
                                pbLogin.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbLogin.setVisibility(View.INVISIBLE);
                                    }
                                });
                            } else {
                                ViewUtils.show(LoginActivity.this, "异常 " + e.getErrorCode());
                                pbLogin.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbLogin.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    });
                }
            }.start();
        }
    }

    public void rootLogin() {
        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return;
        } else {
            pbLogin.setVisibility(View.VISIBLE);
            root = new Root();
            root.setUsername(username);
            root.setPassword(password);

            new Thread() {
                @Override
                public void run() {
                    BmobQuery<Root> query = new BmobQuery<>();

                    query.findObjects(new FindListener<Root>() {
                        @Override
                        public void done(List<Root> list, BmobException e) {
                            if (e == null) {
                                boolean isSuccessful = false;
                                for (Root root : list) {
                                    if (username.equals(root.getUsername()) && password.equals(root.getPassword())) {
                                        //登录成功
                                        Variable.rootId = root.getObjectId();
                                        startActivity(new Intent(LoginActivity.this, com.yohann.traffic107.root.MapActivity.class));
                                        finish();
                                        ViewUtils.show(LoginActivity.this, "登录成功");
                                        isSuccessful = true;
                                        break;
                                    }
                                }
                                if (isSuccessful) {
                                } else {
                                    ViewUtils.show(LoginActivity.this, "登录失败");
                                }
                                pbLogin.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbLogin.setVisibility(View.INVISIBLE);
                                    }
                                });
                            } else {
                                ViewUtils.show(LoginActivity.this, "异常 " + e.getErrorCode());
                                pbLogin.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbLogin.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }
                    });
                }
            }.start();
        }
    }
}
