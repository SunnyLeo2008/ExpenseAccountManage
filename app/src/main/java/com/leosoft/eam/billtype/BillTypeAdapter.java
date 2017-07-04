package com.leosoft.eam.billtype;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.BillTypeInfo;

import java.util.ArrayList;

/**
 * Created by Leo on 2017-06-17.
 */

public class BillTypeAdapter extends RecyclerView.Adapter<BillTypeAdapter.ViewHolder> {

    private ArrayList<BillTypeInfo> mDataset = new ArrayList<>();
    private Context context;

    private OnItemClickListener mOnItemClickListener;


    public BillTypeAdapter(ArrayList<BillTypeInfo> dataset , Context context) {
        mDataset.clear();
        mDataset.addAll(dataset);

        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivCardViewPhoto;
        public TextView tvCardViewBillType;

        public ViewHolder(View itemView) {
            super(itemView);

            ivCardViewPhoto = (ImageView) itemView.findViewById(R.id.card_view_ico);
            tvCardViewBillType = (TextView) itemView.findViewById(R.id.card_view_bill_type);
        }
    }

    @Override
    public BillTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_adapter_billtype, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final BillTypeAdapter.ViewHolder holder, int position) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(context.getFilesDir().getAbsoluteFile() + "/images/" + mDataset.get(position).getmCardViewPhotoID());
            if (bitmap != null) {
                holder.ivCardViewPhoto.setImageBitmap(bitmap);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        holder.tvCardViewBillType.setText(mDataset.get(position).getmCardViewBillType());

        if(mOnItemClickListener != null){
            //为ItemView设置监听器
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
