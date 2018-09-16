package com.mostanad.plus.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.mostanad.plus.fragment.CategoriesFragment;
import com.mostanad.plus.fragment.MainFragment;
import com.mostanad.plus.fragment.NewestVideosFragment;
import com.mostanad.plus.utils.Constants;


public class MainHomeViewPagerAdapter extends FragmentPagerAdapter {
    SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public MainHomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return NewestVideosFragment.newInstance(Constants.POPULAR_TYPE);
            case 1:
                return NewestVideosFragment.newInstance(Constants.NEWEST_TYPE);
            case 2:
                return CategoriesFragment.newInstance();
            case 3:
                return MainFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

}