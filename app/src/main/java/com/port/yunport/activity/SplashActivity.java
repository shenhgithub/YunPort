package com.port.yunport.activity;
/**
 * Created by 超悟空 on 2015/4/21.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.port.yunport.R;
import com.port.yunport.util.Config;
import com.port.yunport.util.MemoryBroadcast;
import com.port.yunport.util.MemoryConfig;
import com.port.yunport.util.StaticValue;
import com.port.yunport.work.CheckLogin;

import org.mobile.common.function.CheckUpdate;

/**
 * 启动页
 *
 * @author 超悟空
 * @version 1.0 2015/4/21
 * @since 1.0
 */
public class SplashActivity extends Activity {
    /**
     * 日志标签前缀
     */
    private static final String LOG_TAG = "SplashActivity.";

    /**
     * 延迟时间
     */
    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    /**
     * 本地广播管理器
     */
    private LocalBroadcastManager localBroadcastManager = null;

    /**
     * 数据加载结果的广播接收者
     */
    private LoadingReceiver loadingReceiver = null;

    /**
     * 用于跳转等待的线程
     */
    private Handler handler = null;

    /**
     * 用于执行跳转的动作
     */
    private Runnable runnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 定时
        splashWait();

        // 新线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                // 变量初始化
                initMemoryConfig();

                // 网络可用时继续执行
                if (MemoryConfig.getConfig().isOpenNetwork()) {

                    // 注册广播接收者
                    registerReceivers();

                    // 检查更新
                    checkUpdate();

                    // 自动登录
                    autoLogin();

                } else {
                    Log.i(LOG_TAG + "onResume", "no network");
                    // 没有数据要加载，提前跳转
                    instantJump();
                }
            }
        });

        // 启动线程
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收者
        unregisterReceivers();
    }

    /**
     * 执行检查更新
     */
    private void checkUpdate() {
        // 新建检查更新任务
        CheckUpdate checkUpdate = new CheckUpdate(this, StaticValue.APP_CODE);
        // 执行检查
        checkUpdate.checkNoPrompt();
    }

    /**
     * 启动页等待
     */
    private void splashWait() {
        Log.i(LOG_TAG + "splashWait", "timeout timer open");

        handler = new Handler();

        // 新建动作，执行跳转
        runnable = new Runnable() {
            public void run() {
                // 跳转意图
                Intent mainIntent = null;
                if (MemoryConfig.getConfig().isLogin()) {
                    // 已登录
                    mainIntent = new Intent(SplashActivity.this, IndexActivity.class);
                } else {
                    // 未登录
                    mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
                runnable = null;
            }
        };

        handler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
    }

    /**
     * 初始化全局变量
     */
    private void initMemoryConfig() {
        Log.i(LOG_TAG + "initMemoryConfig", "init memory config");

        // 刷新内存变量
        MemoryConfig.getConfig().Reset();
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        Log.i(LOG_TAG + "autoLogin", "autoLogin() is invoked");

        // 新线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 判断是否自动登录
                if (Config.getConfig().isLoginAuto()) {
                    Log.i(LOG_TAG + "autoLogin", "auto login");

                    // 进行登录验证
                    CheckLogin login = new CheckLogin();
                    // 执行登录任务
                    Log.i(LOG_TAG + "autoLogin", "auto login begin");
                    login.execute(Config.getConfig().getUserName(), Config.getConfig().getPassword());
                } else {
                    Log.i(LOG_TAG + "autoLogin", "no auto login");
                    // 发送登录状态改变广播，标识一个加载动作结束
                    MemoryBroadcast.sendBroadcast(SplashActivity.this, MemoryBroadcast.MEMORY_STATE_LOGIN);
                }
            }
        });

        // 启动线程
        thread.start();
    }

    /**
     * 数据加载结果的广播接收者，
     * 用于提前结束启动页
     *
     * @author 超悟空
     * @version 1.0 2015/1/31
     * @since 1.0
     */
    private class LoadingReceiver extends BroadcastReceiver {

        /**
         * 动作队列信号量，
         * 初始时为注册的动作数量，
         * 当减少到0时表示数据加载完毕
         */
        private volatile int actionSemaphore = 1;

        /**
         * 得到本接收者监听的动作集合
         *
         * @return 填充完毕的意图集合
         */
        public final IntentFilter getRegisterIntentFilter() {
            // 新建动作集合
            IntentFilter filter = new IntentFilter();
            // 登录结果监听
            filter.addAction(MemoryBroadcast.MEMORY_STATE_LOGIN);

            return filter;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            // 得到动作字符串
            String actionString = intent.getAction();
            Log.i(LOG_TAG + "LoadingReceiver.onReceive", "action is " + actionString);

            switch (actionString) {
                case MemoryBroadcast.MEMORY_STATE_LOGIN:
                    // 完成一个动作信号量减1
                    actionSemaphore--;
                    Log.i(LOG_TAG + "LoadingReceiver.onReceive", "actionSemaphore--");
                    break;
            }
            Log.i(LOG_TAG + "LoadingReceiver.onReceive", "now actionSemaphore is " + actionSemaphore);

            if (actionSemaphore <= 0) {
                // 数据加载完成
                // 注销广播接收者
                unregisterReceivers();
                // 提前跳转
                instantJump();
            }
        }
    }

    /**
     * 注册广播接收者
     */
    private void registerReceivers() {
        Log.i(LOG_TAG + "registerReceivers", "registerReceivers() is invoked");
        // 新建本地广播管理器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 新建接收者
        loadingReceiver = new LoadingReceiver();

        // 注册
        localBroadcastManager.registerReceiver(loadingReceiver, loadingReceiver.getRegisterIntentFilter());
    }

    /**
     * 注销广播接收者
     */
    private void unregisterReceivers() {
        Log.i(LOG_TAG + "unregisterReceivers", "unregisterReceivers() is invoked");
        if (localBroadcastManager == null) {
            return;
        }

        if (loadingReceiver != null) {
            localBroadcastManager.unregisterReceiver(loadingReceiver);
        }
    }

    /**
     * 提前跳转
     */
    private void instantJump() {
        Log.i(LOG_TAG + "instantJump", "instantJump() is invoked");

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler.post(runnable);
        }
    }
}
