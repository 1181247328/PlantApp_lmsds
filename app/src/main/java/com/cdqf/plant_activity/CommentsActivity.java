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
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.CommentsAdapter;
import com.cdqf.plant_class.Commentlist;
import com.cdqf.plant_class.GoodsFist;
import com.cdqf.plant_dilog.CommentsDilogFragment;
import com.cdqf.plant_dilog.PromptDilogFragment;
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
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 评论
 * Created by liu on 2017/10/25.
 */

public class CommentsActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = CommentsActivity.class.getSimpleName();

    private Context context = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    //返回
    private RelativeLayout rlCommentsReturn = null;

    //集合
    private PullToRefreshLayout ptrlCommentsPull = null;

    private ListView lvCommentsList = null;

    private CommentsAdapter commentsAdapter = null;

    private RelativeLayout rlComments = null;

    //商品id
    private int commId = 0;

    private int type = 2;

    private Gson gson = new Gson();

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
        setContentView(R.layout.activity_comments);

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
        httpRequestWrap = new HttpRequestWrap(context);
        Intent intent = getIntent();
        commId = intent.getIntExtra("position", 0);
    }

    private void initView() {
        rlCommentsReturn = (RelativeLayout) this.findViewById(R.id.rl_comments_return);
        ptrlCommentsPull = (PullToRefreshLayout) this.findViewById(R.id.ptrl_comments_pull);
        lvCommentsList = (ListView) ptrlCommentsPull.getPullableView();
        rlComments = (RelativeLayout) this.findViewById(R.id.rl_comments);
    }

    private void initAdapter() {
        commentsAdapter = new CommentsAdapter(context);
        lvCommentsList.setAdapter(commentsAdapter);
    }

    private void initListener() {
        ptrlCommentsPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                //下拉刷新操作
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---商品评论刷新解密失败---" + data);
                            //刷新失败
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG,"---商品评论---"+data);
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        type = 2;
                        plantState.getGoodsFist().getCommentList().clear();
                        GoodsFist goodsFist = new GoodsFist();
                        goodsFist = gson.fromJson(data, GoodsFist.class);
                        plantState.setGoodsFist(goodsFist);
                        if (goodsFist.getCommentList().size() <= 0) {
                            plantState.initToast(context, context.getResources().getString(R.string.not_fist), true, 0);
                            return;
                        }
                        if (commentsAdapter != null) {
                            commentsAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //商品id
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

            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                // 加载更多操作
                httpRequestWrap.setMethod(HttpRequestWrap.POST);
                httpRequestWrap.setCallBack(new RequestHandler(context, new OnResponseHandler() {
                    @Override
                    public void onResponse(String result, RequestStatus status) {
                        String data = Errer.isResult(context, result, status);
                        if (data == null) {
                            Log.e(TAG, "---商品评论刷新解密失败---" + data);
                            //刷新失败
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
                            return;
                        }
                        Log.e(TAG,"---商品评论---"+data);
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        type++;
                        JSONObject dataJSON = JSON.parseObject(data);
                        JSONArray dataArray = dataJSON.getJSONArray("commentList");
                        if (dataArray.size() <= 0) {
                            plantState.initToast(context, context.getResources().getString(R.string.more), true, 0);
                            return;
                        }
                        for (int i = 0; i < dataArray.size(); i++) {
                            Commentlist commentlist = new Commentlist();
                            String comment = dataArray.getString(i);
                            Log.e(TAG, "---加载评论---" + comment);
                            commentlist = gson.fromJson(comment, Commentlist.class);
                            plantState.getGoodsFist().getCommentList().add(commentlist);
                        }
                        if (commentsAdapter != null) {
                            commentsAdapter.notifyDataSetChanged();
                        }
                    }
                }));
                Map<String, Object> params = new HashMap<String, Object>();
                //商品id
                params.put("commId", commId);
                //当前页
                int pageIndex = type;
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
        });
        rlCommentsReturn.setOnClickListener(this);
        rlComments.setOnClickListener(this);
    }

    private void initBack() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_comments_return:
                finish();
                break;
            //写评论
            case R.id.rl_comments:
                if(!plantState.isLogin()){
                    PromptDilogFragment promptDilogFragment = new PromptDilogFragment();
                    promptDilogFragment.initPrompt("您还未登录,是否前住!",3);
                    promptDilogFragment.show(getSupportFragmentManager(),"前住登录");
                    return;
                }
                CommentsDilogFragment commentsDilogFragment = new CommentsDilogFragment();
                commentsDilogFragment.show(getSupportFragmentManager(), "写评论");
                break;
        }
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
