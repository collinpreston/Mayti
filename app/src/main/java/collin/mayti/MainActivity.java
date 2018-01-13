package collin.mayti;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class MainActivity extends FragmentActivity {
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.pager);

        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();

        /** Instantiating FragmentPagerAdapter */
        final Fragment_Pager pagerAdapter = new Fragment_Pager(fm){

        };

        /** Setting the pagerAdapter to the pager object */
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(1);
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
