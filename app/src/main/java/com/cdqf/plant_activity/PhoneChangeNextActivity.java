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
import com.cdqf.plant_state.WIFIGpRs;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.Map;

/**
 * 换绑手机第二步
 * Created by liu on 2017/11/14.
 */

public class PhoneChangeNextActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PhoneChangeNextActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlPhoneChangeNextReturn = null;

    //检验码
    private XEditText xetPhonechangeNextPhone = null;

    //重发检验码
    private TextView tvPhonexhangenextTest = null;

    //下一步
    private TextView tvPhonechangenextNext = null;

    private String ipAddress = null;

    private int consumerId;

    private String mobile = null;

    private int type = 0;

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
        setContentView(R.layout.activity_phonechangenext);

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
        ipAddress = intent.getStringExtra("ipAddress");
        consumerId = intent.getIntExtra("consumerId",consumerId);
        mobile = intent.getStringExtra("mobile");
        type = intent.getIntExtra("type",0);
    }

    private void initView() {
        rlPhoneChangeNextReturn = this.findViewById(R.id.rl_phonechangnext_return);
        xetPhonechangeNextPhone = this.findViewById(R.id.xet_phonechangenext_phone);
        tvPhonexhangenextTest = this.findViewById(R.id.tv_phonexhangenext_test);
        tvPhonechangenextNext = this.findViewById(R.id.tv_phonechangenext_next);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlPhoneChangeNextReturn.setOnClickListener(this);
        tvPhonexhangenextTest.setOnClickListener(this);
        tvPhonechangenextNext.setOnClickListener(this);
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
            case R.id.rl_phonechangnext_return:
                finish();
                break;
            //重发检验码
            case R.id.tv_phonexhangenext_test:
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.send_code), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isCode(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户验证码解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---用户验证码解密成功---" + data);
                        if (TextUtils.equals(data, "发送成功")) {
                           plantState.initToast(context,data,true,0);
                        } else {

                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //IP地址
                if (WIFIGpRs.isWifi(context)) {
                    ipAddress = WIFIGpRs.getWiFiIp(context);
                } else {
                    ipAddress = WIFIGpRs.getGpRsIp();
                }
                params.put("ipAddress", ipAddress);
                //用户id
                consumerId = plantState.getUser().getConsumerId();
                params.put("consumerId", consumerId);
                //手机号
                params.put("mobile", mobile);
                //验证类型
                params.put("type", type);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + ipAddress + consumerId + mobile + type;
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
                break;
            //下一步
            case R.id.tv_phonechangenext_next:
                code = xetPhonechangeNextPhone.getText().toString();
                if (code.length() <= 0) {
                    plantState.initToast(context, context.getResources().getString(R.string.code), true, 0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.phone), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isColltion(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户手机更换解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---用户手机更换解密成功---" + data);
                        PhoneChangeActivity.phoneChangeActivity.finish();
                        finish();
                    }
                }));
                Map<String, Object> paramss = new HashMap<String, Object>();
                //设备ip
                paramss.put("ipAddress", ipAddress);
                //用户id
                paramss.put("consumerId", consumerId);
                //手机号
                paramss.put("mobile", mobile);
                paramss.put("code", code);
                //随机数
                int randoms = plantState.getRandom();
                String signs = randoms + "" + ipAddress + consumerId + mobile + code;
                Log.e(TAG, "---明文---" + signs);
                //加密文字
                String signEncrypts = null;
                try {
                    signEncrypts = DESUtils.encryptDES(signs, Constants.secretKey.substring(0, 8));
                    Log.e(TAG, "---加密成功---" + signEncrypts);
                } catch (Exception e) {
                    Log.e(TAG, "---加密失败---");
                    e.printStackTrace();
                }
                if (signEncrypts == null) {
                    plantState.initToast(context, "加密失败", true, 0);
                }
                //随机数
                paramss.put("random", randoms);
                paramss.put("sign", signEncrypts);
                httpRequestWrap.send(PlantAddress.USER_PHONE, paramss);
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
