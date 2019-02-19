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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.ForDetailsActivity;
import com.cdqf.plant_activity.PlantScienceActivity;
import com.cdqf.plant_activity.PlantsDetailsActivity;
import com.cdqf.plant_adapter.ScienceAdapter;
import com.cdqf.plant_adapter.SituationAdapter;
import com.cdqf.plant_class.Carousel;
import com.cdqf.plant_class.News;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.PlantCollectionFind;
import com.cdqf.plant_find.ScienceCollectionFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_okhttp.OKHttpHanlder;
import com.cdqf.plant_okhttp.OKHttpRequestWrap;
import com.cdqf.plant_okhttp.OnHttpRequest;
import com.cdqf.plant_state.ACache;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_view.ListViewForScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;


/**
 * 资质
 * Created by liu on 2017/10/19.
 */

public class AskFragment extends Fragment {

    private String TAG = AskFragment.class.getSimpleName();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus = EventBus.getDefault();

    private Gson gson = new Gson();

    private View view = null;

    //下拉刷新
    @BindView(R.id.srl_plant_pull)
    public SwipeRefreshLayout srlPlantPull = null;

    //滚动
    @BindView(R.id.sr_plant_scroll)
    public ScrollView srPlantScroll = null;

    //头部轮播图
    @BindView(R.id.ba_ask_carousel)
    public Banner baAskCarousel = null;

    //天气
    @BindView(R.id.tv_ask_sum)
    public TextView tvAskSum = null;

    //温度
    @BindView(R.id.tv_ask_temperature)
    public TextView tvAskTemperature = null;

    //湿度
    @BindView(R.id.tv_ask_humidity)
    public TextView tvAskHumidity = null;

    //查看更多
    @BindView(R.id.tv_ask_more)
    public TextView tvAskMore = null;

    //科普内容
    @BindView(R.id.hlv_ask_list)
    public HListView hlvAskList = null;

    //景区情况
    @BindView(R.id.lvfsv_ask_situation)
    public ListViewForScrollView lvfsvAskSituation = null;

    private ScienceAdapter scienceAdapter = null;

    private SituationAdapter situationAdapter = null;

    private HttpRequestWrap httpRequestWrap = null;

    private int plantPosition = 0;

    private int plantPull = 0;

    private ACache aCache = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.e(TAG, "---创建---");

        view = inflater.inflate(R.layout.fragment_ask, null);

        initAgo();

        initListener();

        initAdapter();

        initBack();

