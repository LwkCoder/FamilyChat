package com.lwk.familycontact.im;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.lib.base.BuildConfig;
import com.lib.base.log.KLog;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.common.FCCallBack;
import com.lwk.familycontact.project.common.FCError;
import com.lwk.familycontact.storage.db.DbOpenHelper;

import java.util.Iterator;
import java.util.List;

/**
 * Created by LWK
 * TODO 环信sdk帮助类
 * 2016/8/4
 */
public class HxSdkHelper
{
    private HxSdkHelper()
    {
    }

    private static final class HxSdkHelperHolder
    {
        public static HxSdkHelper instance = new HxSdkHelper();
    }

    public static HxSdkHelper getInstance()
    {
        return HxSdkHelperHolder.instance;
    }

    private Context mAppContext;
    private boolean mIsSdkInited;
    private boolean mHasAsyncUserList;

    /**
     * 初始化环信sdk
     * 放在Application的onCreate()
     */
    public void initSdkOptions(Context context)
    {
        if (mIsSdkInited)
            return;

        mAppContext = context.getApplicationContext();

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(mAppContext.getPackageName()))
            return;

        EMOptions options = new EMOptions();
        //添加好友需要验证
        options.setAcceptInvitationAlways(false);
        //不需要阅读回执
        options.setRequireAck(false);
        //不需要发送服务器回执
        options.setRequireDeliveryAck(false);
        //可以自动登录
        options.setAutoLogin(true);

