package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_dilog.GoodsDilogFragment;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_dilog.WhyDilogFragment;
import com.cdqf.plant_find.GoodsDilogFind;
import com.cdqf.plant_find.TypeFind;
import com.cdqf.plant_find.TypeNumberFind;
import com.cdqf.plant_find.TypesFind;
import com.cdqf.plant_find.WhyDilogFind;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 退款说明
 * Created by liu on 2017/12/28.
 */

public class TypeActivity extends BaseActivity {

    private String TAG = TypeActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_type_return)
    public RelativeLayout rlTypeReturn = null;

    //商品图片
    @BindView(R.id.iv_type_icon)
    public ImageView ivTypeIcon = null;

    //商品名称
    @BindView(R.id.tv_type_title)
    public TextView tvTypeTitle = null;

    //价格
    @BindView(R.id.tv_type_price)
    public TextView tvTypePrice = null;

    //货物状态
    @BindView(R.id.ll_type_state)
    public LinearLayout llTypeState = null;

    //状态
    @BindView(R.id.tv_type_state)
    public TextView tvTypeState = null;

    //退款原因
    @BindView(R.id.ll_type_why)
    public LinearLayout llTypeWhy = null;

    //原因
    @BindView(R.id.tv_type_why)
    public TextView tvTypeWhy = null;

    //退款数量
    @BindView(R.id.ll_type_number)
    public LinearLayout llTypenumber = null;

    //数量
    @BindView(R.id.tv_type_number)
    public TextView tvTypeNumber = null;

    //退款金额
    @BindView(R.id.tv_type_prite)
    public TextView tvTypePrite = null;

    //选填
    @BindView(R.id.tv_type_instructions)
    public EditText tvTypeInstructions = null;

    //提交
    @BindView(R.id.tv_type_submit)
    public TextView tvTypeSubmit = null;

    private String url = null;

    private String title = null;

    private double price = 0;

    //用户id
    private int consumerId = 0;

    //订单id
    private int orderId = 0;

    //商品id
    private int commId = 0;

    //退款数量
    private int commNum = 0;

    //退款类型
    private int returnType = -1;

    //金额
    private double money = 0;

    //退款原因
    private int returnGoodsReason = -1;

    //说明
    private String remark = null;

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
        setContentView(R.layout.activity_type);

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
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        consumerId = plantState.getUser().getConsumerId();
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        price = intent.getDoubleExtra("price", 0);
        commNum = intent.getIntExtra("commNum", 0);
        money = intent.getDoubleExtra("money", 0);
        orderId = intent.getIntExtra("orderId", orderId);
        commId = intent.getIntExtra("commId", commId);
        //退款类型
        ButterKnife.bind(this);
    }

    private void initView() {

    }

    private void initAdapter() {

    }

    private void initListener() {
    }

    private void initBack() {
        imageLoader.displayImage(url, ivTypeIcon, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        tvTypeTitle.setText(title);
        tvTypePrice.setText(price + "");
        tvTypeNumber.setText(commNum + "");
        tvTypePrite.setText("￥" + money);
    }

    @OnClick({R.id.rl_type_return, R.id.ll_type_state, R.id.ll_type_why, R.id.ll_type_number, R.id.tv_type_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_type_return:
                finish();
                break;
            //货物状态
            case R.id.ll_type_state:
                GoodsDilogFragment goodsDilogFragment = new GoodsDilogFragment();
                goodsDilogFragment.show(getSupportFragmentManager(), "货物状态");
                break;
            //退款原因
            case R.id.ll_type_why:
                WhyDilogFragment whyDilogFragment = new WhyDilogFragment();
                whyDilogFragment.show(getSupportFragmentManager(), "退款原因");
                break;
            //退款数量
            case R.id.ll_type_number:
//                TypeNumberDilogFragment typeNumberDilogFragment = new TypeNumberDilogFragment();
//                typeNumberDilogFragment.setNumber(commNum);
//                typeNumberDilogFragment.show(getSupportFragmentManager(), "退款数量");
                break;
            //提交
            case R.id.tv_type_submit:
                //货物状态
                if (returnType == -1) {
                    plantState.initToast(context, "请选择货物状态", true, 0);
                    return;
                }
                //退款原因
                if (returnGoodsReason == -1) {
                    plantState.initToast(context, "请选择退货原因", true, 0);
                    return;
                }
                remark = tvTypeInstructions.getText().toString();
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt("是否申请退款", 28);
                promptDilogFragment.show(getSupportFragmentManager(), "提交退款");
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
     * 货物状态
     *
     * @param g
     */
    public void onEventMainThread(GoodsDilogFind g) {
        returnType = g.type;
        if (returnType == 0) {
            tvTypeState.setText("未收到货");
        } else if (returnType == 1) {
            tvTypeState.setText("已收到货");
        }
    }

    /**
     * 退款原因
     *
     * @param g
     */
    public void onEventMainThread(WhyDilogFind g) {
        returnGoodsReason = g.position;
        tvTypeWhy.setText(g.content);
    }

    /**
     * 商品数量
     *
     * @param t
     */
    public void onEventMainThread(TypeNumberFind t) {
        commNum = t.position;
    }

    /**
     * 退款
     *
     * @param t
     */
    public void onEventMainThread(TypesFind t) {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "提交", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---提交退款解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---提交退款解密成功---" + data);
                OrderDetailsActivity.orderDetailsActivity.finish();
                ServiceActivity.serviceActivity.finish();
                eventBus.post(new TypeFind());

                finish();
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //用户id
        params.put("consumerId", consumerId);
        Log.e(TAG, "---用户id---" + consumerId);
        //订单id
        params.put("orderId", orderId);
        Log.e(TAG, "---订单id---" + orderId);
        //商品id
        params.put("commId", commId);
        Log.e(TAG, "---商品id---" + commId);
        //商品数量
        params.put("commNum", commNum);
        Log.e(TAG, "---商品数量---" + commNum);
        //退款类型
        params.put("returnType", returnType);
        Log.e(TAG, "---退款类型---" + returnType);
        //金额
        params.put("money", money);
        Log.e(TAG, "---金额---" + money);
        //退款原因
        params.put("returnGoodsReason", returnGoodsReason+1);
        Log.e(TAG, "---退款原因---" + returnGoodsReason);
        //说明
        params.put("remark", remark);
        Log.e(TAG, "---说明---" + remark);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + consumerId + orderId + commId + commNum + returnType + money + (returnGoodsReason+1) + remark;
        Log.e(TAG, "---随机数---" + random);
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
        httpRequestWrap.send(PlantAddress.USER_RETURN, params);
    }
}
