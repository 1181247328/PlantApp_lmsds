package com.cdqf.plant_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cdqf.plant_activity.IntroductionActivity;
import com.cdqf.plant_activity.LoginActivity;
import com.cdqf.plant_activity.MyOrderActivity;
import com.cdqf.plant_activity.RefundActivity;
import com.cdqf.plant_activity.RegisteredOneActivity;
import com.cdqf.plant_activity.SetActivity;
import com.cdqf.plant_adapter.OrderAdapter;
import com.cdqf.plant_adapter.OtherAdapter;
import com.cdqf.plant_find.Login;
import com.cdqf.plant_find.LoginExitFind;
import com.cdqf.plant_find.LoginWXFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;
import com.cdqf.plant_view.MyGridView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


/**
 * 我的
 * Created by liu on 2017/10/19.
 */

public class MyFragment extends Fragment implements View.OnClickListener {

    private String TAG = MyFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    private PlantState plantState = PlantState.getPlantState();

    private ImageLoader imageLoader = ImageLoader.getInstance();

    //消息
    private TextView tvMySet = null;

    //头像
    private ImageView ivMyHear = null;

    //登录
    private TextView tvMyLogin = null;

    //注册
    private TextView tvMyRegistered = null;

    //查看更多
    private LinearLayout llMyMore = null;

    //集合
    private MyGridView mgvMyList = null;

    private OrderAdapter orderAdapter = null;

    //其它功能
    private MyGridView mgvMyOther = null;

    private OtherAdapter otherAdapter = null;

    /****登录之后显示*****/
    //未登录
    @BindView(R.id.ll_my_no)
    public LinearLayout llMyNo = null;

    //登录
    @BindView(R.id.ll_my_yes)
    public LinearLayout llMyYes = null;

    //头像
    @BindView(R.id.iv_my_hear_yes)
    public ImageView ivMyHearYes = null;

    //名称
    @BindView(R.id.tv_my_login_name)
    public TextView tvMyLoginName = null;

    //电话
    @BindView(R.id.tv_my_registered_phone)
    public TextView tvMyRegisteredPhone = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //未登录
                case 0x001:
                    llMyNo.setVisibility(View.VISIBLE);
                    llMyYes.setVisibility(View.GONE);
                    break;
                //登录
                case 0x002:
                    llMyNo.setVisibility(View.GONE);
                    llMyYes.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my, null);

        initAgo();

        initView();

        initAdapter();

        initListener();

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

    private void initView() {
        tvMySet = view.findViewById(R.id.tv_my_set);
        ivMyHear = view.findViewById(R.id.iv_my_hear);
        tvMyLogin = view.findViewById(R.id.tv_my_login);
        tvMyRegistered = view.findViewById(R.id.tv_my_registered);
        llMyMore = view.findViewById(R.id.ll_my_more);
        mgvMyList = view.findViewById(R.id.mgv_my_list);
        mgvMyOther = view.findViewById(R.id.mgv_my_other);
    }

    private void initAdapter() {
        orderAdapter = new OrderAdapter(getContext());
        mgvMyList.setAdapter(orderAdapter);
        otherAdapter = new OtherAdapter(getContext());
        mgvMyOther.setAdapter(otherAdapter);
    }

    private void initListener() {
        tvMySet.setOnClickListener(this);
        tvMyRegistered.setOnClickListener(this);
        tvMyLogin.setOnClickListener(this);
        llMyMore.setOnClickListener(this);
        llMyYes.setOnClickListener(this);
        mgvMyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!plantState.isLogin()) {
                    plantState.initToast(getContext(), getContext().getResources().getString(R.string.is_login), true, 0);
                    return;
                }
                if (position == orderAdapter.getCount() - 1) {
                    initIntent(RefundActivity.class);
                    return;
                }
                initIntent(MyOrderActivity.class, position + 1);
            }
        });
        mgvMyOther.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //景区介绍
                    case 0:
                        initIntent(IntroductionActivity.class, 0);
                        break;
