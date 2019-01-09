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
import android.widget.TextView;

import com.cdqf.plant_find.NumberSettFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantState;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 数量添加框
 */
public class NumberDilogFragment extends DialogFragment {

    //商品名称
    @BindView(R.id.tv_number_dilog_name)
    public TextView tvNumberDilogName = null;
    //金额
    @BindView(R.id.tv_number_dilog_pricte)
    public TextView tvNumberDilogPricte = null;
    //减
    @BindView(R.id.tv_number_dilog_reduction)
    public TextView tvNumberDilogReduction = null;
    //数量
    @BindView(R.id.tv_number_dilog_context)
    public TextView tvNumberDilogContext = null;
    //加
    @BindView(R.id.tv_number_dilog_add)
    public TextView tvNumberDilogAdd = null;
    //确定
    @BindView(R.id.tv_number_dilog_determine)
    public TextView tvNumberDilogDetermine = null;
    private String TAG = PayDilogFragment.class.getSimpleName();
    private View view = null;
    private PlantState plantState = PlantState.getPlantState();
    private EventBus eventBus = EventBus.getDefault();
    private String shopName;

    private double shopPricte;

    private int sum = 1;

    private int commId;

    public void setInIt(String shopName, double shopPricte, int commId) {
        this.shopName = shopName;
        this.shopPricte = shopPricte;
        this.commId = commId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_number, null);
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
        ButterKnife.bind(getContext(), view);
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
        tvNumberDilogName.setText(shopName);
        tvNumberDilogPricte.setText(shopPricte + "");
        tvNumberDilogContext.setText(sum + "");
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick({R.id.tv_number_dilog_determine, R.id.tv_number_dilog_reduction, R.id.tv_number_dilog_add})
    public void onClick(View v) {
        switch (v.getId()) {
            //减
            case R.id.tv_number_dilog_reduction:
                if (sum == 1) {
                    return;
                }
                sum--;
                tvNumberDilogContext.setText(sum + "");
                break;
            //加
            case R.id.tv_number_dilog_add:
                sum++;
                tvNumberDilogContext.setText(sum + "");
                break;
            //确定
            case R.id.tv_number_dilog_determine:
                eventBus.post(new NumberSettFind(sum, commId));
                break;
        }
    }
}
