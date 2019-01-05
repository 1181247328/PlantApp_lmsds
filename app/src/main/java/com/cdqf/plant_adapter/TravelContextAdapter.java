package com.cdqf.plant_adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_activity.AddTextActivity;
import com.cdqf.plant_activity.EditTextActivity;
import com.cdqf.plant_find.TravelFind;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;


/**
 * 游记内容适配器
 * Created by liu on 2017/10/27.
 */

public class TravelContextAdapter extends BaseAdapter {

    private String TAG = TravelContextAdapter.class.getSimpleName();

    private Context context = null;

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    public TravelContextAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(getCount()-1==position){
            return 2;
        }
        if(plantState.getTravelList().size() == 0){
            return 2;
        } else if(plantState.getTravelOneList().get(position).isText()){
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getCount() {
        return plantState.getTravelOneList().size()+1;
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
        int type = getItemViewType(position);
        PlantViewHolder plantViewHolder = null;
        switch (type) {
            case 0:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_travel_text, null);
                    plantViewHolder = new PlantViewHolder();
                    plantViewHolder.rlTextItemText = (RelativeLayout) convertView.findViewById(R.id.rl_text_item_text);
                    plantViewHolder.rlTextItemPicture = (RelativeLayout) convertView.findViewById(R.id.rl_text_item_picture);
                    plantViewHolder.tvTextItemContext = (TextView) convertView.findViewById(R.id.tv_text_item_context);
                    plantViewHolder.tvTextItemedit = (TextView) convertView.findViewById(R.id.tv_text_item_edit);
                    convertView.setTag(plantViewHolder);
                } else {
                    plantViewHolder = (PlantViewHolder) convertView.getTag();
                }
                Log.e(TAG,"---"+plantState.getTravelOneList().get(position).getText());
                plantViewHolder.tvTextItemContext.setText(plantState.getTravelOneList().get(position).getText());
                plantViewHolder.rlTextItemText.setOnClickListener(new OnPictureListener(position));
                plantViewHolder.rlTextItemPicture.setOnClickListener(new OnContextLisener(position));
                plantViewHolder.tvTextItemedit.setOnClickListener(new OnEditListener(position));
                break;
            case 1:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_travel_picture, null);
                    plantViewHolder = new PlantViewHolder();
                    plantViewHolder.rlPictureItemText = (RelativeLayout) convertView.findViewById(R.id.rl_picture_item_text);
                    plantViewHolder.rlPictureItemPicture = (RelativeLayout) convertView.findViewById(R.id.rl_picture_item_picture);
                    plantViewHolder.ivPictrueItemContext = (ImageView) convertView.findViewById(R.id.iv_pictrue_item_context);
                    convertView.setTag(plantViewHolder);
                } else {
                    plantViewHolder = (PlantViewHolder) convertView.getTag();
                }
                Log.e(TAG,"---"+plantState.getTravelOneList().get(position).getPictureList().get(0));
                if(!plantState.isUrl(plantState.getTravelOneList().get(position).getPictureList().get(0))){
                    imageLoader.displayImage("file://"+plantState.getTravelOneList().get(position).getPictureList().get(0),plantViewHolder.ivPictrueItemContext,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
                } else {
                    imageLoader.displayImage(plantState.getTravelOneList().get(position).getPictureList().get(0),plantViewHolder.ivPictrueItemContext,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
                }
                plantViewHolder.rlPictureItemText.setOnClickListener(new OnPictureListener(position));
                plantViewHolder.rlPictureItemPicture.setOnClickListener(new OnContextLisener(position));
                break;
            case 2:
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_travel_last, null);
                    plantViewHolder = new PlantViewHolder();
                    plantViewHolder.rlLastItemText = (RelativeLayout) convertView.findViewById(R.id.rl_last_item_text);
                    plantViewHolder.rlLastItemPicture = (RelativeLayout) convertView.findViewById(R.id.rl_last_item_picture);
                    convertView.setTag(plantViewHolder);
                } else {
                    plantViewHolder = (PlantViewHolder) convertView.getTag();
                }
                plantViewHolder.rlLastItemText.setOnClickListener(new OnPictureListener(position));
                plantViewHolder.rlLastItemPicture.setOnClickListener(new OnContextLisener(position));
                break;
        }
        return convertView;
    }

    /**
     * 图片
     */
    class OnPictureListener implements View.OnClickListener {

        public int position;

        public OnPictureListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new TravelFind(position));
        }
    }

    /**
     * 内容
     */
    class OnContextLisener implements View.OnClickListener{

        public int position;

        public OnContextLisener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, AddTextActivity.class);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }

    /**
     * 编辑文字
     */
    class OnEditListener implements View.OnClickListener{
        public int position;

        public OnEditListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, EditTextActivity.class);
            intent.putExtra("type",1);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }
}
