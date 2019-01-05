package com.cdqf.plant_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_find.ScienceCollectionFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * 植物科普适配器
 * Created by liu on 2017/10/20.
 */

public class PlantscienceAdapter extends BaseAdapter {

    private String TAG = PlantscienceAdapter.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public PlantscienceAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getPlant().getList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plantscience, null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.rlAskItemCollection = convertView.findViewById(R.id.rl_plantscience_item_collection);
            plantViewHolder.ivAskItemCollection = convertView.findViewById(R.id.iv_plantscience_item_collection);
            plantViewHolder.ivASkItemFigure = convertView.findViewById(R.id.iv_plantscience_item_figure);
            plantViewHolder.tvAskItemName = convertView.findViewById(R.id.tv_plantscience_item_name);
            plantViewHolder.tvAskItemContent = convertView.findViewById(R.id.tv_plantscience_item_content);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        Log.e(TAG, "---" + plantState.getPlant().getList().get(position).getHttpBotanyPic());
        imageLoader.displayImage(plantState.getPlant().getList().get(position).getHttpBotanyPic(),plantViewHolder.ivASkItemFigure,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        plantViewHolder.tvAskItemName.setText(plantState.getPlant().getList().get(position).getBotanyName());
        plantViewHolder.tvAskItemContent.setText(plantState.getPlant().getList().get(position).getBrief());
        plantViewHolder.rlAskItemCollection.setOnClickListener(new OnCollectionListener(position));
        return convertView;
    }

    /**
     * 收藏
     */
    class OnCollectionListener implements View.OnClickListener {

        private int position;

        public OnCollectionListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new ScienceCollectionFind(position));
        }
    }
}
