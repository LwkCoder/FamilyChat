package com.lwk.familycontact.project.call.view;

import com.lwk.familycontact.im.listener.HxCallView;

/**
 * Created by LWK
 * TODO 实时语音通话界面接口
 * 2016/10/21
 */
public interface HxVoiceCallView extends HxCallView
{
    void setHead(String url);

    void setName(String name);

    void showError(int errResId);
}
