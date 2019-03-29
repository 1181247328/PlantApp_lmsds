package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cdqf.plant_find.ImageDeleteFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.gcssloop.widget.RCRelativeLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 评论适配器
 * Created by liu on 2017/12/25.
 */

public class EvaluateCommentAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private List<String> pictureHttpList = new ArrayList<String>();

    public EvaluateCommentAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    public void setPictureHttpList(List<String> pictureHttpList) {
        this.pictureHttpList = pictureHttpList;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getCount() - 1 == position) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getCount() {
        return pictureHttpList.size() + 1;
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
        ViewHolder plantViewHolder = null;
        switch (type) {
            //图片显示区
            case 0:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
                plantViewHolder = new ViewHolder(convertView);
                imageLoader.displayImage(pictureHttpList.get(position), plantViewHolder.ivEvaluateItemImage, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                plantViewHolder.rcrlEvaluateItemDelete.setOnClickListener(new OnImageDeleteListener(position));
                break;
            case 1:
                convertView = LayoutInflater.from(context).inflate(R.layout.item_comment_add, null);
                break;
        }
        return convertView;
    }

    class ViewHolder {
        /**
         * 买家评论图片
         **/
        @BindView(R.id.iv_evaluate_item_image)
        public ImageView ivEvaluateItemImage = null;

        @BindView(R.id.rcrl_evaluate_item_delete)
        public RCRelativeLayout rcrlEvaluateItemDelete = null;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    class OnImageDeleteListener implements View.OnClickListener {

        private int position;

        public OnImageDeleteListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new ImageDeleteFind(position));
        }
    }
}
