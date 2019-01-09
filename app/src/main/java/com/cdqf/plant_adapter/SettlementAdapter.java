package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_class.Settlement;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 订单信息集合
 * Created by liu on 2018/1/5.
 */

public class SettlementAdapter extends BaseAdapter {

    private String TAG = SettlementAdapter.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private Settlement settlement;

    public SettlementAdapter(Context context) {
        this.context = context;
        settlement = new Settlement();
        imageLoader = plantState.getImageLoader(context);
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return settlement.getCommList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_settlement, null);
            plantViewHolder = new PlantViewHolder();
            //图片
            plantViewHolder.ivSettlementItemFigure = convertView.findViewById(R.id.iv_settlement_item_figure);
            //商品名称
            plantViewHolder.tvSettlementItemName = convertView.findViewById(R.id.tv_settlement_item_name);
            //价格
            plantViewHolder.tvSettlementItemPrice = convertView.findViewById(R.id.tv_settlement_item_price);
            //数量
            plantViewHolder.tvSettlementItemReduction = convertView.findViewById(R.id.tv_settlement_item_reduction);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(settlement.getCommList().get(position).getCommHttpPic(), plantViewHolder.ivSettlementItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        plantViewHolder.tvSettlementItemName.setText(settlement.getCommList().get(position).getCommName());
        plantViewHolder.tvSettlementItemPrice.setText((double) settlement.getCommList().get(position).getPrice() + "");
        plantViewHolder.tvSettlementItemReduction.setText("X" + settlement.getCommList().get(position).getNumber());
        return convertView;
    }
}
