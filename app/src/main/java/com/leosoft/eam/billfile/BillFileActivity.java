package com.leosoft.eam.billfile;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.BillCardInfo;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.InfoText;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

import static java.lang.Double.parseDouble;

public class BillFileActivity extends AppCompatActivity {

    private FilterPopupWindow popupWindow;
    private View filterPopWin;
    private DatabaseAccess databaseAccess;
    private Cursor mCursor;
    private String SQLStr;
    private ArrayList<BillCardInfo> itemsBillCardInfos =  new ArrayList<>();
    private BillFileHeadersAdapter mBillFileAdapter;
    private StickyRecyclerHeadersDecoration headersDecor = null;

    DecimalFormat df=(DecimalFormat) DecimalFormat.getInstance();

    private Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case InfoText.RESULT_POPUPWINOW_SAVE:
                    Bundle mBundle = msg.getData();
                    SQLStr = mBundle.getString(InfoText.POPUP_WINDOW_RESULT);
                    itemsBillCardInfos.clear();

                    databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                    databaseAccess.open();
                    mCursor = databaseAccess.querySQL(SQLStr);
                    if (mCursor != null && mCursor.moveToFirst()) {
                        do {
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

                            itemsBillCardInfos.add(mBillCardInfo);
                        } while (mCursor.moveToNext());
                    }
                    mCursor.close();
                    databaseAccess.close();

                    mBillFileAdapter.reloadBillFileAdapter(itemsBillCardInfos);

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = getLayoutInflater().inflate(R.layout.activity_bill_file,null);
        setContentView(contentView);

        RecyclerView rv_bill_file = (RecyclerView) findViewById(R.id.rv_bill_file);
        filterPopWin = findViewById(R.id.filter_popwin_main);

        LinearLayoutManager mLayoutMgr = new LinearLayoutManager(BillFileActivity.this, LinearLayoutManager.VERTICAL, false);
        rv_bill_file.setLayoutManager(mLayoutMgr);
        rv_bill_file.setHasFixedSize(true);

        TextView tvBillFileTotal = (TextView)  findViewById(R.id.tv_bill_file_total);
        TextView tvBillFileDeal = (TextView)  findViewById(R.id.tv_bill_file_deal);
        TextView tvBillFileNum = (TextView)  findViewById(R.id.tv_bill_file_num);
        TextView tvBillFileSum = (TextView)  findViewById(R.id.tv_bill_file_sum);
        ImageButton ibBillFileBack = (ImageButton)  findViewById(R.id.ib_bill_file_back);
        ImageButton ibBillFileFilter = (ImageButton)  findViewById(R.id.ib_bill_file_filter);

        df.applyPattern("0.00");

        Double mBillFileTotal =0.00;
        Double mBillFileDeal =0.00;
        int mBillFileNum = 0;
        Double mBillFileSum = 0.00;

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        SQLStr = "SELECT DEALFLAG,COUNT(*) AS COUNT_NUM,SUM(BILLSUM) AS BILLSUM "
                + "FROM T_BILLINFO GROUP BY DEALFLAG";
        mCursor = databaseAccess.querySQL(SQLStr);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                switch (Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("DEALFLAG")))) {
                    case 0: {
                        mBillFileTotal = parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                        break;
                    }
                    case 1: {
                        mBillFileDeal = parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                        break;
                    }
                    default: {
                        mBillFileNum = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("COUNT_NUM")));
                        mBillFileSum = parseDouble(mCursor.getString(mCursor.getColumnIndex("BILLSUM")));
                        break;
                    }

                }
            } while (mCursor.moveToNext());

            mBillFileTotal = mBillFileTotal + mBillFileDeal + mBillFileSum;
            mBillFileDeal = mBillFileDeal + mBillFileSum;
        }
        mCursor.close();

        SQLStr = "SELECT JNL,BILLTYPE,BILLCHILDTYPE,BILLSUM,BILLDATE,CREATEDATE,DEALDATE,BILLMEMO,DEALFLAG,ICONNAME "
                + "FROM T_BILLINFO A,T_STEPINFO B WHERE DEALFLAG=2 AND STEPLEVEL=0 AND STEPTITLE=BILLTYPE ORDER BY DEALDATE DESC";
        mCursor = databaseAccess.querySQL(SQLStr);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
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

                itemsBillCardInfos.add(mBillCardInfo);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        databaseAccess.close();

        tvBillFileTotal.setText("￥" + df.format(mBillFileTotal));
        tvBillFileDeal.setText("￥" + df.format(mBillFileDeal));
        tvBillFileSum.setText("￥" + df.format(mBillFileSum));
        df.applyPattern("0");
        tvBillFileNum.setText(df.format(mBillFileNum));

        mBillFileAdapter = new BillFileHeadersAdapter(itemsBillCardInfos, this);
        rv_bill_file.setAdapter(mBillFileAdapter);

        StikkyHeaderBuilder.stickTo(rv_bill_file)
                .setHeader(R.id.bill_file_header, (ViewGroup) contentView)
                .minHeightHeaderDim(R.dimen.min_height_header)
                .animator(new HeaderAnimator())
                .build();

        headersDecor = new StickyRecyclerHeadersDecoration(mBillFileAdapter);
        rv_bill_file.addItemDecoration(headersDecor);

        mBillFileAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        ibBillFileBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibBillFileFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                popupWindow = new FilterPopupWindow(BillFileActivity.this,mHandler);
                popupWindow.setPopWinTouchInterceptor(popupWindow);
                popupWindow.showFilterPopup(filterPopWin);
            }
        });

    }
}
