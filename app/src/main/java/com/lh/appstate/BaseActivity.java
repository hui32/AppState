package com.lh.appstate;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

public class BaseActivity extends Activity {

    private boolean isCurrentRunningForeground1 = true;
    private boolean isCurrentRunningForeground2 = true;
    private boolean isCurrentRunningForeground3 = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isCurrentRunningForeground1 = isRunningForeground1();
        isCurrentRunningForeground2 = isRunningForeground2();
        isCurrentRunningForeground3 = isRunningForeground3();
        if (!isCurrentRunningForeground1){
            Log.e("is_background","isCurrentRunningForeground1 -->切到后台");
        }
        if (!isCurrentRunningForeground2){
            Log.e("is_background","isCurrentRunningForeground2 -->切到后台");
        }
        if (!isCurrentRunningForeground3){
            Log.e("is_background","isCurrentRunningForeground3 -->切到后台");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isCurrentRunningForeground1){
            Log.e("is_background","isCurrentRunningForeground1 -->切到前台");
        }
        if (!isCurrentRunningForeground2){
            Log.e("is_background","isCurrentRunningForeground2 -->切到前台");
        }
        if (!isCurrentRunningForeground3){
            Log.e("is_background","isCurrentRunningForeground3 -->切到前台");
        }
    }

    /**
     * medth 1
     * 通过getRunningTasks获取
     * 这种方式需要权限android:name=”android.permission.GET_TASKS”
     * 且这个getRunningTasks已经过期了，Android L开始，Google开始对getRunningTasks接口进行限制使用。
     * 但是我在实际使用过程中，（Genymotion模拟器），在5.1和6.0的机器上，还是能够正常使用获取到包名*/
    public boolean isRunningForeground1(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = activityManager.getRunningTasks(1).get(0).topActivity;
        String currentPName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPName)&&currentPName.equals(getPackageName())){
            return true;
        }
        return false;
    }

    public boolean isRunningForeground2(){

        if (!TextUtils.isEmpty(getCurrentPkgName(this))&&getCurrentPkgName(this).equals(getPackageName())){
            return true;
        }
        return false;
    }

    /**
     * 查询当前进程名
     *
     * @param context
     * @return
     * 在Android6.0的机器上，在某些情景下使用无法获取到当前正在运行的包名。
     * 在屏幕暗下后，使用以下方法，可能会获取不到正确的进程列表
     */
    public static String getCurrentPkgName(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        String pkgName = null;
        if (Build.VERSION.SDK_INT >= 22) {
            ActivityManager.RunningAppProcessInfo currentInfo = null;
            Field field = null;
            int START_TASK_TO_FRONT = 2;
            try {
                field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo app : appList) {
                if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Integer state = null;
                    try {
                        state = field.getInt(app);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (state != null && state == START_TASK_TO_FRONT) {
                        currentInfo = app;
                        break;
                    }
                }
            }
            if (currentInfo != null) {
                pkgName = currentInfo.processName;
            }
        } else {
            List<ActivityManager.RunningTaskInfo> runTaskInfos = am.getRunningTasks(1);
            // 拿到当前运行的任务栈
            ActivityManager.RunningTaskInfo runningTaskInfo = runTaskInfos.get(0);
            // 拿到要运行的Activity的包名
            pkgName = runningTaskInfo.baseActivity.getPackageName();
        }
        return pkgName;
    }


    /**
     * */
    public boolean isRunningForeground3() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();
        // 枚举进程
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(this.getApplicationInfo().processName)) {
                    Log.d("is_background","EntryActivity isRunningForeGround");
                    return true;
                }
            }
        }
        Log.d("is_background", "EntryActivity isRunningBackGround");
        return false;
    }
}
