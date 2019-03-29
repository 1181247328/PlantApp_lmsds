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

import com.cdqf.plant_adapter.TypeNumberAdapter;
import com.cdqf.plant_find.TypeNumberFind;
import com.cdqf.plant_lmsd.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import de.greenrobot.event.EventBus;

/**
 * 数量
 */
public class TypeNumberDilogFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    private String TAG = TypeNumberDilogFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    private int number = 0;

    //退款原因集合
    @BindView(R.id.lv_why_dilog_list)
    public ListView lvWhyDilogList = null;

    private TypeNumberAdapter whyDilogAdapter = null;

    @BindView(R.id.tv_why_dilog_submit)
    public TextView tvWhyDilogSubmit = null;

    public void setNumber(int number) {
        this.number = number;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_typenumber, null);
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
        whyDilogAdapter = new TypeNumberAdapter(getContext(), number);
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
//        getDialog().setCanceledOnTouchOutside(false);
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
        eventBus.post(new TypeNumberFind(position + 1));
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
