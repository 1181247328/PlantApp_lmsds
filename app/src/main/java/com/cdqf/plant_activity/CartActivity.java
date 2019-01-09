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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.CartAdapter;
import com.cdqf.plant_class.CartList;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.CartAddFind;
import com.cdqf.plant_find.CartFind;
import com.cdqf.plant_find.CartReductionFind;
import com.cdqf.plant_find.DeteleShopFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.DoubleOperationUtil;
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
import de.greenrobot.event.EventBus;

/**
 * 购物车
 * Created by liu on 2017/11/10.
 */

public class CartActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = CartActivity.class.getSimpleName();

    public static CartActivity cartActivity = null;

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_cart_return)
    public RelativeLayout rlCartReturn = null;

    //删除
    @BindView(R.id.tv_cart_delete)
    public TextView tvCartDelete = null;

    //集合
    @BindView(R.id.ptrl_cart_pull)
    public PullToRefreshLayout ptrlCartPull = null;

    private ListView lvCartList = null;

    private CartAdapter cartAdapter = null;

    //全选
    @BindView(R.id.cb_cart_checkbox)
    public CheckBox cbCartCheckbox = null;

    //价格
    @BindView(R.id.tv_cart_price)
    public TextView tvCartPrice = null;

    //去结算
    @BindView(R.id.tv_cart_settlement)
    public TextView tvCartSettlement = null;

    //购物车无商品显示
    @BindView(R.id.ll_cart_there)
    public LinearLayout llCartThere = null;

    //底部操作
    @BindView(R.id.rl_cart_layout_one)
    public RelativeLayout rlCartLayoutOne = null;

    private Gson gson = new Gson();

    private double allPrice = 0.0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //无商品显示
                case 0x001:
                    tvCartDelete.setVisibility(View.GONE);
                    ptrlCartPull.setVisibility(View.GONE);
                    rlCartLayoutOne.setVisibility(View.GONE);
                    llCartThere.setVisibility(View.VISIBLE);
                    break;
                //有商品显示
                case 0x002:
                    tvCartDelete.setVisibility(View.VISIBLE);
                    ptrlCartPull.setVisibility(View.VISIBLE);
                    rlCartLayoutOne.setVisibility(View.VISIBLE);
                    llCartThere.setVisibility(View.GONE);
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
        setContentView(R.layout.activity_cart);

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
        cartActivity = this;
        httpRequestWrap = new HttpRequestWrap(context);
        ButterKnife.bind(this);
        if (!eventBus.isRegistered(context)) {
            eventBus.register(context);
        }
    }

    private void initView() {
        lvCartList = (ListView) ptrlCartPull.getPullableView();
    }

    private void initAdapter() {
        cartAdapter = new CartAdapter(context);
        lvCartList.setAdapter(cartAdapter);
    }

    private void initListener() {
        rlCartReturn.setOnClickListener(this);
        tvCartDelete.setOnClickListener(this);
        tvCartSettlement.setOnClickListener(this);
        ptrlCartPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                // 下拉刷新操作
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        ptrlCartPull.refreshFinish(PullToRefreshLayout.SUCCEED);
                    }
                }.sendEmptyMessageDelayed(0, 5000);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                // 加载更多操作
                new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        ptrlCartPull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                    }
                }.sendEmptyMessageDelayed(0, 5000);
            }
        });
        cbCartCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isPressed()) {
                    return;
                }
                allPrice = 0.0;
                if (isChecked) {
                    for (CartList cart : plantState.getCartList()) {
                        allPrice = DoubleOperationUtil.add(allPrice, cart.getPrice() * cart.getNumber());
                        cart.setCart(true);
                    }
                } else {
                    for (CartList cart : plantState.getCartList()) {
                        allPrice = 0;
                        cart.setCart(false);
                    }
                }
                tvCartPrice.setText("￥" + allPrice);
                tvCartSettlement.setText("去结算(" + allPrice + ")");
                if (cartAdapter != null) {
                    cartAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void initBack() {
        ptrlCartPull.setPullDownEnable(false);
        ptrlCartPull.setPullUpEnable(false);
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isCart(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---购物车获取失败---" + data);
                    handler.sendEmptyMessage(0x001);
                    return;
                }
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x001);
                    return;
                }
                Log.e(TAG, "---购物车获取成功---" + data);
                handler.sendEmptyMessage(0x002);
                List<CartList> cartLists = gson.fromJson(data, new TypeToken<List<CartList>>() {
                }.getType());
                plantState.setCartList(cartLists);
                if (cartAdapter != null) {
                    cartAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //测试的id=1
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + consumerId;
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
        httpRequestWrap.send(PlantAddress.CARTLIST, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }


    private void initIntent(Class<?> activity, String commIds, String numbers) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("commIds", commIds);
        intent.putExtra("numbers", numbers);
        startActivity(intent);
//        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_cart_return:
                if (plantState.getCartList() != null) {
                    plantState.getCartList().clear();
                }
                finish();
                break;
            //删除
            case R.id.tv_cart_delete:
                boolean isCart = false;
                for (CartList c : plantState.getCartList()) {
                    if (c.isCart()) {
                        isCart = true;
                    }
                }
                if (!isCart) {
                    plantState.initToast(context, context.getResources().getString(R.string.delete_cart), true, 0);
                    return;
                }
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt(context.getResources().getString(R.string.delete_not_cart), 6);
                promptDilogFragment.show(getSupportFragmentManager(), "提示删除");
                break;
            //去结算
            case R.id.tv_cart_settlement:
                boolean isCartSelete = false;
                for (CartList s : plantState.getCartList()) {
                    if (s.isCart()) {
                        isCartSelete = true;
                    }
                }
                if (!isCartSelete) {
                    plantState.initToast(context, "请选择至少一个商品", true, 0);
                    return;
                }
                //商品id集合
                String commIds = "";
                //商品数量集合
                String numbers = "";
                for (CartList c : plantState.getCartList()) {
                    if (c.isCart()) {
                        commIds += c.getCommId() + ",";
                        numbers += c.getNumber() + ",";
                    }
                }
                commIds = commIds.substring(0, commIds.length() - 1);
                numbers = numbers.substring(0, numbers.length() - 1);
                Log.e(TAG, "---商品id集合---" + commIds + "---商品数量集合---" + numbers);
                initIntent(SettlementActivity.class, commIds, numbers);

                break;
        }
    }

    /**
     * 总价格加
     *
     * @param c
     */
    public void onEventMainThread(CartFind c) {
        allPrice = DoubleOperationUtil.add(allPrice, plantState.getCartList().get(c.position).getPrice() * plantState.getCartList().get(c.position).getNumber());

        tvCartPrice.setText("￥" + allPrice + "");
        tvCartSettlement.setText("去结算(" + allPrice + ")");

        boolean isCartSelete = true;
        for (CartList s : plantState.getCartList()) {
            if (!s.isCart()) {
                isCartSelete = false;
            }
        }
        if (isCartSelete) {
            cbCartCheckbox.setChecked(true);
        }
    }

    /**
     * 总价格减
     *
     * @param c
     */
    public void onEventMainThread(CartReductionFind c) {
        allPrice = DoubleOperationUtil.sub(allPrice, plantState.getCartList().get(c.position).getPrice() * plantState.getCartList().get(c.position).getNumber());
        tvCartPrice.setText("￥" + allPrice + "");
        tvCartSettlement.setText("去结算(" + allPrice + ")");

        boolean isCartSelete = true;
        for (CartList s : plantState.getCartList()) {
            if (!s.isCart()) {
                isCartSelete = false;
            }
        }
        Log.e(TAG, "---取消全选的操作---" + isCartSelete);
        if (!isCartSelete) {
            cbCartCheckbox.setChecked(false);
        }
    }

    /**
     * 加
     *
     * @param a
     */
    public void onEventMainThread(CartAddFind a) {
        allPrice = 0;
        for (CartList c : plantState.getCartList()) {
            if (c.isCart()) {
                allPrice += c.getPrice() * c.getNumber();
            }
        }
        tvCartPrice.setText("￥" + allPrice + "");
        tvCartSettlement.setText("去结算(" + allPrice + ")");
    }

    /**
     * 删除选择商品
     *
     * @param d
     */
    public void onEventMainThread(DeteleShopFind d) {
        String shoppingCartIds = "";
        for (CartList c : plantState.getCartList()) {
            if (c.isCart()) {
                shoppingCartIds += c.getScId() + ",";
            }
        }
        shoppingCartIds = shoppingCartIds.substring(0, shoppingCartIds.length() - 1);
        Log.e(TAG, "---DeteleShopFind---" + shoppingCartIds);
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(CartActivity.this, 1, context.getResources().getString(R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---删除购物车商品失败---" + data);
                    return;
                }
                Log.e(TAG, "---删除购物车商品成功---" + data);
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x001);
                    return;
                }
                plantState.getCartList().clear();
                List<CartList> cartLists = gson.fromJson(data, new TypeToken<List<CartList>>() {
                }.getType());
                plantState.setCartList(cartLists);
                if (cartAdapter != null) {
                    cartAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("shoppingCartIds", shoppingCartIds);
        //测试id=1
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + shoppingCartIds + consumerId;
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
        httpRequestWrap.send(PlantAddress.DELETEL_CART, params);
    }

    @Override
    public void onBackPressed() {
        if (plantState.getCartList() != null) {
            plantState.getCartList().clear();
        }
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
        if (plantState.getCartList() != null) {
            plantState.getCartList().clear();
        }
    }
}
