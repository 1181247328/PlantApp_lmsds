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

import com.cdqf.plant_adapter.MyOrderAdapter;
import com.cdqf.plant_fragment.AllOrderFragment;
import com.cdqf.plant_fragment.EvaluateFragment;
import com.cdqf.plant_fragment.ForGoodsFragment;
import com.cdqf.plant_fragment.ForPaymentFragment;
import com.cdqf.plant_fragment.SendGoodsFragment;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_lmsd.wxapi.HttpWxPayWrap;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhengsr.viewpagerlib.indicator.TabIndicator;

import java.util.Arrays;
import java.util.List;

/**
 * 我的订单
 * Created by liu on 2017/11/20.
 */

public class MyOrderActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = MyOrderActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlMyorderReturn = null;

    //消息
    private RelativeLayout rlMyorderMessage = null;

    private TabIndicator tiMyorderCicatior = null;

    private ViewPager vpMyorderScreen = null;

    private MyOrderAdapter myOrderAdapter = null;

    private Fragment[] myOrderList = new Fragment[]{
            new AllOrderFragment(),
            new ForPaymentFragment(),
            new SendGoodsFragment(),
            new ForGoodsFragment(),
            new EvaluateFragment()
    };

    private int position = 0;

    private List<String> myOrderName = Arrays.asList("全部", "待付款", "待发货", "待收货", "待评价");

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
        setContentView(R.layout.activity_myorder);

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
        HttpWxPayWrap.isWxApp(context, "wx5048e518874eed43");
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
    }

    private void initView() {
        rlMyorderReturn = this.findViewById(R.id.rl_myorder_return);
        rlMyorderMessage = this.findViewById(R.id.rl_myorder_message);
        tiMyorderCicatior = this.findViewById(R.id.ti_myorder_dicatior);
        vpMyorderScreen = this.findViewById(R.id.vp_myorder_screen);
    }

    private void initAdapter() {
        myOrderAdapter = new MyOrderAdapter(getSupportFragmentManager(), myOrderList);
        vpMyorderScreen.setAdapter(myOrderAdapter);
    }

    private void initListener() {
        rlMyorderReturn.setOnClickListener(this);
        rlMyorderMessage.setOnClickListener(this);
        tiMyorderCicatior.setTabData(vpMyorderScreen, myOrderName, new TabIndicator.TabClickListener() {
            @Override
            public void onClick(int i) {
                vpMyorderScreen.setCurrentItem(i);
            }
        });
    }

    private void initBack() {
        vpMyorderScreen.setCurrentItem(position);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_myorder_return:
                finish();
                break;
            //消息
            case R.id.rl_myorder_message:
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
    }
}
