package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.DetailsCommAdapter;
import com.cdqf.plant_adapter.GoodsQualityAdapter;
import com.cdqf.plant_adapter.GoodsRecommendedAdapter;
import com.cdqf.plant_class.Evaluation;
import com.cdqf.plant_class.GoodsDetails;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.PromptFind;
import com.cdqf.plant_find.ShopColltionFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_niengridview.NineGridTestLayout;
import com.cdqf.plant_niengridview.NineGridTestModel;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.MyGridView;
import com.gcssloop.widget.RCRelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.cdqf.plant_lmsd.R.id.rl_details_cart;
import static com.cdqf.plant_lmsd.R.id.tv_details_courier;


/**
 * 商品详情
 * Created by liu on 2017/11/9.
 */

public class GoodsDetailsActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = GoodsDetailsActivity.class.getSimpleName();

    public static GoodsDetailsActivity goodsDetailsActivity = null;

    private Context context = null;

    private Gson gson = new Gson();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //滑动
    private ScrollView svDetailsSliding = null;

    //返回
    private RelativeLayout rlDetailsReturn = null;

    //购物车
    private RelativeLayout rlDetailsCart = null;

    //图片
    private ImageView ivDetailsPicture = null;

    //商品名
    private TextView tvDetailsName = null;

    //价格
    private TextView tvDetailsPrice = null;

    //原价
    private TextView tvDetailsOriginal = null;

    //分享商品
    private LinearLayout llDeatilsShare = null;

    //快递
    private TextView tvDetailsCourier = null;

    //月销
    private TextView tvDetailsPin = null;

    //优惠
    private LinearLayout llDetailsCoupons = null;

    //商品品质
    private MyGridView mgvDetailsBrand = null;

    private GoodsQualityAdapter goodsQualityAdapter = null;

    //评论数量
    private TextView tvDetailsEmnumber = null;

    //无评论
    private LinearLayout llDetailsComments = null;

    //评论
    private ListView lvDetailsComments = null;
    private DetailsCommAdapter detailsCommAdapter = null;

    private NineGridTestLayout nglDetailsList = null;

    //查看全部评价
    private RCRelativeLayout rcrlDetailsEvaluation = null;

    //详情
    private HtmlTextView htvDetailsTrademark = null;

    //推荐
    private MyGridView mgvDetailsRecommended = null;
    private GoodsRecommendedAdapter goodsRecommendedAdapter = null;

    //收藏
    private LinearLayout llDetailsCollection = null;

    //收藏状态
    private ImageView ivDetailsCollection = null;

    //收藏
    private TextView tvDetailsCollection = null;

    //加入购物车
    private TextView tvDetailsAddcart = null;

    //立即购买
    private TextView tvDetailsBuy = null;

    private int position = 0;

    private int type = 0;

    private boolean isCollection = false;

    private List<NineGridTestModel> mList = new ArrayList<>();

    private String[] mUrls = new String[]{"http://d.hiphotos.baidu.com/image/h%3D200/sign=201258cbcd80653864eaa313a7dca115/ca1349540923dd54e54f7aedd609b3de9c824873.jpg",
            "http://img3.fengniao.com/forum/attachpics/537/165/21472986.jpg",
            "http://d.hiphotos.baidu.com/image/h%3D200/sign=ea218b2c5566d01661199928a729d498/a08b87d6277f9e2fd4f215e91830e924b999f308.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3445377427,2645691367&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2644422079,4250545639&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=1444023808,3753293381&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=882039601,2636712663&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=4119861953,350096499&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2437456944,1135705439&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3251359643,4211266111&fm=21&gp=0.jpg",
            "http://img4.duitang.com/uploads/item/201506/11/20150611000809_yFe5Z.jpeg",
            "http://img5.imgtn.bdimg.com/it/u=1717647885,4193212272&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2024625579,507531332&fm=21&gp=0.jpg"};

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //无评论
                case 0x001:
                    llDetailsComments.setVisibility(View.VISIBLE);
                    lvDetailsComments.setVisibility(View.GONE);
                    break;
                //有评论
                case 0x002:
                    llDetailsComments.setVisibility(View.GONE);
                    lvDetailsComments.setVisibility(View.VISIBLE);
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
        setContentView(R.layout.activity_goodsdetails);

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
        goodsDetailsActivity = this;
        httpRequestWrap = new HttpRequestWrap(context);
        imageLoader = plantState.getImageLoader(context);
        if (!eventBus.isRegistered(context)) {
            eventBus.register(context);
        }
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        position = intent.getIntExtra("position", 0);
    }

    private void initView() {
        svDetailsSliding = this.findViewById(R.id.sv_details_sliding);
        rlDetailsReturn = this.findViewById(R.id.rl_details_return);
        rlDetailsCart = this.findViewById(rl_details_cart);
        ivDetailsPicture = this.findViewById(R.id.iv_details_picture);
        tvDetailsName = this.findViewById(R.id.tv_details_name);
        tvDetailsPrice = this.findViewById(R.id.tv_details_price);
        tvDetailsOriginal = this.findViewById(R.id.tv_details_original);
        llDeatilsShare = this.findViewById(R.id.ll_deatils_share);
        tvDetailsCourier = this.findViewById(tv_details_courier);
        tvDetailsPin = this.findViewById(R.id.tv_details_pin);
        mgvDetailsBrand = this.findViewById(R.id.mgv_details_brand);
        tvDetailsEmnumber = this.findViewById(R.id.tv_details_emnumber);
        htvDetailsTrademark = this.findViewById(R.id.htv_details_trademark);
        mgvDetailsRecommended = this.findViewById(R.id.mgv_details_recommended);
        llDetailsCollection = this.findViewById(R.id.ll_details_collection);
        tvDetailsAddcart = this.findViewById(R.id.tv_details_addcart);
        ivDetailsCollection = this.findViewById(R.id.iv_details_collection);
        tvDetailsCollection = this.findViewById(R.id.tv_details_collection);
        tvDetailsBuy = this.findViewById(R.id.tv_details_buy);
        rcrlDetailsEvaluation = this.findViewById(R.id.rcrl_details_evaluation);
        llDetailsComments = this.findViewById(R.id.ll_details_comments);
        lvDetailsComments = this.findViewById(R.id.lv_details_comments);
        nglDetailsList = this.findViewById(R.id.ngl_details_list);

    }

    private void initAdapter() {
        goodsQualityAdapter = new GoodsQualityAdapter(context);
        mgvDetailsBrand.setAdapter(goodsQualityAdapter);
        detailsCommAdapter = new DetailsCommAdapter(context);
        lvDetailsComments.setAdapter(detailsCommAdapter);
        goodsRecommendedAdapter = new GoodsRecommendedAdapter(context, imageLoader, plantState.getCommlist());
        mgvDetailsRecommended.setAdapter(goodsRecommendedAdapter);
    }

    private void initListener() {
        rlDetailsReturn.setOnClickListener(this);
        rlDetailsCart.setOnClickListener(this);
        llDeatilsShare.setOnClickListener(this);
        llDetailsCollection.setOnClickListener(this);
        tvDetailsAddcart.setOnClickListener(this);
        tvDetailsBuy.setOnClickListener(this);
        rcrlDetailsEvaluation.setOnClickListener(this);
        mgvDetailsRecommended.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                plantState.getEvaluationList().clear();
                if (detailsCommAdapter != null) {
                    detailsCommAdapter.notifyDataSetChanged();
                }
                tvDetailsEmnumber.setText("商品评论(0)条");
                nglDetailsList.setUrlList();
                type = 0;
                GoodsDetailsActivity.this.position = position;
                initShop();
                initFish();
            }
        });
    }

    private void initBack() {
        svDetailsSliding.smoothScrollTo(0, 0);
        svDetailsSliding.setFillViewport(true);
        //添加中划线并清晰
        tvDetailsOriginal.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        initShop();
        initFish();
    }

    /**
     * 商品详情
     */
    private void initShop() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "请稍候", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---商品详情解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---商品详情解密---" + data);
                svDetailsSliding.smoothScrollTo(0, 0);
                GoodsDetails goodsDetails = new GoodsDetails();
                goodsDetails = gson.fromJson(data, GoodsDetails.class);
                plantState.setGoodsDetails(goodsDetails);
                //图片
                imageLoader.displayImage(goodsDetails.getPicturelist().get(0).getImgpicture(), ivDetailsPicture, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
                //商品名称
                tvDetailsName.setText(goodsDetails.getCommName());
                //价格
                tvDetailsPrice.setText(goodsDetails.getPrice() + "");
                //是否免邮
                if (goodsDetails.isPostFree()) {
                    tvDetailsCourier.setText(context.getResources().getString(R.string.post_free));
                } else {
                    tvDetailsCourier.setText(context.getResources().getString(R.string.post_free_cost) + "15元");
                }
                //月销
                tvDetailsPin.setText(context.getResources().getString(R.string.details_pin) + goodsDetails.getPayer() + "笔");
                //详情
                htvDetailsTrademark.setHtml(goodsDetails.getIntroduction(), new HtmlHttpImageGetter(htvDetailsTrademark));
//                htvDetailsTrademark.setHtml(text,new HtmlHttpImageGetter(htvDetailsTrademark));
                goodsRecommendedAdapter.notifyDataSetChanged();
                isCollection = goodsDetails.isCollection();
                //是否收藏
                if (isCollection) {
                    ivDetailsCollection.setImageResource(R.mipmap.details_not_collection);
                    tvDetailsCollection.setText("取消收藏");
                } else {
                    ivDetailsCollection.setImageResource(R.mipmap.details_collection);
                    tvDetailsCollection.setText("收藏");
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //商品id
        int commId = 0;
        switch (type) {
            case 0:
                commId = plantState.getCommlist().get(position).getCommid();
                break;
            case 1:
                commId = plantState.getGoodlist().get(position).getCommid();
                break;
            case 2:
                commId = plantState.getSearchList().get(position).getCommId();
                break;
            case 3:
                commId = plantState.getShopColltionList().get(position).getCommId();
                break;
        }
        params.put("commId", commId);
        //用户id=1,此为测试ID
        //TODO
        int consumerId = plantState.getUser().getConsumerId();
        params.put("consumerId", consumerId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + commId + consumerId;
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
        httpRequestWrap.send(PlantAddress.SHOP_DATILS, params);
    }

    /**
     * 评论
     */
    private void initFish() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---商品评论解密失败---" + data);
                    tvDetailsEmnumber.setText("商品评论(0)条");
                    return;
                }
                if (TextUtils.equals(data, "1001")) {
                    Log.e(TAG, "---商品评论列表为空---" + data);
                    handler.sendEmptyMessage(0x001);
                    tvDetailsEmnumber.setText("商品评论(0)条");
                    return;
                }
                Log.e(TAG, "---商品评论解密---" + data);
                handler.sendEmptyMessage(0x002);
                plantState.getEvaluationList().clear();
                //评论数量
                int commentCount = JSON.parseObject(data).getInteger("commentCount");
                tvDetailsEmnumber.setText("商品评论(" + commentCount + ")条");
                data = JSON.parseObject(data).getString("commentList");
                List<Evaluation> evaluationList = gson.fromJson(data, new TypeToken<List<Evaluation>>() {
                }.getType());
                for (int i = 0; i < evaluationList.get(0).getPicList().size(); i++) {
                    evaluationList.get(0).getUrlList().add(evaluationList.get(0).getPicList().get(i).getHttpUrl());
                }
                plantState.setEvaluationList(evaluationList);
                if (detailsCommAdapter != null) {
                    detailsCommAdapter.notifyDataSetChanged();

                }
                if (evaluationList.get(0).getUrlList().size() >= 0) {
                    nglDetailsList.setUrlList(evaluationList.get(0).getUrlList());
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //商品id
        int commId = 0;
        switch (type) {
            case 0:
                commId = plantState.getCommlist().get(position).getCommid();
                break;
            case 1:
                commId = plantState.getGoodlist().get(position).getCommid();
                break;
            case 2:
                commId = plantState.getSearchList().get(position).getCommId();
                break;
            case 3:
                commId = plantState.getShopColltionList().get(position).getCommId();
                break;
        }
        params.put("commId", commId);
        //当前页
        int pageIndex = 1;
        params.put("pageIndex", pageIndex);
        //当前页的数量
        int pageCount = 10;
        params.put("pageCount", pageCount);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + commId + pageIndex + pageCount;
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
        httpRequestWrap.send(PlantAddress.SHOP_FIST, params);
    }

    private boolean isLogin(int type) {
        if (!plantState.isLogin()) {
//            PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
//            promptDilogFragment.initPrompt(context.getResources().getString(R.string.not_login), type);
//            promptDilogFragment.show(getSupportFragmentManager(), "提示前住登录");
            plantState.initToast(context, "请先登录", true, 0);
            return false;
        }
        return true;
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(context, activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, String commIds, String numbers) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("commIds", commIds);
        intent.putExtra("numbers", numbers);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_details_return:
                plantState.getEvaluationList().clear();
                if (type == 3) {
                    eventBus.post(new ShopColltionFind());
                }
                finish();
                break;
            //购物车
            case R.id.rl_details_cart:
                if (isLogin(3)) {
                    initIntent(CartActivity.class);
                }
                break;
            //分享
            case R.id.ll_deatils_share:
//                if (isLogin(3)) {
//                    ShareDilogFragment shareDilogFragment = new ShareDilogFragment();
////                    shareDilogFragment.shareContext(context.getResources().getString(R.string.share),4);
//                    shareDilogFragment.show(getSupportFragmentManager(), "分享");
//                }
                plantState.initShar(context, tvDetailsName.getText().toString());
                break;
            //收藏
            case R.id.ll_details_collection:
                if (isLogin(3)) {
                    PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                    if (isCollection) {
                        promptDilogFragment.initPrompt(context.getResources().getString(R.string.not_collection), 4);
                    } else {
                        promptDilogFragment.initPrompt(context.getResources().getString(R.string.collection), 4);
                    }
                    promptDilogFragment.show(getSupportFragmentManager(), "收藏");
                }
                break;
            //加入购物车
            case R.id.tv_details_addcart:
                if (isLogin(3)) {
                    PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                    promptDilogFragment.initPrompt(context.getResources().getString(R.string.cart), 5);
                    promptDilogFragment.show(getSupportFragmentManager(), "加入购物车");
                }
                break;
            //立即购买
            case R.id.tv_details_buy:
                if (isLogin(3)) {
                    //商品id
                    int commId = 0;
                    switch (type) {
                        case 0:
                            commId = plantState.getCommlist().get(position).getCommid();
                            break;
                        case 1:
                            commId = plantState.getGoodlist().get(position).getCommid();
                            break;
                        case 2:
                            commId = plantState.getSearchList().get(position).getCommId();
                            break;
                    }
                    initIntent(SettlementActivity.class, commId + "", "1");
                }
                break;
            //查看全部评价
            case R.id.rcrl_details_evaluation:
                //商品id
                int commId = 0;
                switch (type) {
                    case 0:
                        commId = plantState.getCommlist().get(position).getCommid();
                        break;
                    case 1:
                        commId = plantState.getGoodlist().get(position).getCommid();
                        break;
                    case 2:
                        commId = plantState.getSearchList().get(position).getCommId();
                        break;
                }
                initIntent(GoodsAllEvaluationActivity.class, commId);
                break;
        }
    }

    /**
     * 提示
     *
     * @param t
     */
    public void onEventMainThread(PromptFind t) {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        switch (t.type) {
            //收藏
            case 4:
                Log.e(TAG, "---收藏---");
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.collection), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isColltion(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---商品收藏失败---" + data);
                            //刷新失败
                            return;
                        }
                        Log.e(TAG, "---商品收藏或取消成功---" + data);
                        if (TextUtils.equals(data, "收藏成功")) {
                            plantState.initToast(context, data, true, 0);
                            ivDetailsCollection.setImageResource(R.mipmap.details_not_collection);
                            tvDetailsCollection.setText("取消收藏");
                            isCollection = true;
                        } else {
                            ivDetailsCollection.setImageResource(R.mipmap.details_collection);
                            tvDetailsCollection.setText("收藏");
                            isCollection = false;
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //收藏类型
                int collectionType = 1;
                params.put("collectionType", collectionType);
                //用户id 现测试为1
                int consumerId = plantState.getUser().getConsumerId();
                params.put("consumerId", consumerId);
                //商品id
                int collectedId = 0;
                switch (type) {
                    case 0:
                        collectedId = plantState.getCommlist().get(position).getCommid();
                        break;
                    case 1:
                        collectedId = plantState.getGoodlist().get(position).getCommid();
                        break;
                    case 2:
                        collectedId = plantState.getSearchList().get(position).getCommId();
                        break;
                    case 3:
                        collectedId = plantState.getShopColltionList().get(position).getCommId();
                        break;
                }
                params.put("collectedId", collectedId);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + collectionType + consumerId + collectedId;
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
                httpRequestWrap.send(PlantAddress.COLLECTION, params);
                break;
            //加入购车
            case 5:
                Log.e(TAG, "---加入购物车---");
                httpRequestWrap.setCallBack(new RequestHandler(context, 1, context.getResources().getString(R.string.cart), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isColltion(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---加入购物车失败---" + data);
                            //刷新失败
                            return;
                        }
                        Log.e(TAG, "---加入购物车成功---" + data);
                        plantState.initToast(context, data, true, 0);
                        initIntent(CartActivity.class);
                    }
                }));
                Map<String, Object> paramss = new HashMap<String, Object>();
                //用户id
                int consumerIds = plantState.getUser().getConsumerId();
                paramss.put("consumerId", consumerIds);
                //商品id
                int commId = 0;
                switch (type) {
                    case 0:
                        commId = plantState.getCommlist().get(position).getCommid();
                        break;
                    case 1:
                        commId = plantState.getGoodlist().get(position).getCommid();
                        break;
                    case 2:
                        commId = plantState.getSearchList().get(position).getCommId();
                        break;
                    case 3:
                        commId = plantState.getShopColltionList().get(position).getCommId();
                        break;
                }
                paramss.put("commId", commId);
                //数量
                int number = 1;
                paramss.put("number", number);
                //随机数
                int randoms = plantState.getRandom();
                String signs = randoms + "" + consumerIds + commId + number;
                Log.e(TAG, "---明文---" + signs);
                //加密文字
                String signEncrypts = null;
                try {
                    signEncrypts = DESUtils.encryptDES(signs, Constants.secretKey.substring(0, 8));
                    Log.e(TAG, "---加密成功---" + signEncrypts);
                } catch (Exception e) {
                    Log.e(TAG, "---加密失败---");
                    e.printStackTrace();
                }
                if (signEncrypts == null) {
                    plantState.initToast(context, "加密失败", true, 0);
                }
                //随机数
                paramss.put("random", randoms);
                paramss.put("sign", signEncrypts);
                httpRequestWrap.send(PlantAddress.CART, paramss);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        plantState.getEvaluationList().clear();
        if (type == 3) {
            eventBus.post(new ShopColltionFind());
        }
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
        eventBus.unregister(context);
    }
}
