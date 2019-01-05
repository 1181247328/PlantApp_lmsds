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
 * Created by liu on 2017/11/15.
 */

public class RegionDilogAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private String[] licenseList = null;

    public RegionDilogAdapter(Context context, String[] licenseList) {
        this.context = context;
        this.licenseList = licenseList;
    }

    @Override
    public int getCount() {
        return licenseList.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dilog_region, null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.tvRegionDilogItemName = convertView.findViewById(R.id.tv_region_dilog_item_name);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.tvRegionDilogItemName.setText(licenseList[position]);
        return convertView;
    }
}
