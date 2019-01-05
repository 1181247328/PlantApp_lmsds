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
 * 景区情况
 * Created by liu on 2017/10/20.
 */

public class SituationAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public SituationAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getNews().getList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_situation,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.ivSituationItemPicture = (ImageView) convertView.findViewById(R.id.iv_situation_item_picture);
            plantViewHolder.tvSituationItemContext = (TextView) convertView.findViewById(R.id.tv_situation_item_context);
            plantViewHolder.tvSituationItemData = (TextView) convertView.findViewById(R.id.tv_situation_item_data);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //图片
        imageLoader.displayImage(plantState.getNews().getList().get(position).getHttpDefaultPic(),plantViewHolder.ivSituationItemPicture,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
       //内容
        plantViewHolder.tvSituationItemContext.setText(plantState.getNews().getList().get(position).getTitle());
        //时间
        plantViewHolder.tvSituationItemData.setText(plantState.getNews().getList().get(position).getStrPushDate());
        return convertView;
    }
}
