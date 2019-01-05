package com.cdqf.plant_dilog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_adapter.CourierDilogAdapter;
import com.cdqf.plant_find.CourierFind;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 快递公司
 * Created by liu on 2017/12/29.
 */

public class CourierDilogFragment extends DialogFragment{

    private String TAG = CourierDilogFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    //物流公司集合
    @BindView(R.id.lv_courier_dilog_list)
    public ListView lvCourierDilogList = null;

    private CourierDilogAdapter courierDilogAdapter = null;

    //关闭
    @BindView(R.id.tv_courier_dilog_submit)
    public TextView tvCourierDilogSubmit = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_courier, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
        ButterKnife.bind(this,view);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        courierDilogAdapter = new CourierDilogAdapter(getContext());
        lvCourierDilogList.setAdapter(courierDilogAdapter);
    }


    /**
     * 注册监听器
     */
    private void initListener() {
        lvCourierDilogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventBus.post(new CourierFind(position));
                dismiss();
            }
        });
    }

    /**
     * 初始化后
     */
    private void initBack() {
        getDialog().setCanceledOnTouchOutside(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, dm.heightPixels / 2 + dm.heightPixels / 4);
    }

    @OnClick({R.id.tv_courier_dilog_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_courier_dilog_submit:
                dismiss();
                break;
        }
    }
}
