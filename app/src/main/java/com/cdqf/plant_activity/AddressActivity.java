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
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.AddressAdapter;
import com.cdqf.plant_class.Address;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.AddressModifyFind;
import com.cdqf.plant_find.AddressPromptFind;
import com.cdqf.plant_find.AddressPromptTwoFind;
import com.cdqf.plant_find.DetailsFind;
import com.cdqf.plant_find.NewAddressFind;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 管理收货地址
 * Created by liu on 2017/11/14.
 */

public class AddressActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = AddressActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlAddressReturn = null;

    //集合
    private ListView lvAddressList = null;

    //适配器
    private AddressAdapter addressAdapter = null;

    //添加新地址
    private TextView tvAddressNewaddress = null;

    private LinearLayout llAddressThere = null;

    private Gson gson = new Gson();

    private int deletePosition = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x00:
                    llAddressThere.setVisibility(View.VISIBLE);
                    lvAddressList.setVisibility(View.GONE);
                    break;
                case 0x01:
                    llAddressThere.setVisibility(View.GONE);
                    lvAddressList.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_address);

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
        if(!eventBus.isRegistered(this)){
            eventBus.register(this);
        }
    }

    private void initView() {
        rlAddressReturn = this.findViewById(R.id.rl_address_return);
        lvAddressList = this.findViewById(R.id.lv_address_list);
        tvAddressNewaddress = this.findViewById(R.id.tv_address_newaddress);
        llAddressThere = this.findViewById(R.id.ll_address_there);
    }

    private void initAdapter() {
        addressAdapter = new AddressAdapter(context);
        lvAddressList.setAdapter(addressAdapter);
    }

    private void initListener() {
        rlAddressReturn.setOnClickListener(this);
        tvAddressNewaddress.setOnClickListener(this);
    }

    private void initBack() {
        initAddress();
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("type",0);
        startActivity(intent);
    }

    /**
     * 初始数据
     */
    private void initAddress() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isAddress(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户地址修改解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---用户地址修改解密成功---" + data);
                if (TextUtils.equals(data, "列表为空")) {
                    plantState.getAddressList().clear();
                    handler.sendEmptyMessage(0x00);
                    if (addressAdapter != null) {
                        addressAdapter.notifyDataSetChanged();
                    }
                    return;
                }
                plantState.getAddressList().clear();
                List<Address> addressList = gson.fromJson(data, new TypeToken<List<Address>>() {
                }.getType());
                plantState.setAddressList(addressList);
                handler.sendEmptyMessage(0x01);
                if (addressAdapter != null) {
                    addressAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
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
        httpRequestWrap.send(PlantAddress.USER_ADDRESS, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_address_return:
                eventBus.post(new DetailsFind());
                finish();
                break;
            //添加新地址
            case R.id.tv_address_newaddress:
                initIntent(NewAddressActivity.class);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        eventBus.post(new DetailsFind());
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

    public void onEventMainThread(NewAddressFind a){
        initAddress();
    }

    public void onEventMainThread(final AddressModifyFind a){
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isAddress(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户默认地址修改解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---用户默认地址修改解密成功---" + data);
                if (TextUtils.equals(data, "列表为空")) {
                    return;
                }
                initAddress();
                if (addressAdapter != null) {
                    addressAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int crId = plantState.getAddressList().get(a.position).getId();
        params.put("crId", crId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + crId;
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
        httpRequestWrap.send(PlantAddress.USER_DEFAULT, params);
    }

    /**
     * 删除
     */
    public void onEventMainThread(AddressPromptFind a){
        deletePosition = a.position;
            PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
            promptDilogFragment.initPrompt(plantState.getPlantString(context,R.string.delete_address),7);
            promptDilogFragment.show(getSupportFragmentManager(),"删除用户地址");

        }

    public void onEventMainThread(AddressPromptTwoFind a) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isAddress(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户删除地址修改解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---用户删除地址修改解密成功---" + data);
                if (TextUtils.equals(data, "列表为空")) {
                    return;
                }
                initAddress();
                if (addressAdapter != null) {
                    addressAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int crId = plantState.getAddressList().get(deletePosition).getId();
        params.put("crIds", crId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + crId;
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
        httpRequestWrap.send(PlantAddress.USER_DELETE_ADDRESS, params);
    }

}
