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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.LogisticsAdapter;
import com.cdqf.plant_class.Data;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.MD5Utils;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.ListViewForScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 查看物流
 * Created by liu on 2017/12/9.
 */

public class LogisticsActivity extends BaseActivity {

    private String TAG = LogisticsActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_logistics_return)
    public RelativeLayout rlLogisticsReturn = null;

    //图片
    @BindView(R.id.iv_logistics_picture)
    public ImageView ivLogisticsPicture = null;

    //货物状态
    @BindView(R.id.tv_logistics_deliverystatus)
    public TextView tvLogisticsDeliverystatus = null;

    //快递
    @BindView(R.id.tv_logistics_courier)
    public TextView tvLogisticsCourier = null;

    //电话
    @BindView(R.id.tv_logistics_phone)
    public TextView tvLogisticsPhone = null;

    @BindView(R.id.ll_logistics_there)
    public LinearLayout llLogisticsThere = null;

    //快递状态集合
    @BindView(R.id.lv_logistics_list)
    public ListViewForScrollView lvLogisticsList = null;

    public LogisticsAdapter logisticsAdapter = null;

    private int type;

    private int position;

    private Gson gson = new Gson();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x001:
                    //有快递信息显示
                    llLogisticsThere.setVisibility(View.GONE);
                    lvLogisticsList.setVisibility(View.VISIBLE);
                    break;
                case 0x002:
                    //无快递信息显示
                    llLogisticsThere.setVisibility(View.VISIBLE);
                    lvLogisticsList.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_logistics);

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
        httpRequestWrap = new HttpRequestWrap(context);
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        imageLoader = plantState.getImageLoader(context);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        position = intent.getIntExtra("position", 0);
    }

    private void initView() {

    }

    private void initAdapter() {
        logisticsAdapter = new LogisticsAdapter(context);
        lvLogisticsList.setAdapter(logisticsAdapter);
    }

    private void initListener() {

    }

    private void initBack() {
        if (type == 0) {
            imageLoader.displayImage(plantState.getAllOrderList().get(position).getOrderCommList().get(0).getImgCommPic(), ivLogisticsPicture, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        } else {
            imageLoader.displayImage(plantState.getForGoodsList().get(position).getOrderCommList().get(0).getImgCommPic(), ivLogisticsPicture, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        }
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "获取快递信息中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取物流信息解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取物流信息解密成功---" + data);
                JSONObject resultJSON = JSON.parseObject(data);
                //快递公司拼音
                String expressCode = resultJSON.getString("expressCode");
                //快递单号
                String logisticsNum = resultJSON.getString("logisticsNum");
                //快递公司中文
                String expressName = resultJSON.getString("expressName");
                //密钥
                String key = resultJSON.getString("key");
                //分配给贵司的的公司编号
                String customer = resultJSON.getString("customer");
                tvLogisticsCourier.setText(expressName + ":" + logisticsNum);
                Param p = new Param();
                p.setCom(expressCode);
                p.setNum(logisticsNum);
                String param = gson.toJson(p);
                String md5 = param + key + customer;
                String sign = (MD5Utils.getMD5Single(md5)).toUpperCase();
                Log.e(TAG, "---快递---" +sign);
                //获取快递流水单
                initLogistics(customer, sign,param);
            }
        }));
        initPut();
    }

    private void initPut() {
        Map<String, Object> params = new HashMap<String, Object>();
        int qId = 0;
        //订单id
        if (type == 0) {
            qId = plantState.getAllOrderList().get(position).getOrderId();
        } else {
            qId = plantState.getForGoodsList().get(position).getOrderId();
        }
        params.put("qId", qId);
        //查询状态
        int qType = 0;
        params.put("qType", qType);

        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + qId + qType;
        Log.e(TAG, "---明文---" + random + "---" + qId + "---" + qType);
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
        httpRequestWrap.send(PlantAddress.USER_LOGISTICS, params);
    }

    /**
     * 获取快递流水号
     *
     * @param customer
     * @param sign
     */
    private void initLogistics(String customer, String sign,String param) {
        Log.e(TAG, "---快递---" + customer + "---" + sign + "---" + param);
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1,"查询中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG,"---查询快递---"+result);
                JSONObject jsonObject = JSON.parseObject(result);
                String message = jsonObject.getString("message");
                if(TextUtils.equals(message,"ok")){
                    handler.sendEmptyMessage(0x001);
                    String data = jsonObject.getString("data");
                    List<Data> dataList = gson.fromJson(data,new TypeToken<List<Data>>(){}.getType());
                    tvLogisticsDeliverystatus.setText(dataList.get(0).getStatus());
                    logisticsAdapter.setDataList(dataList);
                } else {
                    handler.sendEmptyMessage(0x002);
                    plantState.initToast(context,message,true,0);
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", customer);
        params.put("sign", sign);
        params.put("param", param);
        httpRequestWrap.send(PlantAddress.LOGISTICS, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @OnClick({R.id.rl_logistics_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_logistics_return:
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

    class Param {
        String com = "";
        String num = "";
        String phone = "";
        String from = "";
        String to = "";
        String resultv2 = "1";

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCom() {
            return com;
        }

        public void setCom(String com) {
            this.com = com;
        }

        public String getResultv2() {
            return resultv2;
        }

        public void setResultv2(String resultv2) {
            this.resultv2 = resultv2;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }
    }
}
