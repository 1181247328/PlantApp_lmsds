package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.GoodsAdapter;
import com.cdqf.plant_class.Commlist;
import com.cdqf.plant_lmsd.R;
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
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cdqf.plant_lmsd.R.id.tv_goods_comprehensive;
import static com.cdqf.plant_lmsd.R.id.tv_goods_high;
import static com.cdqf.plant_lmsd.R.id.tv_goods_low;
import static com.cdqf.plant_lmsd.R.id.tv_goods_price;
import static com.cdqf.plant_lmsd.R.id.tv_goods_sales;

/**
 * 商品列表
 * Created by liu on 2017/11/8.
 */

public class GoodsActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = GoodsActivity.class.getSimpleName();

    public static GoodsActivity goodsActivity = null;

    private Context context = null;

    private HttpRequestWrap httpRequestWrap = null;

    private PlantState plantState = PlantState.getPlantState();

    //返回
    private LinearLayout llGoodsSCan = null;

    //搜索
    private XEditText xetGoodsSearch = null;

    //消息
    private LinearLayout llGoodsMessage = null;

    //综合排序
    private TextView tvGoodsComprehensive = null;

    //销售
    private TextView tvGoodsSales = null;

    //价格
    private TextView tvGoodsPrice = null;

    //高
    private TextView tvGoodsHigh = null;

    //低
    private TextView tvGoodsLow = null;

    //下拉刷新器
    private PullToRefreshLayout ptrlGoodsList = null;

    private GridView gvGoodsList = null;

    private GoodsAdapter goodsAdapter = null;

    private LinearLayout llGoodsThere = null;

    private Gson gson = new Gson();

    //true = 按高排序
    private boolean isSelected = true;

    //0综合排序(默认) 1销量 2价格升序 3价格降序
    private int orderBy = 0;

    //0门票 1温泉 2酒店 3特产
    private int position = 0;

    private int page = 1;

    //搜索关键字
    private String searchKey = "";

    private Handler goodsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //有商品的情况显示
                case 0x001:
                    llGoodsThere.setVisibility(View.GONE);
                    ptrlGoodsList.setVisibility(View.VISIBLE);
                    break;
                //无商品的情况显示
                case 0x002:
                    llGoodsThere.setVisibility(View.VISIBLE);
                    ptrlGoodsList.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_goods);

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
        goodsActivity = this;
        httpRequestWrap = new HttpRequestWrap(context);
        //获得分类
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        Log.e(TAG, "---" + position);
    }

    private void initView() {
        llGoodsSCan = this.findViewById(R.id.ll_goods_scan);
        xetGoodsSearch = this.findViewById(R.id.xet_goods_search);
        llGoodsMessage = this.findViewById(R.id.ll_goods_message);
        tvGoodsComprehensive = this.findViewById(tv_goods_comprehensive);
        tvGoodsSales = this.findViewById(tv_goods_sales);
        tvGoodsPrice = this.findViewById(tv_goods_price);
        tvGoodsHigh = this.findViewById(tv_goods_high);
        tvGoodsLow = this.findViewById(tv_goods_low);
        ptrlGoodsList = this.findViewById(R.id.ptrl_goods_list);
        gvGoodsList = (GridView) ptrlGoodsList.getPullableView();
        llGoodsThere = this.findViewById(R.id.ll_goods_there);
    }

    private void initAdapter() {
        goodsAdapter = new GoodsAdapter(context);
        gvGoodsList.setAdapter(goodsAdapter);
    }

    private void initListener() {
        llGoodsSCan.setOnClickListener(this);
        gvGoodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(GoodsDetailsActivity.class, position, 1);
            }
        });
        xetGoodsSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //排序方式改为初始化的综合排序
                initBackOne(0);
                //搜索前先将页数调为1
                page = 1;
                //关键字
                searchKey = xetGoodsSearch.getText().toString();
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, "搜索中", new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---搜索商品列表解密失败---" + data);
                            //刷新失败
                            return;
                        }
                        Log.e(TAG, "---搜索商品刷新列表解密---" + data);
                        initJSON(data);
                    }
                }));
                params();
            }
        });
        llGoodsMessage.setOnClickListener(this);
        tvGoodsComprehensive.setOnClickListener(this);
        tvGoodsSales.setOnClickListener(this);
        tvGoodsPrice.setOnClickListener(this);
        ptrlGoodsList.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            //下拉刷新
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                //当前页
                page = 1;
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---商品列表解密失败---" + data);
                            //刷新失败
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---商品刷新列表解密---" + data);
                        initJSON(data);
                    }
                }));
                params();
            }

            //加载更多
            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                page++;
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---商品加载列表解密失败---" + data);
                            //刷新失败
                            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(context, "没有更多了", true, 0);
                            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---商品加载列表解密---" + data);
                        JSONObject dataJSON = JSON.parseObject(data);
                        JSONArray commList = dataJSON.getJSONArray("list");
                        if (commList.size() <= 0) {
                            plantState.initToast(context, context.getResources().getString(R.string.more), true, 0);
                            return;
                        }
                        String shop = dataJSON.getString("list");
                        List<Commlist> commGoodList = gson.fromJson(shop, new TypeToken<List<Commlist>>() {
                        }.getType());
                        plantState.getGoodlist().addAll(commGoodList);
                        if (goodsAdapter != null) {
                            goodsAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                params();
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        });
    }

    private void initBack() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "获取商品中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG, "---商品列表---" + result);
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---商品列表解密失败---" + data);
                    if (plantState.getGoodlist().size() > 0) {
                        plantState.getGoodlist().clear();
                    }
                    if (goodsAdapter != null) {
                        goodsAdapter.notifyDataSetChanged();
                    }
                    goodsHandler.sendEmptyMessage(0x002);
                    return;
                }
                if (TextUtils.equals(data, "1001")) {
                    plantState.initToast(context, "没有更多了", true, 0);
                    return;
                }
                Log.e(TAG, "---商品列表解密---" + data);
                initJSON(data);
            }
        }));
        params();
    }

    private void initBackOne(int position) {
        isSelected = true;
        tvGoodsPrice.setTextColor(ContextCompat.getColor(context, R.color.white));
        tvGoodsHigh.setTextColor(ContextCompat.getColor(context, R.color.white));
        tvGoodsLow.setTextColor(ContextCompat.getColor(context, R.color.white));
        switch (position) {
            case 0:
                if (this.orderBy == 0) {
                    return;
                }
                tvGoodsComprehensive.setTextColor(ContextCompat.getColor(context, R.color.selected_back));
                tvGoodsSales.setTextColor(ContextCompat.getColor(context, R.color.white));
                this.orderBy = 0;
                initBack();
                break;
            case 1:
                if (this.orderBy == 1) {
                    return;
                }
                tvGoodsComprehensive.setTextColor(ContextCompat.getColor(context, R.color.white));
                tvGoodsSales.setTextColor(ContextCompat.getColor(context, R.color.selected_back));
                this.orderBy = 1;
                initBack();
                break;
        }
    }

    private void initBackTwo() {
        tvGoodsComprehensive.setTextColor(ContextCompat.getColor(context, R.color.white));
        tvGoodsSales.setTextColor(ContextCompat.getColor(context, R.color.white));
        if (isSelected) {
            isSelected = false;
            tvGoodsPrice.setTextColor(ContextCompat.getColor(context, R.color.selected_back));
            tvGoodsHigh.setTextColor(ContextCompat.getColor(context, R.color.selected_back));
            tvGoodsLow.setTextColor(ContextCompat.getColor(context, R.color.white));
            this.orderBy = 2;
            initBack();
        } else {
            isSelected = true;
            tvGoodsPrice.setTextColor(ContextCompat.getColor(context, R.color.selected_back));
            tvGoodsHigh.setTextColor(ContextCompat.getColor(context, R.color.white));
            tvGoodsLow.setTextColor(ContextCompat.getColor(context, R.color.selected_back));
            this.orderBy = 3;
            initBack();
        }
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position, int type) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("position", position);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    private void initJSON(String dataJSOn) {
        JSONObject dataJSON = JSON.parseObject(dataJSOn);
        JSONArray commList = dataJSON.getJSONArray("list");
        if (commList.size() <= 0) {
            goodsHandler.sendEmptyMessage(0x002);
            plantState.initToast(context, context.getResources().getString(R.string.more), true, 0);
            return;
        }
        if (plantState.getGoodlist().size() > 0) {
            plantState.getGoodlist().clear();
        }
        goodsHandler.sendEmptyMessage(0x001);
        String shop = dataJSON.getString("list");
        List<Commlist> commGoodList = gson.fromJson(shop, new TypeToken<List<Commlist>>() {
        }.getType());
        plantState.setGoodlist(commGoodList);
        if (goodsAdapter != null) {
            goodsAdapter.notifyDataSetChanged();
        }
        if (goodsAdapter != null) {
            goodsAdapter.notifyDataSetChanged();
        }
    }

    private void params() {
        Map<String, Object> params = new HashMap<String, Object>();
        //商品分类ID
        Log.e(TAG, "当前的分类为---" + position);
        params.put("cateId", position);
        //排序方式
        Log.e(TAG, "当前的排序方式为---" + orderBy);
        params.put("orderBy", orderBy);
        //当前页数
        Log.e(TAG, "当前是第" + page + "页");
        params.put("pageIndex", page);
        //每页条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //搜索关键字
        params.put("searchKey", searchKey);
        Log.e(TAG, "---关键字---" + searchKey);
        //随机数
        int random = plantState.getRandom();
        Log.e(TAG, "随机" + random);
        String sign = random + "" + position + orderBy + page + pageCount + searchKey + "";
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
        httpRequestWrap.send(PlantAddress.SHOP_LIST, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.ll_goods_scan:
                finish();
                break;
            //消息
            case R.id.ll_goods_message:
                break;
            //综合排序
            case tv_goods_comprehensive:
                initBackOne(0);
                break;
            //销售
            case tv_goods_sales:
                initBackOne(1);
                break;
            //价格
            case tv_goods_price:
                initBackTwo();
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
