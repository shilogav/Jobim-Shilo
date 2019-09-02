package com.hyperactive.shilo.jobimshilo.main_desktop_jobs;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

public class PageAdapterDesktopJob extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS = 3;

    public PageAdapterDesktopJob(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    /*@Override
    public boolean isViewFromObject(View view, Object object) {
        return true;
    }*/

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new RightFragment();
            case 1: // Fragment # 1 - This will show SecondFragment
                return new RightFragment();
            case 2: // Fragment # 2 - This will show ThirdFragment
                return new LeftFragment();
            default:
                return null;

        }


    }

}
