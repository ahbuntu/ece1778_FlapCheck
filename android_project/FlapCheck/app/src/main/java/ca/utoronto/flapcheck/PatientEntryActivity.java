package ca.utoronto.flapcheck;

import java.util.Locale;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;


public class PatientEntryActivity extends ActionBarActivity
                                    implements PatientEntryArchiveInterac.OnArchiveItemSelected{

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_entry);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.patient_container, new PatientEntryArchiveFragment())
//                    .add(R.id.patient_container, new PatientEntryNewFragment())
                    .commit();
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.pe_tabs);
        tabs.setViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_entry, menu);
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

    /**
     * implementation of PatientEntryNewFragment.PatientNewEntryListener.onAddPatientButtonClicked()
     * starts the PatientEntryArchiveFragment
     */
//    public void onAddPatientButtonClicked(long patientId) {
//        //BUG: the following code block doesn't work since view pager always displays fragment 0 as current
////        getSupportFragmentManager().beginTransaction()
////                .replace(R.id.patient_container, new PatientEntryArchiveFragment())
////                .addToBackStack(null)
////                .commit();
//        //WORKAROUND:
////        mSectionsPagerAdapter.notifyDataSetChanged();
////        mViewPager.setCurrentItem(1, true);
//
//        //This is somewhat of a hack, since we should probably just call the PatientEntryNewFragment
//        //directly from the measurement activity when we want ot add a patient....
//        //However it currently works since we currently don't use the 'archived' feature of the
//        //PatientEntry activity any longer.
//        Intent resultIntent = new Intent();
//        resultIntent.putExtra(Constants.PATIENT_ENTRY_KEY_ADDED_PATIENT_ID, patientId);
//        setResult(RESULT_OK, resultIntent);
//        finish();
//    }

    /**
     * Function invoked when user has made a dialog selection
     * @param position
     * @param option
     */
    public void onArchiveItemSelected(int position, int option){
        switch(option) {
            case 0:
                Intent intent = new Intent(this, MeasurementActivity.class);
                startActivity(intent);
                break;
            case 1:
                //need to invoke the Review activity
                break;
            default:
                break;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return  PatientEntryNewFragment.newInstance("foo", "bar") ;
                case 1:
                    return PatientEntryArchiveFragment.newInstance("fo", "b");
            }
            return null;
        }

        @Override
        public int getCount() {
            // [New Patient] and [Archived Patients]
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_PE_new).toUpperCase(l);
                case 1:
                    return getString(R.string.title_PE_archived).toUpperCase(l);
            }
            return null;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_patient_entry, container, false);
            return rootView;
        }
    }

}
