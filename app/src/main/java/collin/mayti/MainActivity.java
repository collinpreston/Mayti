package collin.mayti;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import collin.mayti.addRemoveStock.AddStockPage;
import collin.mayti.notifications.NotificationsPage;
import collin.mayti.watchlist.WatchlistPage;
import collin.mayti.watchlistDB.AppDatabase;

public class MainActivity extends AppCompatActivity {
    NoSwipePager viewPager;
    BottomBarAdapter pagerAdapter;
    AHBottomNavigation bottomNavigation;
    public static AppDatabase db;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Here I set my custom app theme for the action bar.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        // Initialize database
        db = AppDatabase.getDatabase(getApplicationContext());


        /** Getting fragment manager */
        //FragmentManager fm = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
//        final Fragment_Pager pagerAdapter = new Fragment_Pager(fm){
//
//        };

        // Setup the viewpager used for the bottom navigation
        setupViewPager();

        // Custom bottom navigation
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        setupBottomNavigationBar();



        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected) {
                    viewPager.setCurrentItem(position);
                    bottomNavigation.setCurrentItem(position, false);
                }

                return wasSelected;
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void setupViewPager() {
        viewPager = (NoSwipePager) findViewById(R.id.pager);
        // Restrict the viewpager from swiping
        viewPager.setPagingEnabled(false);

        pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());

        // Initialize the three bottom navigation page Fragments and add them to the viewPager
        // TODO: Once working, replace WatchlistFragment with the viewpager: WatchlistPage
        pagerAdapter.addFragments(new WatchlistPage());
        pagerAdapter.addFragments(new AddStockPage());
        pagerAdapter.addFragments(new NotificationsPage());

        /** Setting the pagerAdapter to the pager object */
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
    }
    private void setupBottomNavigationBar() {
        AHBottomNavigationItem item1 =
                new AHBottomNavigationItem(R.string.text_watchlist,
                        R.drawable.ic_menu_black_24dp, R.color.darkBlue);
        AHBottomNavigationItem item2 =
                new AHBottomNavigationItem(R.string.text_add_stock,
                        R.drawable.ic_add_black_24dp, R.color.darkBlue);
        AHBottomNavigationItem item3 =
                new AHBottomNavigationItem(R.string.text_notifications,
                        R.drawable.ic_notifications_black_24dp, R.color.darkBlue);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Coloring the tabs
        bottomNavigation.setColored(false);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.darkBlue));
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.darkRed));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.lightBlue));

        bottomNavigation.setCurrentItem(0);
    }
}
