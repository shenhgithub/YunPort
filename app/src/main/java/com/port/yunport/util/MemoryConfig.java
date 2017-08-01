package com.port.yunport.util;
/**
 * Created by 超悟空 on 2015/4/18.
 */

import android.content.Context;
import android.net.ConnectivityManager;

import org.mobile.model.config.TemporaryConfigModel;
import org.mobile.util.ContextUtil;

/**
 * 程序运行时的全局临时变量
 *
 * @author 超悟空
 * @version 1.0 2015/4/18
 * @since 1.0
 */
public class MemoryConfig extends TemporaryConfigModel {

    /**
     * 自身的静态对象
     */
    private static MemoryConfig config = null;

    /**
     * 标记是否已登录
     */
    private boolean login = false;

    /**
     * 用户标识
     */
    private String userID = null;

    /**
     * 密码
     */
    private String password = null;

    /**
     * 私有构造函数
     */
    private MemoryConfig() {
        super();
    }

    /**
     * 获取全局临时数据对象
     *
     * @return 数据对象
     */
    public static MemoryConfig getConfig() {
        if (config == null) {
            config = new MemoryConfig();
        }
        return config;
    }

    @Override
    protected void onCreate() {

        // 初始化用户参数
        setLogin(false);
        setUserID(null);
    }

    @Override
    protected void onRefresh() {

    }

    /**
     * 判断是否登录
     *
     * @return 返回状态
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * 设置登录状态
     *
     * @param flag 状态标识
     */
    public synchronized void setLogin(boolean flag) {
        this.login = flag;
    }

    /**
     * 获取用户标识
     *
     * @return 用户标识串
     */
    public String getUserID() {
        return userID;
    }

    /**
     * 设置用户标识
     *
     * @param userID 用户标识串
     */
    public synchronized void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * 获取密码
     *
     * @return 密码字符串
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码字符串
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 对网络连接状态进行判断
     *
     * @return true , 可用； false， 不可用
     */
    public boolean isOpenNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) ContextUtil.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo() != null && connManager.getActiveNetworkInfo()
                .isAvailable();
    }
}
