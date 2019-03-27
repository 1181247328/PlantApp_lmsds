package com.cdqf.plant_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.IntegralDetailsActivity;
import com.cdqf.plant_activity.RecordActivity;
import com.cdqf.plant_activity.SubsidiaryActivity;
import com.cdqf.plant_adapter.IntegralAdapter;
import com.cdqf.plant_class.Carousel;
import com.cdqf.plant_class.Integral;
import com.cdqf.plant_find.IntegralNumberFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.MyGridView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
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

/**
 * 积分商城
 * Created by liu on 2018/1/11.
 */

public class IntegralFragment extends Fragment {

    private String TAG = IntegralFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Gson gson = new Gson();

    private HttpRequestWrap httpRequestWrap = null;

    //滑动器
    @BindView(R.id.ptrl_integral_scroll)
    public PullToRefreshLayout ptrlIntegralScroll = null;

    private ScrollView srIntegralScroll = null;

    //轮播图
    @BindView(R.id.ba_integral_carousel)
    public Banner baIntegralCarousel = null;

    //积分
    @BindView(R.id.ll_integral_number)
    public LinearLayout llIntegralNumber = null;

    //余额积分
    @BindView(R.id.tv_integral_number)
    public TextView tvIntegralNumber = null;

    //兑换记录
    @BindView(R.id.ll_integral_record)
    public LinearLayout llIntegralRecord = null;

    //暂无商品兑换
    @BindView(R.id.tv_integral_no)
    public TextView tvIntegralNo = null;

    //兑换商品区
    @BindView(R.id.mgv_integral_recommended)
    public MyGridView mgvIntegralRecommended = null;

    private IntegralAdapter integralAdapter = null;

    //页数
    private int pageIndex = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x001:
                    ptrlIntegralScroll.setPullDownEnable(true);
                    tvIntegralNo.setVisibility(View.GONE);
                    mgvIntegralRecommended.setVisibility(View.VISIBLE);
                    break;
                case 0x002:
                    ptrlIntegralScroll.setPullDownEnable(false);
                    tvIntegralNo.setVisibility(View.VISIBLE);
                    mgvIntegralRecommended.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_integral, null);

        initAgo();

        initView();

        initAdapter();

        initListener();

        initBack();

        return view;
    }

    private void initAgo() {
        ButterKnife.bind(this, view);
        httpRequestWrap = new HttpRequestWrap(getContext());
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        imageLoader = plantState.getImageLoader(getContext());
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {
        srIntegralScroll = (ScrollView) ptrlIntegralScroll.getPullableView();
    }

    private void initAdapter() {

    }

    private void initListener() {
        mgvIntegralRecommended.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initIntent(IntegralDetailsActivity.class, position);
            }
        });
        ptrlIntegralScroll.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                banner();
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---积分商城列表解密失败---" + data);
                            handler.sendEmptyMessage(0x002);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        if (TextUtils.equals(data, "1001")) {
                            plantState.initToast(getContext(), "没有更多了", true, 0);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---积分商城列表商品解密---" + data);
                        data = JSON.parseObject(data).getString("list");
                        plantState.getIntegralList().clear();
                        List<Integral> integralList = gson.fromJson(data, new TypeToken<List<Integral>>() {
                        }.getType());
                        plantState.setIntegralList(integralList);
                        integralAdapter = new IntegralAdapter(getContext(), integralList);
                        mgvIntegralRecommended.setAdapter(integralAdapter);
//                if (integralAdapter != null) {
//                    integralAdapter.notifyDataSetChanged();
//                }
                        handler.sendEmptyMessage(0x001);
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                pageIndex = 1;
                //页数
                params.put("pageIndex", pageIndex);
                //条数
                int pageCount = 8;
                params.put("pageCount", pageCount);
                //加载全部
                boolean isAll = true;
                params.put("isAll", isAll);
                //商品类型id
                int commTypeId = 0;
                params.put("commTypeId", commTypeId);
                //排序方式
                int orderBy = 0;
                params.put("orderBy", orderBy);
                //关键字
                String searchKey = "";
                params.put("searchKey", searchKey);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + pageIndex + pageCount + String.valueOf(isAll) + commTypeId + orderBy + searchKey;
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
                httpRequestWrap.send(PlantAddress.COMMLIST, params);
            }

            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                //上拉加载
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---积分商城列表解密失败---" + data);
                            handler.sendEmptyMessage(0x002);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        if (TextUtils.equals(data, "1001")) {
                            handler.sendEmptyMessage(0x002);
                            plantState.initToast(getContext(), "没有更多了", true, 0);
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                            return;
                        }
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        Log.e(TAG, "---积分商城列表商品解密---" + data);
                        data = JSON.parseObject(data).getString("list");
                        List<Integral> integralList = gson.fromJson(data, new TypeToken<List<Integral>>() {
                        }.getType());
                        integralAdapter.setIntegralList(integralList);
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                pageIndex++;
                //页数
                params.put("pageIndex", pageIndex);

                Log.e(TAG, "---上拉---" + pageIndex);
                //条数
                int pageCount = 8;
                params.put("pageCount", pageCount);
                //加载全部
                boolean isAll = false;
                params.put("isAll", isAll);
                //商品类型id
                int commTypeId = 0;
                params.put("commTypeId", commTypeId);
                //排序方式
                int orderBy = 0;
                params.put("orderBy", orderBy);
                //关键字
                String searchKey = "";
                params.put("searchKey", searchKey);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + pageIndex + pageCount + String.valueOf(isAll) + commTypeId + orderBy + searchKey;
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
                httpRequestWrap.send(PlantAddress.COMMLIST, params);
            }
        });
    }

    private void initBack() {
        if (plantState.isLogin()) {
            tvIntegralNumber.setText(plantState.getUser().getIntegralNumber() + "");
        }
        srIntegralScroll.smoothScrollTo(0, 0);
        ptrlIntegralScroll.setPullUpEnable(false);
        ptrlIntegralScroll.setPullDownEnable(false);
        banner();
        initPictures();
    }

    /**
     * 积分商城头部轮播图
     */
    private void banner() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getActivity(), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {

                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取轮播积分头部解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取轮播积分头部解密成功---" + data);
                plantState.getCarouselList().clear();
                List<Carousel> carouselList = gson.fromJson(data, new TypeToken<List<Carousel>>() {
                }.getType());
                plantState.setCarouselList(carouselList);
                bannerCarousel();
            }
        }));
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
        httpRequestWrap.send(PlantAddress.ASK_NEWHEEL, params);
    }

    /**
     * 兑换商品
     */
    private void initPictures() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---积分商城列表解密失败---" + data);
                    handler.sendEmptyMessage(0x002);
                    return;
                }
                if (TextUtils.equals(data, "1001")) {
                    handler.sendEmptyMessage(0x002);
                    return;
                }
                Log.e(TAG, "---积分商城列表商品解密---" + data);
                data = JSON.parseObject(data).getString("list");
                plantState.getIntegralList().clear();
                List<Integral> integralList = gson.fromJson(data, new TypeToken<List<Integral>>() {
                }.getType());
                plantState.setIntegralList(integralList);
                integralAdapter = new IntegralAdapter(getContext(), integralList);
                mgvIntegralRecommended.setAdapter(integralAdapter);
