package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.AttentionAdapter;
import com.cdqf.plant_class.Refundetails;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.CacenlRefundetaislFind;
import com.cdqf.plant_find.RefundFind;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.ListViewForScrollView;
import com.gcssloop.widget.RCRelativeLayout;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 退款详情
 * Created by liu on 2017/12/29.
 */

public class RefundetailsActivity extends BaseActivity {
    private String TAG = RefundetailsActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    private int position;

    private Refundetails refundetails = null;

    //返回
    @BindView(R.id.rl_refundetils_return)
    public RelativeLayout rlRefundetilsReturn = null;

    //卖家状态
    @BindView(R.id.tv_refundetails_state)
    public TextView tvRefundetailsState = null;

    //剩下时间
    @BindView(R.id.tv_refundetails_data)
    public TextView tvRefundetailsData = null;

    //退款状态
    @BindView(R.id.tv_refundetails_reply)
    public TextView tvRefundetailsReply = null;

    /*****************************
     * 快递
     *************************************/
    //快递情况
    @BindView(R.id.ll_refundetails_logistics)
    public LinearLayout llRefundetailsLogistics = null;

    //快递内容
    @BindView(R.id.tv_refundetails_logistics)
    public TextView tvRefundetailsLogistics = null;

    //注意事项集合
    @BindView(R.id.lvfv_refundetails_list)
    public ListViewForScrollView lvfvRefundetailsList = null;

    private AttentionAdapter attentionAdapter = null;

    //撤消申请
    @BindView(R.id.rcrl_refund_details)
    public RCRelativeLayout rcrlRefundDetails = null;

    //填写快递信息
    @BindView(R.id.rcrl_refund_courier)
    public RCRelativeLayout rcrlRefundCourier = null;

    //商品图片
    @BindView(R.id.iv_refundetails_item_icon)
    public ImageView ivRefundetailsItemIcon = null;

    //商品名称
    @BindView(R.id.tv_refundetails_title)
    public TextView tvRefundetailsTitle = null;

    //商品价格
    @BindView(R.id.tv_refundetails_price)
    public TextView tvRefundetailsPrice = null;

    //退款原因
    @BindView(R.id.tv_refundetails_why)
    public TextView tvRefundetailsWhy = null;

    //退款金额
    @BindView(R.id.tv_refundetails_amount)
    public TextView tvRefundetailsAmount = null;

    //退款数量
    @BindView(R.id.tv_refundetails_number)
    public TextView tvRefundetailsNumber = null;

    //申请时间
    @BindView(R.id.tv_refundetails_time)
    public TextView tvRefundetailsTime = null;

    //退款编号
    @BindView(R.id.tv_refundetails_serial)
    public TextView tvRefundetailsSerial = null;

