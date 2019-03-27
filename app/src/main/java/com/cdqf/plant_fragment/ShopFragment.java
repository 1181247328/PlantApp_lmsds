package com.cdqf.plant_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.GoodsActivity;
import com.cdqf.plant_activity.GoodsDetailsActivity;
import com.cdqf.plant_activity.SearchShopActivity;
import com.cdqf.plant_adapter.RecommendedAdapter;
import com.cdqf.plant_adapter.ShopFunctionAdapter;
import com.cdqf.plant_class.Banners;
import com.cdqf.plant_class.Commlist;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_okhttp.OKHttpHanlder;
import com.cdqf.plant_okhttp.OKHttpRequestWrap;
import com.cdqf.plant_okhttp.OnHttpRequest;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_view.MyGridView;
import com.cdqf.plant_view.VerticalSwipeRefreshLayout;
import com.gcssloop.widget.RCRelativeLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 商城
 * Created by liu on 2017/10/19.
 */

public class ShopFragment extends Fragment {

    private String TAG = ShopFragment.class.getSimpleName();

    private View view = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private HttpRequestWrap httpRequestWrap = null;

    @BindView(R.id.srl_shop_pull)
    public VerticalSwipeRefreshLayout srlShopPull = null;

    //滚动页
    @BindView(R.id.sl_shop_scrol)
    public ScrollView slShopScrol = null;

    //搜索
    @BindView(R.id.rcrl_shop_search)
    public RCRelativeLayout rcrlShopSearch = null;

    //轮播图
    @BindView(R.id.mbv_shop_banner)
    public MZBannerView mbvShopBanner = null;

    //分类
    @BindView(R.id.mgv_shop_list)
    public MyGridView mgvShopList = null;

    //推荐商品
    @BindView(R.id.mgv_shop_recommended)
    public MyGridView mgvShopRecommended = null;

    private ShopFunctionAdapter shopFunctionAdapter = null;

