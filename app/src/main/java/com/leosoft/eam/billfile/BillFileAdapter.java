package com.leosoft.eam.billfile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.BillCardInfo;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Created by Leo on 2017-06-09.
 */

public class BillFileAdapter  extends RecyclerView.Adapter<BillFileAdapter.ViewHolder>{

    public ArrayList<BillCardInfo> getmDataset() {
        return mDataset;
    }

    private ArrayList<BillCardInfo> mDataset = new ArrayList<>();
    private Context context;

    public BillFileAdapter(ArrayList<BillCardInfo> dataset,Context context) {
        mDataset.clear();
        mDataset.addAll(dataset);

        this.context=context;
    }

    public void reloadBillFileAdapter(ArrayList<BillCardInfo> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);

        //此处更新不能用notifyItemRangeChanged，用这个会报Invalid view holder adapter positionViewHolder异常
        //notifyItemRangeChanged(0,mDataset.size());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivCardViewIco;
        public TextView tvCardViewBillFileType;
        public TextView tvCardViewBillFileChildType;
        public TextView tvCardViewBillFileSum;
        public TextView tvCardViewBillFileDate;
        public TextView tvCardViewBillFileCreateDate;
        public TextView tvCardViewBillFileDealDate;
        public TextView tvCardViewBillFileMemo;


        public ViewHolder(View v) {
            super(v);
            ivCardViewIco = (ImageView) v.findViewById(R.id.card_view_ico);
            tvCardViewBillFileType = (TextView) v.findViewById(R.id.card_view_billfile_type);
            tvCardViewBillFileChildType = (TextView) v.findViewById(R.id.card_view_billfile_childtype);
            tvCardViewBillFileSum = (TextView) v.findViewById(R.id.card_view_billfile_sum);
            tvCardViewBillFileDate = (TextView) v.findViewById(R.id.card_view_billfile_date);
            tvCardViewBillFileCreateDate = (TextView) v.findViewById(R.id.card_view_billfile_create_date);
            tvCardViewBillFileDealDate = (TextView) v.findViewById(R.id.card_view_billfile_deal_date);
            tvCardViewBillFileMemo = (TextView) v.findViewById(R.id.card_view_billfile_memo);
        }
    }

    @Override
    public BillFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_adapter_billfile, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getAbsoluteFile() + "/images/" + mDataset.get(position).getmCardViewPhotoID());
            if (bitmap != null) {
                holder.ivCardViewIco.setImageBitmap(bitmap);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        holder.tvCardViewBillFileType.setText(mDataset.get(position).getmCardViewBillType());
        holder.tvCardViewBillFileChildType.setText("[" +mDataset.get(position).getmCardViewBillChildType() + "]");
        holder.tvCardViewBillFileSum.setText(mDataset.get(position).getmCardViewBillSum());
        holder.tvCardViewBillFileDate.setText(mDataset.get(position).getmCardViewBillDate());
        holder.tvCardViewBillFileCreateDate.setText(mDataset.get(position).getmCardViewBillCreateDate());
        holder.tvCardViewBillFileDealDate.setText(mDataset.get(position).getmCardViewBillDealDate());
        holder.tvCardViewBillFileMemo.setText(mDataset.get(position).getmCardViewBillMemo());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public String getItem(int position) {
        return mDataset.get(position).getmCardViewBillDealDate();
    }

    private int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }
}
