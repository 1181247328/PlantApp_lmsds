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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_find.Login;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantPreferences;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 昵称设置
 * Created by liu on 2017/12/15.
 */

public class NickNameActivity extends BaseActivity {

    private String TAG = NickNameActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    private Gson gson = new Gson();

    private EventBus eventBus = EventBus.getDefault();

    //返回
    @BindView(R.id.rl_nickname_return)
    public RelativeLayout rlNicknameReturn = null;

    //确定
    @BindView(R.id.tv_nickname_determine)
    public TextView tvNicknameDetermine = null;

    @BindView(R.id.et_nickname_input)
    public TextView etNicknameInput = null;

    private String nickName = null;

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
        setContentView(R.layout.activity_nickname);

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
        ButterKnife.bind(this);
    }

    private void initView() {
    }

    private void initAdapter() {

    }

    private void initListener() {
    }

    private void initBack() {
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @OnClick({R.id.rl_nickname_return,R.id.tv_nickname_determine})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_nickname_return:
                finish();
                break;
            //确定
            case R.id.tv_nickname_determine:
                nickName = etNicknameInput.getText().toString();
                if(nickName.length()<=0){
                    plantState.initToast(context,plantState.getPlantString(context,R.string.nickname_not),true,0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.nickname), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isColltion(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户昵称修改解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---用户昵称修改解密成功---" + data);
                        plantState.getUser().setNickName(nickName);
                        PlantPreferences.setLogUserComment(context,plantState.getUser());
                        eventBus.post(new Login());
                        plantState.initToast(context,data,true,0);
                        finish();
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                int consumerId = plantState.getUser().getConsumerId();
                params.put("consumerId", consumerId);
                params.put("nickName", nickName);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + consumerId + nickName;
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
                httpRequestWrap.send(PlantAddress.USER_NICKNAME, params);
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
