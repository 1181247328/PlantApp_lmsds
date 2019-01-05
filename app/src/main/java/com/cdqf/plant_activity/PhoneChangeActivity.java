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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
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
 * 换绑定手机第一步
 * Created by liu on 2017/11/14.
 */

public class PhoneChangeActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PhoneChangeActivity.class.getSimpleName();

    public static PhoneChangeActivity phoneChangeActivity = null;

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlPhoneChangeReturn = null;

    //国家和地区
    private LinearLayout llPhonechangeRegion = null;

    //地区
    private TextView tvPhonechangeRegion = null;

    //手机号码
    private XEditText xetPhonechangePhone = null;

    //下一步
    private TextView tvPhonechangeNext = null;

    private String ipAddress = null;

    private int consumerId;

    private String mobile = null;

    private int type = 0;

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
        setContentView(R.layout.activity_phonechange);

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
        phoneChangeActivity = this;
    }

    private void initView() {
        rlPhoneChangeReturn = (RelativeLayout) this.findViewById(R.id.rl_phonechange_return);
        llPhonechangeRegion = (LinearLayout) this.findViewById(R.id.ll_phonechange_region);
        tvPhonechangeRegion = (TextView) this.findViewById(R.id.tv_phonechange_region);
        xetPhonechangePhone = (XEditText) this.findViewById(R.id.xet_phonechange_phone);
        tvPhonechangeNext = (TextView) this.findViewById(R.id.tv_phonechange_next);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlPhoneChangeReturn.setOnClickListener(this);
        llPhonechangeRegion.setOnClickListener(this);
        tvPhonechangeNext.setOnClickListener(this);
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
            case R.id.rl_phonechange_return:
                finish();
                break;
            //国家和地区
            case R.id.ll_phonechange_region:
                break;
            //下一步
            case R.id.tv_phonechange_next:
                mobile = xetPhonechangePhone.getText().toString();
                if (mobile.length() <= 0) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.set_phone), true, 0);
                    return;
                }
                //判断是否是手机号码
                if (!plantState.checkMobileNumber(mobile)) {
                    plantState.initToast(context, context.getResources().getString(R.string.set_phone_modile), true, 0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.send_code), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isCode(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户修改手机号解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---用户修改手机号解密成功---" + data);
                        if (TextUtils.equals(data, "发送成功")) {
                            plantState.initToast(context, data, true, 0);
                            Intent intent = new Intent(context, PhoneChangeNextActivity.class);
                            intent.putExtra("ipAddress", ipAddress);
                            intent.putExtra("consumerId", consumerId);
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("type", type);
                            startActivity(intent);
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
