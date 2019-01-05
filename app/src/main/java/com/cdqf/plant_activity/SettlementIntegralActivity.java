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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_class.Address;
import com.cdqf.plant_class.Settlement;
import com.cdqf.plant_find.DetailsFind;
import com.cdqf.plant_find.SettlementFind;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 积分兑换提交
 * Created by liu on 2018/1/16.
 */

public class SettlementIntegralActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = SettlementIntegralActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_settinte_return)
    public RelativeLayout rlSettinteReturn = null;

    //填写收货地址
    @BindView(R.id.ll_settlement_goods)
    public LinearLayout llSettlementGoods = null;

    //地址
    @BindView(R.id.rl_settlement_complete)
    public RelativeLayout rlSettlementComplete = null;

    //姓名
    @BindView(R.id.tv_settlement_name)
    public TextView tvSettlementName = null;

    //手机号码
    @BindView(R.id.tv_settlement_number)
    public TextView tvSettlementNumber = null;

    //详情地址
    @BindView(R.id.tv_settlement_details)
    public TextView tvSettlementDetails = null;

    //图片
    @BindView(R.id.iv_settlement_item_figure)
    public ImageView ivSettlementItemFigure = null;

    //名称
    @BindView(R.id.tv_settlement_item_name)
    public TextView tvSettlementItemName = null;

    //积分
    @BindView(R.id.tv_settlement_item_price)
    public TextView tvSettlementItemPrice = null;

    //配送方式
    @BindView(R.id.ll_settlement_distribution)
    public LinearLayout llSettlementDistribution = null;

    @BindView(R.id.tv_settlement_distribution)
    public TextView tvSettlementDistribution = null;

    //金额
    @BindView(R.id.tv_settlement_amount)
    public TextView tvSettlementAmount = null;

    //提交订单
    @BindView(R.id.tv_settlement_settlement)
    public TextView tvSettlementSettlement = null;

    @BindView(R.id.sv_settlement_goods)
    public ScrollView svSettlementGoods = null;

    //商品id
    private int commId;

    //商品图片
    private String commHttpPic;

    //商品名称
    private String commName;

    //价格
    private int unitPrice;

    private int id;

    private Settlement settlement;

    private List<Address> addressList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    llSettlementGoods.setVisibility(View.VISIBLE);
                    rlSettlementComplete.setVisibility(View.GONE);
                    break;
                case 0x002:
                    llSettlementGoods.setVisibility(View.GONE);
                    rlSettlementComplete.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_settlementintegral);

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
        ButterKnife.bind(this);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        imageLoader = plantState.getImageLoader(context);
        Intent intent = getIntent();
        commId = intent.getIntExtra("commId", 0);
        //商品图片
        commHttpPic = intent.getStringExtra("commHttpPic");

        //商品名称
        commName = intent.getStringExtra("commName");

        //价格
        unitPrice = intent.getIntExtra("unitPrice", 0);
    }

    private void initView() {

    }

    private void initAdapter() {

    }

    private void initListener() {
        rlSettinteReturn.setOnClickListener(this);
        llSettlementDistribution.setOnClickListener(this);
        llSettlementGoods.setOnClickListener(this);
        rlSettlementComplete.setOnClickListener(this);
        tvSettlementSettlement.setOnClickListener(this);
    }

    private void initBack() {
        initGoods();
        svSettlementGoods.smoothScrollTo(0, 0);
        imageLoader.displayImage(commHttpPic, ivSettlementItemFigure, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        //名称
        tvSettlementItemName.setText(commName);
        //积分
        tvSettlementItemPrice.setText(unitPrice + "");
        tvSettlementAmount.setText(unitPrice + "");
    }

    private void initGoods() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取地址解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取地址解密---" + data);
                if (TextUtils.equals(data, "列表为空")) {
                    handler.sendEmptyMessage(0x001);
                    return;
                } else {
                    handler.sendEmptyMessage(0x002);
                    addressList = gson.fromJson(data, new TypeToken<List<Address>>() {
                    }.getType());
                    for (Address address : addressList) {
                        if (address.isDefault()) {
                            id = address.getId();
                            //姓名
                            tvSettlementName.setText(address.getContacts());

                            //手机号码
                            tvSettlementNumber.setText(address.getContactMobile());

                            //详情地址
                            tvSettlementDetails.setText(address.getFullAddress());
                            break;
                        }
                    }
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id(测试id=1)
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + consumerId;
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
        httpRequestWrap.send(PlantAddress.USER_ADDRESS, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_settinte_return:
                finish();
                break;
            //填写收货地址
            case R.id.ll_settlement_goods:
                initIntent(AddressActivity.class);
                break;
            //地址
            case R.id.rl_settlement_complete:
                initIntent(AddressActivity.class);
                break;
            //配送方式
            case R.id.ll_settlement_distribution:
                break;
            //确定提交
            case R.id.tv_settlement_settlement:
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---提交订单解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---提交订单解密成功---" + data);
                        finish();
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //商品id
                params.put("commId", commId);
                //数量
                int commNum = 1;
                params.put("commNum", commNum);
                //用户id
                int consumerId = plantState.getUser().getConsumerId();
                params.put("consumerId", consumerId);
                //用户收货地址id
                int consumerReceivingId = id;
                params.put("consumerReceivingId", consumerReceivingId);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + commId + commNum + consumerId + consumerReceivingId;
                Log.e(TAG, "---明文---" + random);
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
                httpRequestWrap.send(PlantAddress.INTEGRAL_ORDER, params);
                break;
        }
    }

    /**
     * 提交订单确定操作
     *
     * @param s
     */
    public void onEventMainThread(SettlementFind s) {
    }

    public void onEventMainThread(DetailsFind s) {
        initGoods();
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
}