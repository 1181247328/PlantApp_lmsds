package com.cdqf.plant_adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * 我的收藏适配器
 * Created by liu on 2017/11/16.
 */

public class CollectionAdapter extends FragmentPagerAdapter {

    private Fragment[] collectionList = null;

    private FragmentManager fm = null;

    public CollectionAdapter(FragmentManager fm, Fragment[] collectionList) {
        super(fm);
        this.fm = fm;
        this.collectionList = collectionList;
    }

    @Override
    public Fragment getItem(int position) {
        return collectionList[position];
    }

    @Override
    public int getCount() {
        return collectionList.length;
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
