package com.leosoft.eam;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.PopupWindow;

import com.leosoft.eam.utils.InfoText;
import com.pitt.library.fresh.FreshDownloadView;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Leo on 2017-06-22.
 */

public class DataProgressPopupWindow extends PopupWindow {

    private final int FLAG_SHOW_OK = 10;
    private final int FLAG_SHOW_ERROR = 11;

    private FreshDownloadView mDownloadProgressBar;
    private DataProgressPopupWindow mDataProgressPopupWindow;
    private Activity mContext;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int progress = (int) msg.obj;

            mDownloadProgressBar.upDateProgress(progress);

            switch (msg.what) {
                case FLAG_SHOW_OK: {
                    mDownloadProgressBar.showDownloadOk();
                    new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(InfoText.TEXT_SUCCESS_TITLE)
                            .setContentText(InfoText.TEXT_SUCCESS_DATA_EXCHANGE)
                            .setConfirmText(InfoText.TEXT_OK)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    mDataProgressPopupWindow.dismiss();

                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                    break;
                }
                case FLAG_SHOW_ERROR: {
                    mDownloadProgressBar.showDownloadError();
                    new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(InfoText.TEXT_FAILD_DATA_EXCHANGE)
                            .setConfirmText(InfoText.TEXT_OK)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    mDataProgressPopupWindow.dismiss();

                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                    break;
                }
            }

        }
    };

    public DataProgressPopupWindow(final Activity context, int Width, int Height) {

        mContext = context;

        View contentView = context.getLayoutInflater().inflate(R.layout.popwin_data_progress, null);

        mDataProgressPopupWindow = this;
        mDownloadProgressBar = (FreshDownloadView) contentView.findViewById(R.id.dp_data_exchange);

        this.setContentView(contentView);
        this.setWidth(Width);
        this.setHeight(Height);
        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setAnimationStyle(R.style.popwin_data_progress_style);

    }

    public void switchDataProgressPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent);
            this.update();
        } else {
            this.dismiss();
        }
    }

    public void refreshProgress(final int mProgress, final Boolean flagState) {

        if (flagState == false) {
            Message message = Message.obtain();
            message.what = FLAG_SHOW_ERROR;
            message.obj = mProgress;
            mHandler.sendMessage(message);
        } else {
            Message message = Message.obtain();
            if (100 == mProgress) {
                message.what = FLAG_SHOW_OK;
            }
            message.obj = mProgress;
            mHandler.sendMessage(message);
        }

    }

}


