package com.port.yunport.activity;
/**
 * Created by 超悟空 on 2015/4/18.
 */

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.Toast;

import com.port.yunport.R;
import com.port.yunport.util.MemoryConfig;
import com.port.yunport.util.StaticValue;

import org.mobile.common.function.CheckUpdate;
import org.mobile.common.function.DownLoadTask;
import org.mobile.common.function.SendFile;
import org.mobile.common.webview.MobileWebViewFactory;
import org.mobile.model.work.WorkBack;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 功能主界面
 *
 * @author 超悟空
 * @version 1.0 2015/4/18
 * @since 1.0
 */
public class IndexActivity extends ActionBarActivity {
    /**
     * 日志标签前缀
     */
    private static final String LOG_TAG = "IndexActivity.";

    /**
     * 主界面显示的网页控件
     */
    protected WebView webView = null;

    /**
     * 导航抽屉控件
     */
    private DrawerLayout navigationDrawerLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_navigation_drawer);

        // 初始化WebView控件
        initWebView();

        // 初始化导航抽屉
        initDrawer();

        // 执行检查更新
        checkUpdate();
    }

    /**
     * 检查新版本
     */
    private void checkUpdate() {
        // 新建版本检查工具
        CheckUpdate checkUpdate = new CheckUpdate(this, StaticValue.APP_CODE);
        // 执行检查
        checkUpdate.checkInBackground();
    }

    /**
     * 初始化导航抽屉
     */
    private void initDrawer() {
        Log.i(LOG_TAG + "initDrawer", "initDrawer() is invoked");
        // 获取抽屉控件
        navigationDrawerLayout = (DrawerLayout) findViewById(R.id
                .activity_with_navigation_drawer_layout);

    }

    /**
     * 初始化WebView控件
     */
    private void initWebView() {
        Log.i(LOG_TAG + "initWebView", "initWebView() is invoked");
        // 获取WebView控件
        webView = (WebView) findViewById(R.id.activity_index_webView);

        // 初始化WebView设置
        MobileWebViewFactory.assemblingWebView(this, webView);

        // 设置下载
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {

                int fileNameIndex = url.indexOf("filename=");
                String fileName = null;

                if (fileNameIndex > 0) {
                    fileName = url.substring(fileNameIndex + 9, url.indexOf("&", fileNameIndex));
                }

                try {


                    DownLoadTask downLoadTask = new DownLoadTask(IndexActivity.this);
                    downLoadTask.setWorkBackListener(new WorkBack<File>() {
                        @Override
                        public void doEndWork(boolean state, File data) {
                            if (state) {
                                Log.i(LOG_TAG + "initWebView", "file path is " + data
                                        .getAbsolutePath());
                                Log.i(LOG_TAG + "initWebView", "file size is " + data.length());
                                new SendFile(IndexActivity.this).openFile(data);
                            } else {
                                Toast.makeText(IndexActivity.this, getString(R.string
                                        .download_failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    // 执行下载
                    downLoadTask.beginExecute(url, URLDecoder.decode(fileName, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.e(LOG_TAG + "initWebView", "fileName=" + fileName);
                    Log.e(LOG_TAG + "initWebView", e.getMessage());
                }

            }

        });

        // 加载主页
        loadIndexUrl();
    }

    /**
     * 加载主页
     */
    public void loadIndexUrl() {
        // 初始化加载的网址
        if (MemoryConfig.getConfig().isLogin() && MemoryConfig.getConfig().getUserID() != null) {
            // 成功登录，加载首页
            webView.loadUrl(String.format(StaticValue.INDEX_URL, MemoryConfig.getConfig()
                    .getUserID(), MemoryConfig.getConfig().getPassword()));
        } else {
            // 登录失败，加载失败页
            webView.loadUrl("javascript:document.body.innerHTML=\"" + getString(R.string
                    .index_error_dialog) + "\"");
        }
        Log.i(LOG_TAG + "loadIndexUrl", "url is " + webView.getUrl());
    }

    /**
     * 关闭导航抽屉
     */
    public void closeDrawer() {
        navigationDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {

        // 如果抽屉已打开，则先关闭抽屉
        if (navigationDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
