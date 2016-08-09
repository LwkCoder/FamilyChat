package com.lib.imagepicker.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lib.imagepicker.ImagePicker;
import com.lib.imagepicker.R;
import com.lib.imagepicker.bean.ImageBean;
import com.lib.imagepicker.bean.ImageFloderBean;
import com.lib.imagepicker.model.ImagePickerMode;
import com.lib.imagepicker.presenter.ImagePickerGridPresenter;
import com.lib.imagepicker.utils.CropHelper;
import com.lib.imagepicker.utils.OtherUtils;
import com.lib.imagepicker.view.adapter.ImageGridAdapter;
import com.lib.imagepicker.view.base.ImagePickerBaseActivity;
import com.lib.imagepicker.view.impl.GridPickerViewImpl;
import com.lib.imagepicker.view.pop.ImagePickerFloderPop;
import com.lib.imagepicker.view.widget.ImagePickerActionBar;

import java.io.File;

/**
 * 图片选择界面
 */
public class ImagePickerGridActivity extends ImagePickerBaseActivity implements GridPickerViewImpl
{
    //图片选择activity跳转到拍照界面的requestCode
    private static final int sREQUESTCODE_TAKE_PHOTO = 101;
    //扫描本地数据成功
    private static final int FLAG_SCAN_DATA_SUCCESS = 102;
    //扫描本地数据失败
    private static final int FLAG_SCAN_DATA_FAIL = 103;
    private ImagePickerGridPresenter mPresenter;
    private GridView mGridView;
    private View mRlBottom;
    private TextView mTvCurFloder;
    private Button mBtnOk;
    private ProgressBar mPgbLoading;
    private ImageGridAdapter mAdapter;
    private ImageFloderBean mCurFloader;
    //当前拍照文件路径
    private String mCurTakePhotoPath;
    //是否为第一次加载
    private boolean isFristLoading = true;

    @Override
    protected void beforeOnCreate(Bundle savedInstanceState)
    {
        super.beforeOnCreate(savedInstanceState);
        mPresenter = new ImagePickerGridPresenter(this);
        //沉浸式状态栏
        OtherUtils.changeStatusBarColor(this, getResources().getColor(R.color.black_statusbar));
    }

    @Override
    protected int setContentViewId()
    {
        return R.layout.activity_image_picker_grid;
    }

    @Override
    protected void initUI()
    {
        ImagePickerActionBar actionBar = findView(R.id.cab_imagepicker_gridactivity);
        actionBar.setLeftLayoutAsBack(this);
        actionBar.setBackgroundColor(getResources().getColor(R.color.black_actionbar));
        actionBar.setTitleText(R.string.tv_imagepicker_gridactivity_title);
        if (ImagePicker.getInstance().getOptions().getPickerMode() == ImagePickerMode.MUTIL)
        {
            actionBar.setRightTvText(R.string.tv_imagepicker_actionbar_preview);
            actionBar.setRightLayoutClickListener(this);
        }
        mGridView = findView(R.id.gridView_imagepicker_gridactivity);
        mAdapter = new ImageGridAdapter(this, null, mPresenter);
        mGridView.setAdapter(mAdapter);
        mRlBottom = findView(R.id.bottom_imagepicker_gridactivity);
        mTvCurFloder = findView(R.id.tv_imagepicker_bottom_floder);
        mBtnOk = findView(R.id.btn_imagepicker_bottom_ok);
        mPgbLoading = findView(R.id.pgb_imagepicker_gridactivity);

        addClick(mBtnOk);
        addClick(R.id.fl_imagepicker_bottom_floder);
    }

