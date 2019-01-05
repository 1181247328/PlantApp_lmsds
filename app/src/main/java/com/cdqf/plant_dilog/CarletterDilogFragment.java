package com.cdqf.plant_dilog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_class.Carletter;
import com.cdqf.plant_find.CarletterFind;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 温馨提示
 * Created by liu on 2017/11/15.
 */

public class CarletterDilogFragment extends DialogFragment implements View.OnClickListener {

    private String TAG = RegionDilogFragment.class.getSimpleName();

    private View view = null;

    private HttpRequestWrap httpRequestWrap = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private Gson gson = new Gson();

    //确定出场
    private TextView tvCarletterDilogAppearance = null;

    //暂不出场
    private TextView tvCarletterDilogOut = null;

    //车辆号
    private String platNumber = "";

    private Carletter carletter = new Carletter();

    public void initPlatNumber(String platNumber) {
        this.platNumber = platNumber;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        view = inflater.inflate(R.layout.dilog_carletter, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //初始化前
        initAgo();

        //初始化控件
        initView();

        //适配器
        initAdapter();

        //注册监听器
        initListener();

        //初始化后
        initBack();

        return view;
    }

    /**
     * 初始化前
     */
    private void initAgo() {
        httpRequestWrap = new HttpRequestWrap(getContext());
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tvCarletterDilogAppearance = (TextView) view.findViewById(R.id.tv_carletter_dilog_appearance);
        tvCarletterDilogOut = (TextView) view.findViewById(R.id.tv_carletter_dilog_out);
    }

    private void initAdapter() {

    }

    /**
     * 注册监听器
     */
    private void initListener() {
        tvCarletterDilogAppearance.setOnClickListener(this);
        tvCarletterDilogOut.setOnClickListener(this);
    }

    /**
     * 初始化后
     */
    private void initBack() {
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels - 100, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //确定出场
            case R.id.tv_carletter_dilog_appearance:
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isCarletter(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---确认出场车辆信息解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---确认出场车辆信息解密成功---" + data);
                        carletter = gson.fromJson(data, Carletter.class);
                        eventBus.post(new CarletterFind(carletter));
                        dismiss();
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //用户id
                int consumerId = plantState.getUser().getConsumerId();
                params.put("ConsumerId", consumerId);
                //车牌号
                params.put("PlateNumber", platNumber);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + consumerId + platNumber;
                Log.e(TAG, "---明文---" + random + "---" + consumerId + "---" + platNumber);
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
                    plantState.initToast(getContext(), "加密失败", true, 0);
                }
                //随机数
                params.put("random", random);
                params.put("sign", signEncrypt);
                httpRequestWrap.send(PlantAddress.CART_PAYORDER, params);
                break;
            //暂不出场
            case R.id.tv_carletter_dilog_out:
                dismiss();
                break;
        }
    }
}
