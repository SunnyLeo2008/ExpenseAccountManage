package com.leosoft.eam.billadd;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.leosoft.eam.R;
import com.leosoft.eam.utils.BillCardInfo;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.InfoText;
import com.leosoft.eam.utils.InitStepPickerData;
import com.leosoft.eam.utils.SystemApplication;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import cn.jeesoft.widget.pickerview.CharacterPickerView;
import cn.jeesoft.widget.pickerview.OnOptionChangedListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class BillAddActivity extends AppCompatActivity {

    private ImageButton imgbtnBillAddBack;
    private Button btnBillAddSave;
    private CharacterPickerView cpvBillType;
    private EditText edtBillSumInput;
    private EditText edtBillMemoInput;
    private LinearLayout ll_bill_sum;
    private LinearLayout ll_bill_date;
    private LinearLayout ll_bill_memo;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private TextView text_bill_date;

    private static final String TAG = "DateTimePicker";
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private BillCardInfo mBillCardInfo;
    private BillCardInfo mReceiveBillCardInfo;
    private Bundle mMainFragmentBundle;
    private int mMainFragmentRequestCode;
    private int mItemPosition;
    private Double oldSum = 0.00;
    private Double newSum = 0.00;

    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_add);
        SystemApplication.getInstance().addActivity(this);

        imgbtnBillAddBack = (ImageButton) findViewById(R.id.imageButtonBillAddBack);
        btnBillAddSave = (Button) findViewById(R.id.bill_add_save);
        cpvBillType = (CharacterPickerView) findViewById(R.id.character_picker_view_bill_type);
        edtBillSumInput = (EditText) findViewById(R.id.edit_bill_sum_input);
        edtBillMemoInput = (EditText) findViewById(R.id.edit_bill_memo_input);
        ll_bill_sum = (LinearLayout) findViewById(R.id.ll_bill_sum);
        ll_bill_date = (LinearLayout) findViewById(R.id.ll_bill_date);
        ll_bill_memo = (LinearLayout) findViewById(R.id.ll_bill_memo);
        text_bill_date = (TextView) findViewById(R.id.text_bill_date);

        InitStepPickerData.setPickerData(this, cpvBillType);

        df.applyPattern("0.00");

        mBillCardInfo = new BillCardInfo();

        Intent mIntent = getIntent();
        mMainFragmentBundle = mIntent.getExtras();
        mMainFragmentRequestCode = mMainFragmentBundle.getInt(InfoText.BILL_ADD_REQUEST);
        mItemPosition = mMainFragmentBundle.getInt(InfoText.CURRENT_ITEM_POSITION);

        if (mMainFragmentRequestCode == InfoText.REQUEST_MODIFY_BILL) {
            mReceiveBillCardInfo = (BillCardInfo) mMainFragmentBundle.get(InfoText.BILL_MODIFY_REQUEST);

            mBillCardInfo.setmCardViewBillMemo(mReceiveBillCardInfo.getmCardViewBillMemo());
            mBillCardInfo.setmCardViewBillDate(mReceiveBillCardInfo.getmCardViewBillDate());
            mBillCardInfo.setmCardViewBillChildType(mReceiveBillCardInfo.getmCardViewBillChildType());
            mBillCardInfo.setmCardViewBillSum(mReceiveBillCardInfo.getmCardViewBillSum());
            mBillCardInfo.setmCardViewBillType(mReceiveBillCardInfo.getmCardViewBillType());
            mBillCardInfo.setmCardViewJNL(mReceiveBillCardInfo.getmCardViewJNL());

            edtBillSumInput.setText(mBillCardInfo.getmCardViewBillSum());
            text_bill_date.setText(mBillCardInfo.getmCardViewBillDate());
            edtBillMemoInput.setText(mBillCardInfo.getmCardViewBillMemo());

            oldSum = Double.parseDouble(mReceiveBillCardInfo.getmCardViewBillSum());

        }

        imgbtnBillAddBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //此处写Button点击事件
                Intent intent = new Intent();
                setResult(InfoText.RESULT_ADD_BILL_BACK, intent);
                finish();

            }
        });

        ll_bill_sum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //为EditText弹出数字软键盘
                edtBillSumInput.requestFocus();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edtBillSumInput, 0);
            }
        });

        edtBillSumInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!edtBillSumInput.getText().toString().isEmpty()) {
                        edtBillSumInput.setText(df.format(Double.parseDouble(edtBillSumInput.getText().toString())));
                    }

                    return false;
                }
                return false;
            }
        });

        btnBillAddSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Boolean flagOK = true;
                //此处写Button点击事件
                if (mBillCardInfo.getmCardViewJNL().isEmpty()) {
                    flagOK = false;
                    new SweetAlertDialog(BillAddActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(InfoText.TEXT_ERROR_PK)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                }

                if (mBillCardInfo.getmCardViewBillType().isEmpty() && flagOK) {
                    flagOK = false;
                    new SweetAlertDialog(BillAddActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(InfoText.TEXT_ERROR_BILL_TYPE)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                }

                if (mBillCardInfo.getmCardViewBillChildType().isEmpty() && flagOK) {
                    flagOK = false;
                    new SweetAlertDialog(BillAddActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(InfoText.TEXT_ERROR_BILL_TYPE)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                }

                if (edtBillSumInput.getText().toString().isEmpty() && flagOK) {
                    flagOK = false;
                    new SweetAlertDialog(BillAddActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(InfoText.TEXT_ERROR_BILL_SUM)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                } else {
                    if (flagOK) {
                        mBillCardInfo.setmCardViewBillSum(edtBillSumInput.getText().toString());
                        newSum = Double.parseDouble(edtBillSumInput.getText().toString());
                    }
                }

                if (text_bill_date.getText().toString().isEmpty() && flagOK) {
                    flagOK = false;
                    new SweetAlertDialog(BillAddActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(InfoText.TEXT_ERROR_BILL_DATE)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                } else {
                    if (flagOK) {
                        mBillCardInfo.setmCardViewBillDate(text_bill_date.getText().toString());
                    }
                }

                if (flagOK) {
                    if (mMainFragmentRequestCode == InfoText.REQUEST_ADD_BILL) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date curDate = new Date(System.currentTimeMillis());
                        mBillCardInfo.setmCardViewBillCreateDate(formatter.format(curDate));
                    }

                    mBillCardInfo.setmCardViewBillMemo(edtBillMemoInput.getText().toString());

                    DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getBaseContext());
                    databaseAccess.open();

                    String SQLStr = "SELECT ICONNAME FROM T_STEPINFO WHERE STEPLEVEL=0 AND STEPTITLE='" + mBillCardInfo.getmCardViewBillType().toString() + "'";
                    Cursor mCursor = databaseAccess.querySQL(SQLStr);
                    if (mCursor != null && mCursor.moveToFirst()) {
                        mBillCardInfo.setmCardViewPhotoID(mCursor.getString(mCursor.getColumnIndex("ICONNAME")));
                    }
                    mCursor.close();

                    if (mMainFragmentRequestCode == InfoText.REQUEST_ADD_BILL) {
                        SQLStr = "INSERT INTO T_BILLINFO (JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,BILLMEMO) "
                                + "values('" + mBillCardInfo.getmCardViewJNL().toString() + "','"
                                + mBillCardInfo.getmCardViewBillType().toString() + "','"
                                + mBillCardInfo.getmCardViewBillChildType().toString() + "','"
                                + mBillCardInfo.getmCardViewBillSum().toString() + "','"
                                + mBillCardInfo.getmCardViewBillDate().toString() + "','"
                                + mBillCardInfo.getmCardViewBillCreateDate().toString() + "','"
                                + mBillCardInfo.getmCardViewBillMemo().toString() + "')";
                    }

                    if (mMainFragmentRequestCode == InfoText.REQUEST_MODIFY_BILL) {
                        SQLStr = "UPDATE T_BILLINFO SET BILLTYPE='" + mBillCardInfo.getmCardViewBillType().toString() + "',"
                                + "BILLCHILDTYPE='" + mBillCardInfo.getmCardViewBillChildType().toString() + "',"
                                + "BILLSUM='" + mBillCardInfo.getmCardViewBillSum().toString() + "',"
                                + "BILLDATE='" + mBillCardInfo.getmCardViewBillDate().toString() + "',"
                                + "BILLMEMO='" + mBillCardInfo.getmCardViewBillMemo().toString() + "' "
                                + "WHERE JNL='" + mBillCardInfo.getmCardViewJNL().toString() + "'";
                    }

                    SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                    if (!SQLStr.isEmpty()) {
                        flagOK = false;
                        new SweetAlertDialog(BillAddActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                .setContentText(SQLStr)
                                .setConfirmText(InfoText.TEXT_OK)
                                .show();
                    }
                    databaseAccess.close();
                }

                if (flagOK) {
                    Intent mIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(InfoText.BILL_ADD_BILLCARDINFO, mBillCardInfo);
                    mBundle.putSerializable(InfoText.BILL_SUM_CHANGED, oldSum - newSum);
                    mBundle.putSerializable(InfoText.CURRENT_ITEM_POSITION, mItemPosition);
                    mIntent.putExtras(mBundle);
                    setResult(InfoText.RESULT_ADD_BILL, mIntent);
                    finish();
                }
            }
        });


        ll_bill_memo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //为EditText弹出软键盘
                edtBillMemoInput.requestFocus();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(edtBillMemoInput, 0);
            }
        });

        ll_bill_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //弹出日期选择对话框
                dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
                if (dateTimeFragment == null) {
                    dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                            getString(R.string.label_datetime_dialog),
                            getString(R.string.positive_button_datetime_picker),
                            getString(R.string.negative_button_datetime_picker)
                    );
                }

                //取当前系统日期
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Assign values we want
                final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                dateTimeFragment.startAtCalendarView();
                dateTimeFragment.set24HoursMode(true);
                dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2012, Calendar.DECEMBER, 12).getTime());
                dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2099, Calendar.DECEMBER, 31).getTime());
                dateTimeFragment.setDefaultDateTime(new GregorianCalendar(year, month, day).getTime());

                try {
                    dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MM dd", Locale.getDefault()));
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                    Log.e(TAG, e.getMessage());
                }

                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);

                // Set listener for date
                dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        text_bill_date.setText(myDateFormat.format(date));
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        text_bill_date.setText("");
                    }
                });


            }
        });

        //设置监听事件
        cpvBillType.setOnOptionChangedListener(new OnOptionChangedListener() {

            @Override
            public void onOptionChanged(int i, int i1, int i2) {
                mBillCardInfo.setmCardViewBillType(InitStepPickerData.options1Items.get(i).toString());
                if (i1 > InitStepPickerData.options2Items.get(i).size()) {
                    i1 = 0;
                    cpvBillType.setSelectOptions(i,i1);
                }
                mBillCardInfo.setmCardViewBillChildType(InitStepPickerData.options2Items.get(i).get(i1).toString());
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        InitStepPickerData.init(this);
        InitStepPickerData.setPickerData(this, cpvBillType);
    }

    //通过复写dispatchTouchEvent函数使用在EDITTEXT控件外点击时，使EDITTEXT失去焦点
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && v instanceof EditText) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];
            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                if (v.getId() == edtBillSumInput.getId() && !("".equals(edtBillSumInput.getText().toString()))) {
                    edtBillSumInput.setText(df.format(Double.parseDouble(edtBillSumInput.getText().toString())));
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
