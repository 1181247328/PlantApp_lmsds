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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.Login;
import com.cdqf.plant_find.LoginExitFind;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.greenrobot.event.EventBus;

/**
 * 设置
 * Created by liu on 2017/11/14.
 */

public class SetActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = SetActivity.class.getSimpleName();

    public static SetActivity setActivity = null;

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlSetReturn = null;

    //头像
    private ImageView ivSetHear = null;

    //个人资料
    private LinearLayout llSetNickname= null;
    private TextView tvSetNickname = null;

    //我的收货地址
    private LinearLayout llSetGoods = null;

    //修改手机号码
    private LinearLayout llSetPhone= null;

    //修改登录密码
    private LinearLayout llSetPassword= null;

    //清除缓存
    private LinearLayout llSetCache = null;

    //退出当前账户
    private TextView tvSetExit = null;

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
        setContentView(R.layout.activity_set);

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
        setActivity = this;
        if(!eventBus.isRegistered(this)){
            eventBus.register(this);
        }
    }

    private void initView() {
        rlSetReturn = (RelativeLayout) this.findViewById(R.id.rl_set_return);
        ivSetHear = (ImageView) this.findViewById(R.id.iv_set_hear);
        llSetNickname= (LinearLayout) this.findViewById(R.id.ll_set_nickname);
        tvSetNickname = (TextView) this.findViewById(R.id.tv_set_nickname);
        llSetGoods = (LinearLayout) this.findViewById(R.id.ll_set_goods);
        llSetPhone = (LinearLayout) this.findViewById(R.id.ll_set_phone);
        llSetPassword = (LinearLayout) this.findViewById(R.id.ll_set_password);
        llSetCache = (LinearLayout) this.findViewById(R.id.ll_set_cache);
        tvSetExit = (TextView) this.findViewById(R.id.tv_set_exit);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlSetReturn.setOnClickListener(this);
        llSetNickname.setOnClickListener(this);
        llSetGoods.setOnClickListener(this);
        llSetPhone.setOnClickListener(this);
        llSetPassword.setOnClickListener(this);
        llSetCache.setOnClickListener(this);
        tvSetExit.setOnClickListener(this);
    }

    private void initBack() {
        imageLoader.displayImage(plantState.getUser().getImgAvatat(),ivSetHear, plantState.getImageLoaderOptions(R.mipmap.login_hear,R.mipmap.login_hear,R.mipmap.login_hear));
        tvSetNickname.setText(plantState.getUser().getNickName());
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_set_return:
                finish();
                break;
            //个人资料
            case R.id.ll_set_nickname:
                initIntent(PersonalDataActivity.class);
                break;
            //我的收货地址
            case R.id.ll_set_goods:
                initIntent(AddressActivity.class);
                break;
            //修改手机号码
            case R.id.ll_set_phone:
                initIntent(PhoneChangeActivity.class);
                break;
            //修改密码
            case R.id.ll_set_password:
                initIntent(PassWordOneActivity.class);
                break;
            //清除缓存
            case R.id.ll_set_cache:
                break;
            //关于我们
            case R.id.ll_set_my:
                break;
            //退出当前账户
            case R.id.tv_set_exit:
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt(plantState.getPlantString(context,R.string.user_exit),8);
                promptDilogFragment.show(getSupportFragmentManager(),"退出当前账户");
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

    public void onEventMainThread(Login l){
        imageLoader.displayImage(plantState.getUser().getImgAvatat(),ivSetHear, plantState.getImageLoaderOptions(R.mipmap.login_hear,R.mipmap.login_hear,R.mipmap.login_hear));
        tvSetNickname.setText(plantState.getUser().getNickName());
    }

    /**
     * 退出登录
     * @param l
     */
    public void onEventMainThread(LoginExitFind l){
        finish();
    }
}
