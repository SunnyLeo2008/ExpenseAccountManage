package com.leosoft.eam.billfile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leosoft.eam.R;
import com.leosoft.eam.utils.BillCardInfo;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Leo on 2017-06-10.
 */

public class BillFileHeadersAdapter extends BillFileAdapter implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    public BillFileHeadersAdapter(ArrayList<BillCardInfo> dataset, Context context) {
        super(dataset, context);
    }

    @Override
    public long getHeaderId(int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(getItem(position));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    @Override
    public BillFileAdapter.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_head_bill_file, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextView textView = (TextView) holder.itemView;
        textView.getBackground().setAlpha(75);
        textView.setText("[" + getmDataset().get(position).getmCardViewBillDealDate() + "]");
    }

}
