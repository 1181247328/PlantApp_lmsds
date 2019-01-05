package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_lmsd.R;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.util.HashMap;
import java.util.Map;

import static com.cdqf.plant_activity.PassWordOneActivity.passWordOneActivity;
import static com.cdqf.plant_activity.SetActivity.setActivity;

/**
 * 修改密码第二步
 * Created by liu on 2017/11/14.
 */

public class PassWordTwoActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PassWordTwoActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlPaswordtwoReturn = null;

    //新密码
    private XEditText xetPasswordoneNew = null;

    //确认新密码
    private XEditText xetPasswordoneTwo = null;

    //下一步
    private TextView tvPasswordtwoNext = null;

    //旧密码
    private String oldPwd = null;

    //新密码1
    private String newPwd = null;

    //新密码2
    private String newPwdTwo = null;

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
        setContentView(R.layout.activity_passwordtwo);

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
        oldPwd = intent.getStringExtra("oldPwd");
    }

    private void initView() {
        rlPaswordtwoReturn = this.findViewById(R.id.rl_paswordtwo_return);
        xetPasswordoneNew = this.findViewById(R.id.xet_passwordone_new);
        xetPasswordoneTwo = this.findViewById(R.id.xet_passwordtwo_two);
        tvPasswordtwoNext = this.findViewById(R.id.tv_passwordtwo_next);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlPaswordtwoReturn.setOnClickListener(this);
        tvPasswordtwoNext.setOnClickListener(this);
    }

    private void initBack() {
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_paswordtwo_return:
                finish();
                break;
            //下一步
            case R.id.tv_passwordtwo_next:
                newPwd = xetPasswordoneNew.getText().toString();
                newPwdTwo = xetPasswordoneTwo.getText().toString();
                Log.e(TAG,"---新密码---"+newPwd);
                if (newPwd.length() <= 0) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.newpwd_one), true, 0);
                    return;
                }
                if (newPwdTwo.length() <= 0) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.newpwd_two), true, 0);
                    return;
                }
                if (!TextUtils.equals(newPwd,newPwdTwo)) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.newpwd_three), true, 0);
                    return;
                }
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isCode(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---用户密码修改解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---用户密码修改解密成功---" + data);
                        if(TextUtils.equals(data,"修改成功")){
                            plantState.initToast(context,data,true,0);
                            PlantPreferences.clearLogUserComment(context);
                            passWordOneActivity.finish();
                            setActivity.finish();
                            initIntent(LoginActivity.class);
                            finish();
                        } else {
                            //TODO
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                int consumerId = plantState.getUser().getConsumerId();
                params.put("consumerId", consumerId);
                params.put("oldPwd", oldPwd);
                params.put("newPwd", newPwd);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + consumerId + oldPwd + newPwd;
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
                httpRequestWrap.send(PlantAddress.USER_PASSWORD, params);
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
