package collin.mayti;

/**
 * Created by collinhpreston on 06/03/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import collin.mayti.watchlist.watchlistFragments.DailyWatchlistFragment;
import collin.mayti.watchlist.watchlistFragments.WatchlistFragment;
import collin.mayti.watchlist.watchlistFragments.WeeklyWatchlistFragment;

public class Fragment_Pager extends FragmentStatePagerAdapter {
    public Fragment_Pager(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new WatchlistFragment();
            case 1:
                return new DailyWatchlistFragment();
            case 2:
                return new WeeklyWatchlistFragment();
        }
        return null;

    }

    @Override
    public int getCount() {
        return 3; //No of Tabs
    }

}
