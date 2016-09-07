package com.lwk.familycontact.storage.db.invite;

/**
 * 邀请信息处理状态
 */
public class InviteStatus
{
    //未处理
    public static final int ORIGIN = 0;

    //已同意
    public static final int AGREED = 1;

    //已拒绝
    public static final int REJECTED = 2;

    //对方同意
    public static final int BE_AGREED = 3;

    //对方拒绝
    public static final int BE_REJECTED = 4;
}
