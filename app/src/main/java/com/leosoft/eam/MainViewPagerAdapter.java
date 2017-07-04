package com.leosoft.eam;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Leo on 2017-05-17.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<MainFragment> fragments = new ArrayList<>();
    private MainFragment currentFragment;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.clear();
        fragments.add(MainFragment.newInstance(0));
        fragments.add(MainFragment.newInstance(1));
        fragments.add(MainFragment.newInstance(2));
    }

    @Override
    public MainFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (getCurrentFragment() != object) {
            currentFragment = ((MainFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }

    /**
     * Get the current fragment
     */
    public MainFragment getCurrentFragment() {
        return currentFragment;
    }
}
