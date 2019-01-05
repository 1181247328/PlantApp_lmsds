package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_class.Carletter;
import com.cdqf.plant_dilog.CarletterDilogFragment;
import com.cdqf.plant_dilog.PayDilogFragment;
import com.cdqf.plant_find.CarletterFind;
import com.cdqf.plant_find.PayFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_pay.HttpZFBPayWrap;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.cdqf.plant_lmsd.R.id.tv_carletter_entrancetime;

/**
 * 车辆信息
 * Created by liu on 2017/11/15.
 */

public class CarletterActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = CarletterActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlCarletterReturn = null;

    //车牌号码
    private TextView tvCarletterNumber = null;

    //入场时间
    private TextView tvCarletterEntrancetime = null;

    //出场时间
    private TextView tvCarletterLeavetime = null;

    //确定出场
    private TextView tvCarletterAppearance = null;

    //去支付
    private TextView tvCarletterPay = null;

    //车辆号码
    private String platNumber = "";

    //判断是否出场
    private boolean isAppearance = false;

    private Carletter carletter = new Carletter();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    tvCarletterLeavetime.setVisibility(View.GONE);
                    tvCarletterAppearance.setVisibility(View.VISIBLE);
                    break;
                case 0x002:
                    tvCarletterLeavetime.setVisibility(View.VISIBLE);
                    tvCarletterAppearance.setVisibility(View.GONE);
                    break;
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
        setContentView(R.layout.activity_carletter);

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
        platNumber = intent.getStringExtra("platNumber");
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        rlCarletterReturn = this.findViewById(R.id.rl_carletter_return);
        tvCarletterNumber = this.findViewById(R.id.tv_carletter_number);
        tvCarletterEntrancetime = this.findViewById(tv_carletter_entrancetime);
        tvCarletterLeavetime = this.findViewById(R.id.tv_carletter_leavetime);
        tvCarletterAppearance = this.findViewById(R.id.tv_carletter_appearance);
        tvCarletterPay = this.findViewById(R.id.tv_carletter_pay);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlCarletterReturn.setOnClickListener(this);
        tvCarletterPay.setOnClickListener(this);
        tvCarletterAppearance.setOnClickListener(this);
    }

    private void initBack() {
        initPull();
    }


    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    private void initPull() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isCarletter(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---车辆信息解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---车辆信息解密成功---" + data);
                carletter = gson.fromJson(data, Carletter.class);
                //车牌号
                tvCarletterNumber.setText(carletter.getPlateNumber());
                //进场时间
                tvCarletterEntrancetime.setText(carletter.getEntryTime());
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("ConsumerId", consumerId);
        //车牌号
        params.put("PlateNumber", platNumber);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + consumerId + platNumber;
        Log.e(TAG, "---明文---" + random + "---" + consumerId + "---" + platNumber);
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
        httpRequestWrap.send(PlantAddress.CART_PAYORDER, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_carletter_return:
                finish();
                break;
            //确定出场
            case R.id.tv_carletter_appearance:
                CarletterDilogFragment carletterDilogFragment = new CarletterDilogFragment();
                carletterDilogFragment.initPlatNumber(platNumber);
                carletterDilogFragment.show(getSupportFragmentManager(), "温馨提示");
                break;
            //去支付
            case R.id.tv_carletter_pay:
                if (!isAppearance) {
                    plantState.initToast(context, "请先确认出场", true, 0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isCarletter(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---车辆信息解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---车辆信息解密成功---" + data);
                        carletter = gson.fromJson(data, Carletter.class);
                        PayDilogFragment payDilogFragment = new PayDilogFragment();
                        payDilogFragment.initPayPrice(0, carletter.getParkingPayFee());
                        payDilogFragment.show(getSupportFragmentManager(), "停车场支付");
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //用户id
                int consumerId = plantState.getUser().getConsumerId();
                params.put("ConsumerId", consumerId);
                //车牌号
                params.put("PlateNumber", platNumber);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + consumerId + platNumber;
                Log.e(TAG, "---明文---" + random + "---" + consumerId + "---" + platNumber);
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
                httpRequestWrap.send(PlantAddress.CART_PAYORDER, params);
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
     * 确认出场
     *
     * @param c
     */
    public void onEventMainThread(CarletterFind c) {
        //车牌号
        tvCarletterNumber.setText(c.carletter.getPlateNumber());
        //进场时间
        tvCarletterEntrancetime.setText(c.carletter.getEntryTime());
        //出场时间
        tvCarletterLeavetime.setText(c.carletter.getLeaveTime());
        handler.sendEmptyMessage(0x002);
        isAppearance = true;
        tvCarletterPay.setBackgroundColor(ContextCompat.getColor(context, R.color.strategy_item_published));
    }

    /**
     * 支付宝支付
     *
     * @param c
     */
    public void onEventMainThread(PayFind c) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "订单创建中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---支付宝加签解密失败---" + data);
                    return;
                }
                //2017040706580188
                Log.e(TAG, "---支付宝加签解密成功---" + data);
//                HttpZFBPayWrap.zfbPayParamss(getContext(),"2017040706580188","2014-07-24 22:22:22","0.01","测试",System.currentTimeMillis()+"");
                HttpZFBPayWrap.zfbPayParamss(context, data);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        Log.e(TAG, "---订单id---" + carletter.getParkNo());
        //订单id
        int orderIds = Integer.parseInt(carletter.getParkNo());
        params.put("orderId", orderIds);
        //加签平台
        int signType = 0;
        params.put("signType", signType);
        int random = plantState.getRandom();
        String sign = random + "" + orderIds + signType;
        Log.e(TAG, "---明文---" + random + "---" + orderIds + "---" + signType);
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
        httpRequestWrap.send(PlantAddress.PAY_SING, params);
    }
}