    @BindView(R.id.ll_refundetails_layoutone)
    public LinearLayout llRefundetailsLayoutone = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //提交退款退货申请
                case 0x00:
                    //隐藏快递
                    llRefundetailsLogistics.setVisibility(View.GONE);
                    rcrlRefundCourier.setVisibility(View.GONE);
                    break;
                //退款成功
                case 0x01:
                    llRefundetailsLayoutone.setVisibility(View.GONE);
                    break;
                //退款同意
                case 0x02:
                    //隐藏快递
                    llRefundetailsLogistics.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_refundetails);

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
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        imageLoader = plantState.getImageLoader(context);
        ButterKnife.bind(this);
    }

    private void initView() {

    }

    private void initAdapter() {

    }

    private void initListener() {

    }

    private void initBack() {
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取退款详情解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取退款详情解密成功---" + data);
                refundetails = new Refundetails();
                refundetails = gson.fromJson(data, Refundetails.class);
//                switch (refundetails.getStatus()) {
                switch (4) {
                    //提交退款申请
                    case 0:
                        handler.sendEmptyMessage(0x00);
                        tvRefundetailsState.setText("请等商家处理");
                        tvRefundetailsData.setText("还剩" + refundetails.getOverTimeToReturnGoods() + "天");
                        tvRefundetailsReply.setText("您已成功发起退款申请,请耐心等待商家处理");
                        List<String> attentionOneList = new ArrayList<String>();
                        attentionOneList.add("商家同意后,请按照给出的退货地址退货,并请记录退货运单号");
                        attentionOneList.add("如商家拒绝,您可以修改申请后再次发起,商家会重新处理");
                        attentionOneList.add("如商家超时未处理,退货申请奖达成,请按系统给出的退址退货");
                        attentionAdapter = new AttentionAdapter(context, attentionOneList);
                        lvfvRefundetailsList.setAdapter(attentionAdapter);
                        break;
                    //退款退货成功
                    case 1:
                        handler.sendEmptyMessage(0x01);
                        tvRefundetailsState.setText("退款成功");
                        tvRefundetailsData.setText(refundetails.getOverTimeToReturnGoods() + "天");
                        tvRefundetailsReply.setText("卖家已同意您的申请请求,退款将在3日内退回至原交易账户,请注意查收.");
                        break;
                    //退款失败
                    case 2:
                        handler.sendEmptyMessage(0x01);
                        tvRefundetailsState.setText("退款失败");
                        tvRefundetailsData.setText(refundetails.getOverTimeToReturnGoods() + "天");
                        tvRefundetailsReply.setText("卖家未同意您的申请请求,您可以重新申请退货或联系电话:" + refundetails.getTel());
                        break;
                    //退款关闭
                    case 3:
                        handler.sendEmptyMessage(0x01);
                        tvRefundetailsState.setText("退款关闭");
                        tvRefundetailsData.setText(refundetails.getOverTimeToReturnGoods() + "天");
                        tvRefundetailsReply.setText("因您撤销退款申请,退款已关闭,交易将正常进行.请关注交易超时");
                        break;
                    //同意退款申请
                    case 4:
                        handler.sendEmptyMessage(0x02);
                        List<String> attentionTwoList = new ArrayList<String>();
                        attentionTwoList.add("姓名：" + refundetails.getAddressee());
                        attentionTwoList.add("详细地址：" + refundetails.getAddressee());
                        attentionTwoList.add("联系电话：" + refundetails.getTel());
                        attentionAdapter = new AttentionAdapter(context, attentionTwoList);
                        lvfvRefundetailsList.setAdapter(attentionAdapter);
                        break;
                    //买家已发货
                    case 5:
                        //隐藏快递
                        handler.sendEmptyMessage(0x00);
                        tvRefundetailsState.setText("请等待卖家收货并退款");
                        tvRefundetailsData.setText("还剩" + refundetails.getOverTimeToReturnGoods() + "天");
                        tvRefundetailsReply.setText("如果卖家收到货并验货无误,将操作退款给您");
                        List<String> attentionThreeList = new ArrayList<String>();
                        attentionThreeList.add("如果卖家拒绝退款,需要您重新提交退款申请,或联系电话:"+refundetails.getTel());
                        attentionThreeList.add("如果卖家超时未处理,将自动退款给您");
                        attentionAdapter = new AttentionAdapter(context, attentionThreeList);
                        lvfvRefundetailsList.setAdapter(attentionAdapter);
                        break;
                }
                imageLoader.displayImage(refundetails.getHttpPic(), ivRefundetailsItemIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                tvRefundetailsTitle.setText(refundetails.getCommName());
                tvRefundetailsPrice.setText(refundetails.getMoney() + "");
                tvRefundetailsWhy.setText(refundetails.getStrReturnGoodsType());
                tvRefundetailsAmount.setText(refundetails.getMoney() + "");
                tvRefundetailsNumber.setText(refundetails.getNum() + "");
                tvRefundetailsTime.setText(refundetails.getStrAddDate());
                tvRefundetailsSerial.setText(refundetails.getReturnGoodsNo());
            }
        }));
        initPut();
    }

    private void initPut() {
        Map<String, Object> params = new HashMap<String, Object>();
        //订单id
        int orderId = plantState.getRefundList().get(position).getOrderId();
        params.put("orderId", orderId);

        //商品Id
        int commId = plantState.getRefundList().get(position).getCommId();
        params.put("commId", commId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + orderId + +commId;
        Log.e(TAG, "---明文---" + random + "---" + orderId + "---" + commId + "---");
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
        httpRequestWrap.send(PlantAddress.USER_REFUNDETILS, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("rgId", plantState.getRefundList().get(position).getRgId());
        startActivity(intent);
    }

    @OnClick({R.id.rl_refundetils_return, R.id.rcrl_refund_details, R.id.rcrl_refund_courier})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_refundetils_return:
                finish();
                break;
            //撤销申请
            case R.id.rcrl_refund_details:
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt("是否撤销", 19);
                promptDilogFragment.show(getSupportFragmentManager(), "撤销退款");
                break;
            //填写快递信息
            case R.id.rcrl_refund_courier:
                initIntent(FillActivity.class);
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

    public void onEventMainThread(CacenlRefundetaislFind a) {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "撤销中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取撤销解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取撤销解密成功---" + data);
                eventBus.post(new RefundFind());
                finish();
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //订单id
        int orderId = plantState.getRefundList().get(position).getOrderId();
        params.put("orderId", orderId);

        //商品Id
        int commId = plantState.getRefundList().get(position).getCommId();
        params.put("commId", commId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + orderId + +commId;
        Log.e(TAG, "---明文---" + random + "---" + orderId + "---" + commId + "---");
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
        httpRequestWrap.send(PlantAddress.USER_CANCEL, params);
    }
}
