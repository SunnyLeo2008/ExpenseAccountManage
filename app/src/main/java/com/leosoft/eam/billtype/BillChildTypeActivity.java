package com.leosoft.eam.billtype;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.InfoText;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.yarolegovich.lovelydialog.LovelySaveStateHandler;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BillChildTypeActivity extends AppCompatActivity {

    private List<String> mBillChildTypeList  =  new ArrayList<>();
    private BillChildTypeAdapter mBillChildTypeAdapter;
    private RecyclerView rvBillChildType;

    private Cursor mCursor;
    private String SQLStr;
    private String mBillTypeTitle;

    private LovelySaveStateHandler saveStateHandler;

    private RecyclerTouchListener onTouchListener;

    private static final int ID_TEXT_INPUT_DIALOG = R.id.bill_child_type_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_child_type);

        saveStateHandler = new LovelySaveStateHandler();

        TextView tvBillChildTypeTitle = (TextView) findViewById(R.id.bill_child_type_title);
        ImageButton ibBillChildTypeBack = (ImageButton) findViewById(R.id.ib_bill_child_type_back);
        ImageButton ibBillChildTypeAdd = (ImageButton) findViewById(R.id.ib_bill_child_type_add);

        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        mBillTypeTitle = mBundle.getString(InfoText.BILL_CHILD_TYPE_REQUEST).toString();

        tvBillChildTypeTitle.setText(mBillTypeTitle);

        mBillChildTypeList.clear();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        SQLStr = "SELECT STEPTITLE FROM T_STEPINFO WHERE STEPLEVEL=1 AND FATHERTITLE='" + mBillTypeTitle + "'";
        mCursor = databaseAccess.querySQL(SQLStr);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                mBillChildTypeList.add(mCursor.getString(mCursor.getColumnIndex("STEPTITLE")));
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        databaseAccess.close();

        rvBillChildType = (RecyclerView) findViewById(R.id.rv_bill_child_type);
        mBillChildTypeAdapter = new BillChildTypeAdapter(mBillChildTypeList, this);
        rvBillChildType.setAdapter(mBillChildTypeAdapter);
        rvBillChildType.setLayoutManager(new LinearLayoutManager(this));
        mBillChildTypeAdapter.getBillChildTypeAdapter(mBillChildTypeAdapter);

        onTouchListener = new RecyclerTouchListener(this, rvBillChildType);
        onTouchListener.setSwipeOptionViews(R.id.bill_child_type_modify, R.id.bill_child_type_delete)
                .setSwipeable(R.id.rowFG, R.id.rowBG, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.bill_child_type_delete) {
                            deleteBillChildType(mBillChildTypeList.get(position).toString(), position);
                        } else if (viewID == R.id.bill_child_type_modify) {
                            modifyBillChildType(position);
                        }
                    }
                });

        ibBillChildTypeBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibBillChildTypeAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                showTextInputDialog(null);
            }
        });

        mBillChildTypeAdapter.setOnItemListener(new BillChildTypeAdapter.OnItemListener() {

            @Override
            public void OnItemChangeSwipeState(Boolean flagSwipeState) {
                onTouchListener.setSwipeable(flagSwipeState);
            }

            @Override
            public Boolean OnItemModifyConfirm(int position, String oldValue, String newValue) {
                Boolean result = false;

                if (newValue.isEmpty()) {
                    new SweetAlertDialog(BillChildTypeActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(InfoText.TEXT_ERROR_BILL_TYPE)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                } else {
                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getBaseContext());
                    databaseAccess.open();

                    String SQLStr = "UPDATE T_STEPINFO SET STEPTITLE='" + newValue
                            + "' WHERE STEPLEVEL=1 AND STEPTITLE='" + oldValue
                            + "' AND FATHERTITLE='" + mBillTypeTitle + "'";
                    SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                    if (!SQLStr.isEmpty()) {
                        new SweetAlertDialog(BillChildTypeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                .setContentText(SQLStr)
                                .setConfirmText(InfoText.TEXT_OK)
                                .show();
                    } else {
                        mBillChildTypeAdapter.updateBillChildType(position,newValue);
                    }
                    databaseAccess.close();
                    result = true;
                }
                return result;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        rvBillChildType.addOnItemTouchListener(onTouchListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rvBillChildType.removeOnItemTouchListener(onTouchListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        saveStateHandler.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        if (LovelySaveStateHandler.wasDialogOnScreen(savedState)) {
            //Dialog won't be restarted automatically, so we need to call this method.
            //Each dialog knows how to restore its state
            showTextInputDialog(savedState);
        }
    }

    private void modifyBillChildType(int position) {

        BillChildTypeAdapter.ViewHolder currentItemViewHolder = (BillChildTypeAdapter.ViewHolder) rvBillChildType.findViewHolderForLayoutPosition(position);

        if (currentItemViewHolder.elBillChildType.getContentRelativeLayout().getVisibility() == View.VISIBLE) {
            currentItemViewHolder.elBillChildType.hide();
        }
        else {
            currentItemViewHolder.elBillChildType.show();
            onTouchListener.setSwipeable(false);
        }
    }

    private void deleteBillChildType(String item , final int position) {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(InfoText.TEXT_ASK_TITLE)
                .setContentText("[" + mBillChildTypeList.get(position).toString() + "]" + InfoText.TEXT_ASK_DELETE)
                .setCancelText(InfoText.TEXT_CANCEL)
                .setConfirmText(InfoText.TEXT_CONFIRM)
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(BillChildTypeActivity.this);
                        databaseAccess.open();
                        String SQLStr = "DELETE FROM T_STEPINFO WHERE STEPLEVEL=1 AND FATHERTITLE='" + mBillTypeTitle
                                + "' AND STEPTITLE='" + mBillChildTypeList.get(position).toString() + "'";
                        SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                        if (!SQLStr.isEmpty()) {
                            new SweetAlertDialog(BillChildTypeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                    .setContentText(SQLStr)
                                    .setConfirmText(InfoText.TEXT_OK)
                                    .show();
                        } else {
                            mBillChildTypeAdapter.deleteBillChildType(position);
                            mBillChildTypeList.remove(position);
                        }
                        databaseAccess.close();

                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

    private void showTextInputDialog(Bundle savedInstanceState) {

        new LovelyTextInputDialog(BillChildTypeActivity.this, R.style.EditTextTintTheme)
                .setTopColorRes(R.color.colorPrimaryDark)
                .setMessage(R.string.text_input_message)
                .setTitle(R.string.text_input_title)
                .setIcon(R.drawable.ic_assignment)
                .setInstanceStateHandler(ID_TEXT_INPUT_DIALOG, saveStateHandler)
                .setInputFilter(R.string.text_input_error_message, new LovelyTextInputDialog.TextFilter() {
                    @Override
                    public boolean check(String text) {
                        return text.matches("\\w{1,30}");
                    }
                })
                .setNegativeButton(R.string.but_cancle, null)
                .setConfirmButton(R.string.but_done, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                    @Override
                    public void onTextInputConfirmed(String text) {

                        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(BillChildTypeActivity.this);
                        databaseAccess.open();

                        String SQLStr = "INSERT INTO T_STEPINFO (STEPTITLE,STEPLEVEL,FATHERTITLE) VALUES('"
                                + text + "',1,'" + mBillTypeTitle + "')";
                        SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                        if (!SQLStr.isEmpty()) {
                            new SweetAlertDialog(BillChildTypeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                    .setContentText(SQLStr)
                                    .setConfirmText(InfoText.TEXT_OK)
                                    .show();
                        } else {
                            if (mBillChildTypeAdapter != null) {
                                mBillChildTypeList.add(0,text);
                                mBillChildTypeAdapter.addBillChildType(text);
                            }
                        }
                        databaseAccess.close();
                    }
                })
                .setSavedInstanceState(savedInstanceState)
                .show();
    }
}
