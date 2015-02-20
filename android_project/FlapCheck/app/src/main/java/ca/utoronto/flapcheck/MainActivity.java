package ca.utoronto.flapcheck;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity
            implements SplashScreenFragment.SplashScreenFragmentListener,
                       MainFragment.MainFragmentListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            //Load the splash screen the first time
            getFragmentManager().beginTransaction()
                .add(R.id.main_container, new SplashScreenFragment())
                //Don't add the splash screen to the back stack
                .commit();
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
        //Once the user exits the splash screen swap in the main fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.main_container, new MainFragment())
                //Don't add it to the back stack, since this is the primary fragment
                .commit();
    }

    @Override
    public void startMeasurementActivity() {
//        Intent intent = new Intent(this, MeasurementActivity.class);
//        startActivity(intent);

        //hack for testing :)
        Intent intent = new Intent(this, PatientEntryActivity.class);
        startActivity(intent);
    }
}
