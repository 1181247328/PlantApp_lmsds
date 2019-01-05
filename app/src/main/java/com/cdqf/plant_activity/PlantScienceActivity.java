package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.PlantscienceAdapter;
import com.cdqf.plant_class.Plant;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.PlantCollectionFind;
import com.cdqf.plant_find.ScienceCollectionFind;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;
import com.handmark.pulltorefresh.library.StaggeredGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 科普内容
 * Created by liu on 2017/10/20.
 */

public class PlantScienceActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PlantScienceActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlPlantscienceReturn = null;

    //搜索
    private XEditText xetPlantscienceSearch = null;

    //植物科普
    private PullToRefreshStaggeredGridView prsgPlantscienceContent = null;

    private StaggeredGridView sgvPlantscienceContent = null;

    private PlantscienceAdapter plantscienceAdapter = null;

    private Gson gson = new Gson();

    private int pageIndex = 1;

    private int plantPostion = 0;

    private String searchKey = "";

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
        setContentView(R.layout.activity_plantscience);

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

    public void initAgo() {
        context = this;
        httpRequestWrap = new HttpRequestWrap(context);
        if (!eventBus.isRegistered(context)) {
            eventBus.register(this);
        }
    }


    private void initView() {
        rlPlantscienceReturn = (RelativeLayout) this.findViewById(R.id.rl_plantscience_return);
        xetPlantscienceSearch = (XEditText) this.findViewById(R.id.xet_plantscience_search);
        prsgPlantscienceContent = (PullToRefreshStaggeredGridView) this.findViewById(R.id.sgv_plantscience_content);
        sgvPlantscienceContent = prsgPlantscienceContent.getRefreshableView();
    }

    private void initAdapter() {
        plantscienceAdapter = new PlantscienceAdapter(context);
        sgvPlantscienceContent.setAdapter(plantscienceAdapter);
    }

    private void initListener() {
        rlPlantscienceReturn.setOnClickListener(this);
        prsgPlantscienceContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StaggeredGridView>() {
            /**
             * 下拉刷新
             * @param refreshView
             */
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                plants(true);
            }

            /**
             * 上拉加载
             * @param refreshView
             */
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<StaggeredGridView> refreshView) {
                plants(false);
            }
        });
        sgvPlantscienceContent.setOnItemClickListener(new StaggeredGridView.OnItemClickListener() {
            @Override
            public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
                Log.e(TAG, "---位置---" + position);
                forIntent(PlantsDetailsActivity.class, position);
            }
        });

        /****搜索*****/
        xetPlantscienceSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKey = xetPlantscienceSearch.getText().toString();
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取植物解密失败---" + data);
                            prsgPlantscienceContent.onRefreshComplete();
                            return;
                        }
                        Log.e(TAG, "---获取植物解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(context, plantState.getPlantString(context, R.string.more), true, 0);
                            return;
                        }
                        plantState.setPlant(null);
                        Plant plant = new Plant();
                        plant = gson.fromJson(data, Plant.class);
                        plantState.setPlant(plant);
                        if (plantscienceAdapter != null) {
                            plantscienceAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //用户id
                String consumerId = plantState.getUser().getConsumerId() + "";
                params.put("consumerId", consumerId);
                //当前页
                pageIndex = 1;
                params.put("pageIndex", pageIndex);
                //每页条数
                int pageCount = 10;
                params.put("pageCount", pageCount);
                //景点id
                int scenicSpotId = 0;
                params.put("scenicSpotId", scenicSpotId);
                //分类
                int typeId = 0;
                params.put("typeId", typeId);
                //搜索关键字
                params.put("searchKey", searchKey);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" +consumerId+ pageIndex + pageCount + scenicSpotId + typeId + searchKey;
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
                httpRequestWrap.send(PlantAddress.ASK_PLANTS, params);
            }
        });
    }

    private void initBack() {

    }

    private void forIntent(Class<?> activity, int position) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("position", position);
        intent.putExtra("type", 0);
        startActivity(intent);
    }

    /**
     * 植物
     */
    private void plants(final boolean isPull) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取植物解密失败---" + data);
                    prsgPlantscienceContent.onRefreshComplete();
                    return;
                }
                Log.e(TAG, "---获取植物解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.more), true, 0);
                    return;
                }
                prsgPlantscienceContent.onRefreshComplete();
                Plant plant = new Plant();
                if (isPull) {
                    plant = gson.fromJson(data, Plant.class);
                    plantState.setPlant(plant);
                } else {
                    plant = gson.fromJson(data, Plant.class);
                    plantState.getPlant().getList().addAll(plant.getList());
                }
                if (plantscienceAdapter != null) {
                    plantscienceAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id
        String consumerId = plantState.getUser().getConsumerId() + "";
        params.put("consumerId", consumerId);
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
        //景点id
        int scenicSpotId = 0;
        params.put("scenicSpotId", scenicSpotId);
        //分类
        int typeId = 0;
        params.put("typeId", typeId);
        //搜索关键字
        params.put("searchKey", searchKey);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + consumerId + pageIndex + pageCount + scenicSpotId + typeId + searchKey;
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
        httpRequestWrap.send(PlantAddress.ASK_PLANTS, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_plantscience_return:
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
        eventBus.unregister(context);
    }

    /**
     * 是否收藏植物
     *
     * @param s
     */
    public void onEventMainThread(ScienceCollectionFind s) {
        plantPostion = s.position;
        PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
        promptDilogFragment.initPrompt("是否收藏该植物!", 1);
        promptDilogFragment.show(getSupportFragmentManager(), "是否收藏植物");
    }

    /**
     * 收藏植物
     *
     * @param p
     */
    public void onEventMainThread(PlantCollectionFind p) {
        if (!plantState.isLogin()) {
            plantState.initToast(context, plantState.getPlantString(context, R.string.is_login), true, 0);
            return;
        }
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isCode(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取新闻解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取新闻解密成功---" + data);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //植物收藏
        int collectionType = 2;
        params.put("pageIndex", collectionType);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //收藏对象id
        int collectedIds = plantState.getPlant().getList().get(plantPostion).getBotanyId();
        params.put("collectedIds", collectedIds);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + collectionType + consumerId + collectedIds;
        Log.e(TAG, "---明文---" + random + "---" + collectionType + "---" + consumerId + "---" + collectedIds);
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
        httpRequestWrap.send(PlantAddress.ASK_COLLTION, params);
    }
}
