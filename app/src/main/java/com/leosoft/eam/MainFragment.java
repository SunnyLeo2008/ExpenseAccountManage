package com.leosoft.eam;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.john.waveview.WaveView;
import com.leosoft.eam.about.AboutActivity;
import com.leosoft.eam.billadd.BillAddActivity;
import com.leosoft.eam.billfile.BillFileActivity;
import com.leosoft.eam.billtype.BillTypeActivity;
import com.leosoft.eam.modifypassword.ModifyPassActivity;
import com.leosoft.eam.userphoto.UserPhotoActivity;
import com.leosoft.eam.utils.BillCardInfo;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.FileUtils;
import com.leosoft.eam.utils.InfoText;
import com.leosoft.eam.utils.ShapedImageView;
import com.leosoft.eam.utils.TableBillInfo;
import com.leosoft.eam.utils.TableStepInfo;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;


/**
 * Created by Leo on 2017-05-17.
 */

public class MainFragment extends Fragment {

    private FrameLayout fragmentContainer;
    private RecyclerView rvBillAdd;
    private RecyclerView rvBillDelete;
    private RecyclerView.LayoutManager layoutManager;
    private WaveView waveBackgroundView;
    private CircularFillableLoaders waveFillableLoaders;
    private MainFragmentAddAdapter mainFragmentAddAdapter;
    private MainFragmentHistoryAdapter mainFragmentHistoryAdapter;

    private TextView tvTotalBillInfo;
    private TextView tvHandleBillPercent;
    private ImageButton imgBtnAddBill;
    private ShapedImageView imageUser;
    private Button btnModifyPassword;
    private Button btnModifyType;
    private Button btnAbout;
    private Button btnBillFile;
    private Button btnExchangeFile;

    private LinearLayout llTab3Title;
    private FrameLayout flShapedImageView;

    private double mTotalMoney;
    private double mHandledMoney;
    private double mPendingMoney;

    private ArrayList<BillCardInfo> itemsBillAddData;
    private ArrayList<BillCardInfo> itemsBillDeleteData;

    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();

    /**
     * Create a new instance of the fragment
     */
    public static MainFragment newInstance(int index) {
        MainFragment fragment = new MainFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView;
        switch (getArguments().getInt("index", 0)) {
            case 1:
                mView = inflater.inflate(R.layout.fragment_main_tab_2_container, container, false);
                initTabBillDelete(mView);
                break;
            case 2:
                mView = inflater.inflate(R.layout.fragment_main_tab_3_container, container, false);
                initTabSetup(mView);
                break;
            default:
                mView = inflater.inflate(R.layout.fragment_main_tab_1_container, container, false);
                initTabBillAdd(mView);
        }

        return mView;
    }

    /**
     * 初始化“待报账”面板
     */
    private void initTabBillAdd(View view) {

        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragment_container_1);

        imgBtnAddBill = (ImageButton) view.findViewById(R.id.imageButtonAddBill);

        rvBillAdd = (RecyclerView) view.findViewById(R.id.fragment_main_recycler_view_1);
        rvBillAdd.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvBillAdd.setLayoutManager(layoutManager);

        tvTotalBillInfo = (TextView) view.findViewById(R.id.total_bill_info_1);
        tvHandleBillPercent = (TextView) view.findViewById(R.id.handle_bill_percent_1);
        waveBackgroundView = (WaveView) view.findViewById(R.id.wave_background_view_1);
        waveFillableLoaders = (CircularFillableLoaders) view.findViewById(R.id.wave_fillable_loaders_1);

        df.applyPattern("0.00");

        //以下初始化面板上的具体数据内容，示例如下：
        itemsBillAddData = new ArrayList<>();
        mTotalMoney = 0.00;
        mHandledMoney = 0.00;
        mPendingMoney = 0.00;

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        String SQLStr = "SELECT JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,DEALDATE,BILLMEMO,DEALFLAG,ICONNAME "
                + "FROM T_BILLINFO A,T_STEPINFO B WHERE DEALFLAG<>2 AND STEPLEVEL=0 AND STEPTITLE=BILLTYPE";
        Cursor mCursor = databaseAccess.querySQL(SQLStr);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                if ("0".equals(mCursor.getString(mCursor.getColumnIndex("DEALFLAG")))) {
                    BillCardInfo mBillCardInfo = new BillCardInfo();
                    mBillCardInfo.setmCardViewJNL(mCursor.getString(mCursor.getColumnIndex("JNL")));
                    mBillCardInfo.setmCardViewPhotoID(mCursor.getString(mCursor.getColumnIndex("ICONNAME")));
                    mBillCardInfo.setmCardViewBillType(mCursor.getString(mCursor.getColumnIndex("BILLTYPE")));
                    mBillCardInfo.setmCardViewBillChildType(mCursor.getString(mCursor.getColumnIndex("BILLCHILDTYPE")));
                    mBillCardInfo.setmCardViewBillSum(df.format(Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")))));
                    mBillCardInfo.setmCardViewBillDate(mCursor.getString(mCursor.getColumnIndex("BILLDATE")));
                    mBillCardInfo.setmCardViewBillCreateDate(mCursor.getString(mCursor.getColumnIndex("CREATEDATE")));
                    mBillCardInfo.setmCardViewBillMemo(mCursor.getString(mCursor.getColumnIndex("BILLMEMO")));

                    itemsBillAddData.add(mBillCardInfo);

                    mPendingMoney = mPendingMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                } else {
                    mHandledMoney = mHandledMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                }

            } while (mCursor.moveToNext());
        }
        mCursor.close();
        databaseAccess.close();

