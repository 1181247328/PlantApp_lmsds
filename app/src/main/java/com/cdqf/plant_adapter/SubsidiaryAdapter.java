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
 * 积分明细适配器
 * Created by liu on 2018/1/11.
 */

public class SubsidiaryAdapter extends BaseAdapter {

    private String TAG = SubsidiaryAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    public SubsidiaryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantState.getSubsidiaryList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_subidiary, null);
            plantViewHolder = new PlantViewHolder();
            //名称
            plantViewHolder.tvSubsidiaryItemName = convertView.findViewById(R.id.tv_subsidiary_item_name);
            //时间
            plantViewHolder.tvSubsidiaryItemTimer = convertView.findViewById(R.id.tv_subsidiary_item_timer);
            //兑换所得积分
            plantViewHolder.tvSubsidiaryItemMoney = convertView.findViewById(R.id.tv_subsidiary_item_money);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.tvSubsidiaryItemName.setText(plantState.getSubsidiaryList().get(position).getOrderNo());
        plantViewHolder.tvSubsidiaryItemTimer.setText(plantState.getSubsidiaryList().get(position).getStrRecordDate());
        if (plantState.getSubsidiaryList().get(position).getIntegralNumber() < 0) {
            plantViewHolder.tvSubsidiaryItemMoney.setText(plantState.getSubsidiaryList().get(position).getIntegralNumber() + "");
        } else if (plantState.getSubsidiaryList().get(position).getIntegralNumber() > 0) {
            plantViewHolder.tvSubsidiaryItemMoney.setText("+" + plantState.getSubsidiaryList().get(position).getIntegralNumber());
        } else {
            plantViewHolder.tvSubsidiaryItemMoney.setText("0");
        }
        return convertView;
    }
}