    private RecommendedAdapter recommendedAdapter = null;

    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shop, null);

        initAgo();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        ButterKnife.bind(this, view);
        imageLoader = plantState.getImageLoader(getContext());
        httpRequestWrap = new HttpRequestWrap(getContext());
    }

    private void initAdapter() {
        shopFunctionAdapter = new ShopFunctionAdapter(getContext());
        mgvShopList.setAdapter(shopFunctionAdapter);
    }

    private void initListener() {

        srlShopPull.setRefreshing(true);
        srlShopPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initPictures(false);
            }
        });
        mbvShopBanner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int i) {

            }
        });
        //商品分类
        mgvShopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(GoodsActivity.class, position);
            }
        });
        //为您推荐
        mgvShopRecommended.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntentType(GoodsDetailsActivity.class, position, 0);
            }
        });
    }

    private void initBack() {
        srlShopPull.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.immersion));
        slShopScrol.smoothScrollTo(0, 0);
        //首页
        initPictures(true);
        mgvShopList.setOnScrollListener(new PauseOnScrollListener(plantState.getImageLoader(getContext()), true, false));
    }

    private void initBanner(List<Banners> datas) {
        List<String> urlsList = new ArrayList<String>();

        for (Banners s : datas) {
            urlsList.add(s.getImgbanner());
        }
        mbvShopBanner.setPages(urlsList, new MZHolderCreator<BannerViewHolder>() {

            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        mbvShopBanner.setDelayedTime(3000);
        mbvShopBanner.setIndicatorVisible(false);
        mbvShopBanner.setDuration(1000);
        mbvShopBanner.start();
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    private void initIntent(Class<?> activity, int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void initIntentType(Class<?> activity, int posiiton, int type) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position", posiiton);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    //首页轮播图片
    private void initPictures(boolean isToast) {

        Map<String, Object> params = new HashMap<String, Object>();
        //随机数
        int random = plantState.getRandom();
        String sign = random + "";
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        //随机数
        params.put("random", random);
        params.put("sign", signEncrypt);

        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(getContext());
        okHttpRequestWrap.post(PlantAddress.SHOP_HOME, isToast, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---onOkHttpResponse---" + response);
                String data = OKHttpHanlder.isOKHttpResult(getContext(), response);
                if (data == null) {
                    Log.e(TAG, "---首页解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---首页解密---" + data);
                srlShopPull.setRefreshing(false);
                //头部图片
                JSONObject dataJSON = JSON.parseObject(data);
                plantState.getBanners().clear();
                JSONArray banners = dataJSON.getJSONArray("banners");
                for (int i = 0; i < banners.size(); i++) {
                    JSONObject j = banners.getJSONObject(i);
                    //图片
                    String banner = PlantAddress.ADDRESS + j.getString("banner");
                    String imgBanner = j.getString("imgBanner");
                    //id
                    int bannerToCommId = j.getInteger("bannerToCommId");
                    Banners b = new Banners(banner, imgBanner, bannerToCommId);
                    plantState.getBanners().add(b);
                }
                initBanner(plantState.getBanners());
                //推荐
                plantState.getCommlist().clear();
                String shop = dataJSON.getString("commList");
                List<Commlist> commList = gson.fromJson(shop, new TypeToken<List<Commlist>>() {
                }.getType());
                plantState.setCommlist(commList);
//                JSONArray commList = dataJSON.getJSONArray("commList");
//                for (int i = 0; i < commList.size(); i++) {
//                    JSONObject j = commList.getJSONObject(i);
//                    Log.e(TAG, "---商品---" + commList.getString(i));
//                    //商品ID
//                    int commId = j.getInteger("commId");
//                    //商品名称
//                    String commName = j.getString("commName");
//                    //图片地址
//                    String picture = PlantAddress.ADDRESS + j.getString("picture");
//                    String imgPicture = j.getString("imgPicture");
//                    //价格
//                    double price = j.getDoubleValue("price");
//                    Log.e(TAG, "---商品价格---" + price);
//                    //是否包邮
//                    int postage = j.getInteger("postage");
//                    //
//                    boolean isPostFree = j.getBoolean("isPostFree");
//                    //是否推荐
//                    boolean isRecommend = j.getBoolean("isRecommend");
//                    //推荐顺序
//                    int recommendedOrder = j.getInteger("recommendedOrder");
//                    //付款人数
//                    int payer = j.getInteger("payer");
//                    //是否原价
//                    boolean isOriginalPrice = j.getBoolean("isOriginalPrice");
//                    Commlist commlist = new Commlist(commId, commName, picture, imgPicture, price, postage, isPostFree, isRecommend, recommendedOrder, payer, isOriginalPrice);
//
//                    plantState.getCommlist().add(commlist);
//                }
                recommendedAdapter = new RecommendedAdapter(getContext(), imageLoader, plantState.getCommlist());
                mgvShopRecommended.setAdapter(recommendedAdapter);
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });

//        httpRequestWrap.setMethod(HttpRequestWrap.POST);
//        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, "请稍候", new OnResponseHandler() {
//            @Override
//            public void onResponse(String result, RequestStatus status) {
//                Log.e(TAG, "---首页---" + result);
//                String data = Errer.isResult(getContext(), result, status);
//                if (data == null) {
//                    Log.e(TAG, "---首页解密失败---" + data);
//                    return;
//                }
//                Log.e(TAG, "---首页解密---" + data);
//                //头部图片
//                JSONObject dataJSON = JSON.parseObject(data);
//                plantState.getBanners().clear();
//                JSONArray banners = dataJSON.getJSONArray("banners");
//                for (int i = 0; i < banners.size(); i++) {
//                    JSONObject j = banners.getJSONObject(i);
//                    //图片
//                    String banner = PlantAddress.ADDRESS + j.getString("banner");
//                    String imgBanner = j.getString("imgBanner");
//                    //id
//                    int bannerToCommId = j.getInteger("bannerToCommId");
//                    Banners b = new Banners(banner, imgBanner, bannerToCommId);
//                    plantState.getBanners().add(b);
//                }
//                initBanner(plantState.getBanners());
//                //推荐
//                plantState.getCommlist().clear();
//                String shop = dataJSON.getString("commList");
//                List<Commlist> commList = gson.fromJson(shop, new TypeToken<List<Commlist>>() {
//                }.getType());
//                plantState.setCommlist(commList);
////                JSONArray commList = dataJSON.getJSONArray("commList");
////                for (int i = 0; i < commList.size(); i++) {
////                    JSONObject j = commList.getJSONObject(i);
////                    Log.e(TAG, "---商品---" + commList.getString(i));
////                    //商品ID
////                    int commId = j.getInteger("commId");
////                    //商品名称
////                    String commName = j.getString("commName");
////                    //图片地址
////                    String picture = PlantAddress.ADDRESS + j.getString("picture");
////                    String imgPicture = j.getString("imgPicture");
////                    //价格
////                    double price = j.getDoubleValue("price");
////                    Log.e(TAG, "---商品价格---" + price);
////                    //是否包邮
////                    int postage = j.getInteger("postage");
////                    //
////                    boolean isPostFree = j.getBoolean("isPostFree");
////                    //是否推荐
////                    boolean isRecommend = j.getBoolean("isRecommend");
////                    //推荐顺序
////                    int recommendedOrder = j.getInteger("recommendedOrder");
////                    //付款人数
////                    int payer = j.getInteger("payer");
////                    //是否原价
////                    boolean isOriginalPrice = j.getBoolean("isOriginalPrice");
////                    Commlist commlist = new Commlist(commId, commName, picture, imgPicture, price, postage, isPostFree, isRecommend, recommendedOrder, payer, isOriginalPrice);
////
////                    plantState.getCommlist().add(commlist);
////                }
//                recommendedAdapter = new RecommendedAdapter(getContext(), imageLoader, plantState.getCommlist());
//                mgvShopRecommended.setAdapter(recommendedAdapter);
//            }
//        }));
//        Map<String, Object> params = new HashMap<String, Object>();
//        //随机数
//        int random = plantState.getRandom();
//        String sign = random + "";
//        Log.e(TAG, "---明文---" + sign);
//        //加密文字
//        String signEncrypt = null;
//        try {
//            signEncrypt = DESUtils.encryptDES(sign, Constants.secretKey.substring(0, 8));
//            Log.e(TAG, "---加密成功---" + signEncrypt);
//        } catch (Exception e) {
//            Log.e(TAG, "---加密失败---");
//            e.printStackTrace();
//        }
//        if (signEncrypt == null) {
//            plantState.initToast(getContext(), "加密失败", true, 0);
//        }
//        //随机数
//        params.put("random", random);
//        params.put("sign", signEncrypt);
//        httpRequestWrap.send(PlantAddress.SHOP_HOME, params);
    }

    @OnClick({R.id.rcrl_shop_search})
    public void onClick(View v) {
        switch (v.getId()) {
            //搜索
            case R.id.rcrl_shop_search:
                initIntent(SearchShopActivity.class);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
        mbvShopBanner.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
        mbvShopBanner.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
    }

    class BannerViewHolder implements MZViewHolder<String> {

        private ImageView ivShopItemImage = null;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shop_banner, null);
            ivShopItemImage = view.findViewById(R.id.iv_shop_item_image);
            return view;
        }

        @Override
        public void onBind(Context context, int i, String s) {
            imageLoader.displayImage(s, ivShopItemImage, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        }
    }
}