        mTotalMoney = mHandledMoney + mPendingMoney;

        tvTotalBillInfo.setText(InfoText.TEXT_TOTAL_SUM + df.format(mTotalMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_DEAL
                + df.format(mHandledMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_SUSPENDING + df.format(mPendingMoney));

        int mBillPercent = 0;
        if (mTotalMoney != 0) {
            mBillPercent = (int) ((mHandledMoney / mTotalMoney) * 100);
        }
        tvHandleBillPercent.setText(mBillPercent + "%");
        waveBackgroundView.setProgress(mBillPercent);
        waveFillableLoaders.setProgress(100 - mBillPercent);

        mainFragmentAddAdapter = new MainFragmentAddAdapter(itemsBillAddData, getActivity());
        mainFragmentAddAdapter.getMainFragmentAddAdapter(mainFragmentAddAdapter);
        rvBillAdd.setAdapter(mainFragmentAddAdapter);

        rvBillAdd.setItemAnimator(new OvershootInLeftAnimator());
        rvBillAdd.getItemAnimator().setAddDuration(500);
        rvBillAdd.getItemAnimator().setRemoveDuration(500);
        rvBillAdd.getItemAnimator().setMoveDuration(500);
        rvBillAdd.getItemAnimator().setChangeDuration(500);

        mainFragmentAddAdapter.setOnItemListener(new MainFragmentAddAdapter.OnItemListener() {

            public void OnItemDelete(int position, Double billsum, Boolean flagBillSubmit) {

                itemsBillAddData.remove(position);

                if (flagBillSubmit) {
                    refreshSumTitle(billsum, 3);
                } else {
                    refreshSumTitle(billsum, 1);
                }
            }

            public void OnItemModify(int position, BillCardInfo mBillCardInfo) {

                Intent mIntent = new Intent(getActivity(), BillAddActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(InfoText.BILL_MODIFY_REQUEST, mBillCardInfo);
                mBundle.putSerializable(InfoText.BILL_ADD_REQUEST, InfoText.REQUEST_MODIFY_BILL);
                mBundle.putSerializable(InfoText.CURRENT_ITEM_POSITION, position);
                mIntent.putExtras(mBundle);
                startActivityForResult(mIntent, InfoText.REQUEST_MODIFY_BILL);
            }
        });

        imgBtnAddBill.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //此处写Button点击事件
                Intent mIntent = new Intent(getActivity(), BillAddActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(InfoText.BILL_ADD_REQUEST, InfoText.REQUEST_ADD_BILL);
                mIntent.putExtras(mBundle);
                startActivityForResult(mIntent, InfoText.REQUEST_ADD_BILL);
            }
        });

    }

    /**
     * 初始化“已报账”面板
     */
    private void initTabBillDelete(View view) {

        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragment_container_2);

        rvBillDelete = (RecyclerView) view.findViewById(R.id.fragment_main_recycler_view_2);
        rvBillDelete.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvBillDelete.setLayoutManager(layoutManager);

        tvTotalBillInfo = (TextView) view.findViewById(R.id.total_bill_info_2);
        tvHandleBillPercent = (TextView) view.findViewById(R.id.handle_bill_percent_2);
        waveBackgroundView = (WaveView) view.findViewById(R.id.wave_background_view_2);
        waveFillableLoaders = (CircularFillableLoaders) view.findViewById(R.id.wave_fillable_loaders_2);

        df.applyPattern("0.00");

