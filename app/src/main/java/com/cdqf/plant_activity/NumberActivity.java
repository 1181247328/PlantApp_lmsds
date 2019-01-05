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

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_dilog.RegionDilogFragment;
import com.cdqf.plant_find.NumberFind;
import com.cdqf.plant_find.RegionFind;
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

import de.greenrobot.event.EventBus;

/**
 * 绑定车牌号
 * Created by liu on 2017/11/15.
 */

public class NumberActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = NumberActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlNumberReturn = null;

    //下一步
    private TextView tvNumberNext = null;

    //地区
    private LinearLayout llNumberRegion = null;

    private TextView tvNumberRegion = null;

    //车牌号
    private XEditText xetNumberCar = null;

    private String platNumber = null;

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
        setContentView(R.layout.activity_number);

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
        rlNumberReturn = this.findViewById(R.id.rl_number_return);
        tvNumberNext = this.findViewById(R.id.tv_number_next);
        llNumberRegion = this.findViewById(R.id.ll_number_region);
        tvNumberRegion = this.findViewById(R.id.tv_number_region);
        xetNumberCar = this.findViewById(R.id.xet_number_car);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlNumberReturn.setOnClickListener(this);
        tvNumberNext.setOnClickListener(this);
        llNumberRegion.setOnClickListener(this);
    }

    private void initBack() {

    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("platNumber",platNumber);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_number_return:
                finish();
                break;
            //下一步
            case R.id.tv_number_next:
                String plate = xetNumberCar.getText().toString();
                //判断是否为空
                if (plate.length() <= 0) {
                    plantState.initToast(context, "请输入车牌号码", true, 0);
                    return;
                }
                //判断是否是正确的车牌号
                String region = tvNumberRegion.getText().toString();
                platNumber = region + plate;
                if (!plantState.licensePlate(platNumber)) {
                    plantState.initToast(context, "车牌号不正确", true, 0);
                    return;
                }
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt("请确认车牌号是否正确", 24);
                promptDilogFragment.show(getSupportFragmentManager(), "绑定车牌号");

                break;
            //地区
            case R.id.ll_number_region:
                RegionDilogFragment regionDilogFragment = new RegionDilogFragment();
                regionDilogFragment.show(getSupportFragmentManager(), "地区");
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
     * 提示绑定
     *
     * @param l
     */
    public void onEventMainThread(RegionFind l) {
        tvNumberRegion.setText(l.region);
    }

    /**
     * 提示绑定
     *
     * @param l
     */
    public void onEventMainThread(NumberFind l) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "绑定中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(context,result,status);
                if (data == null) {
                    Log.e(TAG, "---绑定车牌号解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---绑定车牌号解密成功---" + data);
                if(TextUtils.equals(data,"添加用户车牌号成功")){
                    initIntent(CarletterActivity.class);
                    finish();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //车牌号
        params.put("platNumber", platNumber);
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
        httpRequestWrap.send(PlantAddress.CART_BINDING, params);

    }
}
