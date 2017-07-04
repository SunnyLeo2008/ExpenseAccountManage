package com.leosoft.eam.billfile;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.leosoft.eam.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo on 2017-06-11.
 */

public class BillTypesAdapter  extends BaseAdapter {

    private Context context;
    private List<BillFilterAttrsVo> data = new ArrayList<>();

    public BillTypesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final MyView myView;
        if (v == null) {
            myView = new MyView();
            v = View.inflate(context, R.layout.item_bill_types, null);
            myView.attr = (TextView) v.findViewById(R.id.bill_type_name);
            v.setTag(myView);
        } else {
            myView = (MyView) v.getTag();
        }
        myView.attr.setText(data.get(position).getValue());
        myView.attr.setTextSize(TypedValue.COMPLEX_UNIT_SP,12f);

        /**
         * 根据选中状态来设置item的背景和字体颜色
         */
        if (data.get(position).isChecked()) {
            myView.attr.setBackgroundResource(R.drawable.bill_file_filter_attr_selected_shape);
            myView.attr.setTextColor(Color.WHITE);
        } else {
            myView.attr.setBackgroundResource(R.drawable.bill_file_filter_attr_unselected_shape);
            myView.attr.setTextColor(Color.GRAY);
        }
        return v;
    }

    static class MyView {
        public TextView attr;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void notifyDataSetChanged(boolean isUnfold, final List<BillFilterAttrsVo> tempData) {
        if (tempData == null || 0 == tempData.size()) {
            return;
        }
        data.clear();
        // 如果是展开的，则加入全部data，反之则只显示3条
        if (isUnfold) {
            data.addAll(tempData);
        } else {
            for (int i = 0; i <= tempData.size(); i++) {
                data.add(tempData.get(i));
                if (2 == i) {
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }
}
