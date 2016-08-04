package com.mark.timer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.mark.cmd.RootCmd;

/**
 * Created by Administrator on 2016/8/4.
 */
public class RunScriptService extends Service implements Runnable  {

    private static final String START_UIAUTOMATOR =
            "am instrument -w -r   -e debug false -e class com.rzx.godhandmator.AutomatorTest com.rzx.godhandmator.test/android.support.test.runner.AndroidJUnitRunner";

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(this).start();
    }

    @Override
    public void run() {
        RootCmd.execRootCmd(START_UIAUTOMATOR);
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
