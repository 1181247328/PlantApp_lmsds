package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantViewHolder;

/**
 * 分享适配器
 * Created by liu on 2017/11/22.
 */

public class ShareDilogAdapter extends BaseAdapter {

    private Context context = null;

    //图片
    private int[] shareImage = new int[]{
            R.mipmap.login_qq,
            R.mipmap.login_wecht,
    };

    //名称
    private String[] shareName = new String[]{
            "QQ好友", "微信好友"
    };

    public ShareDilogAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dilog_share, null);
            plantViewHolder = new PlantViewHolder();
            //布局
            plantViewHolder.llDilogItemLayout = (LinearLayout) convertView.findViewById(R.id.ll_dilog_item_layout);
            //图片
            plantViewHolder.ivDilogItemImage = (ImageView) convertView.findViewById(R.id.iv_dilog_item_image);
            //分享名
            plantViewHolder.tvDilogItemName = (TextView) convertView.findViewById(R.id.tv_dilog_item_name);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.ivDilogItemImage.setImageResource(shareImage[position]);
        plantViewHolder.tvDilogItemName.setText(shareName[position]);
        return convertView;
    }
}
