package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;

/**
 * 商品品质适配器
 * Created by liu on 2017/11/9.
 */

public class GoodsQualityAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    public GoodsQualityAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantState.getGoodsDetails().getServiceList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goodsquality,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.tvDetailsItemEnsure = convertView.findViewById(R.id.tv_details_item_ensure);
            convertView.setTag(plantViewHolder);
        }else{
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.tvDetailsItemEnsure.setText(plantState.getGoodsDetails().getServiceList().get(position).getName());
        return convertView;
    }
}
