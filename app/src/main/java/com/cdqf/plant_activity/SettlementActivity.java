package com.cdqf.plant_activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.SettlementAdapter;
import com.cdqf.plant_class.Settlement;
import com.cdqf.plant_dilog.PayDilogFragment;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.DetailsFind;
import com.cdqf.plant_find.DissFind;
import com.cdqf.plant_find.PayFind;
import com.cdqf.plant_find.SettlementFind;
import com.cdqf.plant_find.WeChatFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_lmsd.wxapi.HttpWxPayWrap;
import com.cdqf.plant_lmsd.wxapi.Util;
import com.cdqf.plant_lmsd.wxapi.WXReturnFind;
import com.cdqf.plant_okhttp.OKHttpRequestWrap;
import com.cdqf.plant_okhttp.OnHttpRequest;
import com.cdqf.plant_pay.CancelFind;
import com.cdqf.plant_pay.HttpZFBPayWrap;
import com.cdqf.plant_pay.ZFBFind;
import com.cdqf.plant_pay.ZFBPayFind;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.ListViewForScrollView;
import com.cdqf.wechtlocalpay.MD5;
import com.cdqf.wechtlocalpay.WXPayLocalConstants;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * 确定订单
 * Created by liu on 2017/11/10.
 */

public class SettlementActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = SettlementActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_settlement_return)
    public RelativeLayout rlCartReturn = null;

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

    //商品集合
    @BindView(R.id.lvsv_settlement_list)
    public ListViewForScrollView lvsvSettlementList = null;

    private SettlementAdapter settlementAdapter = null;

    //配送方式
    @BindView(R.id.ll_settlement_distribution)
    public LinearLayout llSettlementDistribution = null;

    @BindView(R.id.tv_settlement_distribution)
    public TextView tvSettlementDistribution = null;

    //件数
    @BindView(R.id.tv_settlement_numbers)
    public TextView tvSettlementNumbers = null;

    //小计
    @BindView(R.id.tv_settlement_subtotal)
    public TextView tvSettlementSubtotal = null;

    //金额
    @BindView(R.id.tv_settlement_amount)
    public TextView tvSettlementAmount = null;

    //提交订单
    @BindView(R.id.tv_settlement_settlement)
    public TextView tvSettlementSettlement = null;

    @BindView(R.id.sv_settlement_goods)
    public ScrollView svSettlementGoods = null;

    private String commIds;

    private String numbers;

    private int orderIds;

    private Settlement settlement;

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
        setContentView(R.layout.activity_settlement);

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
//        HttpWxPayWrap.isWxApp(context, "wx5048e518874eed43");
        Intent intent = getIntent();
        commIds = intent.getStringExtra("commIds");
        numbers = intent.getStringExtra("numbers");

        Log.e(TAG, "---" + commIds + "---" + numbers);
    }

    private void initView() {

    }

    private void initAdapter() {
        settlementAdapter = new SettlementAdapter(context);
        lvsvSettlementList.setAdapter(settlementAdapter);
    }

    private void initListener() {
        rlCartReturn.setOnClickListener(this);
        llSettlementDistribution.setOnClickListener(this);
        llSettlementGoods.setOnClickListener(this);
        rlSettlementComplete.setOnClickListener(this);
        tvSettlementSettlement.setOnClickListener(this);
    }

    private void initBack() {
        initGoods();
        svSettlementGoods.smoothScrollTo(0, 0);
    }

    private void initGoods() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---商品确认订单解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---商品确认订单解密---" + data);
                settlement = new Settlement();
                settlement = gson.fromJson(data, Settlement.class);
                settlementAdapter.setSettlement(settlement);
                if (settlement.getReceivingAddress() != null) {
                    handler.sendEmptyMessage(0x002);
                    //姓名
                    tvSettlementName.setText(settlement.getReceivingAddress().getContacts());
                    //手机号码
                    tvSettlementNumber.setText(settlement.getReceivingAddress().getContactMobile());
                    //详情地址
                    tvSettlementDetails.setText(settlement.getReceivingAddress().getFullName());
                } else {
                    handler.sendEmptyMessage(0x001);
                }
                //件数
                tvSettlementNumbers.setText(context.getResources().getString(R.string.sett_one) + settlement.getTotalNum() + context.getResources().getString(R.string.sett_two));
                //合计价格
                tvSettlementSubtotal.setText(settlement.getAllCommTotalPrice() + "");
                //总价
                tvSettlementAmount.setText("￥" + settlement.getAllCommTotalPrice() + "");
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //商品id集合
        params.put("commIds", commIds);
        //商品id对应数量
        params.put("numbers", numbers);
        //用户id(测试id=1)
        int userId = plantState.getUser().getConsumerId();
        params.put("consumerId", userId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + commIds + numbers + userId;
        Log.e(TAG, "---参数---commIds---" + commIds + "---numbers---" + numbers + "---userId---" + userId);
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
        httpRequestWrap.send(PlantAddress.FOR_ORDER, params);
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
            case R.id.rl_settlement_return:
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
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, "创建订单中", new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---提交订单解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---提交订单解密成功---" + data);
                        orderIds = JSON.parseObject(data).getInteger("orderId");
                        PromptDilogFragment payDilogFragment = new PromptDilogFragment();
                        payDilogFragment.initPrompt("确定订单", 22);
                        payDilogFragment.show(getSupportFragmentManager(), "确定订单");
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //商品id
                params.put("commIds", commIds);
                //商品id对应数量
                params.put("commNums", numbers);
                //用户id(测试id=1)
                int consumerId = plantState.getUser().getConsumerId();
                params.put("consumerId", consumerId);
                //用户收货地址id
                int consumerReceivingId = settlement.getReceivingAddress().getCrId();
                params.put("consumerReceivingId", consumerReceivingId);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + commIds + numbers + consumerId + consumerReceivingId;
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
                httpRequestWrap.send(PlantAddress.FOR_ADDORDER, params);
                break;
        }
    }

    /**
     * 提交订单确定操作
     *
     * @param s
     */
    public void onEventMainThread(SettlementFind s) {
        PayDilogFragment payDilogFragment = new PayDilogFragment();
        payDilogFragment.initPayPrice(0, settlement.getAllCommTotalPrice());
        payDilogFragment.show(getSupportFragmentManager(), "提交订单");
    }

    /**
     * 支付宝操作
     *
     * @param t
     */
    public void onEventMainThread(PayFind t) {
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
                String d = gson.fromJson(data, String.class);
//                HttpZFBPayWrap.zfbPayParamss(context,"2017040706580188","2014-07-24 22:22:22","0.01","测试",System.currentTimeMillis()+"");
                HttpZFBPayWrap.zfbPayParamss(context, d);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int orderIds = this.orderIds;
        params.put("orderId", orderIds);
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

    /**
     * 支付宝支付成功返回
     *
     * @param pay
     */
    public void onEventMainThread(ZFBFind pay) {
        Map<String, Object> params = new HashMap<String, Object>();
        //当前页
        params.put("Orderid", orderIds);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + orderIds;
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
        params.put("random", random);
        params.put("sign", signEncrypt);

        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        okHttpRequestWrap.post(PlantAddress.RETURN_PAY, true, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---onOkHttpResponse---" + response);
                JSONObject resultJSON = JSON.parseObject(response);
                int StatusCode = resultJSON.getInteger("StatusCode");
                if (StatusCode != 0) {
                    Log.e(TAG, "---待支付成功返回失败---" + StatusCode);
                    return;
                }
                Log.e(TAG, "---待支付成功返回解密成功---" + StatusCode);
                //关闭搜索界面
                if (SearchShopActivity.searchShopActivity != null) {
                    SearchShopActivity.searchShopActivity.finish();
                }
                //关闭分类商品界面
                if (GoodsActivity.goodsActivity != null) {
                    GoodsActivity.goodsActivity.finish();
                }
                //商品详情界面
                if (GoodsDetailsActivity.goodsDetailsActivity != null) {
                    GoodsDetailsActivity.goodsDetailsActivity.finish();
                }
                //购物车
                if (CartActivity.cartActivity != null) {
                    CartActivity.cartActivity.finish();
                }
                initIntent(MyOrderActivity.class, 2);
                finish();
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });
    }

    public void onEventMainThread(CancelFind pay) {
        //关闭搜索界面
        if (SearchShopActivity.searchShopActivity != null) {
            SearchShopActivity.searchShopActivity.finish();
        }
        //关闭分类商品界面
        if (GoodsActivity.goodsActivity != null) {
            GoodsActivity.goodsActivity.finish();
        }
        //商品详情界面
        if (GoodsDetailsActivity.goodsDetailsActivity != null) {
            GoodsDetailsActivity.goodsDetailsActivity.finish();
        }
        //购物车
        if (CartActivity.cartActivity != null) {
            CartActivity.cartActivity.finish();
        }
        initIntent(MyOrderActivity.class, 2);
        finish();
    }

    public void onEventMainThread(ZFBPayFind pay) {
        plantState.initToast(context, pay.toast, true, 0);
    }

    /**
     * 微信操作
     *
     * @param w
     */
    public void onEventMainThread(WeChatFind w) {
//        WXPay.openIWXAPI(SettlementActivity.this);
//        WXPayIdAsyncTask wxPayIdAsyncTask = new WXPayIdAsyncTask(context);
//        wxPayIdAsyncTask.execute(WXPayLocalConstants.URL_STRING);
//        req = new PayReq();
//        String urlString = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//        PrePayIdAsyncTask prePayIdAsyncTask = new PrePayIdAsyncTask();
//        prePayIdAsyncTask.execute(urlString);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "订单创建中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---微信加签失败---" + data);
                    return;
                }
                //2017040706580188
                Log.e(TAG, "---微信成功---" + data);
                HttpWxPayWrap.wxPostJSON(data);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int orderIds = this.orderIds;
        params.put("orderId", orderIds);
        int signType = 3;
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

    public void onEventMainThread(WXReturnFind wx) {
        Map<String, Object> params = new HashMap<String, Object>();
        //当前页
        params.put("OrderId", orderIds);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + orderIds;
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
        params.put("random", random);
        params.put("sign", signEncrypt);

        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        okHttpRequestWrap.post(PlantAddress.RETURN_PAY, true, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---onOkHttpResponse---" + response);
                JSONObject resultJSON = JSON.parseObject(response);
                int StatusCode = resultJSON.getInteger("StatusCode");
                if (StatusCode != 0) {
                    Log.e(TAG, "---待支付成功返回失败---" + StatusCode);
                    return;
                }
                Log.e(TAG, "---待支付成功返回解密成功---" + StatusCode);
                //关闭搜索界面
                if (SearchShopActivity.searchShopActivity != null) {
                    SearchShopActivity.searchShopActivity.finish();
                }
                //关闭分类商品界面
                if (GoodsActivity.goodsActivity != null) {
                    GoodsActivity.goodsActivity.finish();
                }
                //商品详情界面
                if (GoodsDetailsActivity.goodsDetailsActivity != null) {
                    GoodsDetailsActivity.goodsDetailsActivity.finish();
                }
                //购物车
                if (CartActivity.cartActivity != null) {
                    CartActivity.cartActivity.finish();
                }
                initIntent(MyOrderActivity.class, 2);
                finish();
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });
    }

    public void onEventMainThread(DissFind d) {
        //关闭搜索界面
        if (SearchShopActivity.searchShopActivity != null) {
            SearchShopActivity.searchShopActivity.finish();
        }
        //关闭分类商品界面
        if (GoodsActivity.goodsActivity != null) {
            GoodsActivity.goodsActivity.finish();
        }
        //商品详情界面
        if (GoodsDetailsActivity.goodsDetailsActivity != null) {
            GoodsDetailsActivity.goodsDetailsActivity.finish();
        }
        //购物车
        if (CartActivity.cartActivity != null) {
            CartActivity.cartActivity.finish();
        }
        initIntent(MyOrderActivity.class, 1);
        finish();
    }

    /**
     * 修改地址有变
     *
     * @param d
     */
    public void onEventMainThread(DetailsFind d) {
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

    private PayReq req;

    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

    private Map<String, String> resultunifiedorder;

    class PrePayIdAsyncTask extends AsyncTask<String, Void, Map<String, String>> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = new XProgressDialog(detailsContext, "提交订单中...");
//            dialog.show();
            dialog = ProgressDialog.show(context, "提示", "正在提交订单");
        }

        @Override
        protected Map<String, String> doInBackground(String... params) {
            String url = String.format(params[0]);
            String entity = getProductArgs();
            byte[] buf = Util.httpPost(url, entity);
            String content = new String(buf);
            Map<String, String> xml = decodeXml(content);
            return xml;
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            super.onPostExecute(result);
            if (dialog != null) {
                dialog.dismiss();
            }
            Set<String> r = result.keySet();
            for (String s : r) {
                Log.e(TAG, "---键---" + s + "---值---" + result.get(s));
            }
            resultunifiedorder = result;
            Log.e(TAG, "---提交订单成功---");
            //加签
            genPayReq();
            //调起微信
            sendPayReq();
        }
    }

    /**
     * 调起微信支付
     */
    private void sendPayReq() {
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
        Log.e(TAG, "---调起微信支付---" + req.partnerId);
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 生成签名参数
     */
    private void genPayReq() {
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        Log.e(TAG, "---生成签名参数");
        if (resultunifiedorder != null) {
            req.prepayId = resultunifiedorder.get("prepay_id");
            req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
        } else {
            Toast.makeText(SettlementActivity.this, "prepayid为空", Toast.LENGTH_SHORT).show();
        }
        req.nonceStr = getNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genAppSign(signParams);
        Log.e(TAG, "----生成签名参数---" + req.sign);
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        Log.e(TAG, "----" + appSign);
        return appSign;
    }

    public Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
            return xml;
        } catch (Exception e) {
            Log.e("Simon", "----" + e.toString());
        }
        return null;
    }

    /**
     * 订单信息
     *
     * @return
     */
    private String getProductArgs() {
        StringBuffer xml = new StringBuffer();
        try {
            String nonceStr = getNonceStr();
            xml.append("<xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            //开放平台APP_ID
            packageParams.add(new BasicNameValuePair("appid", WXPayLocalConstants.APP_ID));
            //商品名称
            packageParams.add(new BasicNameValuePair("body", "hdsj ticket"));
            //商户平台MCH_ID
            packageParams.add(new BasicNameValuePair("mch_id", WXPayLocalConstants.MCH_ID));
            //随机数防重发
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            //回调地址
            packageParams.add(new BasicNameValuePair("notify_url", "https://www.baidu.com"));//写你们的回调地址
            //订单号
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            //金额
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            //交易类型
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
            //生成签名
            String sign = getPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));
            //转化为xml
            String xmlString = toXml(packageParams);
            return xmlString;
        } catch (Exception e) {
            return null;
        }
    }

    //生成订单号,测试用，在客户端生成
    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    //生成随机号，防重发
    private String getNonceStr() {

        Random random = new Random();

        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 生成签名
     */
    private String getPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);


        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e(TAG, ">>>>" + packageSign);
        return packageSign;
    }

    /*
     * 转换成xml
     */
    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");


            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("Simon", ">>>>" + sb.toString());
        return sb.toString();
    }
}
