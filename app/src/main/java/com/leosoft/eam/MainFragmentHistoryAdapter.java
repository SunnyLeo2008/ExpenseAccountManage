package com.leosoft.eam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leosoft.eam.utils.BillCardInfo;
import com.leosoft.eam.utils.DatabaseAccess;
import com.leosoft.eam.utils.InfoText;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Leo on 2017-05-17.
 */

public class MainFragmentHistoryAdapter extends RecyclerView.Adapter<MainFragmentHistoryAdapter.ViewHolder> {

    //此处与MainFragment.java中面板上要显示的数据内容匹配
    private ArrayList<BillCardInfo> mDataset = new ArrayList<>();
    private MainFragmentHistoryAdapter mMainFragmentHistoryAdapter;
    private Context context;
    private OnItemListener mOnItemListener;

    //添加一个OnItemDeleteListener接口，并且定义OnItemDelete方法
    public interface OnItemListener {
        void OnItemFile(int position , Double billsum , Boolean flagBillSubmit);
        void OnItemReturn(int position, Double billsum, Boolean flagBillSubmit);
    }
    //然后定义一个监听的方法，便于主类调用
    public void setOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivCardViewPhoto2;
        public ImageView ivCardViewPhotoRight2;
        public TextView tvCardViewBillType2;
        public TextView tvCardViewBillChildType2;
        public TextView tvCardViewBillSumTitle2;
        public TextView tvCardViewBillSum2;
        public TextView tvCardViewBillDateTitle2;
        public TextView tvCardViewBillDate2;
        public TextView tvCardViewBillCreateDateTitle2;
        public TextView tvCardViewBillCreateDate2;
        public TextView tvCardViewBillDealDateTitle2;
        public TextView tvCardViewBillDealDate2;
        public TextView tvCardViewBillMemoTitle2;
        public TextView tvCardViewBillMemo2;
        public Button btnTabBillDeleteSubmit;
        public Button btnTabBillDeleteReturn;

        public ViewHolder(View v) {
            super(v);
            ivCardViewPhoto2 = (ImageView) v.findViewById(R.id.card_view_photo_2);
            ivCardViewPhotoRight2 = (ImageView) v.findViewById(R.id.card_view_photo_right_2);
            tvCardViewBillType2 = (TextView) v.findViewById(R.id.card_view_bill_type_2);
            tvCardViewBillChildType2 = (TextView) v.findViewById(R.id.card_view_bill_child_type_2);
            tvCardViewBillSumTitle2 = (TextView) v.findViewById(R.id.card_view_bill_sum_title_2);
            tvCardViewBillSum2 = (TextView) v.findViewById(R.id.card_view_bill_sum_2);
            tvCardViewBillDateTitle2 = (TextView) v.findViewById(R.id.card_view_bill_date_title_2);
            tvCardViewBillDate2 = (TextView) v.findViewById(R.id.card_view_bill_date_2);
            tvCardViewBillCreateDateTitle2 = (TextView) v.findViewById(R.id.card_view_bill_create_date_title_2);
            tvCardViewBillCreateDate2 = (TextView) v.findViewById(R.id.card_view_bill_create_date_2);
            tvCardViewBillDealDateTitle2 = (TextView) v.findViewById(R.id.card_view_bill_deal_date_title_2);
            tvCardViewBillDealDate2 = (TextView) v.findViewById(R.id.card_view_bill_deal_date_2);
            tvCardViewBillMemoTitle2 = (TextView) v.findViewById(R.id.card_view_bill_memo_title_2);
            tvCardViewBillMemo2 = (TextView) v.findViewById(R.id.card_view_bill_memo_2);
            btnTabBillDeleteSubmit = (Button) v.findViewById(R.id.button_tab_bill_delete_submit);
            btnTabBillDeleteReturn = (Button) v.findViewById(R.id.button_tab_bill_delete_return);
        }
    }

    public MainFragmentHistoryAdapter(ArrayList<BillCardInfo> dataset,Context context) {
        mDataset.clear();
        mDataset.addAll(dataset);

        this.context=context;
    }

    public void reloadMainFragmentHistoryAdapter(ArrayList<BillCardInfo> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);

        mMainFragmentHistoryAdapter.notifyItemRangeChanged(0,mDataset.size());
    }

    public void getMainFragmentHistoryAdapter(MainFragmentHistoryAdapter mMainFragmentHistoryAdapter) {
        this.mMainFragmentHistoryAdapter = mMainFragmentHistoryAdapter;

    }

    @Override
    public MainFragmentHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_main_tab_2_disp_content, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    //此处position一定要用final int，如果只用int内部的position将不对。
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getAbsoluteFile() + "/images/" + mDataset.get(position).getmCardViewPhotoID());
            if (bitmap != null) {
                holder.ivCardViewPhoto2.setImageBitmap(bitmap);
                holder.ivCardViewPhotoRight2.setImageBitmap(bitmap);
                holder.ivCardViewPhotoRight2.setAlpha(0.15f);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        holder.tvCardViewBillType2.setText(mDataset.get(position).getmCardViewBillType());
        holder.tvCardViewBillChildType2.setText("[" +mDataset.get(position).getmCardViewBillChildType() + "]");
        holder.tvCardViewBillSum2.setText(mDataset.get(position).getmCardViewBillSum());
        holder.tvCardViewBillDate2.setText(mDataset.get(position).getmCardViewBillDate());
        holder.tvCardViewBillCreateDate2.setText(mDataset.get(position).getmCardViewBillCreateDate());
        holder.tvCardViewBillDealDate2.setText(mDataset.get(position).getmCardViewBillDealDate());
        holder.tvCardViewBillMemo2.setText(mDataset.get(position).getmCardViewBillMemo());

        holder.btnTabBillDeleteSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(InfoText.TEXT_ASK_TITLE)
                        .setContentText(InfoText.TEXT_ASK_FILE)
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
                                Double mDouble = Double.parseDouble(mDataset.get(position).getmCardViewBillSum());

                                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                                databaseAccess.open();
                                String SQLStr = "UPDATE T_BILLINFO SET DEALFLAG=2 WHERE JNL='" + mDataset.get(position).getmCardViewJNL() +"'";
                                SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                                if (!SQLStr.isEmpty()) {
                                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                            .setContentText(SQLStr)
                                            .setConfirmText(InfoText.TEXT_OK)
                                            .show();
                                } else {
                                    mDataset.remove(position);
                                    mMainFragmentHistoryAdapter.notifyItemRemoved(position);
                                    mMainFragmentHistoryAdapter.notifyItemRangeChanged(0,mDataset.size()-position);
                                }
                                databaseAccess.close();

                                mOnItemListener.OnItemFile(position,mDouble,false);

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        holder.btnTabBillDeleteReturn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Double mDouble = Double.parseDouble(mDataset.get(position).getmCardViewBillSum());

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();
                String SQLStr = "UPDATE T_BILLINFO SET DEALFLAG=0 WHERE JNL='" + mDataset.get(position).getmCardViewJNL() +"'";
                SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                if (!SQLStr.isEmpty()) {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(SQLStr)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                } else {
                    mDataset.remove(position);
                    mMainFragmentHistoryAdapter.notifyItemRemoved(position);
                    mMainFragmentHistoryAdapter.notifyItemRangeChanged(0,mDataset.size()-position);
                }
                databaseAccess.close();

                mOnItemListener.OnItemReturn(position,mDouble,false);
            }
        });
        
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
