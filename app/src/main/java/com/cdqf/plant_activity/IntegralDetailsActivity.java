package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_class.IntegralDetails;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.IntegralFind;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 积分详情
 * Created by liu on 2018/1/15.
 */

public class IntegralDetailsActivity extends BaseActivity {

    private String TAG = IntegralDetailsActivity.class.getSimpleName();

    public static IntegralDetailsActivity integralDetailsActivity = null;

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    private Gson gson = new Gson();

    //返回
    @BindView(R.id.rl_indetails_return)
    public RelativeLayout rlIndetailsReturn = null;

    //主题
    @BindView(R.id.tv_indetails_destination)
    public TextView tvIndetailsDestination = null;

    //图片
    @BindView(R.id.ba_indetails_carousel)
    public Banner baIndetailsCarousel = null;

    //商品名称
    @BindView(R.id.tv_indetails_name)
    public TextView tvIndetailsName = null;

    //积分
    @BindView(R.id.tv_indetails_number)
    public TextView tvIndetailsNumber = null;

    //原价
    @BindView(R.id.tv_indetails_sale)
    public TextView tvIndetailsSale = null;

    //商品简介
    @BindView(R.id.htv_indetails_introduction)
    public HtmlTextView htvIndetailsIntroduction = null;

    //规定
    @BindView(R.id.tv_indetails_rules)
    public TextView tvIndetailsRules = null;

    //确定兑换
    @BindView(R.id.tv_indetails_confirm)
    public TextView tvIndetailsConfirm = null;

    private int position = 0;

    private IntegralDetails integralDetails = null;

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
        setContentView(R.layout.activity_integraldetails);

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
        ButterKnife.bind(this);
        integralDetailsActivity = this;
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        httpRequestWrap = new HttpRequestWrap(context);
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
    }

    private void initAdapter() {

    }

    private void initListener() {

    }

    private void initBack() {
        //添加中划线并清晰
        tvIndetailsSale.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        initPull();
    }

    private void bannerCarousel() {
        baIndetailsCarousel.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        baIndetailsCarousel.setImageLoader(new GlideImageLoader());
        baIndetailsCarousel.setImages(integralDetails.getPicList());
        baIndetailsCarousel.setBannerAnimation(Transformer.Default);
        baIndetailsCarousel.isAutoPlay(true);
        baIndetailsCarousel.setDelayTime(3000);
        baIndetailsCarousel.setIndicatorGravity(BannerConfig.CENTER);
        baIndetailsCarousel.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
        baIndetailsCarousel.start();
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取积分详情解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取积分详情解密成功---" + data);
                integralDetails = new IntegralDetails();
                integralDetails = gson.fromJson(data, IntegralDetails.class);
                tvIndetailsDestination.setText(integralDetails.getCommName());
                tvIndetailsName.setText(integralDetails.getCommName());
                tvIndetailsNumber.setText(integralDetails.getUnitPrice() + "");
                htvIndetailsIntroduction.setHtml(integralDetails.getIntroduction(), new HtmlHttpImageGetter(htvIndetailsIntroduction));
                tvIndetailsRules.setText(context.getResources().getString(R.string.rules) + integralDetails.getTel());
                //图片
                bannerCarousel();
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int commId = plantState.getIntegralList().get(position).getCommId();
        params.put("commId", commId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + commId;
        Log.e(TAG, "---明文---" + random);
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
        httpRequestWrap.send(PlantAddress.COMMDETAILS, params);
    }

    private void initIntent(Class<?> activity, String text) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @OnClick({R.id.rl_indetails_return, R.id.tv_indetails_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_indetails_return:
                finish();
                break;
            //确定兑换
            case R.id.tv_indetails_confirm:
                if (!plantState.isLogin()) {
                    plantState.initToast(context, "请先登录", true, 0);
                    return;
                }
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt("是否提交兑换", 25);
                promptDilogFragment.show(getSupportFragmentManager(), "是否提交兑换");
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

    public void onEventMainThread(IntegralFind i) {
        Intent intent = new Intent(context, SettlementIntegralActivity.class);
        intent.putExtra("commId", integralDetails.getCommId());
        intent.putExtra("commHttpPic", integralDetails.getPicList().get(0).getHttpPic());
        intent.putExtra("commName", integralDetails.getCommName());
        intent.putExtra("unitPrice", integralDetails.getUnitPrice());
        startActivity(intent);
    }

    class GlideImageLoader extends com.youth.banner.loader.ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            IntegralDetails.PicList picList = (IntegralDetails.PicList) path;
            imageLoader.displayImage(
                    picList.getHttpPic(),
                    imageView,
                    plantState.getImageLoaderOptions(
                            R.mipmap.not_loaded,
                            R.mipmap.not_loaded,
                            R.mipmap.not_loaded));
        }
    }

}
