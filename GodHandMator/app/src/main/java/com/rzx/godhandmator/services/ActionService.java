package com.rzx.godhandmator.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/6.
 */
public class ActionService extends Service {

    @Override
    public void onCreate() {
//        Handler handler=new Handler(Looper.getMainLooper());
//        handler.post(new Runnable(){
//            public void run(){
//                Toast.makeText(getApplicationContext(), "Service is created!", Toast.LENGTH_LONG).show();
//            }
//        });
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String text = "";
        try {
            FileInputStream fin = new FileInputStream("/mnt/sdcard/GodHand/tmp/toastText.txt");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            text = EncodingUtils.getString(buffer, "UTF-8");
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
