package com.cdqf.plant_adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_activity.ServiceActivity;
import com.cdqf.plant_class.OrderDetails;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 订单详情商品适配器
 * Created by liu on 2018/1/5.
 */

public class OrderDetailsAdapter extends BaseAdapter {

    private String TAG = OrderDetailsAdapter.class.getSimpleName();

    private Context context = null;

    private OrderDetails orderDetails = new OrderDetails();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private String orderStatus;

    public OrderDetailsAdapter(Context context) {
        this.context = context;
        orderDetails = new OrderDetails();
        imageLoader = plantState.getImageLoader(context);
    }

    public void setOrderDetails(OrderDetails orderDetails, String orderStatus) {
        this.orderDetails = orderDetails;
        this.orderStatus = orderStatus;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return orderDetails.getCommList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_orderdetails, null);
            plantViewHolder = new PlantViewHolder();
            //图片
            plantViewHolder.ivOrderdetailsItemFigure = convertView.findViewById(R.id.iv_orderdetails_item_figure);
            //商品名称
            plantViewHolder.tvOrderdetailsItemName = convertView.findViewById(R.id.tv_ordertails_item_name);
            //价格
            plantViewHolder.tvOrderdetailstemPrice = convertView.findViewById(R.id.tv_orderdetails_item_price);
            //数量
            plantViewHolder.tvOrderdetailsItemReduction = convertView.findViewById(R.id.tv_orderdetails_item_number);
            //退款
            plantViewHolder.rcrlOrderdetailsItemRefund = convertView.findViewById(R.id.rcrl_orderdetails_item_refund);
            plantViewHolder.tvOrderdetailsItemRefund = convertView.findViewById(R.id.tv_orderdetails_item_refund);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //图片
        imageLoader.displayImage(orderDetails.getCommList().get(position).getImgCommPic(), plantViewHolder.ivOrderdetailsItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        plantViewHolder.tvOrderdetailsItemName.setText(orderDetails.getCommList().get(position).getCommName());
        plantViewHolder.tvOrderdetailstemPrice.setText(orderDetails.getCommList().get(position).getCommPrice() + "");
        plantViewHolder.tvOrderdetailsItemReduction.setText(orderDetails.getCommList().get(position).getCommNum() + "");

        if (TextUtils.equals(orderStatus, "待发货") || TextUtils.equals(orderStatus, "待评价")) {

            /*****退款状态******/
            plantViewHolder.rcrlOrderdetailsItemRefund.setVisibility(View.VISIBLE);
            int returnGoodsStatus = orderDetails.getCommList().get(position).getReturnGoodsStatus();
            plantViewHolder.tvOrderdetailsItemRefund.setText(orderDetails.getCommList().get(position).getStrReturnGoodsStatus());
            plantViewHolder.rcrlOrderdetailsItemRefund.setOnClickListener(new OnRefundListener(position, returnGoodsStatus));
        } else {
            plantViewHolder.rcrlOrderdetailsItemRefund.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 退款操作
     */
    class OnRefundListener implements View.OnClickListener {

        private int position;

        private int refundState;

        public OnRefundListener(int position, int refundState) {
            this.position = position;
            this.refundState = refundState;
        }

        @Override
        public void onClick(View v) {
            if (refundState == 0) {
                Intent intent = new Intent(context, ServiceActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        }
    }
}
