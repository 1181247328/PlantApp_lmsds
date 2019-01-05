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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_find.PayFind;
import com.cdqf.plant_find.PayForFind;
import com.cdqf.plant_find.WeChatFind;
import com.cdqf.plant_state.PlantState;
import com.gcssloop.widget.RCRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 支付对话框
 * Created by liu on 2017/12/11.
 */

public class PayDilogFragment extends DialogFragment {

    private String TAG = PayDilogFragment.class.getSimpleName();

    private View view = null;

    private PlantState plantState = PlantState.getPlantState();

    private EventBus eventBus = EventBus.getDefault();

    //取消
    @BindView(R.id.rl_settlement_dilog_cancel)
    public RelativeLayout rlSettlementDilogCancel = null;

    //价格
    @BindView(R.id.tv_settlement_dilog_price)
    public TextView tvSettlementDilogPrice = null;

    //支付宝
    @BindView(R.id.ll_settlement_dilog_pay)
    public LinearLayout llSettlementDilogPay = null;

    @BindView(R.id.iv_settlement_dilog_one)
    public ImageView ivSettlementDilogOne = null;

    //微信
    @BindView(R.id.ll_settlement_dilog_wecnt)
    public LinearLayout llSettlementDilogWecnt = null;

    @BindView(R.id.iv_settlement_dilog_two)
    public ImageView ivSettlementDilogTwo = null;

    //确定
    @BindView(R.id.rl_settlement_dilog_determine)
    public RCRelativeLayout rlSettlementDilogDetermine = null;

    @BindView(R.id.tv_settlement_dilog_determine)
    public TextView tvSettlementDilogDetermine = null;

    private int pay = 0;

    private int payPosition = 0;

    private int type = 0;

    private int price = 0;

    public void initPayPrice(int type, int price) {
        this.type = type;
        this.price = price;
    }

    public void initPay(int type, int position) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_pay, null);
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
        getDialog().setCanceledOnTouchOutside(false);
        tvSettlementDilogPrice.setText(price + "");
        tvSettlementDilogDetermine.setText("确定支付" + price + "元");
    }

    private void paySelect(int pay) {
        this.pay = pay;
        switch (pay) {
            case 1:
                ivSettlementDilogOne.setImageResource(R.mipmap.tongguo);
                ivSettlementDilogTwo.setImageBitmap(null);
                break;
            case 2:
                ivSettlementDilogOne.setImageBitmap(null);
                ivSettlementDilogTwo.setImageResource(R.mipmap.tongguo);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick({R.id.rl_settlement_dilog_cancel, R.id.ll_settlement_dilog_pay,
            R.id.ll_settlement_dilog_wecnt, R.id.rl_settlement_dilog_determine,
            R.id.iv_settlement_dilog_two, R.id.iv_settlement_dilog_one})
    public void onClick(View v) {
        switch (v.getId()) {
            //取消
            case R.id.rl_settlement_dilog_cancel:
                dismiss();
                break;
            //支付宝
            case R.id.ll_settlement_dilog_pay:
                paySelect(1);
                break;
            //微信
            case R.id.ll_settlement_dilog_wecnt:
                paySelect(2);
                break;
            //确定
            case R.id.rl_settlement_dilog_determine:
                if (pay == 0) {
                    plantState.initToast(getContext(), getContext().getResources().getString(R.string.pay), true, 0);
                    return;
                }
                switch (pay) {
                    //支付宝
                    case 1:
                        if (type == 0) {
                            eventBus.post(new PayFind());
                        } else if (type == 1) {
                            //待付款页面支付
                            eventBus.post(new PayForFind(payPosition));
                        }
                        dismiss();
                        break;
                    //微信
                    case 2:
                        eventBus.post(new WeChatFind());
                        dismiss();
                        break;
                }
                break;
        }
    }
}
