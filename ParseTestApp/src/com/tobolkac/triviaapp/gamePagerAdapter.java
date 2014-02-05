package com.tobolkac.triviaapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class gamePagerAdapter extends FragmentPagerAdapter {
	 
    public gamePagerAdapter(FragmentManager fm) {
        super(fm);
    }
 
    @Override
    public Fragment getItem(int index) {
//    	return new ScreenSlidePageFragment();
//        switch (index) {
//        case 0:
//            // Top Rated fragment activity
////            return new currentGamesFragment();
//        case 1:
//            // Games fragment activity
////            return new finishedGamesFragment();
//        }
// 
        return null;
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
 
}
