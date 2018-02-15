package collin.mayti;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import collin.mayti.watchlistDB.AppDatabase;

public class MainActivity extends AppCompatActivity {
    NoSwipePager pager;
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

        pager = (NoSwipePager) findViewById(R.id.pager);

        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
        BottomBarAdapter pagerAdapter = new BottomBarAdapter(getSupportFragmentManager());
//        final Fragment_Pager pagerAdapter = new Fragment_Pager(fm){
//
//        };

        /** Setting the pagerAdapter to the pager object */
        pager.setAdapter(pagerAdapter);
        // Restrict the viewpager from swiping
        pager.setPagingEnabled(false);
        pager.setCurrentItem(1);


        // Custom bottom navigation
        final AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
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
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.darkBlue));
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.darkRed));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.lightBlue));
        //bottomNavigation.setForceTint(true);
        //bottomNavigation.setColored(false);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                return wasSelected;
            }
        });

//        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
//                findViewById(R.id.bottom_navigation);
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
//
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(
//                new BottomNavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.action_watchlist:
//
//                                break;
//
//                            case R.id.action_add_stock:
//
//                                break;
//
//                            case R.id.action_all_notifications:
//
//                                break;
//
//
//                        }
//                        return true;
//
//                    }
//                });
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
}
