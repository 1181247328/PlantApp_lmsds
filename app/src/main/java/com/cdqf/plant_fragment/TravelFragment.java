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
import com.cdqf.plant_activity.StrategyDetailsActivity;
import com.cdqf.plant_adapter.TravelAdapter;
import com.cdqf.plant_class.TravelCollection;
import com.cdqf.plant_class.TravelCollectionOneFind;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.TravelCollectionTwoFind;
import com.cdqf.plant_find.TravelColltionFind;
import com.cdqf.plant_find.TravelShareFind;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 游记
 * Created by liu on 2017/11/16.
 */

public class TravelFragment extends Fragment implements View.OnClickListener {
    private String TAG = TravelFragment.class.getSimpleName();

    private View view = null;

    private PullToRefreshLayout ptrlTravelPull = null;

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    private ListView lvtravelList = null;

    private LinearLayout llCollectionNocontext = null;

    private TravelAdapter travelAdapter = null;

    private int pageIndex = 0;

    private Gson gson = new Gson();

    private int travelPosition = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    ptrlTravelPull.setVisibility(View.VISIBLE);
                    llCollectionNocontext.setVisibility(View.GONE);
                    break;
                case 0x01:
                    ptrlTravelPull.setVisibility(View.GONE);
                    llCollectionNocontext.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_travel, null);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        httpRequestWrap = new HttpRequestWrap(getContext());
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        ptrlTravelPull = (PullToRefreshLayout) view.findViewById(R.id.ptrl_travel_pull);
        lvtravelList = (ListView) ptrlTravelPull.getPullableView();
        llCollectionNocontext = (LinearLayout) view.findViewById(R.id.ll_collection_nocontext);
    }

    private void initAdapter() {
        travelAdapter = new TravelAdapter(getContext());
        lvtravelList.setAdapter(travelAdapter);
    }

    private void initListener() {
        lvtravelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(StrategyDetailsActivity.class, position);
            }
        });
        ptrlTravelPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取游记收藏解密失败---" + data);
                            ptrlTravelPull.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG, "---获取游记收藏解密成功---" + data);
                        ptrlTravelPull.refreshFinish(PullToRefreshLayout.SUCCEED);
                        plantState.getTravelCollectionList().clear();
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.more), true, 0);
                            return;
                        }
                        data = JSON.parseObject(data).getString("list");
                        List<TravelCollection> travelCollectionList = gson.fromJson(data, new TypeToken<List<TravelCollection>>() {
                        }.getType());
                        plantState.setTravelCollectionList(travelCollectionList);
                        if (travelAdapter != null) {
                            travelAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //当前页
                pageIndex = 1;
                params.put("pageIndex", pageIndex);
                //每页条数
                int pageCount = 10;
                params.put("pageCount", pageCount);
                //是否全部
                boolean isAll = false;
                params.put("isAll", pageCount);
                //收藏类型
                String collectionType = "0";
                params.put("collectionType", collectionType);
                //用户id
                String consumerId = plantState.getUser().getConsumerId() + "";
                params.put("consumerId", consumerId);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + pageIndex + pageCount + isAll + collectionType + consumerId;
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
                //随机数
                params.put("random", random);
                params.put("sign", signEncrypt);
                httpRequestWrap.send(PlantAddress.USER_COLLECTION, params);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //上拉加载
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取游记收藏解密失败---" + data);
                            ptrlTravelPull.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG, "---获取游记收藏解密成功---" + data);
                        ptrlTravelPull.refreshFinish(PullToRefreshLayout.SUCCEED);
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.more), true, 0);
                            return;
                        }
                        data = JSON.parseObject(data).getString("list");
                        List<TravelCollection> travelCollectionList = gson.fromJson(data, new TypeToken<List<TravelCollection>>() {
                        }.getType());
                        plantState.getTravelCollectionList().addAll(travelCollectionList);
                        if (travelAdapter != null) {
                            travelAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //当前页
                pageIndex++;
                params.put("pageIndex", pageIndex);
                //每页条数
                int pageCount = 10;
                params.put("pageCount", pageCount);
                //是否全部
                boolean isAll = false;
                params.put("isAll", pageCount);
                //收藏类型
                String collectionType = "0";
                params.put("collectionType", collectionType);
                //用户id
                String consumerId = plantState.getUser().getConsumerId() + "";
                params.put("consumerId", consumerId);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + pageIndex + pageCount + isAll + collectionType + consumerId;
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
                //随机数
                params.put("random", random);
                params.put("sign", signEncrypt);
                httpRequestWrap.send(PlantAddress.USER_COLLECTION, params);
            }
        });
    }

    private void initBack() {
        if (plantState.getTravelCollectionList().size() <= 0) {
            handler.sendEmptyMessage(0x01);
        } else {
            handler.sendEmptyMessage(0x00);
        }
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记收藏解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取游记收藏解密成功---" + data);
                plantState.getTravelCollectionList().clear();
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                List<TravelCollection> travelCollectionList = gson.fromJson(data, new TypeToken<List<TravelCollection>>() {
                }.getType());
                plantState.setTravelCollectionList(travelCollectionList);
                if (travelAdapter != null) {
                    travelAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(0x00);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //当前页
        pageIndex = 1;
        params.put("pageIndex", pageIndex);
        //每页条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //是否全部
        boolean isAll = false;
        params.put("isAll", pageCount);
        //收藏类型
        String collectionType = "0";
        params.put("collectionType", collectionType);
        //用户id
        String consumerId = plantState.getUser().getConsumerId() + "";
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + pageIndex + pageCount + isAll + collectionType + consumerId;
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
        //随机数
        params.put("random", random);
        params.put("sign", signEncrypt);
        httpRequestWrap.send(PlantAddress.USER_COLLECTION, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position", position);
        intent.putExtra("type", 2);
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
     * 收藏通知更改
     * @param e
     */
    public void onEventMainThread(TravelColltionFind e){
        initPull();
    }

    /**
     * 取消
     *
     * @param one
     */
    public void onEventMainThread(TravelCollectionOneFind one) {
        travelPosition = one.position;
        PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
        promptDilogFragment.initPrompt("是否取消收藏的游记", 10);
        promptDilogFragment.show(getFragmentManager(), "是否取消收藏的游记");
    }

    /**
     * 取消收藏结果
     */
    public void onEventMainThread(TravelCollectionTwoFind two) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.collection_not), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isCode(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记取消收藏解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取游记取消收藏解密成功---" + data);
                initPull();
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //游记取消收藏
        int collectionType = 0;
        params.put("collectionType", collectionType);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //收藏对象id
        int collectedIds = plantState.getTravelCollectionList().get(travelPosition).getCollectedId();
        params.put("collectedId", collectedIds);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + collectionType + consumerId + collectedIds;
        Log.e(TAG, "---取消收藏明文---" + random + "---" + collectionType + "---" + consumerId + "---" + collectedIds);
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
        httpRequestWrap.send(PlantAddress.ASK_COLLTION, params);
    }

    /**
     * 分享
     *
     * @param s
     */
    public void onEventMainThread(TravelShareFind s) {
    }
}
