package collin.mayti.watchlist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import collin.mayti.Fragment_Pager;
import collin.mayti.R;


/**
 * Created by collinhpreston on 16/02/2018.
 */

public class WatchlistPage extends Fragment {
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.watchlist_viewpager_page, container, false);
        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        viewPager = rootView.findViewById(R.id.pager);
        pagerAdapter =
                new Fragment_Pager(
                        getFragmentManager());
        viewPager = rootView.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        return rootView;
    }

}