    @Override
    protected void initData()
    {
        super.initData();
        //开启扫描
        mPresenter.scanAllData(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //刷新UI
        if (!isFristLoading)
        {
            mPresenter.selectedNumChanged();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void startScanData()
    {
        mPgbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void scanDataSuccess()
    {
        mMainHanlder.sendEmptyMessage(FLAG_SCAN_DATA_SUCCESS);
    }

    @Override
    public void scanDataFail()
    {
        mMainHanlder.sendEmptyMessage(FLAG_SCAN_DATA_SUCCESS);
    }

    @Override
    public void clickTakePhoto()
    {
        if (!OtherUtils.isSdExist())
        {
            showToast(R.string.error_no_sdcard);
            return;
        }
        // 拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
        // 有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //自己指定保存路径
        File tempPicFile = mPresenter.getTakePhotoPath();
        mCurTakePhotoPath = tempPicFile.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPicFile));
        startActivityForResult(intent, sREQUESTCODE_TAKE_PHOTO);
    }

    @Override
    public void onSelectedNumChanged(int curNum, int maxNum)
    {
        if (ImagePicker.getInstance().getOptions().getPickerMode() != ImagePickerMode.SINGLE)
        {
            mBtnOk.setText(getString(R.string.btn_imagepicker_ok, curNum, maxNum));
            if (curNum == 0)
                mBtnOk.setEnabled(false);
            else
                mBtnOk.setEnabled(true);
        }
    }

    @Override
    public void onNumLimited(int maxNum)
    {
        showToast(getString(R.string.warning_imagepicker_limit_num, maxNum));
    }

    @Override
    public void onSingleImageSelected(ImageBean imageBean)
    {
        if (ImagePicker.getInstance().getOptions().isNeedCrop())
        {
            int max = OtherUtils.getScreenWidth(this);
            CropHelper.startCropInRect(this
                    , imageBean.getImagePath()
                    , ImagePicker.getInstance().getOptions().getCachePath()
                    , max, max);
        } else
        {
            ImagePicker.getInstance().handleSingleModeListener(imageBean);
        }
    }

    @Override
    public void onCurFloderChanged(ImageFloderBean curFloder)
    {
        if (curFloder == null)
            return;

        mCurFloader = curFloder;
        if (OtherUtils.isEquals(ImageFloderBean.ALL_FLODER_ID, mCurFloader.getFloderId()))
        {
            mAdapter.refreshData(mPresenter.getAllImages());
        } else
        {
            mAdapter.refreshData(mPresenter.getImagesByFloder(curFloder));
        }

        mTvCurFloder.setText(mCurFloader.getFloderName());
        mGridView.smoothScrollToPosition(0);//滑动到顶部
    }

    @Override
    public void enterDetailActivity(int startPosition)
    {
        ImagePickerDetailActivity.start(this, startPosition, mCurFloader.getFloderId());
    }

    @Override
    protected void onHandlerMessage(Message msg)
    {
        super.onHandlerMessage(msg);
        if (msg.what == FLAG_SCAN_DATA_SUCCESS)
        {
            mPgbLoading.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            mRlBottom.setVisibility(View.VISIBLE);
            //默认展示"全部图片"数据
            mPresenter.changeFloder(mPresenter.getFloderById(ImageFloderBean.ALL_FLODER_ID));
            //刷新其他UI
            if (ImagePicker.getInstance().getOptions().getPickerMode() == ImagePickerMode.SINGLE)
            {
                mBtnOk.setVisibility(View.GONE);
            } else
            {
                mBtnOk.setVisibility(View.VISIBLE);
                //刷新下确认按钮的文案
                mPresenter.selectedNumChanged();
            }
        } else if (msg.what == FLAG_SCAN_DATA_FAIL)
        {
            mPgbLoading.setVisibility(View.GONE);
            showToast(R.string.error_imagepicker_scanfail);
        }
        isFristLoading = false;
    }

    @Override
    protected void onClick(int id, View v)
    {
        if (id == R.id.btn_imagepicker_bottom_ok)
        {
            //选择完毕
            ImagePicker.getInstance().handleMutilModeListener();
        } else if (id == R.id.fl_imagepicker_bottom_floder)
        {
            //展示文件夹菜单
            ImagePickerFloderPop pop = new ImagePickerFloderPop(this, mCurFloader, mPresenter);
            pop.showAtLocation(mRootLayout, Gravity.CENTER, 0, 0);
        } else if (id == R.id.fl_imagepicker_actionbar_right)
        {
            //预览已选图片
            ImagePickerPreviewActivity.start(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            if (mAdapter != null)
                mAdapter.reset();
            if (resultCode == 404)
                showToast(R.string.error_imagepicker_parsefail);
            return;
        }
        //拍照返回
        if (requestCode == sREQUESTCODE_TAKE_PHOTO)
        {
            ImageBean imageBean = new ImageBean();
            imageBean.setImagePath(mCurTakePhotoPath);
            mPresenter.singleImageSelected(imageBean);
        }
        //裁剪返回
        else if (requestCode == CropHelper.REQUEST_CODE_CROP)
        {
            Pair<Uri, String> resultData = CropHelper.getCropedData(this, data);
            ImageBean imageBean = new ImageBean();
            imageBean.setImagePath(resultData.second);
            ImagePicker.getInstance().handleSingleModeListener(imageBean);
        }
    }

    @Override
    protected void onDestroy()
    {
        mPresenter.clearData();
        mPresenter = null;
        ImagePicker.getInstance().clear();
        super.onDestroy();
    }
}
