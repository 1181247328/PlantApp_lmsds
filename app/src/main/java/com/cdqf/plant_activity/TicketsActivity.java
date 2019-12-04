package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.TicketsAdapter;
import com.cdqf.plant_class.Tickets;
import com.cdqf.plant_find.TicketsFind;
import com.cdqf.plant_fragment.TicketsDilogFragment;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_okhttp.OKHttpRequestWrap;
import com.cdqf.plant_okhttp.OnHttpRequest;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.VerticalSwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingchen.pulltorefresh.PullToRefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 我的门票
 */
public class TicketsActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = TicketsActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    @BindView(R.id.vsrl_order_pull)
    public VerticalSwipeRefreshLayout vsrlOrderPull = null;

    //返回
    @BindView(R.id.rl_myorder_return)
    public RelativeLayout rlMyorderReturn = null;

    //刷新
    @BindView(R.id.rl_orders_bar)
    public RelativeLayout rlOrdersBar = null;

    //当没有订单的时候显示
    @BindView(R.id.ll_allorder_there)
    public LinearLayout llAllorderThere = null;

    //刷新器
    @BindView(R.id.ptrl_allorder_pull)
    public PullToRefreshLayout ptrlAllorderPull = null;

    private ListView lvGoodsevList = null;

    private TicketsAdapter ticketsAdapter = null;

    private int type = 2;

    private boolean isPull = false;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //加载布局
        setContentView(R.layout.activity_tickets);

//        StaturBar.setStatusBar(this, R.color.strategy_item_published);

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
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        lvGoodsevList = (ListView) ptrlAllorderPull.getPullableView();
    }

    private void initAdapter() {
        ticketsAdapter = new TicketsAdapter(context);
        lvGoodsevList.setAdapter(ticketsAdapter);
    }

    private void initListener() {
        ptrlAllorderPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
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
                            Log.e(TAG, "---我的门票解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        if (TextUtils.equals(data, "1001")) {
                            Log.e(TAG, "---我的门票列表为空---" + data);
                            plantState.initToast(context, "没有更多了", true, 0);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        type++;
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---我的门票解密---" + data);
                        data = JSON.parseObject(data).getString("list");
                        List<Tickets> ticketsList = gson.fromJson(data, new TypeToken<List<Tickets>>() {
                        }.getType());
                        if (ticketsList.size() <= 0) {
                            plantState.initToast(context, "没有更多了", true, 0);
                            return;
                        }
                        plantState.initToast(context, "加载完成", true, 0);
                        plantState.getTicketsList().addAll(ticketsList);
                        if (ticketsAdapter != null) {
                            ticketsAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //用户id
                int commenterId = plantState.getUser().getConsumerId();
                params.put("commenterId", commenterId);
                //当前页
                int pageIndex = type;
                params.put("pageIndex", pageIndex);
                //当前页的数量
                int pageCount = 10;
                params.put("pageCount", pageCount);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + commenterId + pageIndex + pageCount;
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
                httpRequestWrap.send(PlantAddress.TICKETS, params);
            }
        });

        vsrlOrderPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initFish(false);
            }
        });

        lvGoodsevList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View firstView = view.getChildAt(firstVisibleItem);
                // 当firstVisibleItem是第0位。如果firstView==null说明列表为空，需要刷新;或者top==0说明已经到达列表顶部, 也需要刷新
                if (firstVisibleItem == 0 && (firstView == null || firstView.getTop() == view.getPaddingTop())) {
                    if (isPull) {
                        vsrlOrderPull.setEnabled(true);
                    } else {
                        vsrlOrderPull.setEnabled(false);
                    }
                } else {
                    vsrlOrderPull.setEnabled(false);
                }
            }
        });
    }

    private void initBack() {
        ptrlAllorderPull.setPullDownEnable(false);
        vsrlOrderPull.setEnabled(false);
        initFish(false);
    }

    /**
     * 我的门票
     */
    private void initFish(boolean isToast) {
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id
        int commenterId = plantState.getUser().getConsumerId();
        params.put("consumerId", commenterId);
        //当前页
        int pageIndex = 1;
        params.put("pageIndex", pageIndex);
        //当前页的数量
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + commenterId;
        Log.e(TAG, "---明文---" + sign + "---" + commenterId);
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
        okHttpRequestWrap.post(PlantAddress.TICKETS, isToast, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                if (vsrlOrderPull != null) {
                    isPull = true;
                    vsrlOrderPull.setEnabled(true);
                    vsrlOrderPull.setRefreshing(false);
                }
                String data = Errer.evaluations(context, response);
                if (data == null) {
                    Log.e(TAG, "---我的门票解密失败---" + data);
                    rlOrdersBar.setVisibility(View.GONE);
                    llAllorderThere.setVisibility(View.VISIBLE);
                    ptrlAllorderPull.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.equals(data, "1001")) {
                    Log.e(TAG, "---我的门票列表为空---" + data);
                    rlOrdersBar.setVisibility(View.GONE);
                    llAllorderThere.setVisibility(View.VISIBLE);
                    ptrlAllorderPull.setVisibility(View.GONE);
                    return;
                }
                type = 2;
                Log.e(TAG, "---我的门票解密---" + data);
                rlOrdersBar.setVisibility(View.GONE);
                llAllorderThere.setVisibility(View.GONE);
                ptrlAllorderPull.setVisibility(View.VISIBLE);
                plantState.getEvaluationList().clear();
                data = JSON.parseObject(data).getString("list");
                List<Tickets> ticketsList = gson.fromJson(data, new TypeToken<List<Tickets>>() {
                }.getType());
                if (ticketsList.size() <= 0) {
                    rlOrdersBar.setVisibility(View.GONE);
                    llAllorderThere.setVisibility(View.VISIBLE);
                    ptrlAllorderPull.setVisibility(View.GONE);
                    return;
                }
                plantState.setTicketsList(ticketsList);
                if (ticketsAdapter != null) {
                    ticketsAdapter.notifyDataSetChanged();
                }
            }

//            @Override
//            public void onOkHttpCode(String code, int state) {
//                Log.e(TAG, "---onOkHttpCode---" + code);
//                if (vsrlOrderPull != null) {
//                    isPull = true;
//                    vsrlOrderPull.setEnabled(true);
//                    vsrlOrderPull.setRefreshing(false);
//                }
//                rlOrdersBar.setVisibility(View.GONE);
//                llAllorderThere.setVisibility(View.VISIBLE);
//                ptrlAllorderPull.setVisibility(View.GONE);
//                plantState.initToast(context, code, true, 0);
//            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
                if (vsrlOrderPull != null) {
                    isPull = true;
                    vsrlOrderPull.setEnabled(true);
                    vsrlOrderPull.setRefreshing(false);
                }
                rlOrdersBar.setVisibility(View.GONE);
                llAllorderThere.setVisibility(View.VISIBLE);
                ptrlAllorderPull.setVisibility(View.GONE);
                plantState.initToast(context, error, true, 0);
            }
        });
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @OnClick({R.id.rl_myorder_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_myorder_return:
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
        eventBus.unregister(this);
    }

    /**
     * @param t
     */
    public void onEventMainThread(TicketsFind t) {
        TicketsDilogFragment ticketsDilogFragment = new TicketsDilogFragment();
        ticketsDilogFragment.setPosition(t.position);
        ticketsDilogFragment.show(getSupportFragmentManager(), "验证码");
    }

}
