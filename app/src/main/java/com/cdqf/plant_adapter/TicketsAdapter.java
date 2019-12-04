package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_find.TicketsFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * 门票适配器
 */
public class TicketsAdapter extends BaseAdapter {

    private String TAG = TicketsAdapter.class.getSimpleName();

    private Context context = null;

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = null;

    public TicketsAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getTicketsList().size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        PlantViewHolder plantViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tickets, null);
            plantViewHolder = new PlantViewHolder();
            //图标
            plantViewHolder.ivOrderItemIcon = convertView.findViewById(R.id.iv_order_item_icon);
            //商品名称
            plantViewHolder.tvOrderItemTitle = convertView.findViewById(R.id.tv_order_item_title);
            //价格
            plantViewHolder.tvOrderItemPrice = convertView.findViewById(R.id.tv_order_item_price);
            //数量
            plantViewHolder.tvOrderItemNumber = convertView.findViewById(R.id.tv_order_item_number);
            //合计
            plantViewHolder.tvOrderItemCombined = convertView.findViewById(R.id.tv_order_item_combined);
            plantViewHolder.rcrlOrderItemThree = convertView.findViewById(R.id.rcrl_order_item_three);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(plantState.getTicketsList().get(position).getAdmissionTicketPic(), plantViewHolder.ivOrderItemIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        plantViewHolder.tvOrderItemTitle.setText(plantState.getTicketsList().get(position).getAdmissionTicketTitle());
        plantViewHolder.tvOrderItemPrice.setText(plantState.getTicketsList().get(position).getAdmissionTicketPrice()+"");
        plantViewHolder.tvOrderItemNumber.setText(plantState.getTicketsList().get(position).getAdmissionTicketCount()+"");
        plantViewHolder.tvOrderItemCombined.setText("共" + plantState.getTicketsList().get(position).getAdmissionTicketCount() + "件商品  合计:￥" + plantState.getTicketsList().get(position).getAdmissionTicketTotalCount());
        //支付
        plantViewHolder.rcrlOrderItemThree.setOnClickListener(new OnPayListener(position));
        return convertView;
    }

    /**
     * 验证码
     */
    class OnPayListener implements View.OnClickListener {

        private int position;

        public OnPayListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new TicketsFind(position));
        }
    }
}
