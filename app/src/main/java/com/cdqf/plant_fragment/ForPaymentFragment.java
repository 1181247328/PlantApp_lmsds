package com.cdqf.plant_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.OrderDetailsActivity;
import com.cdqf.plant_adapter.ForPaymentAdapter;
import com.cdqf.plant_class.ForPayment;
import com.cdqf.plant_dilog.PayDilogFragment;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.AllOrderDissFind;
import com.cdqf.plant_find.AllOrderPullFind;
import com.cdqf.plant_find.CacenlOrderFind;
import com.cdqf.plant_find.CacenlOrderTwoFind;
import com.cdqf.plant_find.ForPayFind;
import com.cdqf.plant_find.ForPaymentFind;
import com.cdqf.plant_find.ForPullFind;
import com.cdqf.plant_find.ForWxFind;
import com.cdqf.plant_find.PayForFind;
import com.cdqf.plant_find.SendGoodsPullFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_lmsd.wxapi.HttpWxPayWrap;
import com.cdqf.plant_lmsd.wxapi.WXReturnFind;
import com.cdqf.plant_okhttp.OKHttpRequestWrap;
import com.cdqf.plant_okhttp.OnHttpRequest;
import com.cdqf.plant_pay.HttpZFBPayWrap;
import com.cdqf.plant_pay.ZFBFind;
import com.cdqf.plant_pay.ZFBPayFind;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 待付款
 * Created by liu on 2017/11/20.
 */

public class ForPaymentFragment extends Fragment implements View.OnClickListener {

    private String TAG = ForPaymentFragment.class.getSimpleName();

    private View view = null;

    private HttpRequestWrap httpRequestWrap = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    //无
    private LinearLayout llAllorderThere = null;

    //存在
    private LinearLayout llAllorderAre = null;

    //刷新器
    private PullToRefreshLayout ptrlAllorderPull = null;

    private ListView llAllorderList = null;

    private ForPaymentAdapter forPaymentAdapter = null;

    private int pageIndex = 0;

    private Gson gson = new Gson();

    private int orderIds = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    llAllorderThere.setVisibility(View.GONE);
                    ptrlAllorderPull.setVisibility(View.VISIBLE);
                    break;
                case 0x01:
                    llAllorderThere.setVisibility(View.VISIBLE);
                    ptrlAllorderPull.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forpayment, null);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        httpRequestWrap = new HttpRequestWrap(getContext());
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        llAllorderThere = view.findViewById(R.id.ll_allorder_there);
        llAllorderAre = view.findViewById(R.id.ll_allorder_are);
        ptrlAllorderPull = view.findViewById(R.id.ptrl_allorder_pull);
        llAllorderList = (ListView) ptrlAllorderPull.getPullableView();
    }

    private void initAdapter() {
        forPaymentAdapter = new ForPaymentAdapter(getContext());
        llAllorderList.setAdapter(forPaymentAdapter);
        //Utility.setListViewHeightBasedOnChildren(llAllorderList);
    }

    private void initListener() {
        ptrlAllorderPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                // 下拉刷新操作
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取订单待付款解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---获取订单待付款解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            handler.sendEmptyMessage(0x01);
                            return;
                        }
                        data = JSON.parseObject(data).getString("list");
                        plantState.getForPaymentList().clear();
                        List<ForPayment> forPaymentList = gson.fromJson(data, new TypeToken<List<ForPayment>>() {
                        }.getType());
                        plantState.setForPaymentList(forPaymentList);
                        if (forPaymentAdapter != null) {
                            forPaymentAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                initPut(true);
            }

            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                // 下拉刷新操作
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取订单待付款解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---获取订单待付款解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.more), true, 0);
                            return;
                        }
                        data = JSON.parseObject(data).getString("list");
                        List<ForPayment> forPaymentList = gson.fromJson(data, new TypeToken<List<ForPayment>>() {
                        }.getType());
                        plantState.getForPaymentList().addAll(forPaymentList);
                        if (forPaymentAdapter != null) {
                            forPaymentAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                initPut(false);
            }
        });
        //订单详情
        llAllorderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(OrderDetailsActivity.class, position);
            }
        });
    }

    private void initBack() {
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取订单待付款解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取订单待付款解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                plantState.getForPaymentList().clear();
                List<ForPayment> forPaymentList = gson.fromJson(data, new TypeToken<List<ForPayment>>() {
                }.getType());
                plantState.setForPaymentList(forPaymentList);
                if (forPaymentAdapter != null) {
                    forPaymentAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(0x00);
            }
        }));
        initPut(true);
    }

    private void initPut(boolean isIndex) {
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //订单状态
        int orderListStatus = 1;
        params.put("orderListStatus", orderListStatus);
        if (isIndex) {
            //当前页数
            pageIndex = 1;
        } else {
            pageIndex++;
        }
        params.put("pageIndex", pageIndex);
        //每页显示条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + consumerId + orderListStatus + pageIndex + pageCount;
        Log.e(TAG, "---明文---" + random + "---" + consumerId + "---" + orderListStatus + "---" + pageIndex + "---" + pageCount);
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        //随机数
        params.put("random", random);
        params.put("sign", signEncrypt);
        httpRequestWrap.send(PlantAddress.USER_ORDER, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("type", 1);
        intent.putExtra("position", position);
        intent.putExtra("isAllOrder", false);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        eventBus.unregister(this);
        plantState.getForPaymentList().clear();
    }

    public void onEventMainThread(ForPullFind f) {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取订单待付款解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取订单待付款解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                plantState.getForPaymentList().clear();
                List<ForPayment> forPaymentList = gson.fromJson(data, new TypeToken<List<ForPayment>>() {
                }.getType());
                plantState.setForPaymentList(forPaymentList);
                if (forPaymentAdapter != null) {
                    forPaymentAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(0x00);
            }
        }));
        initPut(true);
    }

    /**
     * 取消订单
     *
     * @param c
     */
    public void onEventMainThread(CacenlOrderFind c) {
        PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
        promptDilogFragment.initPrompt("是否取消订单", 18, c.position);
        promptDilogFragment.show(getFragmentManager(), "取消订单");
    }

    /**
     * 取消订单二次操作
     *
     * @param delete
     */
    public void onEventMainThread(final CacenlOrderTwoFind delete) {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, "删除订单中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---取消订单解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---取消订单解密成功---" + data);
                plantState.initToast(getContext(), data, true, 0);
                eventBus.post(new AllOrderDissFind());
                initPull();
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //订单状态
        int orderIds = plantState.getAllOrderList().get(delete.position).getOrderId();
        params.put("orderId", orderIds);

        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);

        int random = plantState.getRandom();
        String sign = random + "" + orderIds + consumerId;
        Log.e(TAG, "---明文---" + random + "---" + consumerId + "---" + orderIds);
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        //随机数
        params.put("random", random);
        params.put("sign", signEncrypt);
        httpRequestWrap.send(PlantAddress.USER_CACENL, params);
    }

    /**
     * 支付
     */
    public void onEventMainThread(ForPayFind d) {
        PayDilogFragment payDilogFragment = new PayDilogFragment();
        payDilogFragment.initPayPricePosition(1, plantState.getForPaymentList().get(d.position).getTotalPrice(), d.position);
        payDilogFragment.show(getFragmentManager(), "支付");
    }

    /**
     * 支付第二次操作
     *
     * @param pay
     */
    public void onEventMainThread(PayForFind pay) {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, "订单创建中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---支付宝加签解密失败---" + data);
                    return;
                }
                //2017040706580188
                Log.e(TAG, "---支付宝加签解密成功---" + data);
                String d = gson.fromJson(data, String.class);
