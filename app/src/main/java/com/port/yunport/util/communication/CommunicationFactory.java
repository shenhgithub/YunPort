package com.port.yunport.util.communication;
/**
 * Created by 超悟空 on 2015/4/23.
 */

import com.port.yunport.util.StaticValue;

import org.mobile.mobilelibrary.R;
import org.mobile.network.communication.HttpClientGetCommunication;
import org.mobile.network.communication.ICommunication;
import org.mobile.util.ContextUtil;


/**
 * 通讯对象工厂
 *
 * @author 超悟空
 * @version 1.0 2015/4/23
 * @since 1.0
 */
public class CommunicationFactory {

    /**
     * HttpGet请求对象
     */
    private static HttpClientGetCommunication httpClientGetCommunication = null;

    /**
     * 创建通讯工具对象
     *
     * @param networkType 网络工具类型
     *
     * @return 初始化完成的通讯工具
     */
    public static ICommunication Create(NetworkType networkType) {

        switch (networkType) {
            case HTTP_GET:
                // HttpGet请求对象
                if (httpClientGetCommunication == null) {
                    httpClientGetCommunication = initHttpClientGetCommunication();
                }
                return httpClientGetCommunication;
            default:
                throw new UnsupportedOperationException("指定协议未实现");
        }
    }

    /**
     * 初始化HttpGet请求对象
     *
     * @return 初始化完成的HttpGet对象
     */
    private static HttpClientGetCommunication initHttpClientGetCommunication() {

        // 新建HttpGet请求对象
        HttpClientGetCommunication httpClient = new HttpClientGetCommunication();

        // 设置get请求根路径
        httpClient.setUrlRoot(StaticValue.HTTP_GET_URL_ROOT);

        // 设置超时时间
        httpClient.setTimeout(ContextUtil.getContext().getResources().getInteger(R.integer.http_get_timeout));

        return httpClient;
    }
}
