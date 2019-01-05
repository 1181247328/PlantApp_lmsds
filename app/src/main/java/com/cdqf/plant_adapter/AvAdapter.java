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
 * 影音管理适配器
 * Created by liu on 2017/11/27.
 */

public class AvAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public AvAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantState.getAvList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_av,null);
            plantViewHolder = new PlantViewHolder();
            //删除
            plantViewHolder.rlAvItemDelete = convertView.findViewById(R.id.rl_av_item_delete);

            //图片
            plantViewHolder.ivAvItemVideo = convertView.findViewById(R.id.iv_av_item_video);

            //标题
            plantViewHolder.tvAvItemTitle = convertView.findViewById(R.id.tv_av_item_title);

            //状态
            plantViewHolder.tvAvItemState = convertView.findViewById(R.id.tv_av_item_state);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        if(plantState.getAvList().get(position).isDelete()){
            plantViewHolder.rlAvItemDelete.setVisibility(View.VISIBLE);
        } else {
            plantViewHolder.rlAvItemDelete.setVisibility(View.GONE);
        }
        //图片
        imageLoader.displayImage(plantState.getAvList().get(position).getHttpPic(),plantViewHolder.ivAvItemVideo,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        //景点
        plantViewHolder.tvAvItemTitle.setText(plantState.getAvList().get(position).getStrVoiceType());
        //声音进度条
        plantViewHolder.tvAvItemState.setText(plantState.getAvList().get(position).getTotal());
        plantViewHolder.tvAvItemState.setVisibility(View.GONE);
        return convertView;
    }
}
