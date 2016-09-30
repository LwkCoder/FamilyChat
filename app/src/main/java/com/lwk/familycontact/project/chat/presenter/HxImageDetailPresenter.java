package com.lwk.familycontact.project.chat.presenter;

import android.os.Handler;
import android.util.Pair;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lwk.familycontact.project.chat.model.HxImageDetailModel;
import com.lwk.familycontact.project.chat.view.HxImageDetailView;
import com.lwk.familycontact.utils.other.ThreadManager;

import java.util.List;

/**
 * Created by LWK
 * TODO 查看大图界面Presenter
 * 2016/9/28
 */
public class HxImageDetailPresenter
{
    private HxImageDetailView mViewImpl;
    private Handler mMainHandler;
    private HxImageDetailModel mModel;

    public HxImageDetailPresenter(HxImageDetailView viewImpl, Handler handler)
    {
        this.mViewImpl = viewImpl;
        this.mMainHandler = handler;
        mModel = new HxImageDetailModel();
    }

    /**
     * 扫描数据获取全部图片消息
     */
    public void scanImageData(final EMConversation.EMConversationType conType, final String conId, final String firstVisiableMsgId)
    {
        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                Pair<List<EMMessage>, Integer> resultPair = mModel.initData(conType, conId, firstVisiableMsgId);
                onScanDataSuccess(resultPair.first, resultPair.second);
            }
        });
    }

    //扫描成功后刷新UI
    private void onScanDataSuccess(final List<EMMessage> messageList, final int startPosition)
    {
        mMainHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                mViewImpl.onScanDataSuccess(messageList, startPosition);
            }
        });
    }
}
