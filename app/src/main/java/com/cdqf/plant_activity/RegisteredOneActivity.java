package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cdqf.plant_state.WIFIGpRs;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 注册第一步
 * Created by liu on 2017/11/14.
 */

public class RegisteredOneActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = RegisteredOneActivity.class.getSimpleName();

    public static RegisteredOneActivity registeredOneActivity = null;

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlRegisteredoneReturn = null;

    //登录
    private TextView tvRegisteredoneLogin = null;

    //登录密码
    private XEditText xetRegisteredonePhone = null;

    //获取验证码
    private TextView tvRegisteredoneObtain = null;

    //验证码
    private XEditText xetRegisteredoneCode = null;

    //下一步
    private TextView tvRegisteredoneNext = null;

    //是否验证验证码
    private boolean isCode = false;

    private String mobile = null;

    private String obtainIp = null;

    private int consumerId = 0;

    private int type = 1;

    private String code = null;

    private int timer = 60;

    private Timer timers = null;

    private boolean isSend = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            timer--;
            if (timer == 0) {
                timer = 60;
                isSend = false;
                tvRegisteredoneObtain.setText("获取验证码");
                timers.cancel();
            } else {
                tvRegisteredoneObtain.setText("重新发送(" + timer + ")s");
            }
        }
    };

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
        setContentView(R.layout.activity_registeredone);

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
        registeredOneActivity = this;
    }

    private void initView() {
        rlRegisteredoneReturn = this.findViewById(R.id.rl_registeredone_return);
        tvRegisteredoneLogin = this.findViewById(R.id.tv_registeredone_login);
        xetRegisteredonePhone = this.findViewById(R.id.xet_registeredone_phone);
        tvRegisteredoneObtain = this.findViewById(R.id.tv_registeredone_obtain);
        xetRegisteredoneCode = this.findViewById(R.id.xet_registeredone_code);
        tvRegisteredoneNext = this.findViewById(R.id.tv_registeredone_next);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlRegisteredoneReturn.setOnClickListener(this);
        tvRegisteredoneObtain.setOnClickListener(this);
        tvRegisteredoneNext.setOnClickListener(this);
        tvRegisteredoneLogin.setOnClickListener(this);
    }

    private void initBack() {

    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    /**
     * 获取验证码
     *
     * @param obtainIp
     * @param consumerId
     * @param mobile
     * @param type
     */
    private void sendCode(String obtainIp, int consumerId, String mobile, int type) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.send_code), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isColltion(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户注册解密失败---" + data);
                    //刷新失败
                    return;
                }
                Log.e(TAG, "---用户注册解密成功---" + data);
                if (TextUtils.equals(data, "发送成功")) {
                    plantState.initToast(context, data, true, 0);
                    isCode = true;
                    isSend = true;
                    tvRegisteredoneObtain.setText("重新发送(" + timer + ")s");
                    timers = new Timer();
                    timers.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0x001);
                        }
                    }, 1, 1000);
                } else {
                    plantState.initToast(context, data, true, 0);
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ipAddress", obtainIp);
        params.put("consumerId", consumerId);
        params.put("mobile", mobile);
        params.put("type", type);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + obtainIp + consumerId + mobile + type;
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
        httpRequestWrap.send(PlantAddress.USER_REGISTERED, params);
    }

    /**
     * 验证验证码
     *
     * @param obtainIp
     * @param consumerId
     * @param mobile
     * @param type
     * @param code
     */
    private void sendCode(final String obtainIp, int consumerId, final String mobile, int type, final String code) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.send_code_one), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isColltion(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户注册验证验证码解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---用户注册验证验证码解密成功---" + data);
                if (TextUtils.equals(data, "验证码正确")) {
                    plantState.initToast(context, data, true, 0);
                    Intent intent = new Intent(context, RegisteredTwoActivity.class);
                    intent.putExtra("userName", mobile);
                    intent.putExtra("mobile", mobile);
                    intent.putExtra("ipAddress", obtainIp);
                    intent.putExtra("code", code);
                    startActivity(intent);
                    finish();
                } else if (TextUtils.equals(data, "该手机号已被使用")) {
                    plantState.initToast(context, data, true, 0);
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ipAddress", obtainIp);
        params.put("consumerId", consumerId);
        params.put("mobile", mobile);
        params.put("type", type);
        params.put("code", code);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + obtainIp + consumerId + mobile + type + code;
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
        httpRequestWrap.send(PlantAddress.USER_CODE, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_registeredone_return:
                finish();
                break;
            //登录
            case R.id.tv_registeredone_login:
                initIntent(LoginActivity.class);
                finish();
                break;
            //获取验证码
            case R.id.tv_registeredone_obtain:
                mobile = xetRegisteredonePhone.getText().toString();
                if (isSend) {
                    plantState.initToast(context, "请不要重复发送", true, 0);
                    return;
                }
                if (mobile.length() <= 0) {
                    plantState.initToast(context, context.getResources().getString(R.string.set_phone), true, 0);
                    return;
                }
                //判断是否是手机号码
                if (!PlantState.checkMobileNumber(mobile)) {
                    plantState.initToast(context, context.getResources().getString(R.string.set_phone_modile), true, 0);
                    return;
                }
                //判断手机是否有网
                if (!WIFIGpRs.isNetworkConnected(context)) {
                    plantState.initToast(context, context.getResources().getString(R.string.is_wifi_gprs), true, 0);
                }
                //判断是GPRS还是wifi
                if (WIFIGpRs.isWifi(context)) {
                    obtainIp = WIFIGpRs.getWiFiIp(context);
                } else {
                    obtainIp = WIFIGpRs.getGpRsIp();
                }
                sendCode(obtainIp, consumerId, mobile, type);
                break;
            //下一步
            case R.id.tv_registeredone_next:
                //判断是否验证了手机
                if (!isCode) {
                    plantState.initToast(context, context.getResources().getString(R.string.is_code), true, 0);
                    return;
                }
                code = xetRegisteredoneCode.getText().toString();
                if (code.length() <= 0) {
                    plantState.initToast(context, context.getResources().getString(R.string.code), true, 0);
                    return;
                }
                sendCode(obtainIp, consumerId, mobile, type, code);
                //initIntent(RegisteredTwoActivity.class);
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
        if (timers != null) {
            timers.cancel();
        }
    }
}
