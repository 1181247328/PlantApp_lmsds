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

import com.cdqf.plant_class.PublishedFind;
import com.cdqf.plant_class.SaveFind;
import com.cdqf.plant_find.AddressPromptTwoFind;
import com.cdqf.plant_find.AllDeleteOrderTwoFind;
import com.cdqf.plant_find.CacenlOrderTwoFind;
import com.cdqf.plant_find.CacenlRefundetaislFind;
import com.cdqf.plant_find.CancelAllOneFind;
import com.cdqf.plant_find.CartDeteleFind;
import com.cdqf.plant_find.DeleteOrderOneFind;
import com.cdqf.plant_find.DeteleShopFind;
import com.cdqf.plant_find.EvaluateDeleteOneFind;
import com.cdqf.plant_find.ForGoodsOneFind;
import com.cdqf.plant_find.GoodsAllOneFind;
import com.cdqf.plant_find.IntegralFind;
import com.cdqf.plant_find.LoginExitFind;
import com.cdqf.plant_find.NumberFind;
import com.cdqf.plant_find.PlantCollectionFind;
import com.cdqf.plant_find.PromptFind;
import com.cdqf.plant_find.ScienceCollectionTwoFind;
import com.cdqf.plant_find.SettIntegralFind;
import com.cdqf.plant_find.SettlementFind;
import com.cdqf.plant_find.StrategyColltionFind;
import com.cdqf.plant_find.TraveReturnFind;
import com.cdqf.plant_find.TravelCollectionTwoFind;
import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_state.PlantPreferences;
import com.cdqf.plant_state.PlantState;

import de.greenrobot.event.EventBus;

/**
 * 提示
 * Created by liu on 2017/11/22.
 */

public class PromptDilogFragment extends DialogFragment implements View.OnClickListener {

    private String TAG = PromptDilogFragment.class.getSimpleName();

    private PlantState plantState = PlantState.getPlantState();

    private View view = null;

    private int position = 0;

    private String context = null;

    private int type = 0;

    //提示内容
    private TextView tvPromapDilogContent = null;

    //确定
    private TextView tvPromapDilogCancel = null;

    //取消
    private TextView tvPromapDilogDetermine = null;

    private EventBus eventBus = EventBus.getDefault();

    public void initPrompt(String context, int position) {
        this.context = context;
        this.position = position;
    }

    public void initPrompt(String context, int position, int type) {
        this.context = context;
        this.position = position;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        view = inflater.inflate(R.layout.dilog_prompt, null);
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

    }

    /**
     * 初始化控件
     */
    private void initView() {
        tvPromapDilogContent = view.findViewById(R.id.tv_promap_dilog_content);
        tvPromapDilogCancel = view.findViewById(R.id.tv_promap_dilog_cancel);
        tvPromapDilogDetermine = view.findViewById(R.id.tv_promap_dilog_determine);
    }

    private void initAdapter() {

    }

    /**
     * 注册监听器
     */
    private void initListener() {
        tvPromapDilogCancel.setOnClickListener(this);
        tvPromapDilogDetermine.setOnClickListener(this);
    }

