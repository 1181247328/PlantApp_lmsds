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
import com.cdqf.plant_adapter.WhyDilogAdapter;
import com.cdqf.plant_find.WhyDilogFind;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

/**
 * Created by liu on 2017/12/28.
 */

public class WhyDilogFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private String TAG = RegionDilogFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    private int type = -1;

    //退款原因集合
    @BindView(R.id.lv_why_dilog_list)
    public ListView lvWhyDilogList = null;

    private WhyDilogAdapter whyDilogAdapter = null;

    @BindView(R.id.tv_why_dilog_submit)
    public TextView tvWhyDilogSubmit = null;

    private String[] why = new String[]{
            "不喜欢/不想要",
            "空包裹",
            "大小尺寸不符",
            "少发/漏发",
            "卖家发错货"
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilgo_why, null);
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
        whyDilogAdapter = new WhyDilogAdapter(getContext(), why);
        lvWhyDilogList.setAdapter(whyDilogAdapter);
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

    @OnClick({R.id.tv_why_dilog_submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_why_dilog_submit:
                dismiss();
                break;
        }
    }

    @OnItemClick({R.id.lv_why_dilog_list})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        eventBus.post(new WhyDilogFind(position, why[position]));
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, dm.heightPixels / 2 + dm.heightPixels / 4);
    }
}
