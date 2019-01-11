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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_class.User;
import com.cdqf.plant_find.Login;
import com.cdqf.plant_hear.FileUtil;
import com.cdqf.plant_hear.PersonalDilogFragment;
import com.cdqf.plant_hear.ShelvesImageFind;
import com.cdqf.plant_image.Base64Img;
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
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import cn.addapp.pickers.common.LineConfig;
import cn.addapp.pickers.listeners.OnItemPickListener;
import cn.addapp.pickers.picker.SinglePicker;
import de.greenrobot.event.EventBus;

/**
 * 个人资料
 * Created by liu on 2017/11/14.
 */

public class PersonalDataActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PersonalDataActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    private EventBus eventBus = EventBus.getDefault();

    //返回
    private RelativeLayout rlPersonaldataReturn = null;

    //头像
    private LinearLayout llPersonaldataHear = null;

    private ImageView ivPersonaldataHear = null;

    //昵称
    private LinearLayout llPersonaldataNickname = null;

    private TextView tvPersonaldataNick = null;

    //性别
    private LinearLayout llPersonaldataGender = null;

    private TextView tvPersonaldataSex = null;

    private Gson gson = new Gson();

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
        setContentView(R.layout.activity_personaldata);

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
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        imageLoader = plantState.getImageLoader(context);
    }

    private void initView() {
        rlPersonaldataReturn = this.findViewById(R.id.rl_personaldata_return);
        llPersonaldataHear = this.findViewById(R.id.ll_personaldata_hear);
        ivPersonaldataHear = this.findViewById(R.id.iv_personaldata_hear);
        llPersonaldataNickname = this.findViewById(R.id.ll_personaldata_nickname);
        tvPersonaldataNick = this.findViewById(R.id.tv_personaldata_nick);
        llPersonaldataGender = this.findViewById(R.id.ll_personaldata_gender);
        tvPersonaldataSex = this.findViewById(R.id.tv_personaldata_sex);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlPersonaldataReturn.setOnClickListener(this);
        llPersonaldataHear.setOnClickListener(this);
        llPersonaldataGender.setOnClickListener(this);
        llPersonaldataNickname.setOnClickListener(this);
    }

    private void initBack() {
        imageLoader.displayImage(plantState.getUser().getImgAvatat(), ivPersonaldataHear, plantState.getImageLoaderOptions(R.mipmap.login_hear, R.mipmap.login_hear, R.mipmap.login_hear));
        tvPersonaldataNick.setText(plantState.getUser().getUserName());
        userAge();
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    private void userAge(){
        String age;
        switch (plantState.getUser().getGender()) {
            case 0:
                age = "未知";
                break;
            case 1:
                age = "男";
                break;
            case 2:
                age = "女";
                break;
            default:
                age = "未知";
                //TODO
                break;
        }
        tvPersonaldataSex.setText(age);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_personaldata_return:
                finish();
                break;
            //头像
            case R.id.ll_personaldata_hear:
                PersonalDilogFragment personalDilogFragment = new PersonalDilogFragment();
                personalDilogFragment.show(getSupportFragmentManager(), "更换头像");
                break;
            //昵称
            case R.id.ll_personaldata_nickname:
                initIntent(NickNameActivity.class);
                break;
            //性别
            case R.id.ll_personaldata_gender:
                SinglePicker<String> picker = new SinglePicker<>(PersonalDataActivity.this, new String[]{"男", "女", "未知"});
                picker.setCanLoop(true);//不禁用循环
                picker.setTopBackgroundColor(0xFFEEEEEE);
                picker.setTopHeight(50);
                picker.setTopLineColor(0xFF33B5E5);
                picker.setTopLineHeight(1);
                picker.setTitleText("请选择");
                picker.setTitleTextColor(0xFF999999);
                picker.setTitleTextSize(12);
                picker.setCancelTextColor(0xFF33B5E5);
                picker.setCancelTextSize(13);
                picker.setSubmitTextColor(0xFF33B5E5);
                picker.setSubmitTextSize(13);
                picker.setSelectedTextColor(0xFFEE0000);
                picker.setUnSelectedTextColor(0xFF999999);
                LineConfig config = new LineConfig();
                config.setColor(0xFFEE0000);//线颜色
                config.setAlpha(140);//线透明度
                config.setRatio((float) (1.0 / 8.0));//线比率
                picker.setLineConfig(config);
                picker.setItemWidth(200);
                picker.setBackgroundColor(0xFFE1E1E1);
                picker.setSelectedIndex(0);
                picker.setOnItemPickListener(new OnItemPickListener<String>() {
                    @Override
                    public void onItemPicked(int index, String item) {
                        final int gender=index+1;
                        httpRequestWrap.setMethod(HttpRequestWrap.POST);
                        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.nickname), new OnResponseHandler() {
                            @Override
                            public void onResponse(String result, RequestStatus status) {
                                String data = Errer.isColltion(context, result, status);
                                if (data == null) {
                                    Log.e(TAG, "---用户性别修改解密失败---" + data);
                                    return;
                                }
                                Log.e(TAG, "---用户性别修改解密成功---" + data);
                                plantState.getUser().setAge(gender);
                                PlantPreferences.setLogUserComment(context,plantState.getUser());
                                userAge();
                                plantState.initToast(context,data,true,0);
                            }
                        }));
                        Map<String, Object> params = new HashMap<String, Object>();
                        int consumerId = plantState.getUser().getConsumerId();
                        params.put("consumerId", consumerId);
                        params.put("gender", gender);
                        //随机数
                        int random = plantState.getRandom();
                        String sign = random + "" + consumerId + gender;
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
                        httpRequestWrap.send(PlantAddress.USER_AGE, params);
                    }
                });
                picker.show();
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
     * 图片
     *
     * @param s
     */
    public void onEventMainThread(ShelvesImageFind s) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.user_haer), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---用户像失败---" + data);
                    return;
                }
                Log.e(TAG, "---用户头像成功---" + data);
                User user = new User();
                user = gson.fromJson(data,User.class);
                plantState.setUser(user);
                PlantPreferences.setLogUserComment(context,user);
                imageLoader.displayImage(plantState.getUser().getImgAvatat(), ivPersonaldataHear, plantState.getImageLoaderOptions(R.mipmap.login_hear, R.mipmap.login_hear, R.mipmap.login_hear));
                eventBus.post(new Login());
                plantState.initToast(context,context.getResources().getString(R.string.login_complete),true,0);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        String imageHear = "data:image/jpeg;base64,"+Base64Img.GetImageStrFromPath(FileUtil.IMG_CACHE4);
        Log.e(TAG, "---头像的base64---" + imageHear);
        params.put("image", imageHear);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + consumerId + imageHear;
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
        httpRequestWrap.send(PlantAddress.USER_HEAR, params);
    }

    public void onEventMainThread(Login l){
        imageLoader.displayImage(plantState.getUser().getImgAvatat(),ivPersonaldataHear, plantState.getImageLoaderOptions(R.mipmap.login_hear,R.mipmap.login_hear,R.mipmap.login_hear));
        tvPersonaldataNick.setText(plantState.getUser().getNickName());
    }
}
