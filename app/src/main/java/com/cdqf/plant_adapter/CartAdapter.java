package com.cdqf.plant_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import com.cdqf.plant_find.CartAddFind;
import com.cdqf.plant_find.CartFind;
import com.cdqf.plant_find.CartReductionFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.cdqf.plant_lmsd.R.id.tv_cart_item_add;
import static com.cdqf.plant_lmsd.R.id.tv_cart_item_name;
import static com.cdqf.plant_lmsd.R.id.tv_cart_item_price;
import static com.cdqf.plant_lmsd.R.id.tv_cart_item_reduction;


/**
 * 购物车适配器
 * Created by liu on 2017/11/10.
 */

public class CartAdapter extends BaseAdapter {

    private String TAG = CartAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private Map<Integer, Boolean> lightCheckMap = new HashMap<Integer, Boolean>();

    private Map<CompoundButton, Integer> lightConnectMap = new HashMap<CompoundButton, Integer>();

    private EventBus eventBus = EventBus.getDefault();

    public CartAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantState.getCartList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, null);
            plantViewHolder = new PlantViewHolder();
            //选择
            plantViewHolder.cbCartItemCheckbox = convertView.findViewById(R.id.cb_cart_item_checkbox);
            //图片
            plantViewHolder.ivCartItemFigure = convertView.findViewById(R.id.iv_cart_item_figure);
            //商品名
            plantViewHolder.tvCartItemName = convertView.findViewById(tv_cart_item_name);
            //价格
            plantViewHolder.tvCartItemPrice = convertView.findViewById(tv_cart_item_price);
            //减
            plantViewHolder.tvCartItemReduction = convertView.findViewById(tv_cart_item_reduction);
            //数量
            plantViewHolder.tvCartItemNumber = convertView.findViewById(R.id.tv_cart_item_number);
            //加
            plantViewHolder.tvCartItemAdd = convertView.findViewById(tv_cart_item_add);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //图片
        plantState.getImageLoader(context).displayImage(plantState.getCartList().get(position).getCommHttpPic(), plantViewHolder.ivCartItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        //商品名称
        plantViewHolder.tvCartItemName.setText(plantState.getCartList().get(position).getCommName());
        //价格
        plantViewHolder.tvCartItemPrice.setText(plantState.getCartList().get(position).getPrice() + "");
        //数量
        plantViewHolder.tvCartItemNumber.setText(plantState.getCartList().get(position).getNumber() + "");

        lightConnectMap.put(plantViewHolder.cbCartItemCheckbox, position);
        plantViewHolder.cbCartItemCheckbox.setChecked(lightCheckMap.get(position) != null);
        plantViewHolder.cbCartItemCheckbox.setChecked(plantState.getCartList().get(position).isCart());
        plantViewHolder.tvCartItemReduction.setOnClickListener(new OnReductionListener(position));
        plantViewHolder.tvCartItemAdd.setOnClickListener(new OnAddListener(position));
        plantViewHolder.cbCartItemCheckbox.setOnCheckedChangeListener(new OnCartCheckedChangeListener(position));
        return convertView;
    }

    class OnCartCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        private int position = 0;

        public OnCartCheckedChangeListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isPressed()){
                return;
            }
            if (isChecked) {
                plantState.getCartList().get(position).setCart(true);
                lightCheckMap.put(lightConnectMap.get(buttonView), plantState.getCartList().get(position).isCart());
                eventBus.post(new CartFind(position));
            } else {
                plantState.getCartList().get(position).setCart(false);
                lightCheckMap.remove(lightConnectMap.get(buttonView));
                eventBus.post(new CartReductionFind(position));
            }
        }
    }

    //加
    class OnAddListener implements View.OnClickListener {

        private int position;

        public OnAddListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Log.e(TAG, "---加---" + position);
            //获取总数量
            int numberAll = plantState.getCartList().get(position).getNumber();
            numberAll += 1;
            plantState.getCartList().get(position).setNumber(numberAll);
            notifyDataSetChanged();
            //为true说明选中了
            if (plantState.getCartList().get(position).isCart()) {
                eventBus.post(new CartAddFind(position));
            }
        }
    }

    //减
    class OnReductionListener implements View.OnClickListener {

        private int position;

        public OnReductionListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            //获取总数量
            int numberAll = plantState.getCartList().get(position).getNumber();
            if (numberAll == 0) {
                numberAll = 0;
            } else {
                numberAll -= 1;
            }
            plantState.getCartList().get(position).setNumber(numberAll);
            notifyDataSetChanged();
            //为true说明选中了
            if (plantState.getCartList().get(position).isCart()) {
                eventBus.post(new CartAddFind(position));
            }
        }
    }
}
