package com.cdqf.plant_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.StrategyDetailsActivity;
import com.cdqf.plant_activity.TravelTitleActivity;
import com.cdqf.plant_adapter.StrategyAdapter;
import com.cdqf.plant_class.Strategy;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.PlantCollectionFind;
import com.cdqf.plant_find.ShareFind;
import com.cdqf.plant_find.StrategyFind;
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
 * 游记攻略
 * Created by liu on 2017/10/19.
 */

public class StrategyFragment extends Fragment implements View.OnClickListener {

    private String TAG = StrategyFragment.class.getSimpleName();

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    private View view = null;

    private RelativeLayout rlTrategyPublished = null;

    private PullToRefreshLayout ptrlStrategyPull = null;

    private ListView lvStrategyList = null;

    private StrategyAdapter strategyAdapter = null;

    private int pageIndex = 1;

    private int collectionPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_strategy, null);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        httpRequestWrap = new HttpRequestWrap(getContext());
    }

    private void initView() {
        rlTrategyPublished = (RelativeLayout) view.findViewById(R.id.rl_trategy_published);
        ptrlStrategyPull = (PullToRefreshLayout) view.findViewById(R.id.ptrl_strategy_pull);
        lvStrategyList = (ListView) ptrlStrategyPull.getPullableView();
    }

    private void initAdapter() {
        strategyAdapter = new StrategyAdapter(getContext());
        lvStrategyList.setAdapter(strategyAdapter);
    }

    private void initListener() {
        rlTrategyPublished.setOnClickListener(this);
        ptrlStrategyPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                initStrategyContext(true);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                initStrategyContext(false);
            }
        });
        lvStrategyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(StrategyDetailsActivity.class, position);
            }
        });
    }

    private void initBack() {
        initStrategyContext(true);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position", position);
        intent.putExtra("type",0);
        startActivity(intent);
    }

    private void initStrategyContext(final boolean isPull) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记列表解密失败---" + data);
                    ptrlStrategyPull.refreshFinish(PullToRefreshLayout.FAIL);
                    return;
                }
                Log.e(TAG, "---获取游记列表解密成功---" + data);
                if(TextUtils.equals(data,"1001")){
                    ptrlStrategyPull.refreshFinish(PullToRefreshLayout.SUCCEED);
                    plantState.initToast(getContext(),plantState.getPlantString(getContext(),R.string.more),true,0);
                    return;
                }
                ptrlStrategyPull.refreshFinish(PullToRefreshLayout.SUCCEED);
                data = JSON.parseObject(data).getString("list");
                List<Strategy> strategyList = gson.fromJson(data, new TypeToken<List<Strategy>>() {
                }.getType());
                if(isPull) {
                    plantState.getStrategyList().clear();
                    plantState.setStrategyList(strategyList);
                } else {
                    plantState.getStrategyList().addAll(strategyList);
                }
                if (strategyAdapter != null) {
                    strategyAdapter.notifyDataSetChanged();
                }
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
        int consumerId = 0;
        params.put("consumerId", consumerId);
        //保存类型
        int travelSaveType = 0;
        params.put("travelSaveType", travelSaveType);
        //CId
        int CId = 0;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //发表游记
            case R.id.rl_trategy_published:
                if(!plantState.isLogin()){
                    plantState.initToast(getContext(),getContext().getResources().getString(R.string.is_login),true,0);
                    return;
                }
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
        eventBus.unregister(this);
    }

    /**
     * 是否收藏该游记
     *
     * @param s
     */
    public void onEventMainThread(StrategyFind s) {
        collectionPosition = s.position;
        PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
        promptDilogFragment.initPrompt("是否收藏该游记!", 2);
        promptDilogFragment.show(getFragmentManager(), "是否收藏该游记");
    }

    /**
     * 分享
     *
     * @param s
     */
    public void onEventMainThread(ShareFind s) {
        String content = plantState.getStrategyList().get(s.position).getTitle();
        plantState.initShar(getContext(), content);
//        ShareDilogFragment shareDilogFragment = new ShareDilogFragment();
//        shareDilogFragment.show(getFragmentManager(), "分享");
    }

    /**
     * 游记收藏
     * @param p
     */
    public void onEventMainThread(PlantCollectionFind p) {
        if (!plantState.isLogin()) {
            plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.is_login), true, 0);
            return;
        }
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.collection_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记收藏解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取游记收藏解密成功---" + data);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //游记收藏
        int collectionType = 0;
        params.put("collectionType", collectionType);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //收藏对象id
        int collectedId = plantState.getStrategyList().get(collectionPosition).getTravelId();
        params.put("collectedId", collectedId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + collectionType + consumerId + collectedId;
        Log.e(TAG, "---明文---" + random + "---" + collectionType + "---" + consumerId + "---" + collectedId);
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
}
