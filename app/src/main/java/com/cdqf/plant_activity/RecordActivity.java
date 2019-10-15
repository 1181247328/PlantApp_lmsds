package com.cdqf.plant_activity;

import android.content.Context;
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
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.RecordAdapter;
import com.cdqf.plant_class.Record;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 兑换记录
 * Created by liu on 2018/1/11.
 */

public class RecordActivity extends BaseActivity {
    private String TAG = RecordActivity.class.getSimpleName();

    private Context context = null;

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_record_return)
    public RelativeLayout rlRecordReturn = null;

    //刷新器
    @BindView(R.id.ptrl_record_pull)
    public PullToRefreshLayout ptrlRecordPull = null;

    //无兑换记录显示
    @BindView(R.id.ll_record_there)
    public LinearLayout llRecordThere = null;

    private ListView lvRecordList = null;

    private RecordAdapter recordAdapter = null;

    private int pageIndex = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    llRecordThere.setVisibility(View.GONE);
                    ptrlRecordPull.setVisibility(View.VISIBLE);
                    break;
                case 0x01:
                    llRecordThere.setVisibility(View.VISIBLE);
                    ptrlRecordPull.setVisibility(View.GONE);
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

//        SDKInitializer.initialize(getApplicationContext());

        //加载布局
        setContentView(R.layout.activity_record);

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
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
    }

    private void initView() {
        lvRecordList = (ListView) ptrlRecordPull.getPullableView();
    }

    private void initAdapter() {
        recordAdapter = new RecordAdapter(context);
        lvRecordList.setAdapter(recordAdapter);
    }

    private void initListener() {
        ptrlRecordPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                //下拉刷新
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取订单待收货解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG, "---获取订单待收货解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            handler.sendEmptyMessage(0x01);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        data = JSON.parseObject(data).getString("list");
                        plantState.getRecordList().clear();
                        List<Record> recordList = gson.fromJson(data, new TypeToken<List<Record>>() {
                        }.getType());
                        plantState.setRecordList(recordList);
                        if (recordAdapter != null) {
                            recordAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                initPut(true);
            }

            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                //上拉加载
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取订单待收货解密失败---" + data);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG, "---获取订单待收货解密成功---" + data);
                        if (TextUtils.equals(data, "1001")) {
                            handler.sendEmptyMessage(0x01);
                            plantState.initToast(context, "没有更多了", true, 0);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        data = JSON.parseObject(data).getString("list");
                        List<Record> recordList = gson.fromJson(data, new TypeToken<List<Record>>() {
                        }.getType());
                        plantState.getRecordList().addAll(recordList);
                        if (recordAdapter != null) {
                            recordAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                initPut(false);
            }
        });
    }

    private void initBack() {
        initRecord();
    }

    private void initRecord() {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取积分兑换解密失败---" + data);
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                Log.e(TAG, "---获取积分兑换解密成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x01);
                    return;
                }
                handler.sendEmptyMessage(0x00);
                data = JSON.parseObject(data).getString("list");
                plantState.getRecordList().clear();
                List<Record> recordList = gson.fromJson(data, new TypeToken<List<Record>>() {
                }.getType());
                plantState.setRecordList(recordList);
                if (recordAdapter != null) {
                    recordAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //页数
        params.put("pageIndex", pageIndex);
        //条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //加载全部
        boolean isAll = false;
        params.put("isAll", isAll);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + pageIndex + pageCount + String.valueOf(isAll) + consumerId;
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
        httpRequestWrap.send(PlantAddress.COMMODITY, params);
    }

    private void initPut(boolean isIndex) {
        Map<String, Object> params = new HashMap<String, Object>();
        if(isIndex){
            pageIndex = 1;
        } else {
            pageIndex++;
        }
        //页数
        params.put("pageIndex", pageIndex);
        //条数
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //加载全部
        boolean isAll = false;
        params.put("isAll", isAll);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + pageIndex + pageCount + String.valueOf(isAll) + consumerId;
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
        httpRequestWrap.send(PlantAddress.COMMODITY, params);
    }

    @OnClick({R.id.rl_record_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_record_return:
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
