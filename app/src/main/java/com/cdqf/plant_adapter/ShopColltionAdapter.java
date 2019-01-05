package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 商品收藏适配器
 * Created by liu on 2017/11/24.
 */

public class ShopColltionAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public ShopColltionAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getShopColltionList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recommended, null);
            plantViewHolder = new PlantViewHolder();
            //图片
            plantViewHolder.ivShopItemFigure = (ImageView) convertView.findViewById(R.id.iv_shop_item_figure);
            //门票名
            plantViewHolder.tvShopItemTickets = (TextView) convertView.findViewById(R.id.iv_shop_item_tickets);
            //是否包邮
            plantViewHolder.tvShopItemMail = (TextView) convertView.findViewById(R.id.tv_shop_item_mail);
            //金额
            plantViewHolder.tvShopItemPrice = (TextView) convertView.findViewById(R.id.tv_shop_item_price);
            //付款人
            plantViewHolder.tvShopItemPayment = (TextView) convertView.findViewById(R.id.tv_shop_item_payment);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(plantState.getShopColltionList().get(position).getHttpPic(),plantViewHolder.ivShopItemFigure,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        plantViewHolder.tvShopItemTickets.setText(plantState.getShopColltionList().get(position).getCommName());
        plantViewHolder.tvShopItemPrice.setText(plantState.getShopColltionList().get(position).getPrice()+"");
        plantViewHolder.tvShopItemPayment.setText(plantState.getShopColltionList().get(position).getPayer()+"人付款");
        return convertView;
    }
}