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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.OrderDetailsAdapter;
import com.cdqf.plant_class.OrderDetails;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 订单详情
 * Created by liu on 2017/12/9.
 */

public class OrderDetailsActivity extends BaseActivity {

    private String TAG = OrderDetailsActivity.class.getSimpleName();

    public static OrderDetailsActivity orderDetailsActivity = null;

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    private Gson gson = new Gson();

    //返回
    @BindView(R.id.rl_orderdetails_return)
    public RelativeLayout rlOrderdetailsReturn = null;

    //收货
    @BindView(R.id.rl_orderdetails_layout)
    public RelativeLayout rlOrderdetailsLayout = null;

    //是否已经收货
    @BindView(R.id.tv_orderdetails_out)
    public TextView tvOrderdetailsOut = null;

    //时间
    @BindView(R.id.tv_orderdetails_deta)
    public TextView tvOrderdetailsDeta = null;

    //收件人
    @BindView(R.id.tv_orderdetails_name)
    public TextView tvOrderdetailsName = null;

    //收件人电话
    @BindView(R.id.tv_orderdetails_number)
    public TextView tvOrderdetailsNumber = null;

    //收件地址
    @BindView(R.id.tv_orderdetails_details)
    public TextView tvOrderdetailsDetails = null;

    //商品集合
    @BindView(R.id.lvsv_orderdetails_list)
    public ListViewForScrollView lvsvOrderdetailsList = null;

    private OrderDetailsAdapter orderDetailsAdapter = null;

    //商品总价
    @BindView(R.id.tv_orderdetails_allprice)
    public TextView tvOrderdetailsAllprice = null;

    //邮费
    @BindView(R.id.tv_orderdetails_postage)
    public TextView tvOrderdetailsPostage = null;

    //付款
    @BindView(R.id.tv_orderdetails_payment)
    public TextView tvOrderdetailsPayment = null;

    //订单编号
    @BindView(R.id.tv_orderdetails_ordernumber)
    public TextView tvOrderdetailsOrdernumber = null;

    //支付宝订单号
    @BindView(R.id.tv_orderdetails_paynumber)
    public TextView tvOrderdetailsPaynumber = null;

    //创建时间
    @BindView(R.id.tv_orderdetails_creationtime)
    public TextView tvOrderdetailsCreationtime = null;

    //付款时间
    @BindView(R.id.tv_orderdetails_paymenttime)
    public TextView tvOrderdetailsPaymenttime = null;

    //发货时间
    @BindView(R.id.tv_orderdetails_deliverytime)
    public TextView tvOrderdetailsDeliverytime = null;

    //哪个页面
    private int type = 0;

    //位置
    private int position = 0;

