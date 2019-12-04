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

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_class.User;
import com.cdqf.plant_find.IntegralNumberFind;
import com.cdqf.plant_find.Login;
import com.cdqf.plant_find.LoginWXFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_lmsd.wxapi.QQLogin;
import com.cdqf.plant_lmsd.wxapi.WXFind;
import com.cdqf.plant_lmsd.wxapi.WxLogin;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantPreferences;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 登录页
 * Created by liu on 2017/11/15.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = LoginActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlLoginReturn = null;

    //注册
    private TextView tvLoginRegistered = null;

    //手机号
    private XEditText xetLoginPhone = null;

    //登录密码
    private XEditText xetLogingPassword = null;

    //登录
    private TextView tvLogin = null;

    //QQ
    private ImageView ivLoginQq = null;

    //微信
    private ImageView ivLoginWetch = null;

    //账号
    private String userName = null;

    //密码
    private String pwd = null;

    private Gson gson = new Gson();

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
        setContentView(R.layout.activity_login);

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
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        rlLoginReturn = this.findViewById(R.id.rl_login_return);
        tvLoginRegistered = this.findViewById(R.id.tv_login_registered);
        xetLoginPhone = this.findViewById(R.id.xet_login_phone);
        xetLogingPassword = this.findViewById(R.id.xet_loging_password);
        tvLogin = this.findViewById(R.id.tv_login);
        ivLoginQq = this.findViewById(R.id.iv_login_qq);
        ivLoginWetch = this.findViewById(R.id.iv_login_wetch);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlLoginReturn.setOnClickListener(this);
        tvLoginRegistered.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        ivLoginQq.setOnClickListener(this);
        ivLoginWetch.setOnClickListener(this);
    }

    private void initBack() {
        //用户名
//        xetLoginPhone.setText(plantState.getUser().getUserName());
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_login_return:
                finish();
                break;
            //注册
            case R.id.tv_login_registered:
                initIntent(RegisteredOneActivity.class);
                finish();
                break;
            //登录
            case R.id.tv_login:
                userName = xetLoginPhone.getText().toString();
                if (userName.length() <= 0) {
                    plantState.initToast(context, context.getResources().getString(R.string.userName), true, 0);
                    return;
                }
                pwd = xetLogingPassword.getText().toString();
                if (pwd.length() <= 0) {
                    plantState.initToast(context, context.getResources().getString(R.string.pwd), true, 0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.login), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResultLog(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户登录解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---用户登录解密成功---" + data);
                        User user = gson.fromJson(data, User.class);
                        plantState.setUser(user);
                        plantState.setLogin(true);
                        PlantPreferences.setLogUserComment(context, user);
                        eventBus.post(new Login());
                        eventBus.post(new IntegralNumberFind());
                        plantState.initToast(context, context.getResources().getString(R.string.login_complete), true, 0);
                        finish();
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("userName", userName);
                params.put("pwd", pwd);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + userName + pwd;
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
                httpRequestWrap.send(PlantAddress.USER_LOGIN, params);
                break;
            //QQ登录
            case R.id.iv_login_qq:
                QQLogin.qqLogin(context, LoginActivity.this);
                break;
            //微信登录
            case R.id.iv_login_wetch:
                Log.e(TAG, "---微信登录---");
                WxLogin.loginWx(context);
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

    /**
     * 微信登录
     *
     * @param w
     */
    public void onEventMainThread(WXFind w) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.login), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户微信登录解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---用户登录解密成功---" + data);
                User user = new User();
                user = gson.fromJson(data, User.class);
                plantState.setUser(user);
                plantState.setLogin(true);
                PlantPreferences.setLogUserComment(context, user);
                eventBus.post(new LoginWXFind());
                eventBus.post(new IntegralNumberFind());
                plantState.initToast(context, context.getResources().getString(R.string.login_complete), true, 0);
                finish();
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("openid", w.openid);
        params.put("nickname", w.nickname);
        params.put("headimgurl", w.headimgurl);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + w.nickname + w.openid;
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
        httpRequestWrap.send(PlantAddress.WX_LOGIN, params);
    }
}