//                HttpZFBPayWrap.zfbPayParamss(getContext(),"2017040706580188","2014-07-24 22:22:22","0.01","测试",System.currentTimeMillis()+"");
                HttpZFBPayWrap.zfbPayParamss(getContext(), d);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        orderIds = plantState.getForPaymentList().get(pay.position).getOrderId();
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        //随机数
        params.put("random", random);
        params.put("sign", signEncrypt);
        httpRequestWrap.send(PlantAddress.PAY_SING, params);
    }

    public void onEventMainThread(ZFBPayFind pay) {
        plantState.initToast(getContext(), pay.toast, true, 0);
    }

    public void onEventMainThread(ZFBFind pay) {
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        params.put("random", random);
        params.put("sign", signEncrypt);
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(getContext());
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
                initPull();
                eventBus.post(new AllOrderPullFind());
                eventBus.post(new SendGoodsPullFind());
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });
    }

    /**
     * 微信支付
     *
     * @param pay
     */
    public void onEventMainThread(ForWxFind pay) {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, "订单创建中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---微信加签失败---" + data);
                    return;
                }
                //2017040706580188
                Log.e(TAG, "---微信加签成功---" + data);
                HttpWxPayWrap.wxPostJSON(data);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        orderIds = plantState.getForPaymentList().get(pay.position).getOrderId();
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
            plantState.initToast(getContext(), "加密失败", true, 0);
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        params.put("random", random);
        params.put("sign", signEncrypt);
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(getContext());
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
                initPull();
                eventBus.post(new AllOrderPullFind());
                eventBus.post(new SendGoodsPullFind());
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });
    }

    public void onEventMainThread(ForPaymentFind f) {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取订单待付款解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取订单待付款解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                plantState.getForPaymentList().clear();
                eventBus.post(new SendGoodsPullFind());
                List<ForPayment> forPaymentList = gson.fromJson(data, new TypeToken<List<ForPayment>>() {
                }.getType());
                plantState.setForPaymentList(forPaymentList);
                if (forPaymentAdapter != null) {
                    forPaymentAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(0x00);
            }
        }));
        initPut(true);
    }
}