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

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
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

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liu on 2017/12/27.
 */

public class IntroductionActivity extends BaseActivity {
    private String TAG = IntroductionActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    @BindView(R.id.rl_introduction_return)
    public RelativeLayout rlIntroductionReturn = null;

    //内容
    @BindView(R.id.htv_introduction_content)
    public HtmlTextView htvIntroductionContent = null;

    private int type;

    private String text = "<h2>广西药用植物园位于广西壮族自治区南宁市厢竹大道，创建于1959年，占地面积202公顷，是中国对外（国际）开放的二十一个大型植物园之一。建园52年来，广西药用植物园共收集、保存活体药用植物品种5600多种，其中珍稀濒危药用植物100多种。</h2><img src=\"http://ofrf20oms.bkt.clouddn.com/Clannad.jpg\"/><h2>广西药用植物园坐落在距南宁市区8公里的东郊山峦，占地200多万平方米，是中国及东南亚地区最大的药用植物园之一。园内林木苍翠，藤蔓纵横，加上棚架点缀其间，小桥流水更添雅趣。该园已成立了旅游公司，正式推出广西药用植物园一日游，可以大规模地接待游客。广西药用植物园是一座容游览、科研、教学和生产于一体的综合性园地。</h2>";

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
        setContentView(R.layout.activity_introduction);

        //API>=20以上用于沉侵式菜单栏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            //沉侵
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.plant_background));
        }

        initAgo();

        initView();

        initListener();

        initBack();
    }


    private void initAgo() {
        context = this;
        httpRequestWrap = new HttpRequestWrap(context);
        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);
        imageLoader = plantState.getImageLoader(context);
        ButterKnife.bind(this);
    }

    private void initView() {

    }

    private void initListener() {

    }

    private void initBack() {
//        htvForContent.setHtml(text,new HtmlHttpImageGetter(htvForContent));
        newsContext();
    }

    private void newsContext(){
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data =  Errer.isResult(context,result,status);
                if (data == null) {
                    Log.e(TAG, "---获取景区介绍解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取景区介绍解密成功---" + data);
                htvIntroductionContent.setHtml(data,new HtmlHttpImageGetter(htvIntroductionContent));
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        String describeInfoType;
        if(type == 0) {
            describeInfoType = "1";
            params.put("describeInfoType", describeInfoType);
        } else {
            describeInfoType = "0";
            params.put("describeInfoType", describeInfoType);
        }
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" +describeInfoType;
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
        httpRequestWrap.send(PlantAddress.USER_INTRODUCTION, params);
    }


    @OnClick({R.id.rl_introduction_return})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_introduction_return:
                finish();
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