        return view;
    }

    private void initAgo() {
        ButterKnife.bind(this, view);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
        imageLoader = plantState.getImageLoader(getContext());
    }

    private void initListener() {
        srlPlantPull.setRefreshing(true);
        srlPlantPull.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //轮播图
                banner();

                //天气
                //weather();

                //植物
                //plants();

                //新闻
                news();
            }
        });

        //植物科普
        hlvAskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                forIntent(PlantsDetailsActivity.class, position);
            }
        });

        //新闻
        lvfsvAskSituation.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
                forIntent(ForDetailsActivity.class, position, 1);
            }
        });
    }

    private void initAdapter() {
        //科普内容
        scienceAdapter = new ScienceAdapter(getContext());
        hlvAskList.setAdapter(scienceAdapter);

        //景区情况
        situationAdapter = new SituationAdapter(getContext());
        lvfsvAskSituation.setAdapter(situationAdapter);
    }

    private void initBack() {
        srlPlantPull.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.immersion));
        srPlantScroll.smoothScrollTo(0, 0);
        //轮播图
        banner();

        //天气
        //weather();

        //植物
        //plants();

        //新闻
        news();
    }

    private void banner() {

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
        okHttpRequestWrap.post(PlantAddress.ASK_NEWHEEL, true, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---onOkHttpResponse---" + response);
                String data = OKHttpHanlder.isOKHttpResult(getContext(), response);
                initPull();
                if (data == null) {
                    Log.e(TAG, "---获取轮播新闻解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取轮播新闻解密成功---" + data);
                plantState.getCarouselList().clear();
                List<Carousel> carouselList = gson.fromJson(data, new TypeToken<List<Carousel>>() {
                }.getType());
                plantState.setCarouselList(carouselList);
                bannerCarousel();
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });

//        httpRequestWrap.setMethod(HttpRequestWrap.POST);
//        httpRequestWrap.setCallBack(new RequestHandler(getActivity(), new OnResponseHandler() {
//            @Override
//            public void onResponse(String result, RequestStatus status) {
//                String data = Errer.isResult(getContext(), result, status);
//                initPull();
//                if (data == null) {
//                    Log.e(TAG, "---获取轮播新闻解密失败---" + data);
//                    return;
//                }
//                Log.e(TAG, "---获取轮播新闻解密成功---" + data);
//                plantState.getCarouselList().clear();
//                List<Carousel> carouselList = gson.fromJson(data, new TypeToken<List<Carousel>>() {
//                }.getType());
//                plantState.setCarouselList(carouselList);
//                bannerCarousel();
//            }
//        }));
//
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
//        httpRequestWrap.send(PlantAddress.ASK_NEWHEEL, params);
    }

    /**
     * 天气
     */
    private void weather() {
//        httpRequestWrap.setMethod(HttpRequestWrap.POST);
//        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
//            @Override
//            public void onResponse(String result, RequestStatus status) {
//                String data = Errer.isWeather(getContext(), result, status);
//                if (data == null) {
//                    Log.e(TAG, "---天气---" + data);
//                    return;
//                }
//                JSONObject heWeather6 = JSON.parseObject(data);
//                String sta = heWeather6.getJSONArray("HeWeather6").getJSONObject(0).getString("status");
//                Log.e(TAG, "---status---" + sta);
//                if (TextUtils.equals(sta, "ok")) {
//                    JSONObject now = heWeather6.getJSONArray("HeWeather6").getJSONObject(0).getJSONObject("now");
//                    //天气
//                    tvAskSum.setText(now.getString("cond_txt"));
//                    //温度
//                    tvAskTemperature.setText(now.getString("tmp") + "°c");
//                    //湿度
//                    tvAskHumidity.setText(now.getString("hum") + "%");
//                }
//            }
//        }));
//        String param = "key=44421eb83b0d4858ba339005487d558a&location=南宁";
//        httpRequestWrap.send(PlantAddress.WEATHER + "?" + param);
    }

    /**
     * 植物
     */
    private void plants() {
//        httpRequestWrap.setMethod(HttpRequestWrap.POST);
//        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
//            @Override
//            public void onResponse(String result, RequestStatus status) {
//                String data = Errer.isResult(getContext(), result, status);
//                initPull();
//                if (data == null) {
//                    Log.e(TAG, "---获取植物解密失败---" + data);
//                    return;
//                }
//                Log.e(TAG, "---获取植物解密成功---" + data);
//                Plant plant = new Plant();
//                plant = gson.fromJson(data, Plant.class);
//                plantState.setPlant(plant);
//                if (scienceAdapter != null) {
//                    scienceAdapter.notifyDataSetChanged();
//                }
//            }
//        }));
//        Map<String, Object> params = new HashMap<String, Object>();
//        //用户id
//        String consumerId = plantState.getUser().getConsumerId() + "";
//        params.put("consumerId", consumerId);
//        //当前页
//        int pageIndex = 1;
//        params.put("pageIndex", pageIndex);
//        //每页条数
//        int pageCount = 10;
//        params.put("pageCount", pageCount);
//        //景点id
//        int scenicSpotId = 0;
//        params.put("scenicSpotId", scenicSpotId);
//        //分类
//        int typeId = 0;
//        params.put("typeId", typeId);
//        //随机数
//        int random = plantState.getRandom();
//        String sign = random + "" + consumerId + pageIndex + pageCount + scenicSpotId + typeId;
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
//        httpRequestWrap.send(PlantAddress.ASK_PLANTS, params);
    }

    /**
     * 新闻
     */
    private void news() {
        Map<String, Object> params = new HashMap<String, Object>();
        //当前页
        int pageIndex = 1;
        params.put("pageIndex", pageIndex);
        //每页条数
        int pageCount = 3;
        params.put("pageCount", pageCount);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + pageIndex + pageCount;
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
        params.put("random", random);
        params.put("sign", signEncrypt);
        OKHttpRequestWrap okHttpRequestWrap = new OKHttpRequestWrap(getContext());
        okHttpRequestWrap.post(PlantAddress.ASK_NEWS, true, "请稍候", params, new OnHttpRequest() {
            @Override
            public void onOkHttpResponse(String response, int id) {
                Log.e(TAG, "---onOkHttpResponse---" + response);
                String data = OKHttpHanlder.isOKHttpResult(getContext(), response);
                initPull();
                if (data == null) {
                    Log.e(TAG, "---获取新闻解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取新闻解密成功---" + data);
                News news = new News();
                news = gson.fromJson(data, News.class);
                Log.e(TAG, "---" + news.getList().get(0).getBrief());
                plantState.setNews(news);
                if (situationAdapter != null) {
                    situationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onOkHttpError(String error) {
                Log.e(TAG, "---onOkHttpError---" + error);
            }
        });

//        httpRequestWrap.setMethod(HttpRequestWrap.POST);
//        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
//            @Override
//            public void onResponse(String result, RequestStatus status) {
//                String data = Errer.isResult(getContext(), result, status);
//                initPull();
//                if (data == null) {
//                    Log.e(TAG, "---获取新闻解密失败---" + data);
//                    return;
//                }
//                Log.e(TAG, "---获取新闻解密成功---" + data);
//                News news = new News();
//                news = gson.fromJson(data, News.class);
//                Log.e(TAG, "---" + news.getList().get(0).getBrief());
//                plantState.setNews(news);
//                if (situationAdapter != null) {
//                    situationAdapter.notifyDataSetChanged();
//                }
//            }
//        }));
//        Map<String, Object> params = new HashMap<String, Object>();
//        //当前页
//        int pageIndex = 1;
//        params.put("pageIndex", pageIndex);
//        //每页条数
//        int pageCount = 3;
//        params.put("pageCount", pageCount);
//        //随机数
//        int random = plantState.getRandom();
//        String sign = random + "" + pageIndex + pageCount;
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
//        httpRequestWrap.send(PlantAddress.ASK_NEWS, params);
    }

    private void bannerCarousel() {
        baAskCarousel.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        baAskCarousel.setImageLoader(new GlideImageLoader());
        baAskCarousel.setImages(plantState.getCarouselList());
        baAskCarousel.setBannerAnimation(Transformer.Default);
        baAskCarousel.isAutoPlay(true);
        baAskCarousel.setDelayTime(3000);
        baAskCarousel.setIndicatorGravity(BannerConfig.CENTER);
        baAskCarousel.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                forIntent(ForDetailsActivity.class, position, 0);
            }
        });
        baAskCarousel.start();
    }

    private void initPull() {
        srlPlantPull.setRefreshing(false);
    }

    private void forIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    private void forIntent(Class<?> activity, int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position", position);
        intent.putExtra("type", 0);
        startActivity(intent);
    }

    private void forIntent(Class<?> activity, int position, int type) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("position", position);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @OnClick({R.id.tv_ask_more})
    public void onClick(View v) {
        switch (v.getId()) {
            //查看更多
            case R.id.tv_ask_more:
                forIntent(PlantScienceActivity.class);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "---启动---");
        baAskCarousel.startAutoPlay();//开始轮播
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "---停止---");
        baAskCarousel.stopAutoPlay();//结束轮播
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "---重启---");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        eventBus.unregister(this);
    }

    /**
     * 是否收藏植物
     *
     * @param s
     */
    public void onEventMainThread(ScienceCollectionFind s) {
        plantPosition = s.position;
        PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
        promptDilogFragment.initPrompt("是否收藏该植物!", 0);
        promptDilogFragment.show(getFragmentManager(), "是否收藏植物");
    }

    /**
     * 收藏植物
     *
     * @param p
     */
    public void onEventMainThread(PlantCollectionFind p) {
//        if (!plantState.isLogin()) {
//            plantState.initToast(getContext(), plantState.getPlantString(getContext(), R.string.is_login), true, 0);
//            return;
//        }
//        httpRequestWrap.setMethod(HttpRequestWrap.POST);
//        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
//            @Override
//            public void onResponse(String result, RequestStatus status) {
//                String data = Errer.isCode(getContext(), result, status);
//                if (data == null) {
//                    Log.e(TAG, "---植物收藏解密失败---" + data);
//                    return;
//                }
//                Log.e(TAG, "---植物收藏解密成功---" + data);
//            }
//        }));
//        Map<String, Object> params = new HashMap<String, Object>();
//        //植物收藏
//        int collectionType = 2;
//        params.put("collectionType", collectionType);
//        //用户id
//        int consumerId = plantState.getUser().getConsumerId();
//        params.put("consumerId", consumerId);
//        //收藏对象id
//        int collectedId = plantState.getPlant().getList().get(plantPosition).getBotanyId();
//        params.put("collectedId", collectedId);
//        //随机数
//        int random = plantState.getRandom();
//        String sign = random + "" + collectionType + consumerId + collectedId;
//        Log.e(TAG, "---明文---" + random + "---" + collectionType + "---" + consumerId + "---" + collectedId);
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
//        httpRequestWrap.send(PlantAddress.ASK_COLLTION, params);
    }

    class GlideImageLoader extends com.youth.banner.loader.ImageLoader {

        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Carousel carousel = (Carousel) path;
            imageLoader.displayImage(
                    carousel.getHttpDefaultPic(),
                    imageView,
                    plantState.getImageLoaderOptions(
                            R.mipmap.not_loaded,
                            R.mipmap.not_loaded,
                            R.mipmap.not_loaded));
        }
    }
}
