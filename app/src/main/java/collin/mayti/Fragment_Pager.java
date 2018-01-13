package collin.mayti;

/**
 * Created by collinhpreston on 06/03/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Fragment_Pager extends FragmentStatePagerAdapter {
    public Fragment_Pager(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new WatchlistFragment();
//            case 1:
//                return new DailyWatchlistFragment();
//            case 2:
//                return new WeeklyWatchlistFragment();
        }
        return null;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        // return 3;
        return 1; //No of Tabs
    }

}
