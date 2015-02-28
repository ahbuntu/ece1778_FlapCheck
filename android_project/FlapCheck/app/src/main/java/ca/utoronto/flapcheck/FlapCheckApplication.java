package ca.utoronto.flapcheck;

import android.app.Application;
import android.content.Context;

import com.variable.framework.node.NodeDevice;

/**
 * Created by ahmadul.hassan on 2015-02-27.
 */
public class FlapCheckApplication extends Application {
    public static NodeDevice mActiveNode;
    private static Context context;


    public static void setActiveNode(NodeDevice node){ mActiveNode = node; }

    public static NodeDevice getActiveNode(){  return mActiveNode; }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        com.variable.application.NodeApplication.initialize(this);
    }

    @Override
    public void onTerminate() {
        com.variable.application.NodeApplication.unbindServiceAndReceiver();
        super.onTerminate();

    }
}
