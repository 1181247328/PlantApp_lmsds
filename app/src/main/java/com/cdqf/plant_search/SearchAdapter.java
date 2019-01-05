package com.cdqf.plant_search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;

/**
 * 搜索
 * Created by liu on 2018/1/3.
 */

public class SearchAdapter extends BaseAdapter {

    private Context context = null;

    private Search search = Search.getSearch();

    public SearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return search.getSearchList().size();
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_search, null);
        TextView tvSearchItemContent = (TextView) convertView.findViewById(R.id.tv_search_item_content);
        tvSearchItemContent.setText(search.getSearchList().get(position));
        return convertView;
    }
}
