package com.cdqf.plant_adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_lmsd.wxapi.ShareQQFind;
import com.cdqf.plant_lmsd.wxapi.ShareWxFind;
import com.cdqf.plant_state.PlantViewHolder;

import de.greenrobot.event.EventBus;

/**
 * 分享适配器
 * Created by liu on 2017/11/22.
 */

public class ShareDilogAdapter extends BaseAdapter {

    private String TAG = ShareDilogAdapter.class.getSimpleName();

    private EventBus eventBus = EventBus.getDefault();

    private Context context = null;

    //标题
    private String shareTitle = null;

    //摘要
    private String shareSummary = null;

    //图片
    private String shareURL = null;

    //图片
    private int[] shareImage = new int[]{
            R.mipmap.login_qq,
            R.mipmap.login_wecht,
    };

    //名称
    private String[] shareName = new String[]{
            "QQ好友", "微信好友"
    };

    public ShareDilogAdapter(Context context, String shareTitle, String shareSummary, String shareURL) {
        this.context = context;
        this.shareTitle = shareTitle;
        this.shareSummary = shareSummary;
        this.shareURL = shareURL;
    }

    @Override
    public int getCount() {
        return shareName.length;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_dilog_share, null);
            plantViewHolder = new PlantViewHolder();
            //布局
            plantViewHolder.llDilogItemLayout = convertView.findViewById(R.id.ll_dilog_item_layout);
            //图片
            plantViewHolder.ivDilogItemImage = convertView.findViewById(R.id.iv_dilog_item_image);
            //分享名
            plantViewHolder.tvDilogItemName = convertView.findViewById(R.id.tv_dilog_item_name);
            convertView.setTag(plantViewHolder);
        } else {
            plantViewHolder = (PlantViewHolder) convertView.getTag();
        }
        plantViewHolder.ivDilogItemImage.setImageResource(shareImage[position]);
        plantViewHolder.tvDilogItemName.setText(shareName[position]);
        plantViewHolder.llDilogItemLayout.setOnClickListener(new OnShareListener(position));
        return convertView;
    }

    class OnShareListener implements View.OnClickListener {

        private int position;

        public OnShareListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (position) {
                case 0:
                    eventBus.post(new ShareQQFind("https://www.baidu.com/", "百度一下,你就知道", shareURL, shareSummary));
                    break;
                case 1:
                    eventBus.post(new ShareWxFind(shareTitle, "", shareURL));
//                    HttpWxPayWrap.shareWXImage(context, shareTitle, 0, shareURL);
                    break;
            }
        }
    }
}
