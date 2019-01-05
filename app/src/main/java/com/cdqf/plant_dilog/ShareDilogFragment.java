package com.cdqf.plant_dilog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_adapter.ShareDilogAdapter;

/**
 * 分享
 * Created by liu on 2017/11/22.
 */

public class ShareDilogFragment extends DialogFragment {

    private String TAG = ShareDilogFragment.class.getSimpleName();

    private View view = null;

    private GridView gvShareDilogList = null;

    //标题
    private String shareTitle = null;

    //摘要
    private String shareSummary = null;

    //图片
    private String shareURL = null;

    private ShareDilogAdapter shareDilogAdapter = null;

    private void shareContext(String shareTitle, String shareSummary, String shareURL) {
        Log.e(TAG, "---标题---" + shareTitle + "---摘要---" + shareSummary + "---图片---" + shareURL);
        this.shareTitle = shareTitle;
        this.shareSummary = shareSummary;
        this.shareURL = shareURL;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_share, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //初始化前
        initAgo();

        //初始化控件
        initView();

        //适配器
        initAdapter();

        //注册监听器
        initListener();

        //初始化后
        initBack();

        return view;
    }

    /**
     * 初始化前
     */
    private void initAgo() {

    }

    /**
     * 初始化控件
     */
    private void initView() {
        gvShareDilogList = (GridView) view.findViewById(R.id.gv_share_dilog_list);
    }

    private void initAdapter() {
        shareDilogAdapter = new ShareDilogAdapter(getContext());
        gvShareDilogList.setAdapter(shareDilogAdapter);
    }

    /**
     * 注册监听器
     */
    private void initListener() {
        gvShareDilogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //QQ好友
                    case 0:
                        break;
                    //微信好友
                    case 1:
                        break;
                }
            }
        });
    }

    /**
     * 初始化后
     */
    private void initBack() {

    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
