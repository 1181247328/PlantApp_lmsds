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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.EvaluateCommentAdapter;
import com.cdqf.plant_dilog.PromptDilogFragment;
import com.cdqf.plant_find.EvaluateFind;
import com.cdqf.plant_find.ImageDeleteFind;
import com.cdqf.plant_find.TypeFind;
import com.cdqf.plant_image.PhotoActivity;
import com.cdqf.plant_image.PhotoFind;
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
import com.cdqf.plant_view.MyGridView;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

/**
 * 买家评论
 * Created by liu on 2017/12/25.
 */

public class EvaluateActivity extends BaseActivity {
    private String TAG = EvaluateActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private EventBus eventBus = EventBus.getDefault();

    private HttpRequestWrap httpRequestWrap = null;

    private List<String> pictureHttpList = new CopyOnWriteArrayList<>();

    private Gson gson = new Gson();

    //返回
    @BindView(R.id.rl_evaluate_return)
    public RelativeLayout rlEvaluateReturn = null;

    //发表
    @BindView(R.id.tv_evaluate_complete)
    public TextView tvEvaluateComplete = null;

    //图标
    @BindView(R.id.iv_evaluate_trademark)
    public ImageView ivEvaluateTrademark = null;

    //商品名
    @BindView(R.id.tv_evaluate_name)
    public TextView tvEvaluateName = null;

    //内容
    @BindView(R.id.et_evaluate_context)
    public EditText etEvaluateContext = null;

    //图片
    @BindView(R.id.mgv_evaluate_list)
    public MyGridView mgvEvaluateList = null;

    private EvaluateCommentAdapter evaluateCommentAdapter = null;

    private int position;

    private String commentContent;

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
        setContentView(R.layout.activity_evaluate);

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
        position = intent.getIntExtra("position", 0);
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void initView() {

    }

    private void initAdapter() {
        evaluateCommentAdapter = new EvaluateCommentAdapter(context);
        mgvEvaluateList.setAdapter(evaluateCommentAdapter);
    }

    private void initListener() {

    }

    private void initBack() {
        imageLoader.displayImage(plantState.getEvaluateList().get(position).getOrderCommList().get(0).getImgCommPic(), ivEvaluateTrademark, plantState.getImageLoaderOptions(R.mipmap.not_loaded, R.mipmap.not_loaded, R.mipmap.not_loaded));
        tvEvaluateName.setText(plantState.getEvaluateList().get(position).getOrderCommList().get(0).getCommName());
    }

    private void initIntent(Class<?> activity, String text) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("text", text);
        startActivity(intent);
    }

    @OnClick({R.id.rl_evaluate_return, R.id.tv_evaluate_complete})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_evaluate_return:
                finish();
                break;
            //发表
            case R.id.tv_evaluate_complete:
                commentContent = etEvaluateContext.getText().toString();
                if (commentContent.length() <= 0) {
                    plantState.initToast(context, "请输入内容", true, 0);
                    return;
                }
                PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                promptDilogFragment.initPrompt("是否评价", 29);
                promptDilogFragment.show(getSupportFragmentManager(), "是否评价");
                break;
        }
    }

    @OnItemClick({R.id.mgv_evaluate_list})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (plantState.getEvaluCommentList().size() == 9) {
            return;
        }
        if (position == evaluateCommentAdapter.getCount() - 1) {
            Log.e(TAG, "------22222");
            Intent intent = new Intent(context, PhotoActivity.class);
            startActivity(intent);
        } else {

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

    public void onEventMainThread(PhotoFind p) {
        pictureHttpList = p.photoList;
        evaluateCommentAdapter.setPictureHttpList(pictureHttpList);
        evaluateCommentAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(ImageDeleteFind i) {
        Log.e(TAG, "---数量---" + pictureHttpList.size() + "---删除位置---" + i.position);
        pictureHttpList.remove(i.position);
        evaluateCommentAdapter.setPictureHttpList(pictureHttpList);
        evaluateCommentAdapter.notifyDataSetChanged();
    }

    /**
     * 评价
     *
     * @param ev
     */
    public void onEventMainThread(EvaluateFind ev) {
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, "发表中", new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isTrave(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取评价发表解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取评价发表解密成功---" + data);
                plantState.initToast(context, data, true, 0);
                eventBus.post(new TypeFind());
                finish();
            }
        }));
        Evaluate evaluate = new Evaluate();
        Map<String, Object> params = new HashMap<String, Object>();

        //订单id
        int orderId = plantState.getEvaluateList().get(position).getOrderId();
        evaluate.setOrderId(orderId);

        //用户id
        int consumerId = plantState.getUser().getConsumerId();
        evaluate.setConsumerId(consumerId);

        //内容
        evaluate.setCommentContent(commentContent);

        //图片名称
        List<String> imageNameList = new ArrayList<>();
        for (int i = 0; i < pictureHttpList.size(); i++) {
            imageNameList.add("image" + i);
        }
        evaluate.setPicList(imageNameList);
        String orderContent = gson.toJson(evaluate);
        params.put("orderContent", orderContent);
        //图片
        for (int i = 0; i < pictureHttpList.size(); i++) {
            params.put(imageNameList.get(i), new File(pictureHttpList.get(i)));
        }
        //随机数
        int random = plantState.getRandom();
        String sign = random + "" + orderId + consumerId + commentContent + imageNameList;
        Log.e(TAG, "---明文---" + orderId + "---" + consumerId + "---" + commentContent + "---" + random);
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
        httpRequestWrap.send(PlantAddress.USER_SUB, params);
    }

    class Evaluate {
        private int orderId;
        private int consumerId;
        private String commentContent;
        private List<String> picList = new ArrayList<>();

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public int getConsumerId() {
            return consumerId;
        }

        public void setConsumerId(int consumerId) {
            this.consumerId = consumerId;
        }

        public String getCommentContent() {
            return commentContent;
        }

        public void setCommentContent(String commentContent) {
            this.commentContent = commentContent;
        }

        public List<String> getPicList() {
            return picList;
        }

        public void setPicList(List<String> picList) {
            this.picList = picList;
        }
    }
}