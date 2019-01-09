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
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_city.CityJSON;
import com.cdqf.plant_city.OnWitePickListener;
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
import com.fynn.switcher.Switch;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 新增地址
 * Created by liu on 2017/11/14.
 */

public class NewAddressActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = NewAddressActivity.class.getSimpleName();

    private Context context = null;

    public NewAddressActivity newAddressActivity = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus =EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlNewaddressReturn = null;

    //保存
    private TextView tvNesaddressSave = null;

    //姓名
    private XEditText xetNewaddressGoods = null;

    //电话
    private XEditText xetNewaddressPhone = null;

    //所在地区
    private LinearLayout llNewaddressRegion = null;

    //详细地址
    private XEditText xetNewaddressDetails = null;

    //开关
    private Switch swNewaddressSwitch = null;

    private TextView tvNewaddressArea = null;

    //新增
    private int type = 0;

    private String id = null;

    private String peroince;

    private String city;

    private String area;

    private CityJSON cityJSON = new CityJSON();

    //上传数据
    //用户收货id，新增为0
    private int crId = -1;

    //用户id
    private int consumerId = 0;

    //联系人
    private String contacts;

    //联系人电话
    private String contactNumber;

    //区id
    private int areaRegionDistrictId;

    //详情地址
    private String detailedAddress;

    //是否默认收货地址
    private boolean IsDefault = false;

    private int position = 0;

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
        setContentView(R.layout.activity_newaddress);

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
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        position = intent.getIntExtra("position",0);
        if(type == 0){
            crId=0;
        } else if(type == 1){
            crId = plantState.getAddressList().get(position).getId();
        }
        newAddressActivity = this;
        consumerId = plantState.getUser().getConsumerId();
    }

    private void initView() {
        rlNewaddressReturn = this.findViewById(R.id.rl_newaddress_return);
        tvNesaddressSave = this.findViewById(R.id.tv_newaddress_save);
        xetNewaddressGoods = this.findViewById(R.id.xet_newaddress_goods);
        xetNewaddressPhone = this.findViewById(R.id.xet_newaddress_phone);
        llNewaddressRegion = this.findViewById(R.id.ll_newaddress_region);
        xetNewaddressDetails = this.findViewById(R.id.xet_newaddress_details);
        swNewaddressSwitch = this.findViewById(R.id.sw_newaddress_switch);
        tvNewaddressArea = this.findViewById(R.id.tv_newaddress_area);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlNewaddressReturn.setOnClickListener(this);
        tvNesaddressSave.setOnClickListener(this);
        llNewaddressRegion.setOnClickListener(this);
        swNewaddressSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch s, boolean isChecked) {
                IsDefault = isChecked;
            }
        });
    }

    private void initBack() {
        if(type == 1){
            xetNewaddressGoods.setText(plantState.getAddressList().get(position).getContacts());
            xetNewaddressPhone.setText(plantState.getAddressList().get(position).getContactMobile());
            String provinces = plantState.getAddressList().get(position).getFullAddress();
            if(provinces.indexOf("区")!= -1){
                //存在
                tvNewaddressArea.setText(provinces.substring(0,provinces.indexOf("区")+1));
                xetNewaddressDetails.setText(provinces.substring(provinces.indexOf("区")+1,provinces.length()));
            } else {
                //不存在
                tvNewaddressArea.setText(provinces.substring(0,provinces.indexOf("县")+1));
                xetNewaddressDetails.setText(provinces.substring(provinces.indexOf("县")+1,provinces.length()));
            }
            swNewaddressSwitch.setChecked(plantState.getAddressList().get(position).isDefault());
        }
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    /**
     * 省 市 区
     *
     * @param options1
     * @param options2
     * @param options3
     * @param v
     */
    private void whitOnWitePick(int options1, int options2, int options3, View v) {
        peroince = plantState.getProvinceList().get(options1).getPickerViewText();
        city = plantState.getProvinceList().get(options1).getCityTheList().get(options2).getRegionName();
        area = plantState.getProvinceList().get(options1).getCityTheList().get(options2).getAreaList().get(options3).getRegionName();
        id = plantState.getProvinceList().get(options1).getCityTheList().get(options2).getAreaList().get(options3).getId();
        tvNewaddressArea.setText(peroince + city + area);
        areaRegionDistrictId = Integer.parseInt(id);
    }

    private void closeDecor() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_newaddress_return:
                finish();
                break;
            //保存
            case R.id.tv_newaddress_save:
                //收货人
                contacts = xetNewaddressGoods.getText().toString();
                if (contacts.length() <= 0) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.contacts), true, 0);
                    return;
                }
                //电话
                contactNumber = xetNewaddressPhone.getText().toString();
                if (contactNumber.length() <= 0) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.contactNumber), true, 0);
                    return;
                }
                //是否选择了地区
                if (areaRegionDistrictId == -1) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.crId), true, 0);
                    return;
                }
                //详细地址是否输入
                String detailsAddress = xetNewaddressDetails.getText().toString();
                if (detailsAddress.length() <= 0) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.detaisl_address), true, 0);
                    return;
                }

                if (detailsAddress.length() >= 5) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.detaisl_address_number), true, 0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isAddress(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户添加收货地址解密失败---" + data);
                            //刷新失败
                            return;
                        }
                        Log.e(TAG, "---用户添加收货地址解密成功---" + data);
//                        plantState.initToast(context, data, true, 0);
                        eventBus.post(new NewAddressFind());
                        finish();
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //crId
                params.put("crId",crId);
                //用户id
                params.put("consumerId",consumerId);
                //联系人
                params.put("contacts",contacts);
                //联系人电话
                params.put("contactNumber",contactNumber);
                //区id
                params.put("areaRegionDistrictId",areaRegionDistrictId);
                //详细地址
                detailedAddress = detailsAddress;
                params.put("detailedAddress",detailedAddress);
                //是否为默认地址
                params.put("IsDefault", IsDefault);
                int random = plantState.getRandom();
                String sign = random+""+crId+consumerId+contacts+contactNumber+areaRegionDistrictId+detailedAddress+IsDefault;
                Log.e(TAG, "---明文---" + sign);
                String signEncrypt = null;
                try {
                    signEncrypt = DESUtils.encryptDES(sign, Constants.secretKey.substring(0, 8));
                    Log.e(TAG, "---加密成功---");
                } catch (Exception e) {
                    Log.e(TAG, "---加密成功---");
                    e.printStackTrace();
                }
                if (signEncrypt == null) {
                    plantState.initToast(context, "加密失败", true, 0);
                    return;
                }
                params.put("random", random);
                params.put("sign", signEncrypt);
                httpRequestWrap.send(PlantAddress.USER_RECEIVING, params);
                break;
            //所在地区
            case R.id.ll_newaddress_region:
                closeDecor();
                cityJSON.optionsPickerWith(newAddressActivity, context, plantState.getOptions1Items(), plantState.getOptions2Items(), plantState.getOptions3Items());
                cityJSON.setOnWitePickListener(new OnWitePickListener() {
                    @Override
                    public void onWitePick(int options1, int option2, int options3, View v) {
                        whitOnWitePick(options1, option2, options3, v);
                    }
                });
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
