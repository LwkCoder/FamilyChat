package com.lwk.familycontact.project.common.version;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.lib.base.app.AppManager;
import com.lib.base.log.KLog;
import com.lib.base.sp.Sp;
import com.lib.base.toast.ToastUtils;
import com.lib.base.utils.AppUtil;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.common.FCCache;
import com.lwk.okhttp.OkHttpUtils;
import com.lwk.okhttp.callback.OkDownLoadFileCallBack;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by LWK
 * TODO 版本检查工具类
 * 2017/1/5
 */
public class CheckVersionUtils
{
    private String URL = "http://ogqrscjjw.bkt.clouddn.com/fcverison";
    private boolean isDownloading;
    private final String SP_KEY_CHECK_DATE = "check_version_date";
    private final int MIN_DATE_INTERVAL = 86400000;

    private CheckVersionUtils()
    {
    }

    private static final class CheckVersionUtilsHolder
    {
        private static final CheckVersionUtils instance = new CheckVersionUtils();
    }

    public static CheckVersionUtils getInstance()
    {
        return CheckVersionUtilsHolder.instance;
    }

    /**
     * 检查版本信息
     */
    public void checkVersion(final boolean forceCheck, final onCheckVersionListener listener)
    {
        //如果不是手动检查，且距离上次检查不超过一天，则无需检查更新
        if (!forceCheck && !isTimeEnough())
            return;

        OkHttpUtils
                .get()
                .url(URL)
                .build()
                .execute(new OkVersionResultCallBack()
                {
                    @Override
                    public void onSuccess(VersionBean versionBean)
                    {
                        updateCheckDate();
                        int curVersion = AppUtil.getAppVersionCode(FCApplication.getInstance());
                        int lastestVersionCode = versionBean.getCode();
                        if (lastestVersionCode > curVersion)
                        {
                            if (listener != null)
                                listener.onNewVersionAvaiable(versionBean);
                        } else
                        {
                            if (forceCheck)
                                ToastUtils.showShortMsg(FCApplication.getInstance(), R.string.toast_version_is_lastest);
                        }
                    }

                    @Override
                    public void onError(int errorCode)
                    {
                        if (forceCheck)
                            ToastUtils.showShortMsg(FCApplication.getInstance(), R.string.error_version_data_unavailable);
                    }
                });
    }

    /**
     * 显示新版本信息提示框
     *
     * @param activity    依附的Activity
     * @param versionBean 新版本信息
     */
    public void showVersionDialog(final Activity activity, final VersionBean versionBean)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(versionBean.getDesc_title());
        builder.setMessage(versionBean.getDesc_msg());
        if (versionBean.isForceUpdate())
        {
            builder.setCancelable(false);
        } else
        {
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.dialog_version_cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
        }
        builder.setPositiveButton(R.string.dialog_version_download, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                downloadApk(versionBean);
            }
        });
        builder.create().show();
    }

    //下载apk
    private void downloadApk(VersionBean versionBean)
    {
        if (StringUtil.isEmpty(versionBean.getPath()))
        {
            ToastUtils.showShortMsg(FCApplication.getInstance(), R.string.error_lastest_apk_path_unavailable);
            return;
        }
        if (isDownloading)
            return;

        KLog.i("新版本下载路径：" + versionBean.getPath());
        OkHttpUtils.get().url(versionBean.getPath())
                .build().execute(new OkDownLoadFileCallBack(FCCache.getInstance().getVersionCachePath(), createApkName(versionBean))
        {
            @Override
            public void onBefore(Request request, int id)
            {
                super.onBefore(request, id);
                isDownloading = true;
                ToastUtils.showLongMsg(FCApplication.getInstance(), R.string.toast_apk_downloading);
            }

            @Override
            public void onResponseError(Call call, Exception e, int id)
            {
                KLog.e("新版本下载失败：" + e.toString());
                ToastUtils.showLongMsg(FCApplication.getInstance(), R.string.error_lastest_apk_path_unavailable);
            }

            @Override
            public void onResponseSuccess(File response, int id)
            {
                KLog.i("新版本下载成功:" + response.getAbsolutePath());
                //开始安装apk
                int sdkVersion = AppUtil.getAndroidSDKVersion();
                if (sdkVersion >= 24)
                    startInstallAfterSdk24(response);
                else
                    startInstallBeforeSdk24(response);
            }

            @Override
            public void onAfter(int id)
            {
                super.onAfter(id);
                isDownloading = false;
            }

            @Override
            public void onCancle(Call call, int id)
            {
                super.onCancle(call, id);
                isDownloading = false;
            }
        });
    }

    //sdk24之前唤起apk安装的方法
    private void startInstallBeforeSdk24(File file)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        Activity activity = AppManager.getInstance().getPopActivity();
        if (activity!=null)
            activity.startActivity(intent);
    }

    //sdk24之后唤起apk安装的方法
    private void startInstallAfterSdk24(File file)
    {
        Uri apkUri = FileProvider.getUriForFile(FCApplication.getInstance()
                , "com.lwk.familycontact.fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        Activity activity = AppManager.getInstance().getPopActivity();
        if (activity!=null)
            activity.startActivity(intent);
    }

    //创建新apk本地缓存名字
    private String createApkName(VersionBean versionBean)
    {
        return new StringBuffer().append("FamilyChat")
                .append(versionBean.getCode())
                .append(String.valueOf(System.currentTimeMillis()))
                .append(".apk").toString();
    }

    //当前时间是否足够
    private boolean isTimeEnough()
    {
        return System.currentTimeMillis() - Sp.getLong(FCApplication.getInstance(), SP_KEY_CHECK_DATE) >= MIN_DATE_INTERVAL;
    }

    //更新检查时间
    private void updateCheckDate()
    {
        Sp.putLong(FCApplication.getInstance(), SP_KEY_CHECK_DATE, System.currentTimeMillis());
    }

    public interface onCheckVersionListener
    {
        /**
         * 新版本可用
         */
        void onNewVersionAvaiable(VersionBean versionBean);
    }
}
