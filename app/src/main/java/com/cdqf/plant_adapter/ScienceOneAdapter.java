package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_find.ScienceCollectionOneFind;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * Created by liu on 2017/11/16.
 */

public class ScienceOneAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public ScienceOneAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getScienceCollectionList().size();
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
        PlantViewHolder plantViewHolder= null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plantscience,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.rlAskItemCollection = (RelativeLayout) convertView.findViewById(R.id.rl_plantscience_item_collection);
            plantViewHolder.ivAskItemCollection = (ImageView) convertView.findViewById(R.id.iv_plantscience_item_collection);
            plantViewHolder.ivASkItemFigure = (ImageView) convertView.findViewById(R.id.iv_plantscience_item_figure);
            plantViewHolder.tvAskItemName = (TextView) convertView.findViewById(R.id.tv_plantscience_item_name);
            plantViewHolder.tvAskItemContent = (TextView) convertView.findViewById(R.id.tv_plantscience_item_content);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //图片
        imageLoader.displayImage(plantState.getScienceCollectionList().get(position).getHttpPic(),plantViewHolder.ivASkItemFigure,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        //植物名
        plantViewHolder.tvAskItemName.setText(plantState.getScienceCollectionList().get(position).getPlantName());
        //内容
        plantViewHolder.tvAskItemContent.setText(plantState.getScienceCollectionList().get(position).getBrif());
        plantViewHolder.rlAskItemCollection.setOnClickListener(new OnCollectionListener(position));
        return convertView;
    }
    /**
     * 取消收藏
     */
    class OnCollectionListener implements View.OnClickListener{

        private int position;

        public OnCollectionListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new ScienceCollectionOneFind(position));
        }
    }
}
