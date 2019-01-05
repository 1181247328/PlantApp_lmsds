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
import com.cdqf.plant_find.ScienceCollectionFind;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * 科普内容
 * Created by liu on 2017/10/19.
 */

public class ScienceAdapter extends BaseAdapter{

    private String TAG = ScienceAdapter.class.getSimpleName();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Context context= null;

    public ScienceAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        if(plantState.getPlant().getList().size()<=10){
            return plantState.getPlant().getList().size();
        } else {
            return 10;
        }
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_science,null);
            plantViewHolder = new PlantViewHolder();
            plantViewHolder.rlAskItemCollection = (RelativeLayout) convertView.findViewById(R.id.rl_ask_item_collection);
            plantViewHolder.ivAskItemCollection = (ImageView) convertView.findViewById(R.id.iv_ask_item_collection);
            plantViewHolder.ivASkItemFigure = (ImageView) convertView.findViewById(R.id.iv_ask_item_figure);
            plantViewHolder.tvAskItemName = (TextView) convertView.findViewById(R.id.tv_ask_item_name);
            plantViewHolder.tvAskItemContent = (TextView) convertView.findViewById(R.id.tv_ask_item_content);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(plantState.getPlant().getList().get(position).getHttpBotanyPic(),plantViewHolder.ivASkItemFigure,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        plantViewHolder.tvAskItemName.setText(plantState.getPlant().getList().get(position).getBotanyName());
        plantViewHolder.tvAskItemContent.setText(plantState.getPlant().getList().get(position).getBrief());
        plantViewHolder.rlAskItemCollection.setOnClickListener(new OnCollectionListener(position));
        return convertView;
    }

    /**
     * 收藏
     */
    class OnCollectionListener implements View.OnClickListener{

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
