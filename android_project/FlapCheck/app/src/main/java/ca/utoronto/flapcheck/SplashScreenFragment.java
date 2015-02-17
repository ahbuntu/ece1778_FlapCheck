package ca.utoronto.flapcheck;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashScreenFragment extends Fragment {

    public interface SplashScreenFragmentListener {
        public void exitSplashScreen();
    }

    public SplashScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        //Register the button listener
        Button startButton = (Button) view.findViewById(R.id.splash_start_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashScreenFragmentListener activity = (SplashScreenFragmentListener) getActivity();

                activity.exitSplashScreen();
            }
        });

        return view;
    }


}