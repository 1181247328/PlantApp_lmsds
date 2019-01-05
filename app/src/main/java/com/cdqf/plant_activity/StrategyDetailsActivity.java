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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.StrategyDetailsAdapter;
import com.cdqf.plant_class.StrategyDetails;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.StrategyColltionFind;
import com.cdqf.plant_find.TravelColltionFind;
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
import com.cdqf.plant_view.ListViewForScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


/**
 * 游记详情
 * Created by liu on 2017/10/23.
 */

public class StrategyDetailsActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = StrategyDetailsActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlStrdetailsReturn = null;

    //分享
    private RelativeLayout rlStrdetailsShare = null;

    //收藏
    @BindView(R.id.rl_strdetails_collection)
    public RelativeLayout rlStrdetailsCollection = null;

    @BindView(R.id.iv_strdetails_collection)
    public ImageView ivStrdetailsCollection = null;

    //图片
    private ImageView ivStrdetailsPicture = null;

    //标题
    private TextView tvStrdetailsTitle = null;

    //头像
    private ImageView ivStrdetailsHear = null;

    //姓名
    private TextView tvStrategydetailsName = null;

    //图片数量
    private TextView tvStrategydetailsFigurenubmer = null;

    private ListViewForScrollView lvfsvStrategydetailsList = null;

    private ScrollView svStrdetailsPicture = null;

    //日期
    private TextView tvStrdetailsData = null;

    //收藏数量
    private TextView tvStrategydetailsCollectionnumber = null;

    //评论数量
    private TextView tvStrategydetailsCommentsnumber = null;

    private List<String> coList = null;

    private int position;

    private Gson gson = new Gson();

    private int type = 0;

    private StrategyDetailsAdapter strategyDetailsAdapter = null;

    private boolean isCollection = false;

    private StrategyDetails strategyDetails = new StrategyDetails();


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
        setContentView(R.layout.activity_strategydetails);

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
        coList = new ArrayList<String>();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        rlStrdetailsReturn = this.findViewById(R.id.rl_strdetails_return);
        rlStrdetailsShare = this.findViewById(R.id.rl_strdetails_share);
        ivStrdetailsPicture = this.findViewById(R.id.iv_strdetails_picture);
        tvStrdetailsTitle = this.findViewById(R.id.tv_strdetails_title);
        ivStrdetailsHear = this.findViewById(R.id.iv_strdetails_hear);
        tvStrategydetailsName = this.findViewById(R.id.tv_strategydetails_name);
        tvStrategydetailsFigurenubmer = this.findViewById(R.id.tv_strategydetails_figurenubmer);
        tvStrdetailsData = this.findViewById(R.id.tv_strdetails_data);
        tvStrategydetailsCollectionnumber = this.findViewById(R.id.tv_strategydetails_collectionnumber);
        tvStrategydetailsCommentsnumber = this.findViewById(R.id.tv_strategydetails_commentsnumber);
        lvfsvStrategydetailsList = this.findViewById(R.id.lvfsv_strategydetails_list);
        svStrdetailsPicture = this.findViewById(R.id.sv_strdetails_picture);
    }

    private void initAdapter() {

    }

    private void initListener() {
        rlStrdetailsReturn.setOnClickListener(this);
        rlStrdetailsShare.setOnClickListener(this);
        rlStrdetailsCollection.setOnClickListener(this);
    }

    private void initBack() {
        svStrdetailsPicture.smoothScrollTo(0, 0);
        initDetails();
    }

    /**
     * 游记详情
     */
    private void initDetails() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isAddress(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取游记解密成功---" + data);
                strategyDetails = gson.fromJson(data, StrategyDetails.class);
                imageLoader.displayImage(strategyDetails.getHttpPic(), ivStrdetailsPicture, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                tvStrdetailsTitle.setText(strategyDetails.getTitle());

                imageLoader.displayImage(strategyDetails.getHttpPic(), ivStrdetailsHear, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                tvStrategydetailsName.setText(strategyDetails.getConsumerNickName());
                tvStrategydetailsFigurenubmer.setText(strategyDetails.getPicCount() + "");

                tvStrdetailsData.setText(strategyDetails.getStrEditDate());
                tvStrategydetailsCollectionnumber.setText(strategyDetails.getCollectedCount() + "");
                tvStrategydetailsCommentsnumber.setText(strategyDetails.getCommentCount() + "");
                //是否收藏
                if (strategyDetails.getCollectedCount() > 0) {
                    ivStrdetailsCollection.setImageResource(R.mipmap.details_not_collection);
                } else {
                    ivStrdetailsCollection.setImageResource(R.mipmap.strategy_item_collection);
                }

                coList = gson.fromJson(strategyDetails.getContent(), new TypeToken<List<String>>() {
                }.getType());

                strategyDetailsAdapter = new StrategyDetailsAdapter(context, coList);
                lvfsvStrategydetailsList.setAdapter(strategyDetailsAdapter);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //植物收藏
        int tId = 0;
        if (type == 0) {
            tId = plantState.getStrategyList().get(position).getTravelId();
        } else if (type == 1) {
            tId = plantState.getHasPublishedList().get(position).getTravelId();
        } else if (type == 2) {
            tId = plantState.getTravelCollectionList().get(position).getTrId();
        }
        params.put("tId", tId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + tId;
        Log.e(TAG, "---明文---" + random + "---" + tId);
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
        httpRequestWrap.send(PlantAddress.STRATE_DETAILS, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_strdetails_return:
                finish();
                break;
            //分享
            case R.id.rl_strdetails_share:
                String content = plantState.getStrategyList().get(position).getTitle();
                plantState.initShar(context, content);
//                ShareDilogFragment shareDilogFragment = new ShareDilogFragment();
//                shareDilogFragment.show(getSupportFragmentManager(), "分享");
                break;
            //收藏
            case R.id.rl_strdetails_collection:
                if (!plantState.isLogin()) {
                    plantState.initToast(context, plantState.getPlantString(context, R.string.is_login), true, 0);
                    return;
                }
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                if (strategyDetails.getCollectedCount() > 0) {
                    promptDilogFragment.initPrompt("是否取消收藏该游记!", 23);
                } else {
                    promptDilogFragment.initPrompt("是否收藏该游记!", 23);
                }
                promptDilogFragment.show(getSupportFragmentManager(), "是否收藏该游记");
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
     * 分享
     *
     * @param p
     */
    public void onEventMainThread(StrategyColltionFind p) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.collection_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取游记收藏解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取游记收藏解密成功---" + data);
                if (TextUtils.equals(data,"收藏成功")) {
                    ivStrdetailsCollection.setImageResource(R.mipmap.details_not_collection);
                } else {
                    ivStrdetailsCollection.setImageResource(R.mipmap.strategy_item_collection);
                }
                plantState.initToast(context,data,true,0);
                if (type == 2) {
                    eventBus.post(new TravelColltionFind());
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //游记收藏
        int collectionType = 0;
        params.put("collectionType", collectionType);
        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //收藏对象id
        int collectedId = 0;
        if (type == 0) {
            collectedId = plantState.getStrategyList().get(position).getTravelId();
        } else if (type == 1) {
            collectedId = plantState.getHasPublishedList().get(position).getTravelId();
        } else if (type == 2) {
            collectedId = plantState.getTravelCollectionList().get(position).getTrId();
        }
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
