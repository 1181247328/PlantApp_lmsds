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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 服务类型
 * Created by liu on 2017/12/28.
 */

public class ServiceActivity extends BaseActivity {

    private String TAG = ServiceActivity.class.getSimpleName();

    public static ServiceActivity serviceActivity = null;

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_service_return)
    public RelativeLayout rlServiceReturn = null;

    //商品图片
    @BindView(R.id.iv_Service_icon)
    public ImageView ivServiceIcon = null;

    //商品名称
    @BindView(R.id.tv_service_title)
    public TextView tvServiceTitle = null;

    //价格
    @BindView(R.id.tv_service_price)
    public TextView tvServicePrice = null;

    //仅退款
    @BindView(R.id.rl_service_refund)
    public RelativeLayout rlServiceRefund = null;

    //退款退货
    @BindView(R.id.rl_service_goods)
    public RelativeLayout rlServiceGoods = null;

    private int position = 0;

    private String url = null;

    private String title = null;

    private double price = 0;

    private int commNum = 0;

    private double money = 0;

    //订单id
    private int orderId = 0;

    //商品id
    private int commId = 0;

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
        setContentView(R.layout.activity_service);

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
        imageLoader = plantState.getImageLoader(context);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        ButterKnife.bind(this);
        serviceActivity = this;
    }

    private void initView() {

    }

    private void initAdapter() {

    }

    private void initListener() {
    }

    private void initBack() {
        url = plantState.getOrderDetails().getCommList().get(position).getImgCommPic();
        title = plantState.getOrderDetails().getCommList().get(position).getCommName();
        price = plantState.getOrderDetails().getCommList().get(position).getCommPrice();
        commNum = plantState.getOrderDetails().getCommList().get(position).getCommNum();
        money = plantState.getOrderDetails().getCommList().get(position).getCommPrice();
        commId = plantState.getOrderDetails().getCommList().get(position).getCommId();
        orderId = plantState.getOrderDetails().getOrderId();
        imageLoader.displayImage(url, ivServiceIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        tvServiceTitle.setText(title);
        tvServicePrice.setText(price + "");
    }

    private void initIntent(Class<?> activity, int returnType) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("price", price);
        intent.putExtra("returnType", returnType);
        intent.putExtra("commNum", commNum);
        intent.putExtra("money", money);
        intent.putExtra("orderId", orderId);
        intent.putExtra("commId", commId);
        startActivity(intent);
    }

    @OnClick({R.id.rl_service_return, R.id.rl_service_refund, R.id.rl_service_goods})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_service_return:
                finish();
                break;
            //仅退款
            case R.id.rl_service_refund:
                initIntent(TypeActivity.class, 0);
                break;
            //退款退货
            case R.id.rl_service_goods:
                initIntent(TypeActivity.class, 1);
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
