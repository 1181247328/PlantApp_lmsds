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

import me.codeboy.android.aligntextview.AlignTextView;

/**
 * 评论适配器
 * Created by liu on 2017/12/8.
 */

public class DetailsCommAdapter extends BaseAdapter {

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public DetailsCommAdapter(Context context) {
        this.context = context;
        imageLoader = plantState.getImageLoader(context);
    }

    @Override
    public int getCount() {
        if(plantState.getEvaluationList().size()>0){
            return 1;
        } else {
            return 0;
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
        PlantViewHolder plantViewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_detailscomm, null);
            plantViewHolder = new PlantViewHolder();
            //头像
            plantViewHolder.ivGoodsevItemHear = (ImageView) convertView.findViewById(R.id.iv_goodsev_item_hear);
            //名字
            plantViewHolder.tvGoodsevItemName = (TextView) convertView.findViewById(R.id.tv_goodsev_item_name);
            //时间
            plantViewHolder.tvGoodsevItemData = (TextView) convertView.findViewById(R.id.tv_goodsev_item_data);
            //内容
            plantViewHolder.atvGoodsevItemContent = (AlignTextView) convertView.findViewById(R.id.atv_goodsev_item_content);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(plantState.getEvaluationList().get(0).getHttpCommenterAvatar(), plantViewHolder.ivGoodsevItemHear, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        plantViewHolder.tvGoodsevItemName.setText(plantState.getEvaluationList().get(0).getCommenterName());
        plantViewHolder.tvGoodsevItemData.setText(plantState.getEvaluationList().get(0).getStrCommentDate());
        plantViewHolder.atvGoodsevItemContent.setText(plantState.getEvaluationList().get(0).getCommentContent());
        return convertView;
    }
}
