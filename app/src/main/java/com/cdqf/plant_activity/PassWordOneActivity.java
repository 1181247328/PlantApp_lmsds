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

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

/**
 * 修改密码第一步
 * Created by liu on 2017/11/14.
 */

public class PassWordOneActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PassWordOneActivity.class.getSimpleName();

    public static PassWordOneActivity passWordOneActivity = null;

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlPasswordoneReturn = null;

    //登录密码
    private XEditText xetPasswordonePhone = null;

    //下一步
    private TextView tvPasswordoneNext = null;

    private String oldPwd = null;

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
        setContentView(R.layout.activity_passwordone);

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
        passWordOneActivity =this;
    }

    private void initView() {
        rlPasswordoneReturn = (RelativeLayout) this.findViewById(R.id.rl_passwordone_return);
        xetPasswordonePhone = (XEditText) this.findViewById(R.id.xet_passwordone_phone);
        tvPasswordoneNext = (TextView) this.findViewById(R.id.tv_passwordone_next);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlPasswordoneReturn.setOnClickListener(this);
        tvPasswordoneNext.setOnClickListener(this);
    }

    private void initBack() {

    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity,String password) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("oldPwd",password);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_passwordone_return:
                finish();
                break;
            //下一步
            case R.id.tv_passwordone_next:
                oldPwd = xetPasswordonePhone.getText().toString();
                if(oldPwd.length()<=0){
                    plantState.initToast(context,plantState.getPlantString(context,R.string.password),true,0);
                    return;
                }
                initIntent(PassWordTwoActivity.class,oldPwd);
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
