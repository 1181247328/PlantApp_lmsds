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
import android.widget.GridView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.GoodsDetailsActivity;
import com.cdqf.plant_adapter.ShopColltionAdapter;
import com.cdqf.plant_class.ShopColltion;
import com.cdqf.plant_find.ShopColltionFind;
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
 * 商品收藏
 * Created by liu on 2017/11/24.
 */

public class ShopColltionFragment extends Fragment{

    private String TAG = ShopColltionFragment.class.getSimpleName();

    private View view = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    //刷新器
    private PullToRefreshLayout ptrlShopcolltionList = null;

    private GridView gvShopcolltionList = null;

    private ShopColltionAdapter shopColltionAdapter = null;

    private LinearLayout llCollectionNocontext = null;

    private HttpRequestWrap httpRequestWrap = null;

    private int pageIndex = 0;

    private Gson gson = new Gson();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 0x00:
                    ptrlShopcolltionList.setVisibility(View.VISIBLE);
                    llCollectionNocontext.setVisibility(View.GONE);
                    break;
                case 0x01:
                    ptrlShopcolltionList.setVisibility(View.GONE);
                    llCollectionNocontext.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_shopcolltion,null);
        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        httpRequestWrap = new HttpRequestWrap(getContext());
        if(!eventBus.isRegistered(this)){
            eventBus.register(this);
        }
    }

    private void initView() {
        ptrlShopcolltionList = (PullToRefreshLayout) view.findViewById(R.id.ptrl_shopcolltion_list);
        gvShopcolltionList = (GridView) ptrlShopcolltionList.getPullableView();
        llCollectionNocontext = (LinearLayout) view.findViewById(R.id.ll_collection_nocontext);
    }

    private void initAdapter() {
        shopColltionAdapter = new ShopColltionAdapter(getContext());
        gvShopcolltionList.setAdapter(shopColltionAdapter);
    }

    private void initListener() {
        gvShopcolltionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(GoodsDetailsActivity.class,position);
            }
        });
        ptrlShopcolltionList.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            //下拉刷新
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取游记收藏解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG, "---获取游记收藏解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            handler.sendEmptyMessage(0x00);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        data = JSON.parseObject(data).getString("list");
                        List<ShopColltion> shopColltionList = gson.fromJson(data, new TypeToken<List<ShopColltion>>() {
                        }.getType());
                        plantState.setShopColltionList(shopColltionList);
                        if (shopColltionAdapter != null) {
                            shopColltionAdapter.notifyDataSetChanged();
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
                String collectionType = "1";
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

            //加载更多
            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取游记收藏解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG, "---获取游记收藏解密成功---" + data);
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        data = JSON.parseObject(data).getString("list");
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.more), true, 0);
                            return;
                        }
                        List<ShopColltion> shopColltionList = gson.fromJson(data, new TypeToken<List<ShopColltion>>() {
                        }.getType());
                        plantState.getShopColltionList().addAll(shopColltionList);
                        if (shopColltionAdapter != null) {
                            shopColltionAdapter.notifyDataSetChanged();
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
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取商品收藏解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取商品收藏解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                List<ShopColltion> shopColltionList = gson.fromJson(data, new TypeToken<List<ShopColltion>>() {
                }.getType());
                plantState.setShopColltionList(shopColltionList);
                if (shopColltionAdapter != null) {
                    shopColltionAdapter.notifyDataSetChanged();
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
        String collectionType = "1";
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

    private void initIntent(Class<?> activity,int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position",position);
        intent.putExtra("type",3);
        startActivity(intent);
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
     * 取消收藏通知
     * @param s
     */
    public void onEventMainThread(ShopColltionFind s){
        initPull();
    }
}
