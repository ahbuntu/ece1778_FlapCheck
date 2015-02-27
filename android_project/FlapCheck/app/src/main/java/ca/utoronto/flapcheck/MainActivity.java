package ca.utoronto.flapcheck;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity
            implements SplashScreenFragment.SplashScreenFragmentListener,
                       MainFragment.MainFragmentListener,
                       MeasurementFragment.MeasurementFragmentListener
{
    MainPagerAdapter mViewPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            mViewPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.main_pager);
            mViewPager.setAdapter(mViewPagerAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void exitSplashScreen() {

    }

    @Override
    public void startMeasurementActivity() {
        Intent intent = new Intent(this, MeasurementActivity.class);
        startActivity(intent);
    }

    @Override
    public void startPatientEntryActivity() {
        Intent intent = new Intent(this, PatientEntryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMeasurePhoto() {
        Intent intent = new Intent(this, MeasurementActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(MeasurementActivity.ARG_MEASUREMENT_TYPE, MeasurementActivity.PHOTO_MEASUREMENT);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            if(position == 0) {
                frag = new SplashScreenFragment();
            } else if (position == 1) {
                frag = new MeasurementFragment();
            } else if (position == 2) {
                frag = new ReviewFragment();
            } else if (position == 3) {
                frag = new MainFragment();
            }
            return frag;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if(position == 0) {
                title = "Splash";
            } else if (position == 1) {
                title = "Measure";
            } else if (position == 2) {
                title = "Review";
            } else if (position == 3) {
                title = "Old main fragment";
            }
            return title;
        }
    }
}
