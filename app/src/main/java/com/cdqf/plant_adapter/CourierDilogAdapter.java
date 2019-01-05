package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;

/**
 * 快递集合
 * Created by liu on 2017/12/29.
 */

public class CourierDilogAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    public CourierDilogAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantState.getCourierList().size();
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
        tvWhyDilogContent.setText(plantState.getCourierList().get(position).getZhExpressName());
        return convertView;
    }
}
