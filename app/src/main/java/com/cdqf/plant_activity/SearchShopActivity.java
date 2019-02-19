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

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.SearchShopAdapter;
import com.cdqf.plant_class.SearchShop;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_okhttp.OKHttpHanlder;
import com.cdqf.plant_okhttp.OKHttpRequestWrap;
import com.cdqf.plant_okhttp.OnHttpRequest;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 搜索商品
 * Created by liu on 2018/1/4.
 */

public class SearchShopActivity extends BaseActivity {
    private String TAG = SearchShopActivity.class.getSimpleName();

    public static SearchShopActivity searchShopActivity = null;

    private Context context = null;

    private HttpRequestWrap httpRequestWrap = null;

    private PlantState plantState = PlantState.getPlantState();

    private Gson gson = new Gson();

    private int page = 2;

    //返回
    @BindView(R.id.ll_searchshop_scan)
    public LinearLayout llSearchshopScan = null;

    //搜索商品
    @BindView(R.id.xet_searchshop_search)
    public XEditText xetSearchshopSearch = null;

    //无搜索商品显示
    @BindView(R.id.ll_searchshop_there)
    public LinearLayout llSearchshopThere = null;

    //搜索商品集合区
    @BindView(R.id.ptrl_searchshop_pull)
    public PullToRefreshLayout ptrlSearchshopPull = null;

    private GridView gvSearchshopList = null;

    private SearchShopAdapter searchShopAdapter = null;

    //搜索关键字
    private String searchKey = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //有商品的情况显示
                case 0x001:
                    ptrlSearchshopPull.setVisibility(View.VISIBLE);
                    llSearchshopThere.setVisibility(View.GONE);
                    break;
                //无商品的情况显示
                case 0x002:
                    ptrlSearchshopPull.setVisibility(View.GONE);
                    llSearchshopThere.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_searchshop);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.plant_background));
        }
        initAgo();

        initAdapter();

        initListener();

        initBack();
    }

    private void initAgo() {
        context = this;
        ButterKnife.bind(this);
        searchShopActivity = this;
        httpRequestWrap = new HttpRequestWrap(context);
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        gvSearchshopList = (GridView) ptrlSearchshopPull.getPullableView();
    }

    private void initAdapter() {
        searchShopAdapter = new SearchShopAdapter(context);
        gvSearchshopList.setAdapter(searchShopAdapter);
    }

    private void initListener() {
        xetSearchshopSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKey = xetSearchshopSearch.getText().toString().trim();
                if (searchKey.length() <= 0) {
                    plantState.getSearchList().clear();
                    if (searchShopAdapter != null) {
                        searchShopAdapter.notifyDataSetChanged();
                    }
                    handler.sendEmptyMessage(0x002);
                    return;
                }
                //搜索
                initPull();
            }
        });
        gvSearchshopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(GoodsDetailsActivity.class, position);
            }
        });
        ptrlSearchshopPull.setPullDownEnable(false);
        ptrlSearchshopPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            //下拉刷新
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
            }

            //加载更多
            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                Map<String, Object> params = new HashMap<String, Object>();
                //商品分类ID
                int cateId = 0;
                params.put("cateId", cateId);
                //排序方式
                int orderBy = 0;
                params.put("orderBy", orderBy);
                //当前页数
                int pageIndex = page;
                params.put("pageIndex", pageIndex);
                //每页条数
                int pageCount = 10;
                params.put("pageCount", pageCount);
                //搜索关键字
                params.put("searchKey", searchKey);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + cateId + orderBy + pageIndex + pageCount + searchKey;
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
                OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
                okHttpRequestWrap.post(PlantAddress.SHOP_LIST, false, "请稍候", params, new OnHttpRequest() {
                    @Override
                    public void onOkHttpResponse(String response, int id) {
                        Log.e(TAG, "---商品列表---" + response);
                        String data = OKHttpHanlder.isOKHttpResult(context, response);
                        if (data == null) {
                            Log.e(TAG, "---搜索商品列表解密失败---" + data);
                            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG, "---搜索商品列表解密---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(context, "没有更多了", true, 0);
                            pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        page++;
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        data = JSON.parseObject(data).getString("list");
                        List<SearchShop> searchList = gson.fromJson(data, new TypeToken<List<SearchShop>>() {
                        }.getType());
                        plantState.getSearchList().addAll(searchList);
                        if (searchShopAdapter != null) {
                            searchShopAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onOkHttpError(String error) {
                        Log.e(TAG, "---onOkHttpError---" + error);
                    }
                });
            }
        });
    }

    private void initBack() {
        if (plantState.getSearchList().size() <= 0) {
            handler.sendEmptyMessage(0x002);
        } else {
            handler.sendEmptyMessage(0x001);
        }
    }

    /**
     * 搜索
     */
    private void initPull() {
        Map<String, Object> params = new HashMap<String, Object>();
        //商品分类ID
        int cateId = 0;
        params.put("cateId", cateId);
        //排序方式
        int orderBy = 0;
        params.put("orderBy", orderBy);
        //当前页数
        int pageIndex = 1;
        params.put("pageIndex", pageIndex);
        //每页条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //搜索关键字
        params.put("searchKey", searchKey);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + cateId + orderBy + pageIndex + pageCount + searchKey;
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

        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(context);
        okHttpRequestWrap.post(PlantAddress.SHOP_LIST, true, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---商品列表---" + response);
                String data = OKHttpHanlder.isOKHttpResult(context, response);
                if (data == null) {
                    Log.e(TAG, "---搜索商品列表解密失败---" + data);
                    if (plantState.getGoodlist().size() > 0) {
                        plantState.getGoodlist().clear();
                    }
                    if (searchShopAdapter != null) {
                        searchShopAdapter.notifyDataSetChanged();
                    }
                    handler.sendEmptyMessage(0x002);
                    return;
                }
                Log.e(TAG, "---搜索商品列表解密---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x002);
                    return;
                }
                data = JSON.parseObject(data).getString("list");
                plantState.getSearchList().clear();
                handler.sendEmptyMessage(0x001);
                List<SearchShop> searchList = gson.fromJson(data, new TypeToken<List<SearchShop>>() {
                }.getType());
                plantState.setSearchList(searchList);
                if (searchShopAdapter != null) {
                    searchShopAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("type", 2);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    @OnClick({R.id.ll_searchshop_scan})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.ll_searchshop_scan:
                plantState.getSearchList().clear();
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        plantState.getSearchList().clear();
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
