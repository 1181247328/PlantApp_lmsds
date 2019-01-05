package com.cdqf.plant_adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_class.RoadDaetails;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 规划路线适配器
 * Created by liu on 2017/10/24.
 */

public class PlannignDetailsAdapter extends RecyclerView.Adapter<PlannignDetailsAdapter.ViewHolder>{

    private String TAG = PlannignDetailsAdapter.class.getSimpleName();

    private Context context= null;

    private PlantState plantState = PlantState.getPlantState();

    private RoadDaetails roadDaetails = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public PlannignDetailsAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
        roadDaetails = new RoadDaetails();
    }

    public PlannignDetailsAdapter(Context context, RoadDaetails roadDaetails) {
        this.context = context;
        this.roadDaetails = roadDaetails;
        imageLoader = plantState.getImageLoader(context);
    }

    public void setRoadDaetails(RoadDaetails roadDaetails){
        this.roadDaetails = roadDaetails;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_planningdetails,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        imageLoader.displayImage(roadDaetails.getList().get(position).getHttpPic(),holder.imageView,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        holder.textView.setText(roadDaetails.getList().get(position).getScenicSpotName());
    }

    @Override
    public int getItemCount() {
        return roadDaetails.getList().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.iv_planningdetails_item_pituce);
            textView = view.findViewById(R.id.tv_planningdetails_item_names);
        }
    }
}
