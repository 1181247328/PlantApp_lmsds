package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantViewHolder;

/**
 * 其它功能适配器
 * Created by liu on 2017/10/30.
 */

public class OtherAdapter extends BaseAdapter {

    private Context context = null;

    private int[] orderImage = new int[]{
            R.mipmap.my_spot,
//            R.mipmap.my_av,
//            R.mipmap.my_vehicle,
            R.mipmap.my_visit,
            R.mipmap.my_travel,
//            R.mipmap.my_traffic,
//            R.mipmap.my_surrounding,
//            R.mipmap.my_consulting,
    };

    private String[] orderName = new String[]{
            "景区介绍",
//            "影音管理",
//            "车辆管理",
            "参观指南",
//            "游记",
//            "交通信息",
//            "周边旅游",
//            "收藏"
            "我的门票"
    };

    public OtherAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderName.length;
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
        PlantViewHolder plantViewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.ivMyItemLogo = (ImageView) convertView.findViewById(R.id.iv_my_item_logo);
            plantViewHolder.tvMyItemName = (TextView) convertView.findViewById(R.id.tv_my_item_name);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.ivMyItemLogo.setImageResource(orderImage[position]);
        plantViewHolder.tvMyItemName.setText(orderName[position]);
        return convertView;
    }
}
