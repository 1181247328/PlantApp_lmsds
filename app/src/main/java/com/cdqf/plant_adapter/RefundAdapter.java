package com.cdqf.plant_adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_activity.RefundetailsActivity;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by liu on 2017/12/29.
 */

public class RefundAdapter extends BaseAdapter {

    private String TAG = RefundAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public RefundAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        Log.e(TAG, "---退款数量---" + plantState.getRefundList().size());
        return plantState.getRefundList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_refund, null);
            plantViewHolder = new PlantViewHolder();
            //图标
            plantViewHolder.ivOrderItemIcon = convertView.findViewById(R.id.iv_order_item_icon);
            //商品名称
            plantViewHolder.tvOrderItemTitle = convertView.findViewById(R.id.tv_order_item_title);
            //数量
            plantViewHolder.tvOrderItemNumber = convertView.findViewById(R.id.tv_order_item_number);
            //状态
            plantViewHolder.tvRefundItemState = convertView.findViewById(R.id.tv_refund_item_state);
            //查看详情
            plantViewHolder.rcrlRefundDetails = convertView.findViewById(R.id.rcrl_refund_details);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
//        plantViewHolder.lvRefundItemList.setAdapter(new GoodsAdapter(position));
        imageLoader.displayImage(plantState.getRefundList().get(position).getHttpPic(), plantViewHolder.ivOrderItemIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        plantViewHolder.tvOrderItemTitle.setText(plantState.getRefundList().get(position).getCommName() + "");
        plantViewHolder.tvOrderItemNumber.setText(plantState.getRefundList().get(position).getCommNum() + "");
        plantViewHolder.tvRefundItemState.setText(plantState.getRefundList().get(position).getStrReturnGoodsType());
        plantViewHolder.rcrlRefundDetails.setOnClickListener(new OnDetailsListener(position));
        return convertView;
    }

    class OnDetailsListener implements View.OnClickListener {

        private int position;

        public OnDetailsListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RefundetailsActivity.class);
            intent.putExtra("position", position);
            context.startActivity(intent);
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
            return plantState.getRefundList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.itemtwo_refund, null);
            plantViewHolder = new PlantViewHolder();
            //图标
            plantViewHolder.ivOrderItemIcon = convertView.findViewById(R.id.iv_order_item_icon);
            //商品名称
            plantViewHolder.tvOrderItemTitle = convertView.findViewById(R.id.tv_order_item_title);
            //数量
            plantViewHolder.tvOrderItemNumber = convertView.findViewById(R.id.tv_order_item_number);

            imageLoader.displayImage(plantState.getRefundList().get(position).getHttpPic(), plantViewHolder.ivOrderItemIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));

            plantViewHolder.tvOrderItemTitle.setText(plantState.getRefundList().get(position).getCommName() + "");

            plantViewHolder.tvOrderItemNumber.setText(plantState.getRefundList().get(position).getCommNum() + "");

            return convertView;
        }
    }
}
