package com.lh.appstate;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by liuhui2 on 2017/1/18.
 */

public class MyApplication extends Application {
    public static int stateCount;
    @Override
    public void onCreate() {
        super.onCreate();
        initActivityLife();
    }

    private void initActivityLife() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                stateCount++;
                if (stateCount==1){
                    Log.e("is_background","ActivityLifecycleCallbacks-前台");
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                stateCount--;
                if (stateCount==0){
                    Log.e("is_background","ActivityLifecycleCallbacks-后台");
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    /**
     * 1：前台  0：后台
     * @return
     * */
    public static int isBackGround(){
        if (MyApplication.stateCount==0){
            Log.e("is_background","后台");
            return 0;
        }else {
            Log.e("is_background","前台");
            return 1;
        }
    }
}
