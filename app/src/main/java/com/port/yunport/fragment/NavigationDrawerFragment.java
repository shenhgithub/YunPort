package com.port.yunport.fragment;
/**
 * Created by 超悟空 on 2015/4/20.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.port.yunport.R;
import com.port.yunport.activity.IndexActivity;
import com.port.yunport.activity.LoginActivity;
import com.port.yunport.util.MemoryConfig;
import com.port.yunport.util.StaticValue;

import org.mobile.common.dialog.NoNetworkDialog;
import org.mobile.common.function.CheckUpdate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽屉导航选项菜单片段
 *
 * @author 超悟空
 * @version 1.0 2015/4/20
 * @since 1.0
 */
public class NavigationDrawerFragment extends Fragment implements AdapterView.OnItemClickListener {

    /**
     * 日志标签前缀
     */
    private static final String LOG_TAG = "NavigationDrawerFragment.";

    /**
     * 强类型父activity
     */
    private IndexActivity parentActivity = null;

    /**
     * 功能标题的取值标签，用于数据适配器中
     */
    private static final String FUNCTION_TITLE = "function_title";

    /**
     * 功能图标的取值标签，用于数据适配器中
     */
    private static final String FUNCTION_IMAGE = "function_image";

    /**
     * 列表使用的数据适配器
     */
    private SimpleAdapter adapter = null;

    /**
     * 数据适配器的元数据
     */
    private List<Map<String, Object>> adapterDataList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.navigation_drawer, container, false);

        // 保存父activity
        if (getActivity() instanceof IndexActivity) {
            parentActivity = (IndexActivity) getActivity();
        }

        // 初始化导航菜单
        initNavigationMenu(rootView);

        return rootView;
    }

    /**
     * 初始化导航菜单
     *
     * @param rootView 根布局
     */
    private void initNavigationMenu(View rootView) {
        // 初始化导航菜单功能列表
        initListView(rootView);
    }

    /**
     * 初始化导航菜单功能列表
     *
     * @param rootView 根布局
     */
    private void initListView(View rootView) {
        // 片段中的列表布局
        ListView listView = (ListView) rootView.findViewById(R.id.navigation_drawer_menu_listView);

        // 获取列表元数据
        adapterDataList = getFunction();

        // 列表使用的数据适配器
        adapter = new SimpleAdapter(getActivity(), adapterDataList, R.layout
                .navigation_drawer_menu_item, new String[]{FUNCTION_TITLE}, new int[]{R.id.navigation_drawer_menu_item_function_name});

        // 设置适配器
        listView.setAdapter(adapter);

        // 设置监听器
        listView.setOnItemClickListener(this);
    }

    /**
     * 生成功能项标签资源的数据源
     *
     * @return 返回SimpleAdapter适配器使用的数据源
     */
    private List<Map<String, Object>> getFunction() {
        // 加载功能项
        List<Map<String, Object>> dataList = new ArrayList<>();

        String[] functionTitle = getActivity().getResources().getStringArray(R.array
                .navigation_drawer_menu_function_list);
        for (int i = 0; i < functionTitle.length; i++) {
            // 新建一个功能项标签
            Map<String, Object> function = new HashMap<>();

            // 添加标签资源
            // 添加标题
            function.put(FUNCTION_TITLE, functionTitle[i]);

            // 将标签加入数据源
            dataList.add(function);
        }
        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 0:
                // 全部功能(刷新)
                doRefresh();
                return;
            case 1:
                // 重新登录
                doReLogin();
                return;
            case 2:
                // 检查更新
                doCheckUpdate();
                return;
            case 3:
                // 退出
                doExit();
                return;
            default:
        }
    }

    /**
     * 执行检察更新
     */
    private void doCheckUpdate() {

        // 判断是否有网络
        if (!MemoryConfig.getConfig().isOpenNetwork()) {
            // 提示无网络
            NoNetworkDialog.showNoNetworkDialog(getActivity());
            return;
        }

        // 新建检查更新功能对象
        CheckUpdate checkUpdate = new CheckUpdate(getActivity(), StaticValue.APP_CODE);
        // 执行检查
        checkUpdate.checkWithSpinner();
    }

    /**
     * 执行全部功能(刷新)操作
     */
    private void doRefresh() {
        if (parentActivity != null) {
            parentActivity.loadIndexUrl();
            parentActivity.closeDrawer();
        }
    }

    /**
     * 执行重新登录操作
     */
    private void doReLogin() {

        // 新建意图,跳转到登录页面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // 执行跳转
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * 执行退出操作
     */
    private void doExit() {
        System.exit(0);
    }
}
