package com.leosoft.eam.billfile;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.DateRangePickerFragment;
import com.leosoft.eam.utils.InfoText;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Leo on 2017-06-11.
 */

public class FilterPopupWindow extends PopupWindow implements DateRangePickerFragment.OnDateRangeSelectedListener {

    private Context context;
    private View contentView;
    private View popupNoView;
    private GridView billfileTypeGridView;
    private TextView filterReset;
    private TextView filterSure;
    private EditText etBillFileMinSumInput;
    private EditText etBillFileMaxSumInput;
    private TextView tvBillFileBeginCreateDateInput;
    private TextView tvBillFileEndCreateDateInput;
    private TextView tvBillFileBeginDealDateInput;
    private TextView tvBillFileEndDealDateInput;
    private EditText etBillFileMemoInput;
    private ImageButton ibBillFilterFold;

    private List<BillFilterAttrsVo> billTypesList;

    private BillTypesAdapter billtypesAdapter;

    private Boolean isUnfold = false;

    private String strBeginDate = null;
    private String strEndDate = null;

    private int flagTextViewNum = 0;

    private LinearLayout llMoveDispear;
    private LinearLayout llMoveButton;

    private static final String TAG_DATETIMERANGE_FRAGMENT = "TAG_DATETIMERANGE_FRAGMENT";
    private DateRangePickerFragment dateRangePickerFragment;

    DecimalFormat df=(DecimalFormat) DecimalFormat.getInstance();

    public FilterPopupWindow(final AppCompatActivity context , final Handler mHandler) {

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popwin_filter_bill_file, null);
        popupNoView = contentView.findViewById(R.id.popup_bill_file_noview);
        billfileTypeGridView = (GridView) contentView.findViewById(R.id.gv_bill_file_type);
        etBillFileMinSumInput = (EditText) contentView.findViewById(R.id.et_bill_file_min_sum_input);
        etBillFileMaxSumInput = (EditText) contentView.findViewById(R.id.et_bill_file_max_sum_input);
        tvBillFileBeginCreateDateInput = (TextView) contentView.findViewById(R.id.tv_bill_file_begin_create_date_input);
        tvBillFileEndCreateDateInput = (TextView) contentView.findViewById(R.id.tv_bill_file_end_create_date_input);
        tvBillFileBeginDealDateInput = (TextView) contentView.findViewById(R.id.tv_bill_file_begin_deal_date_input);
        tvBillFileEndDealDateInput = (TextView) contentView.findViewById(R.id.tv_bill_file_end_deal_date_input);
        etBillFileMemoInput = (EditText) contentView.findViewById(R.id.et_bill_file_memo_input);
        ibBillFilterFold = (ImageButton)  contentView.findViewById(R.id.ib_bill_filter_fold);

        llMoveDispear = (LinearLayout) contentView.findViewById(R.id.ll_move_dispear);
        llMoveButton = (LinearLayout) contentView.findViewById(R.id.filter_bill_file_layout);

        filterReset = (TextView) contentView.findViewById(R.id.filter_reset);
        filterSure = (TextView) contentView.findViewById(R.id.filter_sure);

        billTypesList = new ArrayList<BillFilterAttrsVo>();

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        String SQLStr = "SELECT STEPTITLE FROM T_STEPINFO WHERE STEPLEVEL=0";
        Cursor mCursor = databaseAccess.querySQL(SQLStr);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                BillFilterAttrsVo vo = new BillFilterAttrsVo();
                vo.setValue(mCursor.getString(mCursor.getColumnIndex("STEPTITLE")));
                billTypesList.add(vo);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        databaseAccess.close();

