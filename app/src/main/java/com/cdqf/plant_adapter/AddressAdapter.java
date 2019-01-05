package com.cdqf.plant_adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_activity.NewAddressActivity;
import com.cdqf.plant_find.AddressModifyFind;
import com.cdqf.plant_find.AddressPromptFind;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;

import de.greenrobot.event.EventBus;

/**
 * 管理收货地址适配器
 * Created by liu on 2017/11/14.
 */

public class AddressAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    public AddressAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantState.getAddressList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_address, null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.tvAddressItemName = (TextView) convertView.findViewById(R.id.tv_address_item_name);
            plantViewHolder.tvAddressItemPhone = (TextView) convertView.findViewById(R.id.tv_address_item_phone);
            plantViewHolder.tvAddressItemAddress = (TextView) convertView.findViewById(R.id.tv_address_item_address);
            plantViewHolder.cbAddressItemCheckbox = (CheckBox) convertView.findViewById(R.id.cb_address_item_checkbox);
            plantViewHolder.llAddressItemEdit = (LinearLayout) convertView.findViewById(R.id.ll_address_item_edit);
            plantViewHolder.llAddressItemDelete = (LinearLayout) convertView.findViewById(R.id.ll_address_item_delete);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.tvAddressItemName.setText(plantState.getAddressList().get(position).getContacts());
        plantViewHolder.tvAddressItemPhone.setText(plantState.getAddressList().get(position).getContactMobile());
        plantViewHolder.tvAddressItemAddress.setText(plantState.getAddressList().get(position).getFullAddress());
        if (plantState.getAddressList().get(position).isDefault()) {
            plantViewHolder.cbAddressItemCheckbox.setChecked(true);
            plantViewHolder.cbAddressItemCheckbox.setText("默认地址");
            plantViewHolder.cbAddressItemCheckbox.setClickable(false);
            plantViewHolder.cbAddressItemCheckbox.setEnabled(false);
            plantViewHolder.cbAddressItemCheckbox.setTextColor(ContextCompat.getColor(context, R.color.strategy_item_published));
        } else {
            plantViewHolder.cbAddressItemCheckbox.setChecked(false);
            plantViewHolder.cbAddressItemCheckbox.setText("设为默认");
            plantViewHolder.cbAddressItemCheckbox.setClickable(true);
            plantViewHolder.cbAddressItemCheckbox.setEnabled(true);
            plantViewHolder.cbAddressItemCheckbox.setTextColor(ContextCompat.getColor(context, R.color.plant_address));
        }

        plantViewHolder.cbAddressItemCheckbox.setOnCheckedChangeListener(new OnCheckBosListener(position));
        plantViewHolder.llAddressItemEdit.setOnClickListener(new OnEditListener(position));
        plantViewHolder.llAddressItemDelete.setOnClickListener(new OnDeleteListener(position));
        return convertView;
    }

    class OnCheckBosListener implements CompoundButton.OnCheckedChangeListener {

        private int position;

        public OnCheckBosListener(int position) {
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isPressed()){
                return;
            }
            if (isChecked) {
                eventBus.post(new AddressModifyFind(position));
            }
        }
    }

    /**
     * 编辑
     */
    class OnEditListener implements View.OnClickListener {

        private int position;

        public OnEditListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, NewAddressActivity.class);
            intent.putExtra("type", 1);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }
    }

    /**
     * 删除
     */
    class OnDeleteListener implements View.OnClickListener{
        private int position;

        public OnDeleteListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new AddressPromptFind(position));
        }
    }
}
