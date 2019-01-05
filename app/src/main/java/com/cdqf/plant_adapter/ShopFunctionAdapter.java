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
 * 商城类别适配器
 * Created by liu on 2017/10/30.
 */

public class ShopFunctionAdapter extends BaseAdapter {

    private String TAG = ShopFunctionAdapter.class.getSimpleName();

    private Context context = null;

    private int[] shopImage = new int[]{
            R.mipmap.lmsd_tickets,
            R.mipmap.lmsd_hot,
            R.mipmap.lmsd_hotel,
            R.mipmap.lmsd_specialty
    };

    private String[] shopName = new String[]{
            "门票",
            "温泉",
            "酒店",
            "特产"
    };

    public ShopFunctionAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return shopImage.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_shopfunction,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.ivShopItemLogo = (ImageView) convertView.findViewById(R.id.iv_shop_item_logo);
            plantViewHolder.tvShopItemName = (TextView) convertView.findViewById(R.id.tv_shop_item_name);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.ivShopItemLogo.setImageResource(shopImage[position]);
        plantViewHolder.tvShopItemName.setText(shopName[position]);
        return convertView;
    }
}
