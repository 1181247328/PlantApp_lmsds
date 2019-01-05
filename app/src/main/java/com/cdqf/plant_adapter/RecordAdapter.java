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
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by liu on 2018/1/11.
 */

public class RecordAdapter extends BaseAdapter {

    private String TAG = SubsidiaryAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public RecordAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getRecordList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_record,null);
            plantViewHolder= new PlantViewHolder();
            //图片
            plantViewHolder.ivRecordItemName = (ImageView) convertView.findViewById(R.id.iv_record_item_icon);
            //名称
            plantViewHolder.tvRecordItemTimer =(TextView) convertView.findViewById(R.id.tv_record_item_title);
            //时间
            plantViewHolder.tvRecordItemMoney = (TextView) convertView.findViewById(R.id.tv_record_item_price);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(plantState.getRecordList().get(position).getStrPic(),plantViewHolder.ivRecordItemName,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        plantViewHolder.tvRecordItemTimer.setText(plantState.getRecordList().get(position).getCommName());
        plantViewHolder.tvRecordItemMoney.setText(plantState.getRecordList().get(position).getStrAddDate());
        return convertView;
    }
}
