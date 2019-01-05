package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_find.ForFind;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.lnyp.imgdots.bean.ForPoint;
import com.lnyp.imgdots.bean.PointSimple;
import com.lnyp.imgdots.view.ImageLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by liu on 2017/12/5.
 */

public class ForAdapter extends BaseAdapter {

    private Context context = null;

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private int width;

    private int height;

    private List<ForPoint> forPointOneList = null;

    private List<ForPoint> forPointTwoList = null;

    private int[] forMap = new int[]{
            R.mipmap.for_one,
            R.mipmap.for_two
    };

    public ForAdapter(Context context, int width, int height, List<ForPoint> forPointOneList, List<ForPoint> forPointTwoList) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.forPointOneList = forPointOneList;
        this.forPointTwoList = forPointTwoList;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return forMap.length;
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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_for, null);
        plantViewHolder = new PlantViewHolder();
        plantViewHolder.ivPlantItemMap = (ImageLayout) convertView.findViewById(R.id.iv_plant_item_map);
        if (position == 0) {
            plantViewHolder.ivPlantItemMap.setForPoints(forPointOneList, 1);
        } else {
            plantViewHolder.ivPlantItemMap.setForPoints(forPointTwoList, 1);
        }
        int height = (int) (width * 1.6f);
        plantViewHolder.ivPlantItemMap.setImgBg(width, height, forMap[position]);

        plantViewHolder.ivPlantItemMap.setOnForMapListenerF(new OnPlantMapListener(position));
        return convertView;
    }

    class OnPlantMapListener implements ImageLayout.OnForMapListener {

        private int scenicSpotId;

        private int position;

        public OnPlantMapListener(int position) {
            this.position = position;
        }

        @Override
        public void onMap(View view, ArrayList<PointSimple> points, int position) {

        }

        @Override
        public void onMap(View view, List<ForPoint> points, int position) {
            if (this.position == 0) {
                scenicSpotId = forPointOneList.get(position).getScenicSpotId();
            } else {
                scenicSpotId = forPointTwoList.get(position).getScenicSpotId();
            }
            eventBus.post(new ForFind(position,scenicSpotId));
//            Intent intent = new Intent(context, ForMapActivity.class);
//            intent.putExtra("name",points.get(position).getSpotName());
//            context.startActivity(intent);
        }
    }
}
