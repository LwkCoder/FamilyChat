package com.lwk.familycontact.project.common;

import com.hyphenate.EMError;
import com.lwk.familycontact.R;

/**
 * Created by LWK
 * TODO 错误类型和描述集合
 * 2016/8/4
 */
public class FCError
{
    /*自定义错误码*/
    public static final int REGIST_FAIL = 1000;
    public static final int LOGIN_FAIL = 1001;
    public static final int LOGOUT_FAIL = 1002;

    /**
     * 根据环信错误码返回错误描述的资源id
     *
     * @param error 环信错误码
     * @return 错误描述资源id
     */
    public static int getErrorMsgIdFromCode(int error)
    {
        switch (error)
        {
            case EMError.USER_ALREADY_EXIST:
                return R.string.warning_user_already_exist;
            case EMError.INVALID_PASSWORD:
                return R.string.warning_pwd_not_correct;
            case EMError.USER_REG_FAILED:
                return R.string.error_regist_fail;
            case EMError.USER_NOT_FOUND:
                return R.string.warning_user_not_exist;
            case EMError.NETWORK_ERROR:
                return R.string.warning_network_disconnect;
            default:
                return R.string.error_unknow;
        }
    }
}
