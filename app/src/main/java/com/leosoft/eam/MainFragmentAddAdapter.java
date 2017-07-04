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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Leo on 2017-05-17.
 */

public class MainFragmentAddAdapter extends RecyclerView.Adapter<MainFragmentAddAdapter.ViewHolder> {

    //此处与MainFragment.java中面板上要显示的数据内容匹配
    private ArrayList<BillCardInfo> mDataset = new ArrayList<>();
    private Context context;
    private MainFragmentAddAdapter mMainFragmentAddAdapter;
    private OnItemListener mOnItemListener;


    //添加一个OnItemDeleteListener接口，并且定义OnItemDelete方法
    public interface OnItemListener {
        void OnItemDelete(int position , Double billsum , Boolean flagBillSubmit);
        void OnItemModify(int position , BillCardInfo mBillCardInfo);
    }
    //然后定义一个监听的方法，便于主类调用
    public void setOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivCardViewPhoto;
        public ImageView ivCardViewPhotoRight;
        public TextView tvCardViewBillType;
        public TextView tvCardViewBillChildType;
        public TextView tvCardViewBillSumTitle;
        public TextView tvCardViewBillSum;
        public TextView tvCardViewBillDateTitle;
        public TextView tvCardViewBillDate;
        public TextView tvCardViewBillCreateDateTitle;
        public TextView tvCardViewBillCreateDate;
        public TextView tvCardViewBillMemoTitle;
        public TextView tvCardViewBillMemo;
        public Button btnTabBillAddSubmit;
        public Button btnTabBillAddModify;
        public Button btnTabBillAddDelete;

