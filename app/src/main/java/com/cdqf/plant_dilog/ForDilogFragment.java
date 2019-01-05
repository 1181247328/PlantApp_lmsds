package com.cdqf.plant_dilog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 景点介绍
 * Created by liu on 2018/1/4.
 */

public class ForDilogFragment extends DialogFragment {
    private String TAG = ForDilogFragment.class.getSimpleName();

    private View view = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private String path = "http://zwyfile.quanyubao.cn//ScenicSpots/UpdateScenicSpots//Voice/2017/12/22/201712221641342646.MP3";

    //图片
    private String httpVoice = "";

    //true = 播放,false = 暂停
    private boolean isPlay = true;

    //是不是在播放中按了暂停
    private boolean isPause = false;

    private MediaPlayer mediaPlayer = null;

    //图片
    @BindView(R.id.iv_for_dilog_price)
    public ImageView ivForDilogPrice = null;

    //名称
    @BindView(R.id.iv_for_dilog_name)
    public TextView ivForDilogName = null;

    //控制
    @BindView(R.id.iv_for_dilog_control)
    public ImageView ivForDilogControl = null;

    public void setPath(String path) {
        this.path = path;
    }

    //图片
    public void setVoice(String httpVoice){
        this.httpVoice = httpVoice;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        view = inflater.inflate(R.layout.dilog_for, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this, view);
        //初始化前
        initAgo();

        //初始化控件
        initView();

        //注册监听器
        initListener();

        //初始化后
        initBack();
        return view;
    }

    /**
     * 初始化前
     */
    private void initAgo() {
        imageLoader = plantState.getImageLoader(getContext());
    }

    /**
     * 初始化控件
     */
    private void initView() {

    }

    /**
     * 注册监听器
     */
    private void initListener() {

    }

    /**
     * 初始化后
     */
    private void initBack() {
        ivForDilogName.setText(plantState.getForDetails().getSpotName());
        imageLoader.displayImage(httpVoice,ivForDilogPrice,plantState.getImageLoaderOptions(R.mipmap.not_loaded,R.mipmap.not_loaded,R.mipmap.not_loaded));
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels - 100, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.iv_for_dilog_control})
    public void onClick(View v) {
        switch (v.getId()) {
            //播放
            case R.id.iv_for_dilog_control:
                if (mediaPlayer != null && isPlay) {
                    isPlay = false;
                    mediaPlayer.start();
                    ivForDilogControl.setImageResource(R.mipmap.music_pasue);
                    return;
                }
                //播放
                if (isPlay) {
                    isPlay = false;
                    ivForDilogControl.setImageResource(R.mipmap.music_pasue);
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(path);
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
                                isPlay = true;
                                ivForDilogControl.setImageResource(R.mipmap.music_play);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    isPause = true;
                    isPlay = true;
                    ivForDilogControl.setImageResource(R.mipmap.music_play);
                    //暂停
                    mediaPlayer.pause();
                }
                break;
        }
    }
}
