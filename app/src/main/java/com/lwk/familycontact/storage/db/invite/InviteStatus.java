package com.lwk.familycontact.storage.db.invite;

/**
 * 邀请信息处理状态
 */
public class InviteStatus
{
    //未处理
    public static final int ORIGIN = 0;

    //已同意
    public static final int ACCEPT = 1;

    //已拒绝
    public static final int REJECTED = 2;

    //被同意
    public static final int BE_ACCEPTED = 3;

    //被拒绝
    public static final int BE_REJECTED = 4;
}
