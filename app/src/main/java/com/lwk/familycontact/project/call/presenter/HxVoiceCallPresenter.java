package com.lwk.familycontact.project.call.presenter;

import android.os.Handler;

import com.lwk.familycontact.project.call.view.HxVoiceCallView;

/**
 * Created by LWK
 * TODO 实时语音通话界面Presenter
 * 2016/10/21
 */
public class HxVoiceCallPresenter extends HxCallPresenter
{

    public HxVoiceCallPresenter(HxVoiceCallView viewImpl, Handler handler)
    {
        super(viewImpl, handler);
    }

}
