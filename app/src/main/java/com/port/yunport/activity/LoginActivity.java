package com.port.yunport.activity;
/**
 * Created by 超悟空 on 2015/4/18.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.port.yunport.R;
import com.port.yunport.util.Config;
import com.port.yunport.util.MemoryConfig;
import com.port.yunport.util.StaticValue;
import com.port.yunport.work.CheckLogin;

import org.mobile.common.dialog.SimpleDialog;
import org.mobile.common.function.CheckUpdate;
import org.mobile.model.work.WorkBack;
import org.mobile.util.ApplicationVersion;
import org.mobile.util.BroadcastUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录Activity
 *
 * @author 超悟空
 * @version 1.0 2015/4/18
 * @since 1.0
 */
public class LoginActivity extends Activity {
    /**
     * 日志标签前缀
     */
    private static final String LOG_TAG = "LoginActivity.";

    /**
     * 用户名编辑框
     */
    private EditText userNameEditText = null;

    /**
     * 密码编辑框
     */
    private EditText passwordEditText = null;

    /**
     * 保存密码复选框
     */
    private CheckBox loginSaveCheck = null;

    /**
     * 自动登陆复选框
     */
    private CheckBox loginAutoCheck = null;

    /**
     * 进度条
     */
    private ProgressDialog progressDialog = null;

    /**
     * 新版本文本框
     */
    private TextView latestVersionTextView = null;

    /**
     * 本地广播管理器
     */
    private LocalBroadcastManager localBroadcastManager = null;

    /**
     * 全局临时变量改变的广播接收者
     */
    private VersionCheckReceiver versionCheckReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 重置用户参数
        MemoryConfig.getConfig().Reset();