        //以下初始化面板上的具体数据内容，示例如下：
        itemsBillDeleteData = new ArrayList<>();
        mTotalMoney = 0.00;
        mHandledMoney = 0.00;
        mPendingMoney = 0.00;

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
        databaseAccess.open();
        String SQLStr = "SELECT JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,DEALDATE,BILLMEMO,DEALFLAG,ICONNAME "
                + "FROM T_BILLINFO A,T_STEPINFO B WHERE DEALFLAG<>2 AND STEPLEVEL=0 AND STEPTITLE=BILLTYPE";
        Cursor mCursor = databaseAccess.querySQL(SQLStr);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                if ("1".equals(mCursor.getString(mCursor.getColumnIndex("DEALFLAG")))) {
                    BillCardInfo mBillCardInfo = new BillCardInfo();
                    mBillCardInfo.setmCardViewJNL(mCursor.getString(mCursor.getColumnIndex("JNL")));
                    mBillCardInfo.setmCardViewPhotoID(mCursor.getString(mCursor.getColumnIndex("ICONNAME")));
                    mBillCardInfo.setmCardViewBillType(mCursor.getString(mCursor.getColumnIndex("BILLTYPE")));
                    mBillCardInfo.setmCardViewBillChildType(mCursor.getString(mCursor.getColumnIndex("BILLCHILDTYPE")));
                    mBillCardInfo.setmCardViewBillSum(df.format(Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")))));
                    mBillCardInfo.setmCardViewBillDate(mCursor.getString(mCursor.getColumnIndex("BILLDATE")));
                    mBillCardInfo.setmCardViewBillCreateDate(mCursor.getString(mCursor.getColumnIndex("CREATEDATE")));
                    mBillCardInfo.setmCardViewBillDealDate(mCursor.getString(mCursor.getColumnIndex("DEALDATE")));
                    mBillCardInfo.setmCardViewBillMemo(mCursor.getString(mCursor.getColumnIndex("BILLMEMO")));

                    itemsBillDeleteData.add(mBillCardInfo);

                    mHandledMoney = mHandledMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                } else {
                    mPendingMoney = mPendingMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));

                }

            } while (mCursor.moveToNext());
        }
        mCursor.close();
        databaseAccess.close();

        mTotalMoney = mHandledMoney + mPendingMoney;

        tvTotalBillInfo.setText(InfoText.TEXT_TOTAL_SUM + df.format(mTotalMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_DEAL
                + df.format(mHandledMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_SUSPENDING + df.format(mPendingMoney));

        int mBillPercent = 0;
        if (mTotalMoney != 0) {
            mBillPercent = (int) ((mHandledMoney / mTotalMoney) * 100);
        }
        tvHandleBillPercent.setText(mBillPercent + "%");
        waveBackgroundView.setProgress(mBillPercent);
        waveFillableLoaders.setProgress(100 - mBillPercent);

        mainFragmentHistoryAdapter = new MainFragmentHistoryAdapter(itemsBillDeleteData, getActivity());
        mainFragmentHistoryAdapter.getMainFragmentHistoryAdapter(mainFragmentHistoryAdapter);
        rvBillDelete.setAdapter(mainFragmentHistoryAdapter);

        rvBillDelete.setItemAnimator(new OvershootInLeftAnimator());
        rvBillDelete.getItemAnimator().setAddDuration(500);
        rvBillDelete.getItemAnimator().setRemoveDuration(500);
        rvBillDelete.getItemAnimator().setMoveDuration(500);
        rvBillDelete.getItemAnimator().setChangeDuration(500);

        mainFragmentHistoryAdapter.setOnItemListener(new MainFragmentHistoryAdapter.OnItemListener() {

            @Override
            public void OnItemFile(int position, Double billsum, Boolean flagBillSubmit) {

                itemsBillDeleteData.remove(position);

                refreshSumTitle(billsum, 2);
            }

            @Override
            public void OnItemReturn(int position, Double billsum, Boolean flagBillSubmit) {

                itemsBillDeleteData.remove(position);

                refreshSumTitle(billsum, 4);
            }
        });
    }

    /**
     * 初始化“我”面板
     */
    private void initTabSetup(View view) {
        fragmentContainer = (FrameLayout) view.findViewById(R.id.fragment_container_3);

        btnModifyPassword = (Button) view.findViewById(R.id.modify_password);
        btnModifyType = (Button) view.findViewById(R.id.modify_type);
        btnBillFile = (Button) view.findViewById(R.id.settings_file);
        btnExchangeFile = (Button) view.findViewById(R.id.exchange_file);
        btnAbout = (Button) view.findViewById(R.id.about);
        imageUser = (ShapedImageView) view.findViewById(R.id.imageUser);

        llTab3Title = (LinearLayout) view.findViewById(R.id.ll_tab_3_title);
        flShapedImageView = (FrameLayout) view.findViewById(R.id.fl_shaped_image_view);

        final Context context = getActivity().getApplicationContext();
        File file = new File(context.getFilesDir() + "/images", "user_photo.png");
        if (!file.exists()) {
            //设置显示的客户图片，默认显示User
            imageUser.setImageResource(R.drawable.user);
        } else {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getAbsoluteFile() + "/images/user_photo.png");
                if (bitmap != null) {
                    imageUser.setImageBitmap(bitmap);
                } else {
                    imageUser.setImageResource(R.drawable.user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        imageUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent mIntent = new Intent(getActivity(), UserPhotoActivity.class);
                startActivityForResult(mIntent, InfoText.REQUEST_USER_PHOTO);
            }
        });

        btnModifyPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //此处写Button点击事件
                Intent intent = new Intent(getActivity(), ModifyPassActivity.class);
                startActivity(intent);
            }
        });

        btnModifyType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), BillTypeActivity.class);
                startActivity(intent);
            }
        });

        btnBillFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), BillFileActivity.class);
                startActivity(intent);
            }
        });

        btnExchangeFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final int[] mChoice = {0};

                new AlertDialog.Builder(getActivity())
                        .setTitle(InfoText.FUNCTION_SELECT)
                        .setSingleChoiceItems(new String[]{InfoText.FUNCTION_SELECT_BACKUP, InfoText.FUNCTION_SELECT_RESTORE}, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mChoice[0] = which;
                            }
                        }).setNegativeButton(InfoText.BUTTON_TEXT_CANCLE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(InfoText.TEXT_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        Date curDate = new Date(System.currentTimeMillis());

                        String strFileEnd = formatter.format(curDate);
                        String dirRoot = Environment.getExternalStorageDirectory().getPath();
                        String strPackageName = FileUtils.getAppProcessName(getActivity());

                        final String strPath = dirRoot + "/" + strPackageName + "/DataBackup" + strFileEnd;
                        final String filePath = dirRoot + "/" + strPackageName;

                        final DataProgressPopupWindow popupWindow = new DataProgressPopupWindow(getActivity(), flShapedImageView.getWidth(), flShapedImageView.getHeight());
                        popupWindow.switchDataProgressPopupWindow(llTab3Title);

                        switch (mChoice[0]) {
                            case 0: {
                                //此处必须另起一个进程，否则会阻塞主进程，且进度条不会更新
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();

                                        ArrayList<TableBillInfo> lstTableBillInfo = new ArrayList<>();
                                        ArrayList<TableStepInfo> lstTableStepInfo = new ArrayList<>();
                                        ArrayList<String> mDataSet = new ArrayList<>();
                                        mDataSet.clear();

                                        int mRecCount = 0;
                                        int mCurrentRec = 0;
                                        String mTempStr = "";

                                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                                        databaseAccess.open();

                                        String SQLStr = "SELECT COUNT(*) AS RECCOUNT FROM T_STEPINFO";
                                        Cursor mCursor = databaseAccess.querySQL(SQLStr);
                                        if (mCursor != null && mCursor.moveToFirst()) {
                                            mRecCount = mRecCount + mCursor.getInt(mCursor.getColumnIndex("RECCOUNT"));
                                        }
                                        mCursor.close();

                                        SQLStr = "SELECT COUNT(*) AS RECCOUNT FROM T_BILLINFO";
                                        mCursor = databaseAccess.querySQL(SQLStr);
                                        if (mCursor != null && mCursor.moveToFirst()) {
                                            mRecCount = mRecCount + mCursor.getInt(mCursor.getColumnIndex("RECCOUNT"));
                                        }
                                        mCursor.close();

                                        SQLStr = "SELECT JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,DEALDATE,BILLMEMO,DEALFLAG FROM T_BILLINFO";
                                        mCursor = databaseAccess.querySQL(SQLStr);
                                        if (mCursor != null && mCursor.moveToFirst()) {
                                            do {
                                                TableBillInfo mTableBillInfo = new TableBillInfo();
                                                mTableBillInfo.setpJnl(mCursor.getString(mCursor.getColumnIndex("JNL")));
                                                mTableBillInfo.setpBillType(mCursor.getString(mCursor.getColumnIndex("BILLTYPE")));
                                                mTableBillInfo.setpBillChildType(mCursor.getString(mCursor.getColumnIndex("BILLCHILDTYPE")));
                                                mTableBillInfo.setpBillSum(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                                                mTableBillInfo.setpBillDate(mCursor.getString(mCursor.getColumnIndex("BILLDATE")));
                                                mTableBillInfo.setpCreateDate(mCursor.getString(mCursor.getColumnIndex("CREATEDATE")));
                                                if (mCursor.getString(mCursor.getColumnIndex("DEALDATE")) == null) {
                                                    mTableBillInfo.setpDealDate("");
                                                } else {
                                                    mTableBillInfo.setpDealDate(mCursor.getString(mCursor.getColumnIndex("DEALDATE")));
                                                }
                                                mTableBillInfo.setpBillMemo(mCursor.getString(mCursor.getColumnIndex("BILLMEMO")));
                                                mTableBillInfo.setpDealFlag(mCursor.getString(mCursor.getColumnIndex("DEALFLAG")));

                                                lstTableBillInfo.add(mTableBillInfo);

                                                mTempStr = JSON.toJSONString(mTableBillInfo);
                                                mDataSet.add(mTempStr);

                                                mCurrentRec = mCurrentRec + 1;

                                                try {
                                                    if (500 >= mRecCount) {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                if (mRecCount == 0) {
                                                    popupWindow.refreshProgress(0, true);
                                                } else {
                                                    popupWindow.refreshProgress(div(mCurrentRec, mRecCount, 2), true);
                                                }

                                            } while (mCursor.moveToNext());
                                        }
                                        mCursor.close();

                                        try {
                                            FileUtils.backupDataToFiles(strPath, "T_BILLINFO.DAT", mDataSet);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        mDataSet.clear();

                                        SQLStr = "SELECT STEPTITLE,STEPLEVEL,FATHERTITLE,ICONNAME FROM T_STEPINFO";
                                        mCursor = databaseAccess.querySQL(SQLStr);
                                        if (mCursor != null && mCursor.moveToFirst()) {
                                            do {
                                                TableStepInfo mTableStepInfo = new TableStepInfo();
                                                mTableStepInfo.setpStepTitle(mCursor.getString(mCursor.getColumnIndex("STEPTITLE")));
                                                mTableStepInfo.setpStepLevel(mCursor.getString(mCursor.getColumnIndex("STEPLEVEL")));
                                                if (mCursor.getString(mCursor.getColumnIndex("FATHERTITLE")) == null) {
                                                    mTableStepInfo.setpFatherTitle("");
                                                } else {
                                                    mTableStepInfo.setpFatherTitle(mCursor.getString(mCursor.getColumnIndex("FATHERTITLE")));
                                                }
                                                if (mCursor.getString(mCursor.getColumnIndex("ICONNAME")) == null) {
                                                    mTableStepInfo.setpIconName("");
                                                } else {
                                                    mTableStepInfo.setpIconName(mCursor.getString(mCursor.getColumnIndex("ICONNAME")));
                                                }

                                                lstTableStepInfo.add(mTableStepInfo);

                                                mTempStr = JSON.toJSONString(mTableStepInfo);
                                                mDataSet.add(mTempStr);

                                                mCurrentRec = mCurrentRec + 1;

                                                try {
                                                    if (500 >= mRecCount) {
                                                        Thread.sleep(50);
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                                if (mRecCount == 0) {
                                                    popupWindow.refreshProgress(0, true);
                                                } else {
                                                    popupWindow.refreshProgress(div(mCurrentRec, mRecCount, 2), true);
                                                }

                                            } while (mCursor.moveToNext());
                                        }
                                        mCursor.close();
                                        databaseAccess.close();

                                        try {
                                            FileUtils.backupDataToFiles(strPath, "T_STEPINFO.DAT", mDataSet);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText(InfoText.TEXT_SUCCESS_TITLE)
                                                .setContentText(InfoText.TEXT_SUCCESS_DATA_BACKUP)
                                                .setConfirmText(InfoText.TEXT_OK)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {

                                                        popupWindow.switchDataProgressPopupWindow(llTab3Title);

                                                        sDialog.dismissWithAnimation();
                                                    }
                                                })
                                                .show();
                                    }
                                }).start();

                                break;
                            }
                            case 1: {
                                if (FileUtils.getCurrentChildDirectory(filePath) == null) {

                                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                            .setContentText(InfoText.TEXT_ERROR_DATA_RESTORE)
                                            .setConfirmText(InfoText.TEXT_OK)
                                            .show();

                                    popupWindow.switchDataProgressPopupWindow(llTab3Title);

                                    break;
                                }

                                final int[] mChoice_1 = {0};

                                ArrayList<String> mDataSet = new ArrayList<>();
                                mDataSet.clear();
                                mDataSet.addAll(FileUtils.getCurrentChildDirectory(filePath));

                                final String[] dirItems = mDataSet.toArray(new String[mDataSet.size()]);

                                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(InfoText.TEXT_ASK_TITLE)
                                        .setContentText(InfoText.TEXT_ASK_DATA_INPUT)
                                        .setCancelText(InfoText.TEXT_CANCEL)
                                        .setConfirmText(InfoText.TEXT_CONFIRM)
                                        .showCancelButton(true)
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                popupWindow.switchDataProgressPopupWindow(llTab3Title);
                                                
                                                sDialog.cancel();
                                            }
                                        })
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                Boolean flagExec = true;

                                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                                                databaseAccess.open();

                                                try {
                                                    String strSQL = "DELETE FROM T_BILLINFO";
                                                    strSQL = databaseAccess.ddlSQL(strSQL, true);
                                                    if (!strSQL.isEmpty()) {
                                                        flagExec = false;
                                                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                                .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                                                .setContentText(strSQL)
                                                                .setConfirmText(InfoText.TEXT_OK)
                                                                .show();
                                                    }

                                                    if (flagExec) {
                                                        strSQL = "DELETE FROM T_STEPINFO";
                                                        strSQL = databaseAccess.ddlSQL(strSQL, false);
                                                        if (!strSQL.isEmpty()) {
                                                            flagExec = false;
                                                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                                    .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                                                    .setContentText(strSQL)
                                                                    .setConfirmText(InfoText.TEXT_OK)
                                                                    .show();
                                                        }
                                                    }
                                                } finally {
                                                    databaseAccess.close();
                                                }

                                                if (flagExec) {
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle(InfoText.DIRECTORY_SELECT_RESTORE)
                                                            .setSingleChoiceItems(dirItems, 0, new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    mChoice_1[0] = which;
                                                                }
                                                            }).setNegativeButton(InfoText.BUTTON_TEXT_CANCLE, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    }).setPositiveButton(InfoText.TEXT_OK, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //此处必须另起一个进程，否则会阻塞主进程，且进度条不会更新
                                                            new Thread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Looper.prepare();

                                                                    ArrayList<String> mBillInfoList = new ArrayList<>();
                                                                    ArrayList<String> mStepInfoList = new ArrayList<>();
                                                                    int mRecCount = 0;
                                                                    int mCurrentRec = 0;

                                                                    try {
                                                                        mBillInfoList = FileUtils.restoreDataFromFiles(filePath + "/" + dirItems[mChoice_1[0]].toString(), "T_BILLINFO.DAT");
                                                                        mRecCount = mBillInfoList.size();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    try {
                                                                        mStepInfoList = FileUtils.restoreDataFromFiles(filePath + "/" + dirItems[mChoice_1[0]].toString(), "T_STEPINFO.DAT");
                                                                        mRecCount = mRecCount + mStepInfoList.size();
                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }


                                                                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                                                                    databaseAccess.open();

                                                                    databaseAccess.beginTransaction();

                                                                    try {

                                                                        Boolean flagSQL = true;

                                                                        TableBillInfo mTableBillInfo = new TableBillInfo();
                                                                        String strSQL = "";

                                                                        for (String jsonString : mBillInfoList) {
                                                                            mTableBillInfo = JSON.parseObject(jsonString, TableBillInfo.class);

                                                                            strSQL = "INSERT INTO T_BILLINFO VALUES('"
                                                                                    + mTableBillInfo.getpJnl().toString() + "','"
                                                                                    + mTableBillInfo.getpBillType().toString() + "','"
                                                                                    + mTableBillInfo.getpBillChildType().toString() + "',"
                                                                                    + mTableBillInfo.getpBillSum().toString() + ",'"
                                                                                    + mTableBillInfo.getpBillDate().toString() + "','"
                                                                                    + mTableBillInfo.getpCreateDate().toString() + "','"
                                                                                    + mTableBillInfo.getpDealDate().toString() + "','"
                                                                                    + mTableBillInfo.getpBillMemo().toString() + "',"
                                                                                    + mTableBillInfo.getpDealFlag().toString() + ")";
                                                                            strSQL = databaseAccess.ddlSQL(strSQL, true);
                                                                            if (!strSQL.isEmpty()) {
                                                                                flagSQL = false;
                                                                                popupWindow.refreshProgress(div(mCurrentRec, mRecCount, 2), false);
                                                                                break;
                                                                            }

                                                                            mCurrentRec = mCurrentRec + 1;

                                                                            try {
                                                                                if (500 >= mRecCount) {
                                                                                    Thread.sleep(50);
                                                                                }
                                                                            } catch (InterruptedException e) {
                                                                                e.printStackTrace();
                                                                            }

                                                                            if (mRecCount == 0) {
                                                                                popupWindow.refreshProgress(0, true);
                                                                            } else {
                                                                                popupWindow.refreshProgress(div(mCurrentRec, mRecCount, 2), true);
                                                                            }

                                                                        }

                                                                        if (flagSQL) {

                                                                            TableStepInfo mTableStepInfo = new TableStepInfo();

                                                                            for (String jsonString : mStepInfoList) {
                                                                                mTableStepInfo = JSON.parseObject(jsonString, TableStepInfo.class);

                                                                                strSQL = "INSERT INTO T_STEPINFO VALUES('"
                                                                                        + mTableStepInfo.getpStepTitle().toString() + "',"
                                                                                        + mTableStepInfo.getpStepLevel().toString() + ",'"
                                                                                        + mTableStepInfo.getpFatherTitle().toString() + "','"
                                                                                        + mTableStepInfo.getpIconName().toString() + "')";
                                                                                strSQL = databaseAccess.ddlSQL(strSQL, true);
                                                                                if (!strSQL.isEmpty()) {
                                                                                    flagSQL = false;
                                                                                    popupWindow.refreshProgress(div(mCurrentRec, mRecCount, 2), false);
                                                                                    break;
                                                                                }

                                                                                mCurrentRec = mCurrentRec + 1;

                                                                                try {
                                                                                    if (500 >= mRecCount) {
                                                                                        Thread.sleep(50);
                                                                                    }
                                                                                } catch (InterruptedException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                                if (mRecCount == 0) {
                                                                                    popupWindow.refreshProgress(0, true);
                                                                                } else {
                                                                                    popupWindow.refreshProgress(div(mCurrentRec, mRecCount, 2), true);
                                                                                }

                                                                            }

                                                                            if (flagSQL) {
                                                                                databaseAccess.setTransactionSuccessful();
                                                                            }
                                                                        }
                                                                    } finally {
                                                                        databaseAccess.endTransaction();
                                                                        databaseAccess.close();
                                                                    }
                                                                    Looper.loop();
                                                                }
                                                            }).start();
                                                        }
                                                    }).show();
                                                }

                                                sDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();

                                break;
                            }
                            default: {
                                dialog.dismiss();
                                break;
                            }
                        }
                    }
                }).show();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Refresh
     */
    public void refresh(int position) {
        if (position == 0) {
            if (getArguments().getInt("index", 0) > 0 && rvBillAdd != null) {
                rvBillAdd.smoothScrollToPosition(0);
            }
        }

        if (position == 1) {
            if (getArguments().getInt("index", 0) > 0 && rvBillDelete != null) {
                rvBillDelete.smoothScrollToPosition(0);
            }
        }
    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed(int position) {
        // Do what you want here, for example animate the content
        if (fragmentContainer != null) {
            if (position == 0) {
                itemsBillAddData.clear();
                mTotalMoney = 0.00;
                mHandledMoney = 0.00;
                mPendingMoney = 0.00;

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();
                String SQLStr = "SELECT JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,DEALDATE,BILLMEMO,DEALFLAG,ICONNAME "
                        + "FROM T_BILLINFO A,T_STEPINFO B WHERE DEALFLAG<>2 AND STEPLEVEL=0 AND STEPTITLE=BILLTYPE";
                Cursor mCursor = databaseAccess.querySQL(SQLStr);
                if (mCursor != null && mCursor.moveToFirst()) {
                    do {
                        if ("0".equals(mCursor.getString(mCursor.getColumnIndex("DEALFLAG")))) {
                            BillCardInfo mBillCardInfo = new BillCardInfo();
                            mBillCardInfo.setmCardViewJNL(mCursor.getString(mCursor.getColumnIndex("JNL")));
                            mBillCardInfo.setmCardViewPhotoID(mCursor.getString(mCursor.getColumnIndex("ICONNAME")));
                            mBillCardInfo.setmCardViewBillType(mCursor.getString(mCursor.getColumnIndex("BILLTYPE")));
                            mBillCardInfo.setmCardViewBillChildType(mCursor.getString(mCursor.getColumnIndex("BILLCHILDTYPE")));
                            mBillCardInfo.setmCardViewBillSum(df.format(Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")))));
                            mBillCardInfo.setmCardViewBillDate(mCursor.getString(mCursor.getColumnIndex("BILLDATE")));
                            mBillCardInfo.setmCardViewBillCreateDate(mCursor.getString(mCursor.getColumnIndex("CREATEDATE")));
                            mBillCardInfo.setmCardViewBillMemo(mCursor.getString(mCursor.getColumnIndex("BILLMEMO")));

                            itemsBillAddData.add(mBillCardInfo);

                            mPendingMoney = mPendingMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                        } else {
                            mHandledMoney = mHandledMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                        }

                    } while (mCursor.moveToNext());
                }
                mCursor.close();
                databaseAccess.close();

                mTotalMoney = mHandledMoney + mPendingMoney;

                tvTotalBillInfo.setText(InfoText.TEXT_TOTAL_SUM + df.format(mTotalMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_DEAL
                        + df.format(mHandledMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_SUSPENDING + df.format(mPendingMoney));

                int mBillPercent = 0;
                if (mTotalMoney != 0) {
                    mBillPercent = (int) ((mHandledMoney / mTotalMoney) * 100);
                }
                tvHandleBillPercent.setText(mBillPercent + "%");
                waveBackgroundView.setProgress(mBillPercent);
                waveFillableLoaders.setProgress(100 - mBillPercent);

                mainFragmentAddAdapter.reloadMainFragmentAddAdapter(itemsBillAddData);
            }

            if (position == 1) {
                itemsBillDeleteData.clear();
                mTotalMoney = 0.00;
                mHandledMoney = 0.00;
                mPendingMoney = 0.00;

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity());
                databaseAccess.open();
                String SQLStr = "SELECT JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,DEALDATE,BILLMEMO,DEALFLAG,ICONNAME "
                        + "FROM T_BILLINFO A,T_STEPINFO B WHERE DEALFLAG<>2 AND STEPLEVEL=0 AND STEPTITLE=BILLTYPE";
                Cursor mCursor = databaseAccess.querySQL(SQLStr);
                if (mCursor != null && mCursor.moveToFirst()) {
                    do {
                        if ("1".equals(mCursor.getString(mCursor.getColumnIndex("DEALFLAG")))) {
                            BillCardInfo mBillCardInfo = new BillCardInfo();
                            mBillCardInfo.setmCardViewJNL(mCursor.getString(mCursor.getColumnIndex("JNL")));
                            mBillCardInfo.setmCardViewPhotoID(mCursor.getString(mCursor.getColumnIndex("ICONNAME")));
                            mBillCardInfo.setmCardViewBillType(mCursor.getString(mCursor.getColumnIndex("BILLTYPE")));
                            mBillCardInfo.setmCardViewBillChildType(mCursor.getString(mCursor.getColumnIndex("BILLCHILDTYPE")));
                            mBillCardInfo.setmCardViewBillSum(df.format(Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")))));
                            mBillCardInfo.setmCardViewBillDate(mCursor.getString(mCursor.getColumnIndex("BILLDATE")));
                            mBillCardInfo.setmCardViewBillCreateDate(mCursor.getString(mCursor.getColumnIndex("CREATEDATE")));
                            mBillCardInfo.setmCardViewBillDealDate(mCursor.getString(mCursor.getColumnIndex("DEALDATE")));
                            mBillCardInfo.setmCardViewBillMemo(mCursor.getString(mCursor.getColumnIndex("BILLMEMO")));

                            itemsBillDeleteData.add(mBillCardInfo);

                            mHandledMoney = mHandledMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                        } else {
                            mPendingMoney = mPendingMoney + Double.parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));

                        }

                    } while (mCursor.moveToNext());
                }
                mCursor.close();
                databaseAccess.close();

                mTotalMoney = mHandledMoney + mPendingMoney;

                tvTotalBillInfo.setText(InfoText.TEXT_TOTAL_SUM + df.format(mTotalMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_DEAL
                        + df.format(mHandledMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_SUSPENDING + df.format(mPendingMoney));

                int mBillPercent = 0;
                if (mTotalMoney != 0) {
                    mBillPercent = (int) ((mHandledMoney / mTotalMoney) * 100);
                }
                tvHandleBillPercent.setText(mBillPercent + "%");
                waveBackgroundView.setProgress(mBillPercent);
                waveFillableLoaders.setProgress(100 - mBillPercent);

                mainFragmentHistoryAdapter.reloadMainFragmentHistoryAdapter(itemsBillDeleteData);
            }

            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }
    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        BillCardInfo mBillCardInfo;

        if (requestCode == InfoText.REQUEST_ADD_BILL && resultCode == InfoText.RESULT_ADD_BILL) {
            mBillCardInfo = (BillCardInfo) data.getSerializableExtra(InfoText.BILL_ADD_BILLCARDINFO);
            if (mainFragmentAddAdapter != null) {
                mainFragmentAddAdapter.AddBillCardInfo(mBillCardInfo, getActivity());
                itemsBillAddData.add(0,mBillCardInfo);

                refreshSumTitle(Double.parseDouble(mBillCardInfo.getmCardViewBillSum()), 0);
            }
        }

        if (requestCode == InfoText.REQUEST_MODIFY_BILL && resultCode == InfoText.RESULT_ADD_BILL) {
            mBillCardInfo = (BillCardInfo) data.getSerializableExtra(InfoText.BILL_ADD_BILLCARDINFO);
            int mPosition = data.getExtras().getInt(InfoText.CURRENT_ITEM_POSITION);
            Double mDouble = data.getExtras().getDouble(InfoText.BILL_SUM_CHANGED);
            if (mainFragmentAddAdapter != null) {
                mainFragmentAddAdapter.UpdateBillCardInfo(mBillCardInfo, getActivity(), mPosition);

                refreshSumTitle(mDouble, 1);
            }

        }

        if (requestCode == InfoText.REQUEST_USER_PHOTO && resultCode == InfoText.RESULT_USER_PHOTO_SAVE) {
            File file = new File(getActivity().getFilesDir() + "/images", "user_photo.png");
            if (!file.exists()) {
                //设置显示的客户图片，默认显示User
                imageUser.setImageResource(R.drawable.user);
            } else {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(getActivity().getFilesDir().getAbsoluteFile() + "/images/user_photo.png");
                    if (bitmap != null) {
                        imageUser.setImageBitmap(bitmap);
                    } else {
                        imageUser.setImageResource(R.drawable.user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void refreshSumTitle(Double mDouble, int flagBillAdd) {

        switch (flagBillAdd) {
            case 0: {
                mTotalMoney = mTotalMoney + mDouble;
                mPendingMoney = mPendingMoney + mDouble;
                break;
            }
            case 1: {
                mTotalMoney = mTotalMoney - mDouble;
                mPendingMoney = mPendingMoney - mDouble;
                break;
            }
            case 2: {
                mTotalMoney = mTotalMoney - mDouble;
                mHandledMoney = mHandledMoney - mDouble;
                break;
            }
            case 3: {
                mPendingMoney = mPendingMoney - mDouble;
                mHandledMoney = mHandledMoney + mDouble;
                break;
            }
            default: {
                mPendingMoney = mPendingMoney + mDouble;
                mHandledMoney = mHandledMoney - mDouble;
                break;
            }
        }

        tvTotalBillInfo.setText(InfoText.TEXT_TOTAL_SUM + df.format(mTotalMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_DEAL
                + df.format(mHandledMoney) + InfoText.SEPARATOR_VERTICAL_LINE + InfoText.TEXT_BILL_SUSPENDING + df.format(mPendingMoney));

        int mBillPercent = 0;
        if (mTotalMoney != 0) {
            mBillPercent = (int) ((mHandledMoney / mTotalMoney) * 100);
        }

        tvHandleBillPercent.setText(mBillPercent + "%");
        waveBackgroundView.setProgress(mBillPercent);
        waveFillableLoaders.setProgress(100 - mBillPercent);
    }


    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商*100
     */
    public static int div(int v1, int v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Integer.toString(v1));
        BigDecimal b2 = new BigDecimal(Integer.toString(v2));
        Double b3 = b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
        return b3.intValue();
    }

}
