package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_okhttp.OKHttpRequestWrap;
import com.cdqf.plant_okhttp.OnHttpRequest;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;

import com.cdqf.plant_view.WebViewScroll;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看物流
 * Created by liu on 2017/12/9.
 */

public class LogisticsActivity extends BaseActivity {

    private String TAG = LogisticsActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    @BindView(R.id.srl_logistics_pull)
    public SwipeRefreshLayout srlLogisticsPull = null;

    //返回
    @BindView(R.id.rl_logistics_return)
    public RelativeLayout rlLogisticsReturn = null;

    @BindView(R.id.wv_logistics_html)
    public WebViewScroll wvLogisticsHtml = null;

    @BindView(R.id.rl_orders_bar)
    public RelativeLayout rlOrdersBar = null;

    @BindView(R.id.tv_orders_abnormal)
    public TextView tvOrdersAbnormal = null;

    private WebSettings wvLogisticsCarrier = null;

    private int type;

    private int position;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //加载布局
        setContentView(R.layout.activity_logistics);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();
    }

    private void initAgo() {
        context = this;
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        position = intent.getIntExtra("position", 0);
    }

    private void initView() {
        wvLogisticsCarrier = plantState.webSettings(wvLogisticsHtml);
        wvLogisticsHtml.setHorizontalScrollBarEnabled(true);
        wvLogisticsHtml.setVerticalScrollBarEnabled(true);
        wvLogisticsHtml.requestFocus();
        wvLogisticsHtml.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
    }

    private void initAdapter() {

    }

    private void initListener() {
        wvLogisticsHtml.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    Log.e(TAG, "---加载页面完成---");
                    if (srlLogisticsPull != null) {
                        srlLogisticsPull.setRefreshing(false);
                    }
                    rlOrdersBar.setVisibility(View.GONE);
                    tvOrdersAbnormal.setVisibility(View.GONE);
                    wvLogisticsHtml.setVisibility(View.VISIBLE);
                }
            }
        });

        srlLogisticsPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initPull();
            }
        });

        wvLogisticsHtml.setWebScrollListener(new WebViewScroll.WebScrollListener() {
            @Override
            public void scroll(boolean isScroll) {
                srlLogisticsPull.setEnabled(isScroll);
            }
        });
    }

    private void initBack() {
        srlLogisticsPull.setEnabled(false);
        initPull();
    }

    private void initPull() {

        Map<String, Object> params = new HashMap<String, Object>();
        int qId = 0;
        //订单id
        if (type == 0) {
            qId = plantState.getAllOrderList().get(position).getOrderId();
        } else {
            qId = plantState.getForGoodsList().get(position).getOrderId();
        }
        params.put("qId", qId);
        //查询状态
        int qType = 0;
        params.put("qType", qType);

        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + qId + qType;
        Log.e(TAG, "---明文---" + random + "---" + qId + "---" + qType);
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
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        okHttpRequestWrap.post(PlantAddress.USER_LOGISTICS, false, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                if (srlLogisticsPull != null) {
                    srlLogisticsPull.setEnabled(true);
                }
                String data = Errer.unickResult(context, response);
                if (data == null) {
                    Log.e(TAG, "---获取物流信息解密失败---" + data);
                    rlOrdersBar.setVisibility(View.GONE);
                    tvOrdersAbnormal.setVisibility(View.VISIBLE);
                    wvLogisticsHtml.setVisibility(View.GONE);
                    return;
                }
                Log.e(TAG, "---获取物流信息解密成功---" + data);
                JSONObject resultJSON = JSON.parseObject(data);
                //快递公司
                String expressCode = resultJSON.getString("expressCode");
                //快递单号
                String logisticsNum = resultJSON.getString("logisticsNum");
                String url = "https://m.kuaidi100.com/app/query/?com=" + expressCode + "&nu=" + logisticsNum + "&coname=qifukeji";
                wvLogisticsHtml.loadUrl(url);
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
                if (srlLogisticsPull != null) {
                    srlLogisticsPull.setEnabled(true);
                    srlLogisticsPull.setRefreshing(false);
                }
                rlOrdersBar.setVisibility(View.GONE);
                tvOrdersAbnormal.setVisibility(View.VISIBLE);
                wvLogisticsHtml.setVisibility(View.GONE);
                plantState.initToast(context, error, true, 0);
            }
        });
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @OnClick({R.id.rl_logistics_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_logistics_return:
                finish();
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