//                if (integralAdapter != null) {
//                    integralAdapter.notifyDataSetChanged();
//                }
                handler.sendEmptyMessage(0x001);
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //页数
        params.put("pageIndex", pageIndex);
        //条数
        int pageCount = 8;
        params.put("pageCount", pageCount);
        //加载全部
        boolean isAll = true;
        params.put("isAll", isAll);
        //商品类型id
        int commTypeId = 0;
        params.put("commTypeId", commTypeId);
        //排序方式
        int orderBy = 0;
        params.put("orderBy", orderBy);
        //关键字
        String searchKey = "";
        params.put("searchKey", searchKey);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + pageIndex + pageCount + String.valueOf(isAll) + commTypeId + orderBy + searchKey;
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
        httpRequestWrap.send(PlantAddress.COMMLIST, params);
    }

    private void bannerCarousel() {
        baIntegralCarousel.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        baIntegralCarousel.setImageLoader(new GlideImageLoader());
        baIntegralCarousel.setImages(plantState.getCarouselList());
        baIntegralCarousel.setBannerAnimation(Transformer.Default);
        baIntegralCarousel.isAutoPlay(true);
        baIntegralCarousel.setDelayTime(3000);
        baIntegralCarousel.setIndicatorGravity(BannerConfig.CENTER);
        baIntegralCarousel.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
        baIntegralCarousel.start();
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

    @OnClick({R.id.ll_integral_number, R.id.ll_integral_record})
    public void onClick(View v) {
        switch (v.getId()) {
            //积分
            case R.id.ll_integral_number:
                if (!plantState.isLogin()) {
                    plantState.initToast(getContext(), "请先登录", true, 0);
                    return;
                }
                initIntent(SubsidiaryActivity.class);
                break;
            //兑换记录
            case R.id.ll_integral_record:
                if (!plantState.isLogin()) {
                    plantState.initToast(getContext(), "请先登录", true, 0);
                    return;
                }
                initIntent(RecordActivity.class);
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---暂停---");
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "---销毁---");
        eventBus.unregister(this);
    }

    public void onEventMainThread(IntegralNumberFind i) {
        Log.e(TAG, "---积分---" + plantState.getUser().getIntegralNumber());
        tvIntegralNumber.setText(plantState.getUser().getIntegralNumber() + "");
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

