package com.cdqf.plant_adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 我的订单适配器
 * Created by liu on 2017/11/20.
 */

public class MyOrderAdapter extends FragmentPagerAdapter {

    private Fragment[] myOrderList = null;

    private FragmentManager fm = null;

    public MyOrderAdapter(FragmentManager fm, Fragment[] myOrderList) {
        super(fm);
        this.fm = fm;
        this.myOrderList = myOrderList;
    }

    @Override
    public Fragment getItem(int position) {
        return myOrderList[position];
    }

    @Override
    public int getCount() {
        return myOrderList.length;
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        String fragmentTag = fragment.getTag();
//        if (seabedState.getFramentsMap().get(position)) {
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.remove(fragment);
//            fragment = seabedState.getFragments().get(position);
//            ft.add(container.getId(), fragment, fragmentTag);
//            ft.attach(fragment);
//            ft.commitAllowingStateLoss();
//            seabedState.getFramentsMap().put(position, false);
//        }
//        return fragment;
//    }
}