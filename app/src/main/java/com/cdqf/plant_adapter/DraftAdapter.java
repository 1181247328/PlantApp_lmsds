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
 * 草稿适配器
 * Created by liu on 2017/11/16.
 */

public class DraftAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public DraftAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getDraftList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_hasbeen, null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.ivHasbeenItemPicture = (ImageView) convertView.findViewById(R.id.iv_hasbeen_item_picture);
            plantViewHolder.tvHasbeenItemTitle = (TextView) convertView.findViewById(R.id.tv_hasbeen_item_title);
            plantViewHolder.tvHasbeenItemData = (TextView) convertView.findViewById(R.id.tv_hasbeen_item_data);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //图片
        imageLoader.displayImage(plantState.getDraftList().get(position).getHttpPic(), plantViewHolder.ivHasbeenItemPicture, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        //标题
        plantViewHolder.tvHasbeenItemTitle.setText(plantState.getDraftList().get(position).getTitle());
        //更新时间
        plantViewHolder.tvHasbeenItemData.setText("最近更新时间 " + plantState.getDraftList().get(position).getEditDate());

        return convertView;
    }
}