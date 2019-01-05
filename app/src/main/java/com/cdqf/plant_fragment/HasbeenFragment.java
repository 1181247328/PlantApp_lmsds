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
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.StrategyDetailsActivity;
import com.cdqf.plant_activity.TravelTitleActivity;
import com.cdqf.plant_adapter.HasbeenAdapter;
import com.cdqf.plant_class.HasPublished;
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

import static com.cdqf.plant_lmsd.R.id.ll_hasbeen_nocontext;

/**
 * 已发表
 * Created by liu on 2017/11/16.
 */

public class HasbeenFragment extends Fragment implements View.OnClickListener {
    private String TAG = HasbeenFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //刷新
    private PullToRefreshLayout ptrlHasbeenPull = null;

    //无内容
    private LinearLayout llHasbeenNocontext = null;

    private ListView lvHasbeenlList = null;

    private HasbeenAdapter hasbeenAdapter = null;

    private RelativeLayout rlHasbeenPublished = null;

    private int pageIndex = 0;

    private Gson gson = new Gson();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    ptrlHasbeenPull.setVisibility(View.GONE);
                    llHasbeenNocontext.setVisibility(View.VISIBLE);
                    break;
                case 0x01:
                    ptrlHasbeenPull.setVisibility(View.VISIBLE);
                    llHasbeenNocontext.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hasbeen, null);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        httpRequestWrap = new HttpRequestWrap(getContext());
    }

    private void initView() {
        ptrlHasbeenPull = (PullToRefreshLayout) view.findViewById(R.id.ptrl_hasbeen_pull);
        llHasbeenNocontext = (LinearLayout) view.findViewById(ll_hasbeen_nocontext);
        lvHasbeenlList = (ListView) ptrlHasbeenPull.getPullableView();
        rlHasbeenPublished = (RelativeLayout) view.findViewById(R.id.rl_hasbeen_published);
    }

    private void initAdapter() {
        hasbeenAdapter = new HasbeenAdapter(getContext());
        lvHasbeenlList.setAdapter(hasbeenAdapter);
    }

    private void initListener() {
        rlHasbeenPublished.setOnClickListener(this);
        ptrlHasbeenPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                initPull(true);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                //上拉加载
                initPull(false);
            }
        });
        lvHasbeenlList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(StrategyDetailsActivity.class, position);
            }
        });
    }

    private void initBack() {
        if (plantState.getHasPublishedList().size() <= 0) {
            handler.sendEmptyMessage(0x00);
        } else {
            handler.sendEmptyMessage(0x01);
        }
        inintPullJSON();
    }

    private void inintPullJSON() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记列表解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取游记列表解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.more), true, 0);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                List<HasPublished> hasList = gson.fromJson(data, new TypeToken<List<HasPublished>>() {
                }.getType());
                plantState.getHasPublishedList().clear();
                plantState.setHasPublishedList(hasList);
                if (hasbeenAdapter != null) {
                    hasbeenAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(0x01);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //当前页
        pageIndex = 1;
        params.put("pageIndex", pageIndex);
        //每页条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //用户id类型
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //保存类型
        int travelSaveType = 0;
        params.put("travelSaveType", travelSaveType);
        //CId
        int CId = plantState.getUser().getConsumerId();
        params.put("CId", CId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + pageIndex + pageCount + consumerId + travelSaveType + CId;
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
        httpRequestWrap.send(PlantAddress.STRATE_LIST, params);
    }

    private void initPull(final boolean isPull) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(),new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记列表解密失败---" + data);
                    ptrlHasbeenPull.refreshFinish(PullToRefreshLayout.FAIL);
                    return;
                }
                Log.e(TAG, "---获取游记列表解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    ptrlHasbeenPull.refreshFinish(PullToRefreshLayout.SUCCEED);
                    plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.more), true, 0);
                    return;
                }
                ptrlHasbeenPull.refreshFinish(PullToRefreshLayout.SUCCEED);
                data = JSON.parseObject(data).getString("list");
                List<HasPublished> hasList = gson.fromJson(data, new TypeToken<List<HasPublished>>() {
                }.getType());
                if (isPull) {
                    plantState.getHasPublishedList().clear();
                    plantState.setHasPublishedList(hasList);
                } else {
                    plantState.getHasPublishedList().addAll(hasList);
                }
                if (hasbeenAdapter != null) {
                    hasbeenAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(0x01);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        if (isPull) {
            //当前页
            pageIndex = 1;
        } else {
            pageIndex++;
        }
        params.put("pageIndex", pageIndex);
        //每页条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //用户id类型
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //保存类型
        int travelSaveType = 0;
        params.put("travelSaveType", travelSaveType);
        //CId
        int CId = plantState.getUser().getConsumerId();
        params.put("CId", CId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + pageIndex + pageCount + consumerId + travelSaveType + CId;
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
        httpRequestWrap.send(PlantAddress.STRATE_LIST, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position", position);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发表
            case R.id.rl_hasbeen_published:
                initIntent(TravelTitleActivity.class);
                break;
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
    }
}
