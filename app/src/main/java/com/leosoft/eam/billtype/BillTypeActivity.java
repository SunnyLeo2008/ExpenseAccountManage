package com.leosoft.eam.billtype;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.BillTypeInfo;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.InfoText;

import java.util.ArrayList;

public class BillTypeActivity extends AppCompatActivity {

    private DatabaseAccess databaseAccess;
    private Cursor mCursor;
    private String SQLStr;
    private ArrayList<BillTypeInfo> itemsBillCardInfos =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_type);

        RecyclerView rv_bill_type = (RecyclerView) findViewById(R.id.rv_bill_type);
        ImageButton ibBillTypeBack = (ImageButton)  findViewById(R.id.ib_bill_type_back);

        LinearLayoutManager mLayoutMgr = new LinearLayoutManager(BillTypeActivity.this, LinearLayoutManager.VERTICAL, false);
        rv_bill_type.setLayoutManager(mLayoutMgr);
        rv_bill_type.setHasFixedSize(true);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        SQLStr = "SELECT STEPTITLE,ICONNAME FROM T_STEPINFO WHERE STEPLEVEL=0";
        mCursor = databaseAccess.querySQL(SQLStr);
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                BillTypeInfo mBillTypeInfo = new BillTypeInfo();
                mBillTypeInfo.setmCardViewPhotoID(mCursor.getString(mCursor.getColumnIndex("ICONNAME")));
                mBillTypeInfo.setmCardViewBillType(mCursor.getString(mCursor.getColumnIndex("STEPTITLE")));

                itemsBillCardInfos.add(mBillTypeInfo);
            } while (mCursor.moveToNext());
        }
        mCursor.close();
        databaseAccess.close();

        BillTypeAdapter mBillTypeAdapter = new BillTypeAdapter(itemsBillCardInfos , this);
        rv_bill_type.setAdapter(mBillTypeAdapter);

        mBillTypeAdapter.setOnItemClickListener(new BillTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent mIntent = new Intent(BillTypeActivity.this, BillChildTypeActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(InfoText.BILL_CHILD_TYPE_REQUEST , itemsBillCardInfos.get(position).getmCardViewBillType().toString());
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });

        ibBillTypeBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