        // 初始化界面
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收者
        unregisterReceivers();
    }

    /**
     * 初始化界面
     */
    private void init() {
        // 初始化当前版本号
        initNowVersion();
        // 初始化新版本号
        initLatestVersion();
        // 初始化复选框
        initCheck();
        // 初始化编辑框
        initEdit();
    }

    /**
     * 初始化新版本号
     */
    private void initLatestVersion() {
        Log.i(LOG_TAG + "initNowVersion", "initLatestVersion() is invoked");
        // 获取新版本文本框
        latestVersionTextView = (TextView) findViewById(R.id.activity_login_latest_version);
        // 注册广播接收者
        registerReceivers();
        // 尝试获取最新版本号
        showLatestVersionTextView();
    }

    /**
     * 显示最新版本号
     */
    private void showLatestVersionTextView() {

        // 判断是否为最新版本
        if (!ApplicationVersion.getVersionManager().isLatestVersion()) {
            // 设置文版内容
            latestVersionTextView.setText(getString(R.string.latest_version_name_pre) + ApplicationVersion.getVersionManager().getLatestVersionName());
            // 设置点击事件
            latestVersionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 新建检查更新工具
                    CheckUpdate checkUpdate = new CheckUpdate(LoginActivity.this, StaticValue.APP_CODE);
                    checkUpdate.checkWithSpinner();
                }
            });

            // 显示文本框
            latestVersionTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化当前版本号
     */
    private void initNowVersion() {
        Log.i(LOG_TAG + "initNowVersion", "initNowVersion() is invoked");
        // 当前版本号
        String versionName = null;

        try {
            // 包信息
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i(LOG_TAG + "initNowVersion", "NameNotFoundException is " + e.getMessage());
            versionName = getString(R.string.now_version_error_name);
        } finally {
            Log.i(LOG_TAG + "initNowVersion", "now version is " + versionName);
            // 当前版本文本框
            TextView nowVersionTextView = (TextView) findViewById(R.id.activity_login_now_version);
            nowVersionTextView.setText(getString(R.string.now_version_name_pre) + versionName);
        }
    }

    /**
     * 初始化复选框
     */
    private void initCheck() {
        // 获取复选框
        loginSaveCheck = (CheckBox) findViewById(R.id.loginSave);
        loginAutoCheck = (CheckBox) findViewById(R.id.loginAuto);

        // 设置复选框初状态
        loginSaveCheck.setChecked(Config.getConfig().isLoginSave());
        loginAutoCheck.setChecked(Config.getConfig().isLoginAuto());

        // 设置监听器使两个复选框联动
        loginSaveCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    loginAutoCheck.setChecked(false);
                }
            }
        });

        loginAutoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginSaveCheck.setChecked(true);
                }
            }
        });
    }

    /**
     * 初始化编辑框
     */
    private void initEdit() {
        // 文本框初始化
        userNameEditText = (EditText) findViewById(R.id.userName);
        passwordEditText = (EditText) findViewById(R.id.password);

        // 尝试填充数据
        if (Config.getConfig().getUserName() != null) {
            // 填充用户
            userNameEditText.setText(Config.getConfig().getUserName());

            if (loginSaveCheck.isChecked()) {
                // 记住密码状态，填充密码
                passwordEditText.setText(Config.getConfig().getPassword());
            } else {
                // 让密码框拥有焦点
                setSoftInput(passwordEditText);
            }
        } else {
            // 让用户名框拥有焦点
            setSoftInput(userNameEditText);
        }
    }

    /**
     * 设置指定编辑框获取焦点并弹出软键盘
     *
     * @param editText 要获取焦点的编辑框
     */
    private void setSoftInput(final EditText editText) {

        // 获取焦点
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        // 延迟弹出软键盘
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }

        }, 600);
    }

    /**
     * 登录按钮
     *
     * @param view 按钮
     */
    public void LoginButton(View view) {

        // 获取用户名和密码
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // 判断是否输入用户名和密码
        if (userName.length() == 0 || password.length() == 0) {
            return;
        }

        // 进行登录验证
        CheckLogin login = new CheckLogin();

        // 设置回调监听
        login.setWorkBackListener(new WorkBack<String>() {
            @Override
            public void doEndWork(boolean state, String data) {
                // 关闭进度条
                progressDialog.cancel();

                if (state) {
                    // 登录成功

                    // 保存当前设置
                    Config config = Config.getConfig();
                    config.setLoginAuto(loginAutoCheck.isChecked());
                    config.setLoginSave(loginSaveCheck.isChecked());

                    // 检查是否要保存用户名和密码
                    if (loginSaveCheck.isChecked()) {
                        config.setUserName(userNameEditText.getText().toString());
                        config.setPassword(passwordEditText.getText().toString());
                    }

                    // 保存设置
                    config.Save();

                    // 跳转到首页
                    Intent intent = new Intent(LoginActivity.this, IndexActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 登录失败
                    SimpleDialog.showDialog(LoginActivity.this, data);
                }
            }
        });

        // 打开旋转进度条
        startProgressDialog();

        // 执行登录任务
        login.beginExecute(userName, password);

    }

    /**
     * 打开进度条
     */
    private void startProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置提醒
        progressDialog.setMessage(getString(R.string.login_loading));
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    /**
     * 版本检查的广播接收者，
     * 用于实时改变本页的新版本显示框
     *
     * @author 超悟空
     * @version 1.0 2015/4/21
     * @since 1.0
     */
    private class VersionCheckReceiver extends BroadcastReceiver {

        /**
         * 得到本接收者监听的动作集合
         *
         * @return 填充完毕的意图集合
         */
        public final IntentFilter getRegisterIntentFilter() {
            // 新建动作集合
            IntentFilter filter = new IntentFilter();
            // 版本状态监听
            filter.addAction(BroadcastUtil.APPLICATION_VERSION_STATE);

            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取动作字符串
            String action = intent.getAction();
            Log.i(LOG_TAG + "versionCheckReceiver.onReceive", "action is " + action);

            switch (action) {
                case BroadcastUtil.APPLICATION_VERSION_STATE:
                    // 版本检查结束，重置新版本文本框
                    if (latestVersionTextView != null && latestVersionTextView.getVisibility() == View.GONE) {
                        // 文本框没有改变过，则执行重置
                        showLatestVersionTextView();
                    }
            }
        }
    }

    /**
     * 注册广播接收者
     */
    private void registerReceivers() {

        // 新建本地广播管理器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 新建接收者
        versionCheckReceiver = new VersionCheckReceiver();

        // 注册接收者
        localBroadcastManager.registerReceiver(versionCheckReceiver, versionCheckReceiver.getRegisterIntentFilter());
    }

    /**
     * 注销广播接收者
     */
    private void unregisterReceivers() {

        if (localBroadcastManager == null) {
            return;
        }

        if (versionCheckReceiver != null) {
            localBroadcastManager.unregisterReceiver(versionCheckReceiver);
        }
    }
}
