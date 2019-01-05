package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_niengridview.NineGridTestLayout;
import com.cdqf.plant_niengridview.NineGridTestModel;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.PlantViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 商品评论适配器
 * Created by liu on 2017/12/6.
 */

public class GoodsAllEvaluationAdapter extends BaseAdapter {

    private Context context= null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    public GoodsAllEvaluationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return plantState.getEvaluationList().size();
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
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goodsallevaluation,null);
            plantViewHolder = new PlantViewHolder();
            //头像
            plantViewHolder.ivGoodsevItemHear = convertView.findViewById(R.id.iv_goodsev_item_hear);
            //名字
            plantViewHolder.tvGoodsevItemName = convertView.findViewById(R.id.tv_goodsev_item_name);
            //时间
            plantViewHolder.tvGoodsevItemData = convertView.findViewById(R.id.tv_goodsev_item_data);
            //内容
            plantViewHolder.atvGoodsevItemContent = convertView.findViewById(R.id.atv_goodsev_item_content);
            //图片九宫格
            plantViewHolder.NglGoodsevItemList = convertView.findViewById(R.id.ngl_goodsev_item_list);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        imageLoader.displayImage(plantState.getEvaluationList().get(position).getHttpCommenterAvatar(), plantViewHolder.ivGoodsevItemHear, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        plantViewHolder.tvGoodsevItemName.setText(plantState.getEvaluationList().get(position).getCommenterName());
        plantViewHolder.tvGoodsevItemData.setText(plantState.getEvaluationList().get(position).getStrCommentDate());
        plantViewHolder.atvGoodsevItemContent.setText(plantState.getEvaluationList().get(position).getCommentContent());
        plantViewHolder.NglGoodsevItemList.setUrlList(plantState.getEvaluationList().get(position).getUrlList());
        plantViewHolder.NglGoodsevItemList.setOnClickImageListener(new OnClickEvaListener(position));
        return convertView;
    }

    private int getListSize(List<NineGridTestModel> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    class OnClickEvaListener implements NineGridTestLayout.OnClickImage{

        private int position = 0;

        public OnClickEvaListener(int position) {
            this.position = position;
        }

        @Override
        public void clickImage(int i, String url, List<String> urlList) {
            //Toast.makeText(context, "点击了图片" + url, Toast.LENGTH_SHORT).show();
        }
    }
}
