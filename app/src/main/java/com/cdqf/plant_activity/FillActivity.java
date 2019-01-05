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
import com.cdqf.plant_class.Courier;
import com.cdqf.plant_dilog.CourierDilogFragment;
import com.cdqf.plant_find.CourierFind;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 填写物流单号
 * Created by liu on 2017/12/29.
 */

public class FillActivity extends BaseActivity {
    private String TAG = FillActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_fill_return)
    public RelativeLayout rlFillReturn = null;

    //填写物流公司
    @BindView(R.id.tv_fill_company)
    public TextView tvFillCompany = null;

    //填写单号
    @BindView(R.id.xet_fill_number)
    public XEditText xetFillNumber = null;

    //提交
    @BindView(R.id.tv_fill_submit)
    public TextView tvFillSubmit = null;

    private int rgId = 0;

    private int expressId = 0;

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
        setContentView(R.layout.activity_fill);

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
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        ButterKnife.bind(this);
        Intent intent = getIntent();
        rgId = intent.getIntExtra("rgId", 0);
    }

    private void initView() {
    }

    private void initAdapter() {
    }

    private void initListener() {

    }

    private void initBack() {
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "获取物流公司", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取物流公司解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取物流公司解密成功---" + data);
                plantState.getCourierList().clear();
                List<Courier> courierList = gson.fromJson(data, new TypeToken<List<Courier>>() {
                }.getType());
                plantState.setCourierList(courierList);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //随机数
        int random = plantState.getRandom();
        String sign = random + "";
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
        httpRequestWrap.send(PlantAddress.USER_COURIER, params);
    }

    @OnClick({R.id.rl_fill_return, R.id.tv_fill_company, R.id.tv_fill_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_fill_return:
                finish();
                break;
            //物流公司
            case R.id.tv_fill_company:
                CourierDilogFragment courierDilogFragment = new CourierDilogFragment();
                courierDilogFragment.show(getSupportFragmentManager(), "物流公司");
                break;
            //提交
            case R.id.tv_fill_submit:
               String company = tvFillCompany.getText().toString();
                if (company.length() <= 0) {
                    plantState.initToast(context, "请输入物流公司", true, 0);
                    return;
                }
                String logisticsNum = xetFillNumber.getText().toString();
                if (logisticsNum.length() <= 0) {
                    plantState.initToast(context, "请输入物流单号", true, 0);
                    return;
                }
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, "撤销中", new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isTrave(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取物流解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---获取物流解密成功---" + data);
                        if(!TextUtils.equals(data,"当前退款单不存在")){
                            finish();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //退款退货id
                params.put("rgId", rgId);

                //物流公司id
                params.put("expressId", expressId);

                //物流单号
                params.put("logisticsNum", logisticsNum);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + rgId + expressId + logisticsNum;
                Log.e(TAG, "---明文---" + random + "---" + rgId + "---" + expressId + "---" + logisticsNum);
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
                httpRequestWrap.send(PlantAddress.USER_NUM, params);
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
     * 物流
     * @param c
     */
    public void onEventMainThread(CourierFind c) {
        expressId = plantState.getCourierList().get(c.position).getExpressId();
        tvFillCompany.setText(plantState.getCourierList().get(c.position).getZhExpressName());
    }
}
