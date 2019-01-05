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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.GoodsAllEvaluationAdapter;
import com.cdqf.plant_class.Evaluation;
import com.cdqf.plant_niengridview.ImageLoaderUtil;
import com.cdqf.plant_niengridview.NineGridTestModel;
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
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商品评论
 * Created by liu on 2017/12/6.
 */

public class GoodsAllEvaluationActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = GoodsAllEvaluationActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_goodsev_return)
    public RelativeLayout rlGoodsevReturn = null;

    //暂无评论
    @BindView(R.id.ll_goodsev_comments)
    public LinearLayout llGoodsevComments = null;

    //刷新器
    @BindView(R.id.ptrl_goodsev_pull)
    public PullToRefreshLayout ptrlGoodsevPull = null;

    private ListView lvGoodsevList = null;

    private GoodsAllEvaluationAdapter goodsAllEvaluationAdapter = null;

    private int commId = 0;

    private int type = 2;

    private Gson gson = new Gson();

    private List<NineGridTestModel> mList = new ArrayList<>();
    private String[] mUrls = new String[]{"http://d.hiphotos.baidu.com/image/h%3D200/sign=201258cbcd80653864eaa313a7dca115/ca1349540923dd54e54f7aedd609b3de9c824873.jpg",
            "http://img3.fengniao.com/forum/attachpics/537/165/21472986.jpg",
            "http://d.hiphotos.baidu.com/image/h%3D200/sign=ea218b2c5566d01661199928a729d498/a08b87d6277f9e2fd4f215e91830e924b999f308.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3445377427,2645691367&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2644422079,4250545639&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1444023808,3753293381&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=882039601,2636712663&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4119861953,350096499&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2437456944,1135705439&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3251359643,4211266111&fm=21&gp=0.jpg",
            "http://img4.duitang.com/uploads/item/201506/11/20150611000809_yFe5Z.jpeg",
            "http://img5.imgtn.bdimg.com/it/u=1717647885,4193212272&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2024625579,507531332&fm=21&gp=0.jpg"};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //无评论
                case 0x001:
                    llGoodsevComments.setVisibility(View.VISIBLE);
                    ptrlGoodsevPull.setVisibility(View.GONE);
                    break;
                //有评论
                case 0x002:
                    llGoodsevComments.setVisibility(View.GONE);
                    ptrlGoodsevPull.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_goodsallevaluation);

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
        ButterKnife.bind(this);
        httpRequestWrap = new HttpRequestWrap(context);
        Intent intent = getIntent();
        commId = intent.getIntExtra("position", 0);
    }

    private void initView() {
        lvGoodsevList = (ListView) ptrlGoodsevPull.getPullableView();
    }

    private void initAdapter() {
        lvGoodsevList.setOnScrollListener(new PauseOnScrollListener(ImageLoaderUtil.getImageLoader(context), true, true));
        goodsAllEvaluationAdapter = new GoodsAllEvaluationAdapter(context);
        lvGoodsevList.setAdapter(goodsAllEvaluationAdapter);
    }

    private void initListener() {
        rlGoodsevReturn.setOnClickListener(this);
        ptrlGoodsevPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                //下拉刷新操作
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---商品评论解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---商品评论解密---" + data);
                        handler.sendEmptyMessage(0x002);
                        plantState.getEvaluationList().clear();
                        data = JSON.parseObject(data).getString("commentList");
                        List<Evaluation> evaluationList = gson.fromJson(data, new TypeToken<List<Evaluation>>() {
                        }.getType());
                        for (Evaluation e : evaluationList) {
                            for (int i = 0; i < e.getPicList().size(); i++) {
                                e.getUrlList().add(e.getPicList().get(i).getHttpUrl());
                            }
                        }
                        plantState.setEvaluationList(evaluationList);
                        if (goodsAllEvaluationAdapter != null) {
                            goodsAllEvaluationAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //商品id
                params.put("commId", commId);
                //当前页
                int pageIndex = 1;
                params.put("pageIndex", pageIndex);
                //当前页的数量
                int pageCount = 10;
                params.put("pageCount", pageCount);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + commId + pageIndex + pageCount;
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
                httpRequestWrap.send(PlantAddress.SHOP_FIST, params);
            }

            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                // 加载更多操作
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---商品评论解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        if (TextUtils.equals(data, "1001")) {
                            Log.e(TAG, "---商品评论列表为空---" + data);
                            plantState.initToast(context, "没有更多了", true, 0);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        type++;
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---商品评论解密---" + data);
                        data = JSON.parseObject(data).getString("commentList");
                        List<Evaluation> evaluationList = gson.fromJson(data, new TypeToken<List<Evaluation>>() {
                        }.getType());
                        for (Evaluation e : evaluationList) {
                            for (int i = 0; i < e.getPicList().size(); i++) {
                                e.getUrlList().add(e.getPicList().get(i).getHttpUrl());
                            }
                        }
                        plantState.getEvaluationList().addAll(evaluationList);
                        if (goodsAllEvaluationAdapter != null) {
                            goodsAllEvaluationAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //商品id
                params.put("commId", commId);
                //当前页
                int pageIndex = type;
                params.put("pageIndex", pageIndex);
                //当前页的数量
                int pageCount = 10;
                params.put("pageCount", pageCount);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + commId + pageIndex + pageCount;
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
                httpRequestWrap.send(PlantAddress.SHOP_FIST, params);
            }
        });
    }

    private void initBack() {
        initFish();
    }

    /**
     * 评论
     */
    private void initFish() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---商品评论解密失败---" + data);
                    return;
                }
                if (TextUtils.equals(data, "1001")) {
                    Log.e(TAG, "---商品评论列表为空---" + data);
                    handler.sendEmptyMessage(0x001);
                    return;
                }
                Log.e(TAG, "---商品评论解密---" + data);
                handler.sendEmptyMessage(0x002);
                plantState.getEvaluationList().clear();
                data = JSON.parseObject(data).getString("commentList");
                List<Evaluation> evaluationList = gson.fromJson(data, new TypeToken<List<Evaluation>>() {
                }.getType());
                for (Evaluation e : evaluationList) {
                    for (int i = 0; i < e.getPicList().size(); i++) {
                        e.getUrlList().add(e.getPicList().get(i).getHttpUrl());
                    }
                }
                plantState.setEvaluationList(evaluationList);
                if (goodsAllEvaluationAdapter != null) {
                    goodsAllEvaluationAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //商品id
        params.put("commId", commId);
        //当前页
        int pageIndex = 1;
        params.put("pageIndex", pageIndex);
        //当前页的数量
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + commId + pageIndex + pageCount;
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
        httpRequestWrap.send(PlantAddress.SHOP_FIST, params);
    }


    private void initData() {
        NineGridTestModel model1 = new NineGridTestModel();
        model1.urlList.add(mUrls[0]);
        mList.add(model1);

        NineGridTestModel model2 = new NineGridTestModel();
        model2.urlList.add(mUrls[4]);
        mList.add(model2);

        NineGridTestModel model4 = new NineGridTestModel();
        for (int i = 0; i < mUrls.length; i++) {
            model4.urlList.add(mUrls[i]);
        }
        model4.isShowAll = false;
        mList.add(model4);

        NineGridTestModel model5 = new NineGridTestModel();
        for (int i = 0; i < mUrls.length; i++) {
            model5.urlList.add(mUrls[i]);
        }
        model5.isShowAll = true;//显示全部图片
        mList.add(model5);

        NineGridTestModel model6 = new NineGridTestModel();
        for (int i = 0; i < 9; i++) {
            model6.urlList.add(mUrls[i]);
        }
        mList.add(model6);

        NineGridTestModel model7 = new NineGridTestModel();
        for (int i = 3; i < 7; i++) {
            model7.urlList.add(mUrls[i]);
        }
        mList.add(model7);

        NineGridTestModel model8 = new NineGridTestModel();
        for (int i = 3; i < 6; i++) {
            model8.urlList.add(mUrls[i]);
        }
        mList.add(model8);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_goodsev_return:
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