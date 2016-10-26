package com.lwk.familycontact.project.chat.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Pair;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.lib.base.utils.BmpUtils;
import com.lib.base.utils.SdUtils;
import com.lib.base.utils.StringUtil;
import com.lwk.familycontact.R;
import com.lwk.familycontact.base.FCApplication;
import com.lwk.familycontact.project.chat.model.HxImageDetailModel;
import com.lwk.familycontact.project.chat.view.HxImageDetailView;
import com.lwk.familycontact.project.common.CommonUtils;
import com.lwk.familycontact.project.common.OnBitmapDownloadListener;
import com.lwk.familycontact.utils.other.ThreadManager;

import java.io.File;
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

    public void savePic(Context context, EMMessage message)
    {
        if (message == null)
            return;

        mViewImpl.showDownloadDialog();
        EMImageMessageBody messageBody = (EMImageMessageBody) message.getBody();
        String remoteUrl = messageBody.getRemoteUrl();
        CommonUtils.getInstance().getImageDisplayer().downloadBitmap(context, remoteUrl, new OnBitmapDownloadListener()
        {
            @Override
            public void onDownload(Bitmap bitmap)
            {
                saveBitmap(bitmap);
            }
        });
    }

    //保存位图到sd卡
    private void saveBitmap(final Bitmap bitmap)
    {
        final String floderPath = new StringBuffer().append(SdUtils.getSdPath())
                .append("/NewPicures/").toString();
        File floder = new File(floderPath);
        if (!floder.exists())
            floder.mkdirs();

        final String newPicName = new StringBuffer()
                .append("NewPic_")
                .append(String.valueOf(System.currentTimeMillis()))
                .append(".jpg").toString();

        ThreadManager.getInstance().addNewRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                String savePath = BmpUtils.saveBmp(bitmap, floderPath, newPicName);
                mViewImpl.closeDownloadDialog();
                if (StringUtil.isNotEmpty(savePath))
                {
                    String msgEx = FCApplication.getInstance().getString(R.string.toast_image_detail_download_bitmal_success);
                    String msg = msgEx.replaceFirst("%%1", savePath);
                    mViewImpl.showDownloadSuccessToast(msg);
                } else
                {
                    mViewImpl.showDownliadFailToast();
                }
            }
        });
    }
}
