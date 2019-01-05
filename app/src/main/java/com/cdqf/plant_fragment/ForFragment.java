package com.cdqf.plant_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_activity.PlanningActivity;
import com.cdqf.plant_adapter.ForAdapter;
import com.cdqf.plant_class.ForDetails;
import com.cdqf.plant_dilog.ForDilogFragment;
import com.cdqf.plant_find.ForFind;
import com.cdqf.plant_map.AddrStrFind;
import com.cdqf.plant_map.BaiduLBS;
import com.cdqf.plant_search.SearchActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.gcssloop.widget.RCRelativeLayout;
import com.google.gson.Gson;
import com.lnyp.imgdots.bean.ForPoint;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import it.sephiroth.android.library.widget.HListView;

/**
 * 寻览
 * Created by liu on 2017/10/19.
 */

public class ForFragment extends Fragment {

    private String TAG = ForFragment.class.getSimpleName();

    private View view = null;

    private HttpRequestWrap httpRequestWrap = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private Gson gson = new Gson();

    //目的地
    @BindView(R.id.tv_for_place)
    public TextView tvForPlace = null;

    //地图
    @BindView(R.id.hlv_for_list)
    public HListView hlvForList = null;

    //规划
    @BindView(R.id.rcrl_for_planning)
    public RCRelativeLayout rcrlForPlanning = null;

    @BindView(R.id.iv_for_post)
    public ImageView ivForPost = null;

    private ForAdapter forAdapter = null;

    private List<ForPoint> forPointOneList = new ArrayList<>();

    private List<ForPoint> forPointTwoList = new ArrayList<>();

    private int width;

    private int height;

    private String longitude;

    private String latitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_for, null);

        initAgo();

        initBack();

        return view;
    }

    private void initAgo() {
        ButterKnife.bind(this, view);
        httpRequestWrap = new HttpRequestWrap(getContext());
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }


    private void initBack() {
        forAdapter = new ForAdapter(getContext(), width, height, forPointOneList, forPointTwoList);
        hlvForList.setAdapter(forAdapter);
        initPull();
        BaiduLBS.init(getActivity());
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取地图信息解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取地图信息解密成功---" + data);
                JSONArray jsonArray = JSON.parseArray(data);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    String point = jsonArray.getString(i);
                    int imgIndex = j.getInteger("imgIndex");
                    ForPoint forPoint = new ForPoint();
                    forPoint = gson.fromJson(point, ForPoint.class);
                    if (imgIndex == 0) {
                        forPointOneList.add(forPoint);
                    } else {
                        forPointTwoList.add(forPoint);
                    }
                }
                forAdapter = new ForAdapter(getContext(), width, height, forPointOneList, forPointTwoList);
                hlvForList.setAdapter(forAdapter);
            }
        }));
        initPut();
    }

    private void initPut() {
        Map<String, Object> params = new HashMap<String, Object>();
        //搜索关键字
        String searchKey = "";
        params.put("searchKey", searchKey);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + searchKey;
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        //随机数
        params.put("random", random);
        params.put("sign", signEncrypt);
        httpRequestWrap.send(PlantAddress.FOR_WY, params);
    }

    private void initIntent(Class<?> activity) {
        Intent intent = new Intent(getContext(), activity);
        startActivity(intent);
    }

    @OnClick({R.id.tv_for_place, R.id.rcrl_for_planning, R.id.iv_for_post})
    public void onClick(View v) {
        switch (v.getId()) {
            //线路规划
            case R.id.rcrl_for_planning:
                initIntent(PlanningActivity.class);
                break;
            //搜索
            case R.id.tv_for_place:
                initIntent(SearchActivity.class);
                break;
            //定位最近
            case R.id.iv_for_post:
                httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(getContext(), result, status);
                        if (data == null) {
                            Log.e(TAG, "---获取距离人最近的景点解密失败---" + data);
                            return;
                        }
                        Log.e(TAG, "---获取距离人最近的景点解密成功---" + data);
                        int scenicSpotId = JSON.parseObject(data).getInteger("scenicSpotId");
                        for (ForPoint f : forPointOneList) {
                            if (f.getScenicSpotId() == scenicSpotId) {
                                f.setLocation(true);
                            } else {
                                f.setLocation(false);
                            }
                        }
                        for (ForPoint f : forPointTwoList) {
                            if (f.getScenicSpotId() == scenicSpotId) {
                                f.setLocation(true);
                            } else {
                                f.setLocation(false);
                            }
                        }
                        if (forAdapter != null) {
                            forAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //经度
                params.put("longitude", longitude);
                //纬度
                params.put("latitude", latitude);
                //随机数
                int random = plantState.getRandom();
                String sign = random + "" + longitude + latitude;
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
                    plantState.initToast(getContext(), "加密失败", true, 0);
                }
                //随机数
                params.put("random", random);
                params.put("sign", signEncrypt);
                httpRequestWrap.send(PlantAddress.FOR_SPOT, params);
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

    public void onEventMainThread(AddrStrFind a) {
        longitude = a.map.getLongitude() + "";
        latitude = a.map.getLatitude() + "";
    }

    public void onEventMainThread(ForFind f) {
        httpRequestWrap.setCallBack(new RequestHandler(getContext(), 1, plantState.getPlantString(getContext(), R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(getContext(), result, status);
                if (data == null) {
                    Log.e(TAG, "---获取景点详情解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取景点详情解密成功---" + data);
                ForDetails forDetails = new ForDetails();
                forDetails = gson.fromJson(data, ForDetails.class);
                plantState.setForDetails(forDetails);
                ForDilogFragment forDilogFragment = new ForDilogFragment();
//                forDilogFragment.setPath(forDetails.getPicList().get(0).getHttpPic());
                if(forDetails.getVoiceList().size()>0) {
                    forDilogFragment.setVoice(forDetails.getVoiceList().get(0).getHttpVoice());
                }
                forDilogFragment.show(getFragmentManager(), "景点");
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        int spotId = f.scenicSpotId;
        params.put("spotId", spotId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + spotId;
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
            plantState.initToast(getContext(), "加密失败", true, 0);
        }
        //随机数
        params.put("random", random);
        params.put("sign", signEncrypt);
        httpRequestWrap.send(PlantAddress.FOR_GETDETAILS, params);
    }
}
