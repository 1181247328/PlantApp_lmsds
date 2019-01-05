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
import android.widget.GridView;

import com.cdqf.plant_lmsd.R;
import com.cdqf.plant_adapter.RegionDilogAdapter;
import com.cdqf.plant_find.RegionFind;

import de.greenrobot.event.EventBus;

/**
 *地区选择
 * Created by liu on 2017/11/15.
 */

public class RegionDilogFragment extends DialogFragment{

    private String TAG = RegionDilogFragment.class.getSimpleName();

    private View view = null;

    private EventBus eventBus = EventBus.getDefault();

    private GridView gvRegionDilog = null;

    private RegionDilogAdapter regionDilogAdapter = null;

    private String[] licenseList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        view = inflater.inflate(R.layout.dilog_region, null);
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
        gvRegionDilog = (GridView) view.findViewById(R.id.gv_region_dilog);
    }

    private void initAdapter(){
        licenseList = getContext().getResources().getStringArray(R.array.license);
        regionDilogAdapter = new RegionDilogAdapter(getContext(),licenseList);
        gvRegionDilog.setAdapter(regionDilogAdapter);
    }

    /**
     * 注册监听器
     */
    private void initListener() {
        gvRegionDilog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventBus.post(new RegionFind(licenseList[position]));
                dismiss();
            }
        });
    }

    /**
     * 初始化后
     */
    private void initBack() {

    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