//                    //影音管理
//                    case 1:
//                        if (!plantState.isLogin()) {
//                            plantState.initToast(getContext(), getContext().getResources().getString(R.string.is_login), true, 0);
//                            return;
//                        }
//                        initIntent(AvActivity.class);
//                        break;
//                    //车辆管理
//                    case 2:
//                        if (!plantState.isLogin()) {
//                            plantState.initToast(getContext(), getContext().getResources().getString(R.string.is_login), true, 0);
//                            return;
//                        }
//                        initIntent(NumberActivity.class);
//                        break;
                    //参加指南
                    case 1:
                        initIntent(IntroductionActivity.class, 1);
                        break;
//                    //游记
//                    case 4:
//                        if (!plantState.isLogin()) {
//                            plantState.initToast(getContext(), getContext().getResources().getString(R.string.is_login), true, 0);
//                            return;
//                        }
//                        initIntent(PublishedActivity.class);
//                        break;
//                    //路线规划
//                    case 5:
//                        initIntent(MapActivity.class);
//                        break;
//                    // 周边旅游
//                    case 6:
//                        initIntent(PoiActivity.class);
//                        break;
//                    //收藏
//                    case 7:
//                        if (!plantState.isLogin()) {
//                            plantState.initToast(getContext(), getContext().getResources().getString(R.string.is_login), true, 0);
//                            return;
//                        }
//                        initIntent(CollectionActivity.class);
//                        break;
                }
            }
        });
    }

    private void initBack() {
        if (plantState.isLogin()) {
            handler.sendEmptyMessage(0x002);
            imageLoader.displayImage(plantState.getUser().getImgAvatat(), ivMyHearYes, plantState.getImageLoaderOptions(R.mipmap.login_hear, R.mipmap.login_hear, R.mipmap.login_hear));
            tvMyLoginName.setText(plantState.getUser().getNickName());
            tvMyRegisteredPhone.setText(plantState.phoneEmpty(plantState.getUser().getPhone()));
        } else {
            ivMyHear.setImageResource(R.mipmap.login_hear);
            handler.sendEmptyMessage(0x001);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //设置
            case R.id.tv_my_set:
                if (!plantState.isLogin()) {
                    plantState.initToast(getContext(), getContext().getResources().getString(R.string.is_login), true, 0);
                    return;
                }
                initIntent(SetActivity.class);
                break;
            //登录
            case R.id.tv_my_login:
                initIntent(LoginActivity.class);
                break;
            //注册
            case R.id.tv_my_registered:
                initIntent(RegisteredOneActivity.class);
                break;
            //查看更多
            case R.id.ll_my_more:
                if (!plantState.isLogin()) {
                    plantState.initToast(getContext(), getContext().getResources().getString(R.string.is_login), true, 0);
                    return;
                }
                initIntent(MyOrderActivity.class, 0);
                break;
            case R.id.ll_my_yes:
                initIntent(SetActivity.class);
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

    /**
     * 登录
     *
     * @param l
     */
    public void onEventMainThread(Login l) {
        imageLoader.displayImage(plantState.getUser().getImgAvatat(), ivMyHearYes, plantState.getImageLoaderOptions(R.mipmap.login_hear, R.mipmap.login_hear, R.mipmap.login_hear));
        handler.sendEmptyMessage(0x002);
        if (plantState.getUser().getNowMemberName() != null) {
            tvMyLoginName.setText(plantState.getUser().getNickName());
        } else {
            tvMyLoginName.setText(plantState.getUser().getNickName());
        }
        tvMyRegisteredPhone.setText(plantState.phoneEmpty(plantState.getUser().getPhone()));
    }

    /**
     * 微信登录
     *
     * @param l
     */
    public void onEventMainThread(LoginWXFind l) {
        String image = plantState.getUser().getImgAvatat().replace("http://rlmsdfile.quanyubao.cn", "");
        imageLoader.displayImage(image, ivMyHearYes, plantState.getImageLoaderOptions(R.mipmap.login_hear, R.mipmap.login_hear, R.mipmap.login_hear));
        handler.sendEmptyMessage(0x002);
        if (plantState.getUser().getNowMemberName() == null) {
            tvMyLoginName.setText(plantState.getUser().getNickName());
        } else {
            tvMyLoginName.setText(plantState.getUser().getNickName());
        }
        tvMyRegisteredPhone.setText(plantState.phoneEmpty(plantState.getUser().getPhone()));
    }

    public void onEventMainThread(LoginExitFind l) {
        ivMyHear.setImageResource(R.mipmap.login_hear);
        handler.sendEmptyMessage(0x001);
    }
}