    /**
     * 初始化后
     */
    private void initBack() {
        getDialog().setCanceledOnTouchOutside(false);
        switch (position) {
            case 0:
                tvPromapDilogContent.setText(context);
                break;
            case 1:
                tvPromapDilogContent.setText(context);
                break;
            case 2:
                tvPromapDilogContent.setText(context);
                break;
            case 3:
                tvPromapDilogContent.setText(context);
                break;
            case 4:
                tvPromapDilogContent.setText(context);
                break;
            case 5:
                tvPromapDilogContent.setText(context);
                break;
            case 6:
                tvPromapDilogContent.setText(context);
                break;
            case 7:
                tvPromapDilogContent.setText(context);
                break;
            case 8:
                tvPromapDilogContent.setText(context);
                break;
            case 9:
                tvPromapDilogContent.setText(context);
                break;
            case 10:
                tvPromapDilogContent.setText(context);
                break;
            case 11:
                tvPromapDilogContent.setText(context);
                break;
            case 12:
                tvPromapDilogContent.setText(context);
                break;
            case 13:
                tvPromapDilogContent.setText(context);
                break;
            case 14:
                tvPromapDilogContent.setText(context);
                break;
            case 15:
                tvPromapDilogContent.setText(context);
                break;
            case 16:
                tvPromapDilogContent.setText(context);
                break;
            case 17:
                tvPromapDilogContent.setText(context);
                break;
            case 18:
                tvPromapDilogContent.setText(context);
                break;
            case 19:
                tvPromapDilogContent.setText(context);
                break;
            case 20:
                tvPromapDilogContent.setText(context);
                break;
            case 21:
                tvPromapDilogContent.setText(context);
                break;
            case 22:
                tvPromapDilogContent.setText(context);
                break;
            case 23:
                tvPromapDilogContent.setText(context);
            case 24:
                tvPromapDilogContent.setText(context);
                break;
            case 25:
                tvPromapDilogContent.setText(context);
                break;
            case 26:
                tvPromapDilogContent.setText(context);
                break;
            case 27:
                tvPromapDilogContent.setText(context);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels - 100, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //确定
            case R.id.tv_promap_dilog_cancel:
                switch (position) {
                    //AskFragment中植物收藏
                    case 0:
                        eventBus.post(new PlantCollectionFind());
                        dismiss();
                        break;
                    //PlantScienceActivity是否收藏该植物
                    case 1:
                        dismiss();
                        break;
                    //StragegyFragment是否收藏该游记
                    case 2:
                        eventBus.post(new PlantCollectionFind());
                        dismiss();
                        break;
                    //GoodsDetailsActivity商品详情提示前住登录
                    case 3:
                        dismiss();
                        break;
                    //GoodsDetailsActivity商品收藏
                    case 4:
                        eventBus.post(new PromptFind(4));
                        dismiss();
                        break;
                    //GoodsDetailsActivity商品加入购物车
                    case 5:
                        eventBus.post(new PromptFind(5));
                        dismiss();
                        break;
                    //CartActivity提示删除
                    case 6:
                        eventBus.post(new DeteleShopFind());
                        dismiss();
                        break;
                    //AddressAdapter提示删除用户地址
                    case 7:
                        eventBus.post(new AddressPromptTwoFind());
                        dismiss();
                        break;
                    //SetActivity退出当前账户
                    case 8:
                        plantState.setLogin(false);
                        plantState.setUser(null);
                        PlantPreferences.clearLogUserComment(getContext());
                        eventBus.post(new LoginExitFind());
                        dismiss();
                        break;
                    //TravelContextActivity返回操作
                    case 9:
                        eventBus.post(new TraveReturnFind());
                        dismiss();
                        break;
                    //TravelFragment收藏游记
                    case 10:
                        eventBus.post(new TravelCollectionTwoFind());
                        dismiss();
                        break;
                    //ScienceFragment收藏植物
                    case 11:
                        eventBus.post(new ScienceCollectionTwoFind());
                        dismiss();
                        break;
                    //ForPaymentFragment删除订单
                    case 12:
                        eventBus.post(new DeleteOrderOneFind(type));
                        dismiss();
                        break;
                    //AllOrderFragment取消订单
                    case 13:
                        eventBus.post(new CancelAllOneFind(type));
                        dismiss();
                        break;
                    //ForGoodsFragment确定收货
                    case 14:
                        eventBus.post(new ForGoodsOneFind(type));
                        dismiss();
                        break;
                    //EvaluateFragment删除订单
                    case 15:
                        eventBus.post(new EvaluateDeleteOneFind(type));
                        dismiss();
                        break;
                    //AllOrderFragment删除订单
                    case 16:
                        eventBus.post(new AllDeleteOrderTwoFind(type));
                        dismiss();
                        break;
                    //AllOrderFragment确认收货
                    case 17:
                        eventBus.post(new GoodsAllOneFind(type));
                        dismiss();
                        break;
                    //AllOrderFragment确认收货
                    case 18:
                        eventBus.post(new CacenlOrderTwoFind(type));
                        dismiss();
                        break;
                    //RefundetailsActivity退款详情中撤销订单
                    case 19:
                        eventBus.post(new CacenlRefundetaislFind());
                        dismiss();
                        break;
                    //TravelContextActivity发表游记
                    case 20:
                        eventBus.post(new PublishedFind());
                        dismiss();
                        break;
                    //TravelContextActivity草稿
                    case 21:
                        eventBus.post(new SaveFind());
                        dismiss();
                        break;
                    //SettlementActivity提交订单
                    case 22:
                        eventBus.post(new SettlementFind());
                        dismiss();
                        break;
                    //StrategyDetailsActivity收藏游记
                    case 23:
                        eventBus.post(new StrategyColltionFind());
                        dismiss();
                        break;
                    //NumberActivity提示是否绑定车牌号
                    case 24:
                        eventBus.post(new NumberFind());
                        dismiss();
                        break;
                    //IntegralDetailsActivity提示是否兑换商品
                    case 25:
                        eventBus.post(new IntegralFind());
                        dismiss();
                        break;
                    case 26:
                        eventBus.post(new CartDeteleFind());
                        dismiss();
                        break;
                    case 27:
                        eventBus.post(new SettIntegralFind());
                        dismiss();
                        break;
                }
                break;
            //取消
            case R.id.tv_promap_dilog_determine:
                dismiss();
                break;
        }
    }
}
