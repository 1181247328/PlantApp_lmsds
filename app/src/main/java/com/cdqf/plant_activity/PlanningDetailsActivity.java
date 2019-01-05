package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.cdqf.plant_adapter.PlannignDetailsAdapter;
import com.cdqf.plant_class.ItemDecoration;
import com.cdqf.plant_class.RoadDaetails;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.cdqf.plant_view.ScrollInterceptScrollView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.codeboy.android.aligntextview.AlignTextView;

/**
 * 线路详情
 * Created by liu on 2017/10/24.
 */

public class PlanningDetailsActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = PlanningDetailsActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    private Gson gson =new Gson();

    //返回
    private RelativeLayout rlPldetailsReturn = null;

    //滚动条
    @BindView(R.id.sv_pldetails_sc)
    public ScrollInterceptScrollView svPldetailsSc = null;

    //图片
    private ImageView ivPldetailsMap = null;

    //路线名
    private TextView tvPldetailsRoute = null;

    //路线详情
    private AlignTextView atvPldetailsRoute = null;

    //图像
    private TextView tvPldetailsPlanebak = null;

    private RecyclerView rvPldetailsView = null;

    private PlannignDetailsAdapter plannignDetailsAdapter = null;

    private int position;

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
        setContentView(R.layout.activity_plantdetails);

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
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        Intent intent = getIntent();
        position = intent.getIntExtra("position",0);
        imageLoader = plantState.getImageLoader(context);
    }

    private void initView() {
        rlPldetailsReturn = (RelativeLayout) this.findViewById(R.id.rl_pldetails_return);
        ivPldetailsMap = (ImageView) this.findViewById(R.id.iv_pldetails_map);
        tvPldetailsRoute = (TextView) this.findViewById(R.id.tv_pldetails_route);
        atvPldetailsRoute = (AlignTextView) this.findViewById(R.id.atv_pldetails_route);
        tvPldetailsPlanebak = (TextView) this.findViewById(R.id.tv_pldetails_planebak);
        rvPldetailsView = (RecyclerView) this.findViewById(R.id.rv_pldetails_view);
    }

    private void initAdapter(){
        rvPldetailsView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rvPldetailsView.addItemDecoration(new ItemDecoration(context,50));
        plannignDetailsAdapter = new PlannignDetailsAdapter(context);
        rvPldetailsView.setAdapter(plannignDetailsAdapter);

    }

    private void initListener() {
        rlPldetailsReturn.setOnClickListener(this);
        ivPldetailsMap.setOnClickListener(this);
    }

    private void initBack() {
        svPldetailsSc.smoothScrollTo(0,0);
        initPull();
    }

    private void initPull() {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isResult(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---浏览路线详情解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---浏览路线详情解密成功---" + data);
                RoadDaetails roadDaetails = gson.fromJson(data,RoadDaetails.class);
                plannignDetailsAdapter.setRoadDaetails(roadDaetails);
                imageLoader.displayImage(roadDaetails.getHttpPic(),ivPldetailsMap,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
                tvPldetailsRoute.setText(roadDaetails.getVrName());
                atvPldetailsRoute.setText(roadDaetails.getIntroduction());
            }
        }));
        initPut();
    }

    private void initPut() {
        Map<String, Object> params = new HashMap<String, Object>();
        //当前页数
        int vrId = plantState.getRoadList().get(position).getVrId();
        params.put("vrId", vrId);
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + vrId;
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
        httpRequestWrap.send(PlantAddress.FOR_DETAILS, params);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //返回
            case R.id.rl_pldetails_return:
                finish();
                break;
            //地图
            case R.id.iv_pldetails_map:
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
