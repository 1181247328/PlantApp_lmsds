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
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.OrderDetailsActivity;
import com.cdqf.plant_adapter.ForGoodsAdapter;
import com.cdqf.plant_class.ForGoods;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.ForGoddsFind;
import com.cdqf.plant_find.ForGoodsOneFind;
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
 * 待收货
 * Created by liu on 2017/11/20.
 */

public class ForGoodsFragment extends Fragment implements View.OnClickListener {

    private String TAG = ForGoodsFragment.class.getSimpleName();

    private View view = null;

    private HttpRequestWrap httpRequestWrap = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Gson gson = new Gson();

    private int pageIndex = 0;

    //无
    private LinearLayout llAllorderThere = null;

    //存在
    private LinearLayout llAllorderAre = null;

    //刷新器
    private PullToRefreshLayout ptrlAllorderPull = null;

    private ListView llAllorderList = null;

    private ForGoodsAdapter forPaymentAdapter = null;

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
        view = inflater.inflate(R.layout.fragment_forgoods, null);

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
        llAllorderThere = (LinearLayout) view.findViewById(R.id.ll_allorder_there);
        llAllorderAre = (LinearLayout) view.findViewById(R.id.ll_allorder_are);
        ptrlAllorderPull = (PullToRefreshLayout) view.findViewById(R.id.ptrl_allorder_pull);
        llAllorderList = (ListView) ptrlAllorderPull.getPullableView();
    }

    private void initAdapter() {
        forPaymentAdapter = new ForGoodsAdapter(getContext());
        llAllorderList.setAdapter(forPaymentAdapter);
    }

    private void initListener() {
        ptrlAllorderPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                // 下拉刷新操作
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取订单待收货解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---获取订单待收货解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            handler.sendEmptyMessage(0x01);
                            return;
                        }
                        data = JSON.parseObject(data).getString("list");
                        plantState.getForGoodsList().clear();
                        List<ForGoods> forPaymentList = gson.fromJson(data, new TypeToken<List<ForGoods>>() {
                        }.getType());
                        plantState.setForGoodsList(forPaymentList);
                        if (forPaymentAdapter != null) {
                            forPaymentAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                initPut(true);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                // 上拉加载操作
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取订单待收货解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---获取订单待收货解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            handler.sendEmptyMessage(0x01);
                            return;
                        }
                        data = JSON.parseObject(data).getString("list");
                        List<ForGoods> forPaymentList = gson.fromJson(data, new TypeToken<List<ForGoods>>() {
                        }.getType());
                        plantState.getForGoodsList().addAll(forPaymentList);
                        if (forPaymentAdapter != null) {
                            forPaymentAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                initPut(false);
            }
        });
        llAllorderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(OrderDetailsActivity.class,position);
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
                    Log.e(TAG, "---获取订单待收货解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取订单待收货解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                plantState.getForGoodsList().clear();
                List<ForGoods> forPaymentList = gson.fromJson(data, new TypeToken<List<ForGoods>>() {
                }.getType());
                plantState.setForGoodsList(forPaymentList);
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
        //订单状态(待发货
        int orderListStatus = 3;
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

    private void initIntent(Class<?> activity,int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("type",3);
        intent.putExtra("position",position);
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
    }

    /**
     * 确认收货
     * @param g
     */
    public void onEventMainThread(ForGoddsFind g) {
        PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
        promptDilogFragment.initPrompt("是否收货", 14, g.position);
        promptDilogFragment.show(getFragmentManager(), "是否收货");
    }

    /**
     * 确认收货第二次
     * @param g
     */
    public void onEventMainThread(final ForGoodsOneFind g){
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.goods), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---确认收货解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---确认收货解密成功---" + data);
                initPull();
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        int orderId = plantState.getForGoodsList().get(g.position).getOrderId();
        params.put("orderId", orderId);
        int random = plantState.getRandom();
        String sign = random + "" + consumerId + orderId;
        Log.e(TAG, "---明文---" + random + "---" + consumerId + "---" + orderId);
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
        httpRequestWrap.send(PlantAddress.USER_SIGN, params);
    }
}
