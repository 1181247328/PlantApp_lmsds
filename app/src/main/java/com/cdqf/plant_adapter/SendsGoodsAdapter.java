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
import com.cdqf.plant_view.ListViewForScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * 待发货适配器
 * Created by liu on 2017/11/20.
 */

public class SendsGoodsAdapter extends BaseAdapter {

    private Context context = null;

    private ImageLoader imageLoader= ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    public SendsGoodsAdapter(Context context) {
        this.context = context;
        imageLoader= plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getSendGoodsList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sendsgoods,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.lvOrderItemList = (ListViewForScrollView) convertView.findViewById(R.id.lv_order_item_list);
            //合计
            plantViewHolder.tvOrderItemCombined = (TextView) convertView.findViewById(R.id.tv_order_item_combined);
            //状态
            plantViewHolder.tvOrderItemForpayment = (TextView) convertView.findViewById(R.id.tv_order_item_forpayment);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.lvOrderItemList.setAdapter(new GoodsAdapter(position));
        //总价
        plantViewHolder.tvOrderItemCombined.setText("共" + plantState.getSendGoodsList().get(position).getCommCount() + "件商品  合计:￥" + plantState.getSendGoodsList().get(position).getTotalPrice() + "(含运费￥0.00)");
        return convertView;
    }

    /**
     * 商品适配器
     */
    class GoodsAdapter extends BaseAdapter {

        private int position;

        public GoodsAdapter(int position) {
            this.position = position;

        }

        @Override
        public int getCount() {
            return plantState.getSendGoodsList().get(position).getOrderCommList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.itemtwo_goods, null);
            plantViewHolder = new PlantViewHolder();
            //图标
            plantViewHolder.ivOrderItemIcon = (ImageView) convertView.findViewById(R.id.iv_order_item_icon);
            //商品名称
            plantViewHolder.tvOrderItemTitle = (TextView) convertView.findViewById(R.id.tv_order_item_title);
            //价格
            plantViewHolder.tvOrderItemPrice = (TextView) convertView.findViewById(R.id.tv_order_item_price);
            //数量
            plantViewHolder.tvOrderItemNumber = (TextView) convertView.findViewById(R.id.tv_order_item_number);
            imageLoader.displayImage(plantState.getSendGoodsList().get(this.position).getOrderCommList().get(position).getImgCommPic(), plantViewHolder.ivOrderItemIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
            plantViewHolder.tvOrderItemTitle.setText(plantState.getSendGoodsList().get(this.position).getOrderCommList().get(position).getCommName());
            plantViewHolder.tvOrderItemPrice.setText(plantState.getSendGoodsList().get(this.position).getOrderCommList().get(position).getCommPrice() + "");
            plantViewHolder.tvOrderItemNumber.setText(plantState.getSendGoodsList().get(this.position).getOrderCommList().get(position).getCommNum() + "");
            return convertView;
        }
    }
}