    //判断是否为全部订单
    private boolean isAllOrder = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //待付款
                case 0x01:
                    rlOrderdetailsLayout.setVisibility(View.GONE);
                    break;
                //待发货
                case 0x02:
                    rlOrderdetailsLayout.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_orderdetails);

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
        isAllOrder = intent.getBooleanExtra("isAllOrder", false);
        orderDetailsActivity = this;
    }

    private void initView() {

    }

    private void initAdapter() {
        orderDetailsAdapter = new OrderDetailsAdapter(context);
        lvsvOrderdetailsList.setAdapter(orderDetailsAdapter);
    }

    private void initListener() {

    }

    private void initBack() {
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "获取详情中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取订单详情解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取订单详情解密成功---" + data);
                OrderDetails orderDetails = new OrderDetails();
                orderDetails = gson.fromJson(data, OrderDetails.class);
                plantState.setOrderDetails(orderDetails);
                //收件人姓名
                tvOrderdetailsName.setText(orderDetails.getReceivingContacts());
                //收件人电话
                tvOrderdetailsNumber.setText(orderDetails.getTel());
                //收件人地址
                tvOrderdetailsDetails.setText(orderDetails.getStrReceivingAddress());
                //商品总价
                tvOrderdetailsAllprice.setText("￥" + orderDetails.getDealPrice() + "");
                //集合
                orderDetailsAdapter.setOrderDetails(orderDetails, orderDetails.getOrderStatus());
                if (TextUtils.equals(orderDetails.getOrderStatus(), "待付款")) {
                    //邮费
                    tvOrderdetailsPostage.setText("无");
                    //实付款
                    tvOrderdetailsPayment.setText("支付后显示");
                    //支付宝交易号
                    tvOrderdetailsPaynumber.setText("支付后显示");
                    //付款日期
                    tvOrderdetailsPaymenttime.setText("支付后显示");
                    //发货时间
                    tvOrderdetailsDeliverytime.setText("发货后显示");
                } else if (TextUtils.equals(orderDetails.getOrderStatus(), "待发货")) {
                    //邮费
                    tvOrderdetailsPostage.setText(orderDetails.getPostage() + "");
                    //实付款
                    tvOrderdetailsPayment.setText("￥" + orderDetails.getDealPrice() + "");
                    //支付宝交易号
                    tvOrderdetailsPaynumber.setText("支付后显示");
                    //付款日期
                    tvOrderdetailsPaymenttime.setText(orderDetails.getDeliverGoodDate());
                    //发货时间
                    tvOrderdetailsDeliverytime.setText("发货后显示");
                } else if (TextUtils.equals(orderDetails.getOrderStatus(), "待收货")) {
                    //邮费
                    tvOrderdetailsPostage.setText(orderDetails.getPostage() + "");
                    //实付款
                    tvOrderdetailsPayment.setText("￥" + orderDetails.getDealPrice() + "");
                    //支付宝交易号
                    tvOrderdetailsPaynumber.setText("支付后显示");
                    //付款日期
                    tvOrderdetailsPaymenttime.setText(orderDetails.getDeliverGoodDate());
                    //发货时间
                    tvOrderdetailsDeliverytime.setText(orderDetails.getStrDeliverGoodDate());
                } else if (TextUtils.equals(orderDetails.getOrderStatus(), "待收货")) {
                    //邮费
                    tvOrderdetailsPostage.setText(orderDetails.getPostage() + "");
                    //实付款
                    tvOrderdetailsPayment.setText("￥" + orderDetails.getDealPrice() + "");
                    //支付宝交易号
                    tvOrderdetailsPaynumber.setText("支付后显示");
                    //付款日期
                    tvOrderdetailsPaymenttime.setText(orderDetails.getDeliverGoodDate());
                    //发货时间
                    tvOrderdetailsDeliverytime.setText(orderDetails.getStrDeliverGoodDate());
                } else if (TextUtils.equals(orderDetails.getOrderStatus(), "交易成功")) {
                    //邮费
                    tvOrderdetailsPostage.setText(orderDetails.getPostage() + "");
                    //实付款
                    tvOrderdetailsPayment.setText("￥" + orderDetails.getDealPrice() + "");
                    //支付宝交易号
                    tvOrderdetailsPaynumber.setText("支付后显示");
                    //付款日期
                    tvOrderdetailsPaymenttime.setText(orderDetails.getDeliverGoodDate());
                    //发货时间
                    tvOrderdetailsDeliverytime.setText(orderDetails.getStrDeliverGoodDate());
                }

                //订单编号
                tvOrderdetailsOrdernumber.setText(orderDetails.getOrderNo());

                //创建时间
                tvOrderdetailsCreationtime.setText(orderDetails.getOrderDate());
                //是否要查询快递
                int orderId = 0;
                if (isAllOrder) {
                    if (type == 3) {
                        orderId = plantState.getAllOrderList().get(position).getOrderId();
                        initLogistics(orderId);
                    }
                } else {
                    if (type == 3) {
                        orderId = plantState.getForGoodsList().get(position).getOrderId();
                        initLogistics(orderId);
                    }
                }
            }
        }));
        initPut();
    }

    private void initPut() {
        Map<String, Object> params = new HashMap<String, Object>();
        //订单id
        int orderId = 0;
        if (isAllOrder) {
            orderId = plantState.getAllOrderList().get(position).getOrderId();
            switch (type) {
                //待付款
                case 1:
                    handler.sendEmptyMessage(0x01);
                    break;
                //待发货
                case 2:
                    handler.sendEmptyMessage(0x01);
                    break;
                //待收货
                case 3:
                    handler.sendEmptyMessage(0x02);
                    break;
                //交易成功
                case 4:
                    handler.sendEmptyMessage(0x01);
                    break;
            }
        } else {
            switch (type) {
                //待付款
                case 1:
                    handler.sendEmptyMessage(0x01);
                    orderId = plantState.getForPaymentList().get(position).getOrderId();
                    break;
                //待发货
                case 2:
                    handler.sendEmptyMessage(0x01);
                    orderId = plantState.getSendGoodsList().get(position).getOrderId();
                    break;
                //待收货
                case 3:
                    handler.sendEmptyMessage(0x02);
                    orderId = plantState.getForGoodsList().get(position).getOrderId();
                    break;
                //交易成功
                case 4:
                    handler.sendEmptyMessage(0x01);
                    orderId = plantState.getEvaluateList().get(position).getOrderId();
                    break;
            }
        }
        params.put("orderId", orderId);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + orderId + consumerId;
        Log.e(TAG, "---明文---" + random + "---" + orderId + "---" + consumerId);
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
        httpRequestWrap.send(PlantAddress.USER_DETAILSORDER, params);
    }

    private void initLogistics(int orderId) {
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
                Param p = new Param();
                p.setCom(expressCode);
                p.setNum(logisticsNum);
                String param = gson.toJson(p);
                String md5 = param + key + customer;
                String sign = (MD5Utils.getMD5Single(md5)).toUpperCase();
                Log.e(TAG, "---快递---" + sign);
                //获取快递流水单
                initLogistics(customer, sign, param);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int qId = 0;
        //订单id
        params.put("qId", orderId);
        //查询状态
        int qType = 0;
        params.put("qType", qType);

        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + orderId + qType;
        Log.e(TAG, "---明文---" + random + "---" + orderId + "---" + qType);
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
    private void initLogistics(String customer, String sign, String param) {
        Log.e(TAG, "---快递---" + customer + "---" + sign + "---" + param);
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "查询中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                Log.e(TAG, "---查询快递---" + result);
                JSONObject jsonObject = JSON.parseObject(result);
                String message = jsonObject.getString("message");
                if (TextUtils.equals(message, "ok")) {
                    handler.sendEmptyMessage(0x001);
                    String data = jsonObject.getString("data");
                } else {
                    handler.sendEmptyMessage(0x002);
                    plantState.initToast(context, message, true, 0);
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

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("position", position);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @OnClick({R.id.rl_orderdetails_return, R.id.rl_orderdetails_layout})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_orderdetails_return:
                finish();
                break;
            //收货
            case R.id.rl_orderdetails_layout:
                Intent intent = new Intent(context, LogisticsActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("position", position);
                context.startActivity(intent);
                break;
            //退款
//            case R.id.rcrl_orderdetails_refund:
//                switch (type) {
//                    //待发货
//                    case 2:
//                        initIntent(ServiceActivity.class, position);
//                        break;
//                    //待收货
//                    case 3:
//                        initIntent(ServiceActivity.class, position);
//                        break;
//                    //交易成功
//                    case 4:
//                        initIntent(ServiceActivity.class, position);
//                        break;
//                }
//                break;
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
