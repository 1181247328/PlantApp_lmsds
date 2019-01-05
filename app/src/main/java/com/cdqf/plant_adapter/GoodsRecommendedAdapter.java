package com.cdqf.plant_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_class.Commlist;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 推荐适配器
 * Created by liu on 2017/11/9.
 */

public class GoodsRecommendedAdapter extends BaseAdapter {

    private String TAG = GoodsRecommendedAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = new PlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private List<Commlist> commlist = new CopyOnWriteArrayList<Commlist>();

    public GoodsRecommendedAdapter(Context context,ImageLoader imageLoader,List<Commlist> commlist) {
        this.context = context;
        this.imageLoader = imageLoader;
        this.commlist = commlist;
        Log.e(TAG, "---推荐---" + plantState.getCommlist().size());
    }

    @Override
    public int getCount() {
        Log.e(TAG,"---商品数量---"+commlist.size());
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
        //图片
        imageLoader.displayImage(commlist.get(position).getImgpicture(), plantViewHolder.ivShopItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        //门票名
        plantViewHolder.tvShopItemTickets.setText(commlist.get(position).getCommname());
        //是否包邮
        if (commlist.get(position).ispostfree()) {
            plantViewHolder.tvShopItemMail.setText(context.getResources().getString(R.string.mail_package));
        } else {
            plantViewHolder.tvShopItemMail.setText(context.getResources().getString(R.string.mail_dontpackage));
        }
        //金额
        plantViewHolder.tvShopItemPrice.setText(commlist.get(position).getPrice() + "");
        //付款人
        plantViewHolder.tvShopItemPayment.setText(commlist.get(position).getPayer()+"");
        return convertView;
    }
}
