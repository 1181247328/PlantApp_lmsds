package com.cdqf.plant_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 评论适配器
 * Created by liu on 2017/10/25.
 */

public class CommentsAdapter extends BaseAdapter {

    private String TAG = CommentsAdapter.class.getSimpleName();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader= ImageLoader.getInstance();

    private Context context = null;

    public CommentsAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getGoodsFist().getCommentList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comments, null);
            plantViewHolder = new PlantViewHolder();
            //头像
            plantViewHolder.ivCommentsHear = convertView.findViewById(R.id.iv_comments_hear);
            //名称
            plantViewHolder.tvCommentsName = convertView.findViewById(R.id.tv_comments_item_name);
            //评论
            plantViewHolder.rlCommentsItemIn = convertView.findViewById(R.id.rl_comments_item_in);
            //评论内容
            plantViewHolder.tvCommentsItemContext = convertView.findViewById(R.id.tv_comments_item_context);
            //评论时间
            plantViewHolder.tvCommentsItemTmer = convertView.findViewById(R.id.tv_comments_item_time);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        //头像
        imageLoader.displayImage(plantState.getGoodsFist().getCommentList().get(position).getHttpCommenterAvatar(),plantViewHolder.ivCommentsHear,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        //名称
        plantViewHolder.tvCommentsName.setText(plantState.getGoodsFist().getCommentList().get(position).getCommenterName());
        //评论
        plantViewHolder.tvCommentsItemContext.setText(plantState.getGoodsFist().getCommentList().get(position).getCommentContent());
        //评论时间
        plantViewHolder.tvCommentsItemTmer.setText(plantState.getGoodsFist().getCommentList().get(position).getCommentDate());
        plantViewHolder.rlCommentsItemIn.setOnClickListener(new OnCommentsInListener(position));
        return convertView;
    }

    class OnCommentsInListener implements View.OnClickListener {

        private int position;

        public OnCommentsInListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Log.e(TAG, "---OnCommentsInListener---" + position);
        }
    }
}
