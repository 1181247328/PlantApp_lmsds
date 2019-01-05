package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_class.Data;
import com.cdqf.plant_state.PlantState;

import java.util.ArrayList;
import java.util.List;

/**
 * 快递状态适配器
 * Created by liu on 2017/12/9.
 */

public class LogisticsAdapter extends BaseAdapter {

    private Context context = null;

    private List<Data> dataList = new ArrayList<>();

    private PlantState plantState = PlantState.getPlantState();

    public LogisticsAdapter(Context context) {
        this.context = context;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == getCount() - 1) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public int getCount() {
        return dataList.size();
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
        int type = getItemViewType(position);
        switch(type){
            case 0:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_logistics_one,null);
                break;
            case 1:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_logistics_two,null);
                break;
            case 2:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_logistics_three,null);
                break;
        }
        TextView tv_logistics_item_which = (TextView) convertView.findViewById(R.id.tv_logistics_item_which);
        TextView tv_logistics_item_data = (TextView) convertView.findViewById(R.id.tv_logistics_item_data);
        TextView tv_logistics_item_state= (TextView) convertView.findViewById(R.id.tv_logistics_item_state);
        TextView tv_logistics_item_content = (TextView) convertView.findViewById(R.id.tv_logistics_item_content);
        //左侧时间
        tv_logistics_item_which.setText(plantState.getOnDay(dataList.get(position).getTime(),5,10));
        tv_logistics_item_data.setText(plantState.getOnDay(dataList.get(position).getTime(),11,17));
        tv_logistics_item_state.setText(dataList.get(position).getStatus());
        tv_logistics_item_content.setText(dataList.get(position).getContext());
        return convertView;
    }
}
