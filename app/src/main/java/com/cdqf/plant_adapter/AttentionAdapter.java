package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;

import java.util.List;

/**
 * 注意事项集合
 * Created by liu on 2017/12/29.
 */

public class AttentionAdapter extends BaseAdapter {

    private Context context=null;

    private List<String> attention = null;

    public AttentionAdapter(Context context,List<String> attention) {
        this.context = context;
        this.attention = attention;
    }

    @Override
    public int getCount() {
        return attention.size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_attention,null);
        TextView tvAttentionItemContent = convertView.findViewById(R.id.tv_attention_item_content);
        tvAttentionItemContent.setText("● "+attention.get(position));
        return convertView;
    }
}
