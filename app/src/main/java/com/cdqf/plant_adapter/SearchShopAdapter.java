package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by liu on 2018/1/4.
 */

public class SearchShopAdapter extends BaseAdapter {

    private String TAG = GoodsAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public SearchShopAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getSearchList().size();
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
            plantViewHolder.ivShopItemFigure = convertView.findViewById(R.id.iv_shop_item_figure);
            //门票名
            plantViewHolder.tvShopItemTickets = convertView.findViewById(R.id.iv_shop_item_tickets);
            //是否包邮
            plantViewHolder.tvShopItemMail = convertView.findViewById(R.id.tv_shop_item_mail);
            //金额
            plantViewHolder.tvShopItemPrice = convertView.findViewById(R.id.tv_shop_item_price);
            //付款人
            plantViewHolder.tvShopItemPayment = convertView.findViewById(R.id.tv_shop_item_payment);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //图片
        imageLoader.displayImage(plantState.getSearchList().get(position).getImgPicture(), plantViewHolder.ivShopItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        //门票名
        plantViewHolder.tvShopItemTickets.setText(plantState.getSearchList().get(position).getCommName());
        //是否包邮
        if (plantState.getSearchList().get(position).isPostFree()) {
            plantViewHolder.tvShopItemMail.setText(context.getResources().getString(R.string.mail_package));
        } else {
            plantViewHolder.tvShopItemMail.setText(context.getResources().getString(R.string.mail_dontpackage));
        }
        //金额
        plantViewHolder.tvShopItemPrice.setText(plantState.getSearchList().get(position).getPrice() + "");
        //付款人
        plantViewHolder.tvShopItemPayment.setText(plantState.getSearchList().get(position).getPayer()+"");
        return convertView;
    }
}
