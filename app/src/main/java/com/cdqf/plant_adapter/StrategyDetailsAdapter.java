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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 游记详情适配器
 * Created by liu on 2017/12/19.
 */

public class StrategyDetailsAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private List<String> coList = new CopyOnWriteArrayList<String>();

    public StrategyDetailsAdapter(Context context,List<String> coList) {
        this.context = context;
        this.coList = coList;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(plantState.isUrl(coList.get(position))){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getCount() {
        return coList.size();
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
        int type = getItemViewType(position);
        PlantViewHolder plantViewHolder = null;
        switch (type) {
            case 0:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.details_one, null);
                    plantViewHolder = new PlantViewHolder();
                    plantViewHolder.tvTextItemContext = (TextView) convertView.findViewById(R.id.tv_text_item_context);
                    convertView.setTag(plantViewHolder);
                } else {
                    plantViewHolder = (PlantViewHolder) convertView.getTag();
                }
                plantViewHolder.tvTextItemContext.setText(coList.get(position));
                break;
            case 1:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.details_two, null);
                    plantViewHolder = new PlantViewHolder();
                    plantViewHolder.ivPictrueItemContext = (ImageView) convertView.findViewById(R.id.iv_pictrue_item_context);
                    convertView.setTag(plantViewHolder);
                } else {
                    plantViewHolder = (PlantViewHolder) convertView.getTag();
                }
                imageLoader.displayImage(coList.get(position), plantViewHolder.ivPictrueItemContext, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                break;
        }
        return convertView;
    }
}
