package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_map.BaiduLBS;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.StatusBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liu on 2017/12/31.
 */

public class ForMapActivity extends BaseActivity {
    private String TAG = ForMapActivity.class.getSimpleName();

    private Context context = null;

    //返回
    @BindView(R.id.rl_formap_return)
    public RelativeLayout rlFormapReturn = null;

    @BindView(R.id.rl_formap_destination)
    public TextView rlFormapDestination = null;

    //地图
    @BindView(R.id.mv_formap)
    public MapView mvPoiMap = null;

    private BaiduMap bmPopBaidu = null;

    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.initialize(getApplicationContext());

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //API19以下用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //加载布局
        setContentView(R.layout.activity_formap);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.plant_background));
        }

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();
    }

    private void initAgo() {
        ButterKnife.bind(this);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
    }

    private void initView() {

    }

    private void initAdapter() {

    }

    private void initListener() {

    }

    private void initBack() {
        rlFormapDestination.setText("前往" + name);
//        bmPopBaidu = mvPoiMap.getMap();
//        bmPopBaidu.setTrafficEnabled(true);
        bmPopBaidu = mvPoiMap.getMap();
//        bmPopBaidu.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        bmPopBaidu.setTrafficEnabled(true);
        BaiduLBS.init(this);
    }

    @OnClick({R.id.rl_formap_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_formap_return:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "---启动---");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "---恢复---");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
        mvPoiMap.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
        mvPoiMap.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        mvPoiMap.onDestroy();
    }
}
