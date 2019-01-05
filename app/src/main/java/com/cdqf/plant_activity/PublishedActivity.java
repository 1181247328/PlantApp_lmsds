package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_adapter.CollectionAdapter;
import com.cdqf.plant_find.NewAddressFind;
import com.cdqf.plant_fragment.DraftFragment;
import com.cdqf.plant_fragment.HasbeenFragment;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhengsr.viewpagerlib.indicator.TabIndicator;

import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 我的游记
 * Created by liu on 2017/11/16.
 */

public class PublishedActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PublishedActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlPublisheReturn = null;

    //指示器
    private TabIndicator tiPublisheCicatior = null;

    private ViewPager vpPublisheScreen = null;

    private CollectionAdapter collectionAdapter = null;

    private Fragment[] collectionList = new Fragment[]{
            new HasbeenFragment(),
            new DraftFragment()
    };

    private List<String> collectionName = Arrays.asList("已发表", "草稿");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //API19以下用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //加载布局
        setContentView(R.layout.activity_published);

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
        context = this;
        httpRequestWrap = new HttpRequestWrap(context);
        if(!eventBus.isRegistered(this)){
            eventBus.register(this);
        }
    }

    private void initView() {
        rlPublisheReturn = (RelativeLayout) this.findViewById(R.id.rl_published_return);
        tiPublisheCicatior = (TabIndicator) this.findViewById(R.id.ti_published_dicatior);
        vpPublisheScreen = (ViewPager) this.findViewById(R.id.vp_published_screen);
    }

    private void initAdapter() {
        collectionAdapter = new CollectionAdapter(getSupportFragmentManager(), collectionList);
        vpPublisheScreen.setAdapter(collectionAdapter);
    }

    private void initListener() {
        rlPublisheReturn.setOnClickListener(this);
        tiPublisheCicatior.setTabData(vpPublisheScreen, collectionName, new TabIndicator.TabClickListener() {
            @Override
            public void onClick(int i) {
                vpPublisheScreen.setCurrentItem(i);
            }
        });
    }

    private void initBack() {

    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_published_return:
                finish();
                break;
            //添加新地址
            case R.id.tv_address_newaddress:
                initIntent(NewAddressActivity.class);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        eventBus.unregister(this);
    }
    public void onEventMainThread(NewAddressFind n){

    }

}
