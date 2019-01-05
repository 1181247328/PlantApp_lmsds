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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_class.PlantDetails;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.PlantCollectionFind;
import com.cdqf.plant_find.ScienceColltionFind;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.codeboy.android.aligntextview.CBAlignTextView;

/**
 * 植物详情
 * Created by liu on 2017/11/23.
 */

public class PlantsDetailsActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PlantsDetailsActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlPlantsdetailsReturn = null;

    //收藏
    private RelativeLayout rlPlantsdetailsCollection = null;

    @BindView(R.id.iv_plantsdetails_collection)
    public ImageView ivPlantsdetailsCollection = null;

    //消息
    private RelativeLayout rlPlantsdetailsShare = null;

    //图片
    private ImageView ivPlantsdetailsPicture = null;

    //标题
    private TextView tvPlantsdetailsName = null;

    //内容
    private CBAlignTextView cbPlantsdetailsContext = null;

    private int position = 0;

    private int type = 0;

    private Gson gson = new Gson();

    private PlantDetails plantDetails = null;

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
        setContentView(R.layout.activity_plantsdetails);

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
        httpRequestWrap = new HttpRequestWrap(context);
        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        type = intent.getIntExtra("type", 0);
        imageLoader = plantState.getImageLoader(context);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        rlPlantsdetailsReturn = (RelativeLayout) this.findViewById(R.id.rl_plantsdetails_return);
        rlPlantsdetailsCollection = (RelativeLayout) this.findViewById(R.id.rl_plantsdetails_collection);
        rlPlantsdetailsShare = (RelativeLayout) this.findViewById(R.id.rl_plantsdetails_share);
        ivPlantsdetailsPicture = (ImageView) this.findViewById(R.id.iv_plantsdetails_picture);
        tvPlantsdetailsName = (TextView) this.findViewById(R.id.tv_plantsdetails_name);
        cbPlantsdetailsContext = (CBAlignTextView) this.findViewById(R.id.cb_plantsdetails_context);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlPlantsdetailsReturn.setOnClickListener(this);
        rlPlantsdetailsCollection.setOnClickListener(this);
        rlPlantsdetailsShare.setOnClickListener(this);
    }

    private void initBack() {
        initPlant();
    }

    /**
     * 植物
     */
    private void initPlant() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取植物详情解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取植物详情解密成功---" + data);
                plantDetails = new PlantDetails();
                plantDetails = gson.fromJson(data, PlantDetails.class);
                imageLoader.displayImage(plantDetails.getPicList().getList().get(0).getHttpUrl(), ivPlantsdetailsPicture, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                tvPlantsdetailsName.setText(plantDetails.getBotanyName());
                cbPlantsdetailsContext.setText(plantDetails.getIntroduction());
                boolean isCollection = plantDetails.isCollection();
                //是否收藏
                if (isCollection) {
                    ivPlantsdetailsCollection.setImageResource(R.mipmap.details_not_collection);
                } else {
                    ivPlantsdetailsCollection.setImageResource(R.mipmap.strategy_item_collection);
                }

            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //当前页
        int botanyId = 0;
        switch (type) {
            case 0:
                botanyId = plantState.getPlant().getList().get(position).getBotanyId();
                break;
            case 1:
                botanyId = plantState.getScienceCollectionList().get(position).getpId();
                break;
        }
        params.put("botanyId", botanyId);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + botanyId + consumerId;
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
        httpRequestWrap.send(PlantAddress.ASK_DETAILS, params);
    }


    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_plantsdetails_return:
                finish();
                break;
            //收藏
            case R.id.rl_plantsdetails_collection:
                if (!plantState.isLogin()) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.is_login), true, 0);
                    return;
                }
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                if(plantDetails.isCollection()) {
                    promptDilogFragment.initPrompt("是否取消收藏该植物!", 2);
                } else {
                    promptDilogFragment.initPrompt("是否收藏该植物!", 2);
                }
                promptDilogFragment.show(getSupportFragmentManager(), "是否收藏植物");
                break;
            //分享
            case R.id.rl_plantsdetails_share:
                String content = cbPlantsdetailsContext.getText().toString();
                plantState.initShar(context, content);
//                ShareDilogFragment shareDilogFragment = new ShareDilogFragment();
//                shareDilogFragment.show(getSupportFragmentManager(),"分享");
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
     * 收藏植物
     *
     * @param p
     */
    public void onEventMainThread(PlantCollectionFind p) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isCode(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---收藏植物失败---" + data);
                    return;
                }
                Log.e(TAG, "---收藏植物成功---" + data);
                boolean isColltion = Boolean.getBoolean(data);
                //是否收藏
                if (isColltion) {
                    ivPlantsdetailsCollection.setImageResource(R.mipmap.details_not_collection);
                } else {
                    ivPlantsdetailsCollection.setImageResource(R.mipmap.strategy_item_collection);
                }
                if(type == 1){
                    eventBus.post(new ScienceColltionFind());
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //植物收藏
        int collectionType = 2;
        params.put("collectionType", collectionType);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //收藏对象id
        int collectedId = plantDetails.getBotanyId();
        params.put("collectedId", collectedId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + collectionType + consumerId + collectedId;
        Log.e(TAG, "---明文---" + random + "---" + collectionType + "---" + consumerId + "---" + collectedId);
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
        httpRequestWrap.send(PlantAddress.ASK_COLLTION, params);
    }
}
