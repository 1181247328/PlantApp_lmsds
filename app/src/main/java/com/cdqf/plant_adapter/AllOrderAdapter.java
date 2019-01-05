package com.cdqf.plant_adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_activity.EvaluateActivity;
import com.cdqf.plant_activity.LogisticsActivity;
import com.cdqf.plant_find.AllDeleteOrderFind;
import com.cdqf.plant_find.AllPayFind;
import com.cdqf.plant_find.CancelAllFind;
import com.cdqf.plant_find.GoodsAllFind;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.cdqf.plant_view.ListViewForScrollView;
import com.gcssloop.widget.RCRelativeLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * 全部订单适配器
 * Created by liu on 2017/11/20.
 */

public class AllOrderAdapter extends BaseAdapter {

    private String TAG = AllOrderAdapter.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    public AllOrderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        Log.e(TAG, "---getItemViewType---" + plantState.getAllOrderList().get(position).getOrderStatus());
        if (TextUtils.equals(plantState.getAllOrderList().get(position).getOrderStatus(), "待付款")) {
            return 0;
        } else if (TextUtils.equals(plantState.getAllOrderList().get(position).getOrderStatus(), "待发货")) {
            return 1;
        } else if (TextUtils.equals(plantState.getAllOrderList().get(position).getOrderStatus(), "待收货")) {
            return 2;
        } else if (TextUtils.equals(plantState.getAllOrderList().get(position).getOrderStatus(), "交易成功")) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public int getCount() {
        return plantState.getAllOrderList().size();
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
        Log.e(TAG, "---type---" + type);
        switch (type) {
            //待付款
            case 0:
                PlantViewHolder plantViewHolder = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.item_allforpayment, null);
                plantViewHolder = new PlantViewHolder();
                plantViewHolder.lvOrderItemList = (ListViewForScrollView) convertView.findViewById(R.id.lv_order_item_list);
                //合计
                plantViewHolder.tvOrderItemCombined = (TextView) convertView.findViewById(R.id.tv_order_item_combined);
                //状态
                plantViewHolder.tvOrderItemForpayment = (TextView) convertView.findViewById(R.id.tv_order_item_forpayment);
                //取消订单
                plantViewHolder.rcrlOrderItemOne = (RCRelativeLayout) convertView.findViewById(R.id.rcrl_order_item_one);
                //订单操作
                plantViewHolder.rcrlOrderItemThree = (RCRelativeLayout) convertView.findViewById(R.id.rcrl_order_item_three);

                plantViewHolder.lvOrderItemList.setAdapter(new GoodsAdapter(position));
                plantViewHolder.tvOrderItemCombined.setText("共" + plantState.getAllOrderList().get(position).getCommCount() + "件商品  合计:￥" + plantState.getAllOrderList().get(position).getTotalPrice() + "(含运费￥0.00)");
                //取消订单
                plantViewHolder.rcrlOrderItemOne.setOnClickListener(new OnCeancelListener(position));
                //支付
                plantViewHolder.rcrlOrderItemThree.setOnClickListener(new OnPayListener(position));
                break;
            //待发货
            case 1:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_sendsgoods, null);
                plantViewHolder = new PlantViewHolder();
                plantViewHolder.lvOrderItemList = (ListViewForScrollView) convertView.findViewById(R.id.lv_order_item_list);
                //合计
                plantViewHolder.tvOrderItemCombined = (TextView) convertView.findViewById(R.id.tv_order_item_combined);
                //状态
                plantViewHolder.tvOrderItemForpayment = (TextView) convertView.findViewById(R.id.tv_order_item_forpayment);
                //订单操作
                plantViewHolder.rcrlOrderItemThree = (RCRelativeLayout) convertView.findViewById(R.id.rcrl_order_item_three);
                plantViewHolder.lvOrderItemList.setAdapter(new GoodsAdapter(position));
                //总价
                plantViewHolder.tvOrderItemCombined.setText("共" + plantState.getAllOrderList().get(position).getCommCount() + "件商品  合计:￥" + plantState.getAllOrderList().get(position).getTotalPrice() + "(含运费￥0.00)");
                break;
            //待收货
            case 2:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_forgoods, null);
                plantViewHolder = new PlantViewHolder();
                plantViewHolder.lvOrderItemList = (ListViewForScrollView) convertView.findViewById(R.id.lv_order_item_list);
                //合计
                plantViewHolder.tvOrderItemCombined = (TextView) convertView.findViewById(R.id.tv_order_item_combined);
                //状态
                plantViewHolder.tvOrderItemForpayment = (TextView) convertView.findViewById(R.id.tv_order_item_forpayment);
                //查看物流
                plantViewHolder.rcrlOrderItemTwo = (RCRelativeLayout) convertView.findViewById(R.id.rcrl_order_item_two);
                //确认收货
                plantViewHolder.rcrlOrderItemThree = (RCRelativeLayout) convertView.findViewById(R.id.rcrl_order_item_three);                plantViewHolder.lvOrderItemList.setAdapter(new GoodsAdapter(position));
                //总价
                plantViewHolder.tvOrderItemCombined.setText("共" + plantState.getAllOrderList().get(position).getCommCount() + "件商品  合计:￥" + plantState.getAllOrderList().get(position).getTotalPrice() + "(含运费￥0.00)");
                //查看物流
                plantViewHolder.rcrlOrderItemTwo.setOnClickListener(new OnLogisticsListener(position));
                //确认收货
                plantViewHolder.rcrlOrderItemThree.setOnClickListener(new OnOrderListener(position));
                break;
            //交易成功(待评价)
            case 3:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_evaluate, null);
                plantViewHolder = new PlantViewHolder();
                plantViewHolder.lvOrderItemList = (ListViewForScrollView) convertView.findViewById(R.id.lv_order_item_list);
                //合计
                plantViewHolder.tvOrderItemCombined = (TextView) convertView.findViewById(R.id.tv_order_item_combined);
                //状态
                plantViewHolder.tvOrderItemForpayment = (TextView) convertView.findViewById(R.id.tv_order_item_forpayment);
                //订单操作
                plantViewHolder.rcrlOrderItemThree = (RCRelativeLayout) convertView.findViewById(R.id.rcrl_order_item_three);
                plantViewHolder.lvOrderItemList.setAdapter(new GoodsAdapter(position));
                //总价
                plantViewHolder.tvOrderItemCombined.setText("共" + plantState.getAllOrderList().get(position).getCommCount() + "件商品  合计:￥" + plantState.getAllOrderList().get(position).getTotalPrice() + "(含运费￥0.00)");
                //评价
                plantViewHolder.rcrlOrderItemThree.setOnClickListener(new OnEvaluateListener(position));
                break;
            //已取消
            case 4:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_allceanl, null);
                plantViewHolder = new PlantViewHolder();
                plantViewHolder.lvOrderItemList = (ListViewForScrollView) convertView.findViewById(R.id.lv_order_item_list);
                //合计
                plantViewHolder.tvOrderItemCombined = (TextView) convertView.findViewById(R.id.tv_order_item_combined);
                //状态
                plantViewHolder.rcrlOrderItemOne = (RCRelativeLayout) convertView.findViewById(R.id.rcrl_order_item_one);
                plantViewHolder.lvOrderItemList.setAdapter(new GoodsAdapter(position));
                //总价
                plantViewHolder.tvOrderItemCombined.setText("共" + plantState.getAllOrderList().get(position).getCommCount() + "件商品  合计:￥" + plantState.getAllOrderList().get(position).getTotalPrice() + "(含运费￥0.00)");
                plantViewHolder.rcrlOrderItemOne.setOnClickListener(new OnDeteleListener(position));
                break;
        }
        return convertView;
    }

    /********************待付款******************/

    /**
     * 取消订单
     */
    class OnCeancelListener implements View.OnClickListener {

        private int position;

        public OnCeancelListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new CancelAllFind(position));
        }
    }

    /**
     * 支付
     */
    class OnPayListener implements View.OnClickListener {
        public int position;

        public OnPayListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new AllPayFind(position));
        }
    }

    /*****************待收货****************/

    /**
     * 查看物流
     */
    class OnLogisticsListener implements View.OnClickListener{

        private int position;

        public OnLogisticsListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, LogisticsActivity.class);
            intent.putExtra("type",0);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }

    /**
     * 确认收货
     */
    class OnOrderListener implements View.OnClickListener {

        private int position;

        public OnOrderListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new GoodsAllFind(position));
        }
    }

    /****************待评价********************/
    /**
     * 评价
     */
    class OnEvaluateListener implements View.OnClickListener {

        private int position;

        public OnEvaluateListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, EvaluateActivity.class);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }
    }

    /******************已取消*********************/
    /**
     * 删除订单
     */
    class OnDeteleListener implements View.OnClickListener {
        private int position;

        public OnDeteleListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new AllDeleteOrderFind(position));
        }
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
            return plantState.getAllOrderList().get(position).getOrderCommList().size();
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
            imageLoader.displayImage(plantState.getAllOrderList().get(this.position).getOrderCommList().get(position).getImgCommPic(), plantViewHolder.ivOrderItemIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
            plantViewHolder.tvOrderItemTitle.setText(plantState.getAllOrderList().get(this.position).getOrderCommList().get(position).getCommName());
            plantViewHolder.tvOrderItemPrice.setText(plantState.getAllOrderList().get(this.position).getOrderCommList().get(position).getCommPrice() + "");
            plantViewHolder.tvOrderItemNumber.setText(plantState.getAllOrderList().get(this.position).getOrderCommList().get(position).getCommNum() + "");
            return convertView;
        }
    }
}
