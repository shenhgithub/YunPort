package org.mobile.common.dialog;
/**
 * Created by 超悟空 on 2015/4/18.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import org.mobile.mobilelibrary.R;

/**
 * 弹出应用版本更新提示的功能类
 *
 * @author 超悟空
 * @version 1.0 2015/4/18
 * @since 1.0
 */
public class VersionUpdateDialog {

    /**
     * 当前不是最新版本的提示
     *
     * @param context     上下文
     * @param version     最新版本号
     * @param downloadUrl 新版本下载地址
     */
    public static void showUpdate(final Context context, String version, final String downloadUrl) {

        // 弹出确认对话框
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        // 设置标题
        dialog.setTitle(R.string.update_now_version_old);

        // 设置提示
        dialog.setMessage(context.getString(R.string.update_now_version_alert) + ":" + version);

        // 设置确认监听器
        dialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 打开应用下载地址
                Uri uri = Uri.parse(downloadUrl);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(it);
            }
        });

        // 设置取消监听器，无操作
        dialog.setNegativeButton(R.string.button_cancel, null);

        // 显示提示框
        dialog.show();
    }

    /**
     * 当前为最新版本的提示
     *
     * @param context 上下文
     */
    public static void showLatest(Context context) {

        // 提示框
        Dialog dialog = new Dialog(context);

        // 设置标题
        dialog.setTitle(R.string.update_now_version_latest);

        // 显示提示框
        dialog.setCancelable(true);
        dialog.show();
    }
}
