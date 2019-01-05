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
 * 线路适配器
 * Created by liu on 2017/10/24.
 */

public class PlanningAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private String[] planningName = new String[]{
            "精品线路",
            "速览路线",
            "科普线路"
    };

    private int[] planningColor = new int[]{
            R.color.planning_item,
            R.color.planning_item_two,
            R.color.planning_item_three
    };

    public PlanningAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getRoadList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_planning,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.ivPlanningItemPicture = convertView.findViewById(R.id.iv_planning_item_picture);
            plantViewHolder.stvPlanningItemName = convertView.findViewById(R.id.stv_planning_item_name);
            plantViewHolder.tvPlanningItemSpecific = convertView.findViewById(R.id.tv_planning_item_specific);
            plantViewHolder.tvPlanningItemDetails = convertView.findViewById(R.id.tv_planning_item_details);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(plantState.getRoadList().get(position).getHttpPic(),plantViewHolder.ivPlanningItemPicture,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        plantViewHolder.stvPlanningItemName.setText(plantState.getRoadList().get(position).getVrName());
        plantViewHolder.tvPlanningItemDetails.setText(plantState.getRoadList().get(position).getIntroduction());
        return convertView;
    }
}
