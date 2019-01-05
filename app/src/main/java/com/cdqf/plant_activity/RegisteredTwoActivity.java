package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册第二步
 * Created by liu on 2017/11/14.
 */

public class RegisteredTwoActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = RegisteredTwoActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlRegisteredTwoReturn = null;

    //登录
    private TextView tvRegisteredtwoLogin = null;

    //密码
    private XEditText xetRegisteredtwoPassword = null;

    //确认密码
    private XEditText xetRegisteredTwoCode = null;

    //注册
    private TextView tvRegisteredtwoComplete = null;

    //用户名
    private String userName = null;

    //密码
    private String pwd = null;

    //确认密码
    private String confirmationPwd = null;

    //手机
    private String mobile = null;

    //ip地址
    private String ipAddress = null;

    //验证码
    private String code = null;

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
        setContentView(R.layout.activity_registeredtwo);

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
        Intent intent = getIntent();
        //用户名
        userName = intent.getStringExtra("userName");
        //手机
        mobile = intent.getStringExtra("mobile");
        //ip地址
        ipAddress = intent.getStringExtra("ipAddress");
        //验证码
        code = intent.getStringExtra("code");
        Log.e(TAG, "---用户名---" + userName + "---手机---" + mobile + "---ip地址---" + ipAddress + "---验证码---" + code);
    }

    private void initView() {
        rlRegisteredTwoReturn = this.findViewById(R.id.rl_registeredtwo_return);
        tvRegisteredtwoLogin = this.findViewById(R.id.tv_registeredonetwo_login);
        xetRegisteredtwoPassword = this.findViewById(R.id.xet_registeredtwo_password);
        xetRegisteredTwoCode = this.findViewById(R.id.xet_registeredtwo_code);
        tvRegisteredtwoComplete = this.findViewById(R.id.tv_registeredtwo_complete);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlRegisteredTwoReturn.setOnClickListener(this);
        tvRegisteredtwoComplete.setOnClickListener(this);
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
            case R.id.rl_registeredtwo_return:
                finish();
                break;
            //登录
            case R.id.tv_registeredonetwo_login:
                initIntent(LoginActivity.class);
                break;
            //注册
            case R.id.tv_registeredtwo_complete:
                pwd = xetRegisteredtwoPassword.getText().toString();
                //密码不能为空
                if (pwd.length() <= 0) {
                    plantState.initToast(context, context.getResources().getString(R.string.password_one), true, 0);
                    return;
                }
                confirmationPwd = xetRegisteredTwoCode.getText().toString();
                //请确认密码
                if (confirmationPwd.length() <= 0) {
                    plantState.initToast(context, context.getResources().getString(R.string.password_two), true, 0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.registered), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isColltion(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户注册解密失败---" + data);
                            return;
                        }
                        if (TextUtils.equals(data, "注册成功")) {
                            initIntent(LoginActivity.class);
                        }
                        Log.e(TAG, "---用户注册解密成功---" + data);
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //用户名
                params.put("userName", userName);
                //密码
                params.put("pwd", pwd);
                //请确认密码
                params.put("confirmationPwd", confirmationPwd);
                //手机
                params.put("mobile", mobile);
                //ip地址
                params.put("ipAddress", ipAddress);
                //验证码
                params.put("code", code);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + userName + pwd + confirmationPwd + mobile + ipAddress + code;
                Log.e(TAG, "---明文---" + sign);
                //加密文字
                String signEncrypt = null;
                try {
                    signEncrypt = DESUtils.encryptDES(sign, Constants.secretKey.substring(0, 8));
                    Log.e(TAG, "---加密成功---" + signEncrypt);
                } catch (Exception e) {
                    Log.e(TAG, "---加密失败---");
                    e.printStackTrace();
                }
                if (signEncrypt == null) {
                    plantState.initToast(context, "加密失败", true, 0);
                }
                //随机数
                params.put("random", random);
                params.put("sign", signEncrypt);
                httpRequestWrap.send(PlantAddress.USER_REGISTERED_TWO, params);
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