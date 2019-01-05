package com.cdqf.plant_activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_3des.Constants;
import com.cdqf.plant_3des.DESUtils;
import com.cdqf.plant_adapter.AvAdapter;
import com.cdqf.plant_class.Av;
import com.cdqf.plant_state.BaseActivity;
import com.cdqf.plant_state.Errer;
import com.cdqf.plant_state.PlantAddress;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_state.StatusBarCompat;
import com.cdqf.plant_utils.HttpRequestWrap;
import com.cdqf.plant_utils.OnResponseHandler;
import com.cdqf.plant_utils.RequestHandler;
import com.cdqf.plant_utils.RequestStatus;
import com.github.airsaid.zprogressbar.widget.ZProgressBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jingchen.pulltorefresh.PullToRefreshLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 影音管理
 * Created by liu on 2017/11/27.
 */

public class AvActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = AvActivity.class.getSimpleName();

    private Context context = null;

    private PlantState plantState = PlantState.getPlantState();

    private HttpRequestWrap httpRequestWrap = null;

    private Gson gson = new Gson();

    private MediaPlayer mediaPlayer = null;

    //返回
    @BindView(R.id.rl_av_return)
    public RelativeLayout rlAvReturn = null;

    //编辑
    @BindView(R.id.tv_av_edit)
    public TextView tvAvEdit = null;

    //刷新器
    @BindView(R.id.ptrl_av_pull)
    public PullToRefreshLayout ptrlAvPull = null;

    //集合
    private ListView lvAvList = null;

    //总容量
    @BindView(R.id.tv_av_totalcapacity)
    public TextView tvAvTotalcapacity = null;

    //剩余
    @BindView(R.id.tv_av_remaining)
    public TextView tvAvRemaining = null;

    //容量进度显示
    @BindView(R.id.zpb_av_capacity)
    public ZProgressBar zpbAvCapacity = null;

    //全部停止
    @BindView(R.id.tv_av_stop)
    public TextView tvAvStop = null;

    //全部开始
    @BindView(R.id.tv_av_start)
    public TextView tvAvStart = null;

    //布局
    @BindView(R.id.ll_av_layoutone)
    public LinearLayout llAvLayoutone = null;

    private AvAdapter avAdapter = null;

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
        setContentView(R.layout.activity_av);

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
        mediaPlayer = new MediaPlayer();
    }

    private void initView() {
        lvAvList = (ListView) ptrlAvPull.getPullableView();
    }

    private void initAdapter() {
        avAdapter = new AvAdapter(context);
        lvAvList.setAdapter(avAdapter);
    }

    private void initListener() {
        ptrlAvPull.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        });

        lvAvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String httpUrl = plantState.getAvList().get(position).getHttpUrl();
                try {
                    mediaPlayer = new MediaPlayer();
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.setDataSource(httpUrl);
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepareAsync();
                        //装载完毕回调
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mediaPlayer.start();
                            }
                        });
                        //声音播放
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Log.e(TAG, "---播放完毕音乐----");
                                //播放完音乐
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer = null;
                            }
                        });
                    } else {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initBack() {
        zpbAvCapacity.setAnimProgress(80);
        ptrlAvPull.setPullUpEnable(false);
        ptrlAvPull.setPullDownEnable(false);
        initAv();
    }

    private void initIntent(Class<?> activity, String text) {
        Intent intent = new Intent(context, activity);
        intent.putExtra("text", text);
        startActivity(intent);
    }

    private void initAv() {
        httpRequestWrap.setMethod(HttpRequestWrap.POST);
        httpRequestWrap.setCallBack(new RequestHandler(context, 1, plantState.getPlantString(context, R.string.please_while), new OnResponseHandler() {
            @Override
            public void onResponse(String result, RequestStatus status) {
                String data = Errer.isCode(context, result, status);
                if (data == null) {
                    Log.e(TAG, "---获取影音解密失败---" + data);
                    return;
                }
                Log.e(TAG, "---获取影音解密成功---" + data);
                Av av = new Av();
                List<Av> avList = gson.fromJson(data, new TypeToken<List<Av>>() {
                }.getType());
                plantState.setAvList(avList);
                if (avAdapter != null) {
                    avAdapter.notifyDataSetChanged();
                }
            }
        }));
        Map<String, Object> params = new HashMap<String, Object>();
        //随机数
        int random = plantState.getRandom();
        String sign = random + "";
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
        httpRequestWrap.send(PlantAddress.USER_AV, params);
    }

    public void play(String path){

    }

    @OnClick({R.id.rl_av_return, R.id.tv_av_edit, R.id.tv_av_stop, R.id.tv_av_start})
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.rl_av_return:
                finish();
                break;
            //编辑
            case R.id.tv_av_edit:
                break;
            //全部停止
            case R.id.tv_av_stop:
                break;
            //全部开始
            case R.id.tv_av_start:
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
        Log.e(TAG, "---销毁---");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
