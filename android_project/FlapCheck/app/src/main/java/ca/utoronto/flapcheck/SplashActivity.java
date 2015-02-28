package ca.utoronto.flapcheck;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplashActivity extends FragmentActivity
            implements SplashScreenFragment.SplashScreenFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.splash_container, new SplashScreenFragment())
                .commit();
    }

    @Override
    public void exitSplashScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
