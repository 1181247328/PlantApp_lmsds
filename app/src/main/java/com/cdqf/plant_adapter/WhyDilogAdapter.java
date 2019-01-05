package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;

/**
 * 退款原因集合
 * Created by liu on 2017/12/28.
 */

public class WhyDilogAdapter extends BaseAdapter {

    private Context context= null;

    private String[] why = null;

    public WhyDilogAdapter(Context context, String[] why) {
        this.context = context;
        this.why = why;
    }

    @Override
    public int getCount() {
        return why.length;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_dilog_why,null);
        TextView tvWhyDilogContent = (TextView) convertView.findViewById(R.id.tv_why_dilog_content);
        tvWhyDilogContent.setText(why[position]);
        return convertView;
    }
}
