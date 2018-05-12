package collin.mayti.watchlist;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
        TabLayout tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Watchlist"));
        tabLayout.addTab(tabLayout.newTab().setText("Day"));
        tabLayout.addTab(tabLayout.newTab().setText("Week"));

        pagerAdapter =
                new Fragment_Pager(
                        getChildFragmentManager());
        viewPager = rootView.findViewById(R.id.watchlistPager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        pagerAdapter.notifyDataSetChanged();
    }

}
