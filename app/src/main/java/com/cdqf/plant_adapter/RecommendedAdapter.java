package com.cdqf.plant_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_class.Commlist;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 推荐适配器
 * Created by liu on 2017/11/8.
 */

public class RecommendedAdapter extends BaseAdapter {

    private String TAG = RecommendedAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = new PlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private List<Commlist> commlist = new CopyOnWriteArrayList<Commlist>();

    public RecommendedAdapter(Context context, ImageLoader imageLoader, List<Commlist> commlist) {
        this.context = context;
        this.imageLoader = imageLoader;
        this.commlist = commlist;
        Log.e(TAG, "---推荐---" + plantState.getCommlist().size());
    }

    @Override
    public int getCount() {
        Log.e(TAG, "---商品数量---" + commlist.size());
        return commlist.size();
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
        imageLoader.displayImage(commlist.get(position).getImgPicture(), plantViewHolder.ivShopItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        //门票名
        plantViewHolder.tvShopItemTickets.setText(commlist.get(position).getCommName());
        //是否包邮
        if (commlist.get(position).isPostFree()) {
            plantViewHolder.tvShopItemMail.setText(context.getResources().getString(R.string.mail_package));
        } else {
            plantViewHolder.tvShopItemMail.setText(context.getResources().getString(R.string.mail_dontpackage));
        }
        //金额
        plantViewHolder.tvShopItemPrice.setText(commlist.get(position).getPrice() + "");
        //付款人
        plantViewHolder.tvShopItemPayment.setText(commlist.get(position).getPayer() + "人付费");
        return convertView;
    }
}
