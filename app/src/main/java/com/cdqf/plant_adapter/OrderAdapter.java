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
 * 订单适配器
 * Created by liu on 2017/10/30.
 */

public class OrderAdapter extends BaseAdapter {

    private Context context = null;

    private int[] orderImage = new int[]{
            R.mipmap.my_payment,
            R.mipmap.my_delivery,
            R.mipmap.my_goods,
            R.mipmap.my_evaluation,
//            R.mipmap.my_other,
    };

    private String[] orderName = new String[]{
            "待付款",
            "待发货",
            "待收货",
            "待评价",
//            "退款"
    };

    public OrderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 4;
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
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, null);
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
