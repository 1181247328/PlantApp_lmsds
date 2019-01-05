package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_class.Integral;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 兑换商品
 * Created by liu on 2018/1/11.
 */

public class IntegralAdapter extends BaseAdapter {

    private String TAG = RecommendedAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = new PlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private List<Integral> integralList = new CopyOnWriteArrayList<>();

    public IntegralAdapter(Context context,List<Integral> integralList) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
        this.integralList = integralList;
    }

    public void setIntegralList(List<Integral> integralList){
        this.integralList.addAll(integralList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return integralList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_integral, null);
            plantViewHolder = new PlantViewHolder();
            //图片
            plantViewHolder.ivShopItemFigure = (ImageView) convertView.findViewById(R.id.iv_integral_item_figure);
            //门票名
            plantViewHolder.tvShopItemTickets = (TextView) convertView.findViewById(R.id.iv_integral_item_tickets);
            //是否包邮
            plantViewHolder.tvShopItemMail = (TextView) convertView.findViewById(R.id.tv_integral_item_mail);
            //金额
            plantViewHolder.tvShopItemPrice = (TextView) convertView.findViewById(R.id.tv_integral_item_price);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //图片
        imageLoader.displayImage(integralList.get(position).getPicture(), plantViewHolder.ivShopItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        //名称
        plantViewHolder.tvShopItemTickets.setText(integralList.get(position).getCommName());
        //
        plantViewHolder.tvShopItemMail.setText("+" + integralList.get(position).getIntegralMoney() + "元");
        //积分
        plantViewHolder.tvShopItemPrice.setText(integralList.get(position).getIntegralNumber() + "积分");
        return convertView;
    }
}