        EMClient.getInstance().init(mAppContext, options);
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG);
        mIsSdkInited = true;
        KLog.i("HxSdk has inited");
    }

    private String getAppName(int pID)
    {
        String processName = null;
        ActivityManager am = (ActivityManager) mAppContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = mAppContext.getPackageManager();
        while (i.hasNext())
        {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try
            {
                if (info.pid == pID)
                {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e)
            {
            }
        }
        return processName;
    }

    /**
     * 注册方法
     *
     * @param phone    手机号
     * @param pwd      密码
     * @param callBack 回调【注意回调会在子线程中】
     */
    public void regist(final String phone, final String pwd, final FCCallBack callBack)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    EMClient.getInstance().createAccount(phone, pwd);
                    if (callBack != null)
                        callBack.onSuccess(null);
                } catch (HyphenateException e)
                {
                    KLog.e("HxSdk regist from server fail : hxErrCode = " + e.getErrorCode() + " , msg = " + e.getMessage());
                    if (callBack != null)
                        callBack.onFail(FCError.REGIST_FAIL, FCError.getErrorMsgIdFromCode(e.getErrorCode()));
                }
            }
        }).start();
    }

    /**
     * 手动登录环信的方法
     *
     * @param phone    手机号
     * @param pwd      密码
     * @param callBack 回调
     */
    public void login(final String phone, final String pwd, final FCCallBack callBack)
    {
        EMClient.getInstance().login(phone, pwd, new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                KLog.i("HxSdk login from server success");
                loadHxLocalData();
                if (callBack != null)
                    callBack.onSuccess(null);
            }

            @Override
            public void onError(int code, String msg)
            {
                KLog.e("HxSdk login fail : hxErrCode = " + code + " , msg = " + msg);
                if (callBack != null)
                    callBack.onFail(FCError.LOGIN_FAIL, FCError.getErrorMsgIdFromCode(code));
            }

            @Override
            public void onProgress(int progress, String status)
            {

            }
        });
    }

    /**
     * 判断是否能自动登录
     */
    public boolean canAutoLogin()
    {
        return EMClient.getInstance().isLoggedInBefore();
    }

    /**
     * 加载环信本地数据
     */
    public void loadHxLocalData()
    {
        EMClient.getInstance().groupManager().loadAllGroups();
        KLog.i("HxSdk load all groups success");
        EMClient.getInstance().chatManager().loadAllConversations();
        KLog.i("HxSdk load all conversation success");
    }

    /**
     * 退出环信登录
     *
     * @param callBack 回调
     */
    public void logout(final FCCallBack callBack)
    {
        if (!EMClient.getInstance().isLoggedInBefore())
        {
            if (callBack != null)
                callBack.onSuccess(null);
            return;
        }
        EMClient.getInstance().logout(true, new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                resetFlags();
                DbOpenHelper.getInstance(FCApplication.getInstance()).close();
                if (callBack != null)
                    callBack.onSuccess(null);
            }

            @Override
            public void onError(int i, String s)
            {
                resetFlags();
                DbOpenHelper.getInstance(FCApplication.getInstance()).close();
                if (callBack != null)
                    callBack.onFail(FCError.LOGOUT_FAIL, FCError.getErrorMsgIdFromCode(i));
            }

            @Override
            public void onProgress(int i, String s)
            {

            }
        });
    }

    //将某些标记位还原
    private void resetFlags()
    {
        mHasAsyncUserList = false;
    }

    /**
     * 获取当前登录的账号
     *
     * @return 当前登录账号
     */
    public String getCurLoginUser()
    {
        return EMClient.getInstance().getCurrentUser();
    }

    /**
     * 添加连接监听
     *
     * @param listener 环信连接监听
     */
    public void addConnectListener(EMConnectionListener listener)
    {
        if (listener != null)
            EMClient.getInstance().addConnectionListener(listener);
    }

    /**
     * 移除连接监听
     *
     * @param listener 环信连接监听
     */
    public void removeConnectListener(EMConnectionListener listener)
    {
        if (listener != null)
            EMClient.getInstance().removeConnectionListener(listener);
    }

    /**
     * 从环信服务器拉取好友列表【仅包含账号】
     *
     * @param callBack 回调
     */
    public void asyncUserListFromServer(final FCCallBack<List<String>> callBack)
    {
        if (mHasAsyncUserList)
        {
            KLog.i("HxSdk already has asynced user from server.");
            if (callBack != null)
                callBack.onSuccess(null);
            return;
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    List<String> userList = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    KLog.i("HxSdk async user from server : " + userList);
                    mHasAsyncUserList = true;
                    if (callBack != null)
                        callBack.onSuccess(userList);
                } catch (HyphenateException e)
                {
                    KLog.e("HxSdk async user from server fail : hxErrCode = " + e.getErrorCode() + " , msg = " + e.getMessage());
                    if (callBack != null)
                        callBack.onFail(FCError.ASYNC_HXUSER_FAIL, FCError.getErrorMsgIdFromCode(e.getErrorCode()));
                }
            }
        }).start();
    }

    /**
     * 添加好友事件监听
     */
    public void addContactListener(HxContactListener listener)
    {
        if (listener != null)
            EMClient.getInstance().contactManager().setContactListener(listener);
    }

    /**
     * 移除好友事件监听
     */
    public void removeContactListener(HxContactListener listener)
    {
        if (listener != null)
            EMClient.getInstance().contactManager().removeContactListener(listener);
    }

    /**
     * 添加好友
     *
     * @param phone 手机号
     * @throws HyphenateException 鬼知道什么异常
     */
    public void addFriend(String phone) throws HyphenateException
    {
        EMClient.getInstance().contactManager().addContact(phone, null);
    }

    /**
     * 删除好友
     *
     * @param phone 手机号
     * @throws HyphenateException 鬼知道什么异常
     */
    public void deleteFriend(String phone) throws HyphenateException
    {
        EMClient.getInstance().contactManager().deleteContact(phone);
    }

    /**
     * 同意好友请求
     *
     * @param phone    对方手机号
     * @param callBack 回调
     */
    public void agreeNewFriendInvite(String phone, final FCCallBack callBack)
    {
        EMClient.getInstance().contactManager().asyncAcceptInvitation(phone, new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                if (callBack != null)
                    callBack.onSuccess(null);
            }

            @Override
            public void onError(int i, String s)
            {
                if (callBack != null)
                    callBack.onFail(FCError.AGREE_INVITE_FAIL, FCError.getErrorMsgIdFromCode(i));
            }

            @Override
            public void onProgress(int i, String s)
            {

            }
        });
    }

    /**
     * 拒绝好友请求
     *
     * @param phone    对方手机号
     * @param callBack 回调
     */
    public void rejectNewFriendInvite(String phone, final FCCallBack callBack)
    {
        EMClient.getInstance().contactManager().asyncDeclineInvitation(phone, new EMCallBack()
        {
            @Override
            public void onSuccess()
            {
                if (callBack != null)
                    callBack.onSuccess(null);
            }

            @Override
            public void onError(int i, String s)
            {
                if (callBack != null)
                    callBack.onFail(FCError.REJECT_INVITE_FAIL, FCError.getErrorMsgIdFromCode(i));
            }

            @Override
            public void onProgress(int i, String s)
            {

            }
        });
    }
}
