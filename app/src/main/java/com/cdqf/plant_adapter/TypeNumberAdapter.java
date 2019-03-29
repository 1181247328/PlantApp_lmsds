package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 数量选择适配器
 */
public class TypeNumberAdapter extends BaseAdapter {

    private Context context = null;

    private int number = 0;

    private int select = -1;

    public TypeNumberAdapter(Context context, int number) {
        this.context = context;
        this.number = number;
    }

    public void setNumber(int select) {
        this.select = select;
    }

    @Override
    public int getCount() {
        return number;
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
        ViewHoler viewHoler = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_typenumber, null);
            viewHoler = new ViewHoler(convertView);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) convertView.getTag();
        }
        viewHoler.tvDilogItemNumber.setText((position + 1) + "");
        return convertView;
    }

    class ViewHoler {
        @BindView(R.id.tv_dilog_item_number)
        public TextView tvDilogItemNumber = null;

        public ViewHoler(View v) {
            ButterKnife.bind(this, v);
        }
    }
}