        public ViewHolder(View v) {
            super(v);
            ivCardViewPhoto = (ImageView) v.findViewById(R.id.card_view_photo);
            ivCardViewPhotoRight = (ImageView) v.findViewById(R.id.card_view_photo_right);
            tvCardViewBillType = (TextView) v.findViewById(R.id.card_view_bill_type);
            tvCardViewBillChildType = (TextView) v.findViewById(R.id.card_view_bill_child_type);
            tvCardViewBillSumTitle = (TextView) v.findViewById(R.id.card_view_bill_sum_title);
            tvCardViewBillSum = (TextView) v.findViewById(R.id.card_view_bill_sum);
            tvCardViewBillDateTitle = (TextView) v.findViewById(R.id.card_view_bill_date_title);
            tvCardViewBillDate = (TextView) v.findViewById(R.id.card_view_bill_date);
            tvCardViewBillCreateDateTitle = (TextView) v.findViewById(R.id.card_view_bill_create_date_title);
            tvCardViewBillCreateDate = (TextView) v.findViewById(R.id.card_view_bill_create_date);
            tvCardViewBillMemoTitle = (TextView) v.findViewById(R.id.card_view_bill_memo_title);
            tvCardViewBillMemo = (TextView) v.findViewById(R.id.card_view_bill_memo);
            btnTabBillAddSubmit = (Button) v.findViewById(R.id.button_tab_bill_add_submit);
            btnTabBillAddModify = (Button) v.findViewById(R.id.button_tab_bill_add_modify);
            btnTabBillAddDelete = (Button) v.findViewById(R.id.button_tab_bill_add_delete);
        }
    }

    public MainFragmentAddAdapter(ArrayList<BillCardInfo> dataset,Context context) {
        mDataset.clear();
        mDataset.addAll(dataset);

        this.context=context;
    }

    public void reloadMainFragmentAddAdapter(ArrayList<BillCardInfo> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);

        mMainFragmentAddAdapter.notifyItemRangeChanged(0,mDataset.size());
    }

    public void getMainFragmentAddAdapter(MainFragmentAddAdapter mMainFragmentAddAdapter) {
        this.mMainFragmentAddAdapter = mMainFragmentAddAdapter;

    }

    public void AddBillCardInfo(BillCardInfo mBillCardInfo,Context context) {
        mDataset.add(0,mBillCardInfo);
        mMainFragmentAddAdapter.notifyItemInserted(0);
        mMainFragmentAddAdapter.notifyItemRangeChanged(0,mDataset.size());

        this.context=context;
    }


    public void UpdateBillCardInfo(BillCardInfo mBillCardInfo,Context context,int position) {
        mDataset.get(position).setmCardViewPhotoID(mBillCardInfo.getmCardViewPhotoID());
        mDataset.get(position).setmCardViewBillType(mBillCardInfo.getmCardViewBillType());
        mDataset.get(position).setmCardViewBillChildType(mBillCardInfo.getmCardViewBillChildType());
        mDataset.get(position).setmCardViewBillSum(mBillCardInfo.getmCardViewBillSum());
        mDataset.get(position).setmCardViewBillDate(mBillCardInfo.getmCardViewBillDate());
        mDataset.get(position).setmCardViewBillMemo(mBillCardInfo.getmCardViewBillMemo());

        mMainFragmentAddAdapter.notifyItemRangeChanged(0,mDataset.size());

        this.context=context;
    }

    @Override
    public MainFragmentAddAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_main_tab_1_disp_content, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //此处position一定要用final int，如果只用int内部的position将不对。
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getAbsoluteFile() + "/images/" + mDataset.get(position).getmCardViewPhotoID());
            if (bitmap != null) {
                holder.ivCardViewPhoto.setImageBitmap(bitmap);
                holder.ivCardViewPhotoRight.setImageBitmap(bitmap);
                holder.ivCardViewPhotoRight.setAlpha(0.15f);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        holder.tvCardViewBillType.setText(mDataset.get(position).getmCardViewBillType());
        holder.tvCardViewBillChildType.setText("[" + mDataset.get(position).getmCardViewBillChildType() + "]");
        holder.tvCardViewBillSum.setText(mDataset.get(position).getmCardViewBillSum());
        holder.tvCardViewBillDate.setText(mDataset.get(position).getmCardViewBillDate());
        holder.tvCardViewBillCreateDate.setText(mDataset.get(position).getmCardViewBillCreateDate());
        holder.tvCardViewBillMemo.setText(mDataset.get(position).getmCardViewBillMemo());

        holder.btnTabBillAddSubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Double mDouble = Double.parseDouble(mDataset.get(position).getmCardViewBillSum());

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
                databaseAccess.open();

                String SQLStr = "UPDATE T_BILLINFO SET DEALFLAG=1,"
                        + "DEALDATE='" + formatter.format(new Date(System.currentTimeMillis())) + "' "
                        + "WHERE JNL='" + mDataset.get(position).getmCardViewJNL() + "'";

                SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                if (!SQLStr.isEmpty()) {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                            .setContentText(SQLStr)
                            .setConfirmText(InfoText.TEXT_OK)
                            .show();
                } else {
                    mDataset.remove(position);
                    mMainFragmentAddAdapter.notifyItemRemoved(position);
                    mMainFragmentAddAdapter.notifyItemRangeChanged(0,mDataset.size()-position);
                }

                mOnItemListener.OnItemDelete(position,mDouble,true);

            }
        });

        holder.btnTabBillAddModify.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                mOnItemListener.OnItemModify(position , mDataset.get(position));
            }
        });

        holder.btnTabBillAddDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(InfoText.TEXT_ASK_TITLE)
                        .setContentText(InfoText.TEXT_ASK_DELETE)
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
                                String SQLStr = "DELETE FROM T_BILLINFO WHERE JNL='" + mDataset.get(position).getmCardViewJNL() +"'";
                                SQLStr = databaseAccess.ddlSQL(SQLStr, false);
                                if (!SQLStr.isEmpty()) {
                                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(InfoText.TEXT_ERROR_TITLE)
                                            .setContentText(SQLStr)
                                            .setConfirmText(InfoText.TEXT_OK)
                                            .show();
                                } else {
                                    mDataset.remove(position);
                                    mMainFragmentAddAdapter.notifyItemRemoved(position);
                                    mMainFragmentAddAdapter.notifyItemRangeChanged(0,mDataset.size()-position);
                                }
                                databaseAccess.close();

                                mOnItemListener.OnItemDelete(position,mDouble,false);

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
