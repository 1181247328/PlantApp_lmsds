package com.cdqf.plant_activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.cdqf.plant_fragment.AskFragment;
import com.cdqf.plant_fragment.CartFragment;
import com.cdqf.plant_fragment.IntegralFragment;
import com.cdqf.plant_fragment.MyFragment;
import com.cdqf.plant_fragment.ShopFragment;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_service.PlantService;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantPreferences;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;



/**
 * 主activity
 * Created by liu on 2017/10/19.
 */

public class MainActivity extends BaseActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private int[] modular = new int[]{
            R.drawable.plant_ask,
            R.drawable.plant_shop,
            R.drawable.lmsd_integral,
            R.drawable.lmsd_cart,
            R.drawable.plant_my
    };

    private String[] modularName  = null;

    private Fragment[] fragment = new Fragment[]{
            //资讯
            new AskFragment(),
            //商城
            new ShopFragment(),
            //积分商城
            new IntegralFragment(),
            //购物车
            new CartFragment(),
            //我的
            new MyFragment(),
    };

    @BindView(android.R.id.tabhost)
    public FragmentTabHost fragmentTabHost = null;

    private TabHost.TabSpec tabSpec = null;

    //后台返回
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //API19以下用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        //加载布局
        setContentView(R.layout.activity_main);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.plant_background));
        }

        initAgo();

        initView();

        initListener();

        initBack();

    }

    private void initAgo() {
        //上下文
        context = this;
        //注册注解
        ButterKnife.bind(this);
        //获得模块名称
        modularName = context.getResources().getStringArray(R.array.modular);
        //判断是不是登录过了
        if (PlantPreferences.getLogUserComment(this) != null) {
            plantState.setUser(PlantPreferences.getLogUserComment(this));
            plantState.setLogin(true);
        }
        //开启后台
        Intent serviceIntent = new Intent(this, PlantService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 配置底部导航栏
     */
    private void initView() {
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.fragment_layout);
        for (int i = 0; i < fragment.length; i++) {
            tabSpec = fragmentTabHost.newTabSpec(modularName[i]);
            tabSpec.setIndicator(getItem(modular[i], modularName[i]));
            fragmentTabHost.addTab(tabSpec, fragment[i].getClass(), null);
            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
        fragmentTabHost.getTabWidget().setDividerDrawable(null);
    }

    private void initListener() {
        fragmentTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                upDateTabColor(fragmentTabHost);
            }
        });
    }

    private void initBack() {
        upDateTabColor(fragmentTabHost);
        plantState.permission(this);
    }

    /**
     * 底部图片和名字
     *
     * @param drawable
     * @param name
     * @return
     */
    private View getItem(int drawable, String name) {
        View v = this.getLayoutInflater().inflate(R.layout.item_modular, null);
        ImageView icon = v.findViewById(R.id.iv_icon);
        TextView lable = v.findViewById(R.id.tv_name);
        icon.setImageResource(drawable);
        lable.setText(name);
        return v;
    }

    /**
     * @param fragmentTabHost
     */
    private void upDateTabColor(FragmentTabHost fragmentTabHost) {
        for (int i = 0; i < fragmentTabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = fragmentTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tv_name);
            if (fragmentTabHost.getCurrentTab() == i) {
                tv.setTextColor(ContextCompat.getColor(this, R.color.immersion));
            } else {
                tv.setTextColor(ContextCompat.getColor(this, R.color.login_bution_select));
            }
        }
    }

    @Override
    public void onBackPressed() {

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
