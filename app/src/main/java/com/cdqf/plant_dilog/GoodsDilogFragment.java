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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_find.GoodsDilogFind;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 货物状态
 * Created by liu on 2017/12/28.
 */

public class GoodsDilogFragment extends DialogFragment{
    private String TAG = RegionDilogFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    private int type = -1;

    //未收货
    @BindView(R.id.cb_type_dilog_checkbox)
    public CheckBox cbTypeDilogCheckbox = null;

    //收到货
    @BindView(R.id.cb_type_dilog_already)
    public CheckBox cbTypeDilogAlready = null;

    //关闭
    @BindView(R.id.tv_type_dilog_submit)
    public TextView tvTypeDilogSubmit = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_type, null);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //初始化前
        initAgo();

        //初始化控件
        initView();

        //适配器
        initAdapter();

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
        ButterKnife.bind(this, view);
    }

    /**
     * 初始化控件
     */
    private void initView() {
    }

    private void initAdapter() {

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
        getDialog().setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.tv_type_dilog_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_type_dilog_submit:
                if(type ==-1){
                    dismiss();
                    return;
                }
                eventBus.post(new GoodsDilogFind(type));
                dismiss();
                break;
        }
    }

    @OnCheckedChanged({R.id.cb_type_dilog_checkbox,R.id.cb_type_dilog_already})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()){
            case R.id.cb_type_dilog_checkbox:
                if(isChecked){
                    type = 0;
                    cbTypeDilogCheckbox.setChecked(isChecked);
                    cbTypeDilogAlready.setChecked(false);
                }
                break;
            case R.id.cb_type_dilog_already:
                if(isChecked){
                    type = 1;
                    cbTypeDilogCheckbox.setChecked(false);
                    cbTypeDilogAlready.setChecked(isChecked);
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, dm.heightPixels / 2);
    }
}
