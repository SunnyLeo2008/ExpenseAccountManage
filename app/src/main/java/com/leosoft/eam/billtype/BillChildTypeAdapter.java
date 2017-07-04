package com.leosoft.eam.billtype;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayout;
import com.leosoft.eam.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Leo on 2017-06-18.
 */

public class BillChildTypeAdapter extends RecyclerView.Adapter<BillChildTypeAdapter.ViewHolder> {

    private List<String> mDataset  =  new ArrayList<>();
    private Context context;

    private OnItemListener mOnItemListener;

    private BillChildTypeAdapter mBillChildTypeAdapter;

    public interface OnItemListener {
        void OnItemChangeSwipeState(Boolean flagSwipeState);
        Boolean OnItemModifyConfirm(int position, String oldValue, String newValue);
    }
    //然后定义一个监听的方法，便于主类调用
    public void setOnItemListener(OnItemListener mOnItemListener) {
        this.mOnItemListener = mOnItemListener;
    }

    public BillChildTypeAdapter(List<String> dataset , Context context) {
        mDataset.clear();
        mDataset.addAll(dataset);

        this.context = context;
    }

    public void getBillChildTypeAdapter(BillChildTypeAdapter mBillChildTypeAdapter) {
        this.mBillChildTypeAdapter = mBillChildTypeAdapter;

    }

    public void reloadBillChildTypeAdapter(List<String> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);

        mBillChildTypeAdapter.notifyItemRangeChanged(0,mDataset.size());
    }

    public void deleteBillChildType(int position) {
        mDataset.remove(position);

        mBillChildTypeAdapter.notifyItemRemoved(position);
        mBillChildTypeAdapter.notifyItemRangeChanged(0,mDataset.size()-position);
    }


    public void addBillChildType(String data) {
        mDataset.add(0,data);

        mBillChildTypeAdapter.notifyItemInserted(0);
        mBillChildTypeAdapter.notifyItemRangeChanged(0,mDataset.size());
    }

    public void updateBillChildType(int position, String data) {
        mDataset.set(position,data);

        mBillChildTypeAdapter.notifyItemChanged(position);
        mBillChildTypeAdapter.notifyItemRangeChanged(0,mDataset.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBillChildType;
        public TextView tvBillChildTypeCancle;
        public TextView tvBillChildTypeConfirm;
        public LinearLayout llBillChildTypeNew;
        public EditText etBillChildTypeNew;
        public ExpandableLayout elBillChildType;

        public ViewHolder(View view) {
            super(view);

            tvBillChildType = (TextView) view.findViewById(R.id.tv_bill_child_type);
            tvBillChildTypeCancle = (TextView) view.findViewById(R.id.tv_bill_child_type_cancle);
            tvBillChildTypeConfirm = (TextView) view.findViewById(R.id.tv_bill_child_type_confirm);
            llBillChildTypeNew = (LinearLayout) view.findViewById(R.id.ll_bill_child_type_new);
            etBillChildTypeNew = (EditText) view.findViewById(R.id.et_bill_child_type_new);
            elBillChildType = (ExpandableLayout) view.findViewById(R.id.el_bill_child_type);
        }
    }


    @Override
    public BillChildTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandablelayout_adapter_billchildtype, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final BillChildTypeAdapter.ViewHolder holder, final int position) {

        holder.tvBillChildType.setText(mDataset.get(position).toString());

        holder.elBillChildType.getHeaderRelativeLayout().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //屏蔽控件自身的点击事件
            }
        });

        holder.llBillChildTypeNew.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.etBillChildTypeNew.requestFocus();
                ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(holder.etBillChildTypeNew, 0);
            }
        });

        holder.tvBillChildTypeCancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.elBillChildType.hide();
                mOnItemListener.OnItemChangeSwipeState(true);
            }
        });

        holder.tvBillChildTypeConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemListener.OnItemModifyConfirm(position,holder.tvBillChildType.getText().toString(),holder.etBillChildTypeNew.getText().toString())) {
                    holder.elBillChildType.hide();
                    mOnItemListener.OnItemChangeSwipeState(true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
