package com.cdqf.plant_adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_find.ShareFind;
import com.cdqf.plant_find.StrategyFind;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * 游记适配器
 * Created by liu on 2017/10/23.
 */

public class StrategyAdapter extends BaseAdapter {

    private String TAG = StrategyAdapter.class.getSimpleName();

    private Context context = null;

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader= ImageLoader.getInstance();

    private ImageLoader imageLoaders= ImageLoader.getInstance();

    public StrategyAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
        imageLoaders = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        return plantState.getStrategyList().size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_strategy,null);
            plantViewHolder = new PlantViewHolder();

            //图片
            plantViewHolder.ivStrategyItemPicture = (ImageView) convertView.findViewById(R.id.iv_strategy_item_picture);

            //收藏ll_strategy_item_collection
            plantViewHolder.llStrategyItemCollection =(LinearLayout) convertView.findViewById(R.id.ll_strategy_item_collection);

            //收藏图片
            plantViewHolder.ivStrategyItemCollection = (ImageView) convertView.findViewById(R.id.iv_strategy_item_collection);

            //标题
            plantViewHolder.tvStrategyItemTitle = (TextView) convertView.findViewById(R.id.tv_strategy_item_title);

            //头像
            plantViewHolder.ivStrategyItemHear = (ImageView) convertView.findViewById(R.id.iv_strategy_item_hear);

            //名称
            plantViewHolder.tvStrategyItemName = (TextView) convertView.findViewById(R.id.tv_strategy_item_name);

            //分享
            plantViewHolder.llStrategyItemShare = (LinearLayout) convertView.findViewById(R.id.ll_strategy_item_share);

            //评论数量
            plantViewHolder.tvStrategyItemNumber = (TextView) convertView.findViewById(R.id.tv_strategy_item_number);

            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }

        imageLoader.displayImage(plantState.getStrategyList().get(position).getHttpPic(),plantViewHolder.ivStrategyItemPicture,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        plantViewHolder.tvStrategyItemTitle.setText(plantState.getStrategyList().get(position).getTitle());

        Log.e(TAG,"---"+plantState.getStrategyList().get(position).getHttpConsumerAvatar());
        imageLoaders.displayImage(plantState.getStrategyList().get(position).getHttpConsumerAvatar(),plantViewHolder.ivStrategyItemHear,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
        plantViewHolder.tvStrategyItemName.setText(plantState.getStrategyList().get(position).getConsumerNickName());

        plantViewHolder.tvStrategyItemNumber.setText(plantState.getStrategyList().get(position).getCommentCount()+"");

        plantViewHolder.llStrategyItemCollection.setOnClickListener(new OnCollectionListener(position));
        plantViewHolder.llStrategyItemShare.setOnClickListener(new OnShareListener(position));
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
            if(!plantState.isLogin()){
                plantState.initToast(context,plantState.getPlantString(context,R.string.is_login),true,0);
                return;
            }
            eventBus.post(new StrategyFind(position));
        }
    }

    /**
     * 分享
     */
    class OnShareListener implements View.OnClickListener{

        private int position = 0;

        public OnShareListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            eventBus.post(new ShareFind(position));
        }
    }

}
