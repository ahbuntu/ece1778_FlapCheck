package ca.utoronto.flapcheck;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;


public class MeasurementActivity extends Activity
        implements MeasurementFragment.MeasurementFragmentListener,
                   TakePhotoFragment.TakePhotoFragmentListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        if(savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.measure_container, new MeasurementFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_measurement, menu);
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
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if(fm.getBackStackEntryCount() > 0) {
            fm.popBackStack(); //Return to previous fragment
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMeasurePhoto() {
        getFragmentManager().beginTransaction()
                .replace(R.id.measure_container, new TakePhotoFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public File getImageFileDir() {
        return getFilesDir();
    }
}