        billtypesAdapter = new BillTypesAdapter(context);
        billfileTypeGridView.setAdapter(billtypesAdapter);
        billtypesAdapter.notifyDataSetChanged(false, billTypesList);
        billfileTypeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //设置当前选中的位置的状态为非。
                billTypesList.get(arg2).setChecked(!billTypesList.get(arg2).isChecked());
                /*for (int i = 0; i < billTypesList.size(); i++) {
                    //跳过已设置的选中的位置的状态
                    if (i == arg2) {
                        continue;
                    }
                    billTypesList.get(i).setChecked(false);
                }*/
                billtypesAdapter.notifyDataSetChanged(isUnfold, billTypesList);
            }
        });

        popupNoView.setOnClickListener(new CancelOnClickListener());
        contentView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                }
                return true;
            }
        });

        etBillFileMinSumInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etBillFileMinSumInput.getText().toString().isEmpty()) {
                        etBillFileMinSumInput.setGravity(Gravity.CENTER);
                    } else {
                        etBillFileMinSumInput.setText(df.format(Double.parseDouble(etBillFileMinSumInput.getText().toString())));
                        etBillFileMinSumInput.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    }

                    return false;
                }
                return false;
            }
        });

        etBillFileMaxSumInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (etBillFileMaxSumInput.getText().toString().isEmpty()) {
                        etBillFileMaxSumInput.setGravity(Gravity.CENTER);
                    } else {
                        etBillFileMaxSumInput.setText(df.format(Double.parseDouble(etBillFileMaxSumInput.getText().toString())));
                        etBillFileMaxSumInput.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    }

                    return false;
                }
                return false;
            }
        });

        ibBillFilterFold.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isUnfold) {
                    isUnfold = false;
                    ibBillFilterFold.setImageResource(R.drawable.ic_keyboard_arrow_down);
                    billtypesAdapter.notifyDataSetChanged(isUnfold, billTypesList);
                } else {
                    isUnfold = true;
                    ibBillFilterFold.setImageResource(R.drawable.ic_keyboard_arrow_up);
                    billtypesAdapter.notifyDataSetChanged(isUnfold, billTypesList);
                }
            }
        });

        tvBillFileBeginCreateDateInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setDateValue(0,context);
            }
        });

        tvBillFileEndCreateDateInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setDateValue(0,context);
            }
        });

        tvBillFileBeginDealDateInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setDateValue(1,context);
            }
        });

        tvBillFileEndDealDateInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setDateValue(1,context);
            }
        });

        llMoveButton.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                if (top < oldTop) {
                    llMoveDispear.setVisibility(View.GONE);
                }

                if (top == oldTop) {
                    llMoveDispear.setVisibility(View.VISIBLE);
                }
            }
        });

    // 重置的点击监听，将所有选项全设为false
        filterReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                etBillFileMinSumInput.setText("");
                etBillFileMinSumInput.setGravity(Gravity.CENTER);
                etBillFileMaxSumInput.setText("");
                etBillFileMaxSumInput.setGravity(Gravity.CENTER);
                tvBillFileBeginCreateDateInput.setText("");
                tvBillFileEndCreateDateInput.setText("");
                tvBillFileBeginDealDateInput.setText("");
                tvBillFileEndDealDateInput.setText("");
                etBillFileMemoInput.setText("");

                for (int i = 0; i < billTypesList.size(); i++) {
                    billTypesList.get(i).setChecked(false);
                }

                billtypesAdapter.notifyDataSetChanged(isUnfold, billTypesList);
            }
        });

        // 确定的点击监听，将所有已选中项列出
        filterSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String SQLStr = "SELECT JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,DEALDATE,BILLMEMO,DEALFLAG,ICONNAME "
                            + "FROM T_BILLINFO A,T_STEPINFO B WHERE DEALFLAG=2 AND STEPLEVEL=0 AND STEPTITLE=BILLTYPE";

                if (! etBillFileMinSumInput.getText().toString().isEmpty()) {
                    SQLStr = SQLStr + " AND BILLSUM>=" + etBillFileMinSumInput.getText().toString();
                }

                if (! etBillFileMaxSumInput.getText().toString().isEmpty()) {
                    SQLStr = SQLStr + " AND BILLSUM<=" + etBillFileMaxSumInput.getText().toString();
                }

                if (! tvBillFileBeginCreateDateInput.getText().toString().isEmpty()) {
                    SQLStr = SQLStr + " AND BILLDATE>='" + tvBillFileBeginCreateDateInput.getText().toString() + "'";
                }

                if (! tvBillFileEndCreateDateInput.getText().toString().isEmpty()) {
                    SQLStr = SQLStr + " AND BILLDATE<='" + tvBillFileEndCreateDateInput.getText().toString() + "'";
                }

                if (! tvBillFileBeginDealDateInput.getText().toString().isEmpty()) {
                    SQLStr = SQLStr + " AND DEALDATE>='" + tvBillFileBeginDealDateInput.getText().toString() + "'";
                }

                if (! tvBillFileEndDealDateInput.getText().toString().isEmpty()) {
                    SQLStr = SQLStr + " AND DEALDATE<='" + tvBillFileEndDealDateInput.getText().toString() + "'";
                }

                if (! etBillFileMemoInput.getText().toString().isEmpty()) {
                    SQLStr = SQLStr + " AND BILLMEMO LIKE '%" + etBillFileMemoInput.getText().toString() + "%'";
                }

                String billTypesStr = "";
                for (int i = 0; i < billTypesList.size(); i++) {
                    if (billTypesList.get(i).isChecked()) {
                        billTypesStr = billTypesStr + "'" + billTypesList.get(i).getValue() + "',";
                    }
                }

                if (! billTypesStr.isEmpty()) {
                    SQLStr = SQLStr + " AND BILLTYPE IN (" + billTypesStr + "'')";
                }

                SQLStr = SQLStr + " ORDER BY DEALDATE DESC";

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString(InfoText.POPUP_WINDOW_RESULT,SQLStr);
                msg.setData(data);
                msg.what = InfoText.RESULT_POPUPWINOW_SAVE;
                mHandler.sendMessage(msg);

                dismiss();
            }
        });

        df.applyPattern("0.00");

        this.setContentView(contentView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(00000000);
        this.setBackgroundDrawable(dw);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();

    }

    public void setPopWinTouchInterceptor(final FilterPopupWindow mFilterPopupWindow) {

        mFilterPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View mView = v.findFocus();

                if (mView != null && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_MOVE) && mView instanceof EditText) {
                    int scrcoords[] = new int[2];
                    mView.getLocationOnScreen(scrcoords);
                    float x = event.getRawX() + mView.getLeft() - scrcoords[0];
                    float y = event.getRawY() + mView.getTop() - scrcoords[1];
                    if (x < mView.getLeft() || x > mView.getRight() || y < mView.getTop() || y > mView.getBottom()) {
                        if (mView.getId() == etBillFileMinSumInput.getId() && !("".equals(etBillFileMinSumInput.getText().toString()))) {
                            etBillFileMinSumInput.setText(df.format(Double.parseDouble(etBillFileMinSumInput.getText().toString())));
                            etBillFileMinSumInput.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                        }

                        if (mView.getId() == etBillFileMaxSumInput.getId() && !("".equals(etBillFileMaxSumInput.getText().toString()))) {
                            etBillFileMaxSumInput.setText(df.format(Double.parseDouble(etBillFileMaxSumInput.getText().toString())));
                            etBillFileMaxSumInput.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                        }

                        if (etBillFileMaxSumInput.getText().toString().isEmpty()) {
                            etBillFileMaxSumInput.setGravity(Gravity.CENTER);
                        }

                        if (etBillFileMinSumInput.getText().toString().isEmpty()) {
                            etBillFileMinSumInput.setGravity(Gravity.CENTER);
                        }
                    }
                }

                return false;
            }
        });
    }

    @Override
    public void onDateRangeSelected(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = new GregorianCalendar(startYear, startMonth, startDay,0,0,0);
        strBeginDate = format.format(calendar.getTime());

        calendar = new GregorianCalendar(endYear, endMonth, endDay,0,0,0);
        strEndDate = format.format(calendar.getTime());

        switch(flagTextViewNum){
            case 0: {
                tvBillFileBeginCreateDateInput.setText(strBeginDate);
                tvBillFileEndCreateDateInput.setText(strEndDate);
                break;
            }
            default: {
                tvBillFileBeginDealDateInput.setText(strBeginDate);
                tvBillFileEndDealDateInput.setText(strEndDate);
                break;
            }
        }
    }

    public class CancelOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    }

    public boolean onKeyDown(Context context, int keyCode, KeyEvent event) {
        this.context = context;
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }
        return true;
    }

    public void showFilterPopup(View parent) {
        if (!this.isShowing()) {
            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }

    private void setDateValue(int flagTextViewNum,AppCompatActivity context) {
        this.flagTextViewNum = flagTextViewNum;

        dateRangePickerFragment = (DateRangePickerFragment) context.getSupportFragmentManager().findFragmentByTag(TAG_DATETIMERANGE_FRAGMENT);
        if(dateRangePickerFragment == null) {
            dateRangePickerFragment = DateRangePickerFragment.newInstance(FilterPopupWindow.this,false);
        }
        dateRangePickerFragment.show(context.getSupportFragmentManager(),InfoText.DATE_RANGE_SELECT);
    }

}
