package com.rzx.godhand.mark.timer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.rzx.godhand.mark.cmd.Cmd;
import com.rzx.godhand.mark.heartdata.HeartData;
import com.rzx.godhand.mark.util.HttpUtils;
import com.rzx.godhand.mark.util.PreferenceMgr;

import org.apache.http.client.ClientProtocolException;

import java.io.File;
import java.io.IOException;

/**
 * Created by coder80 on 2014/10/31.
 */
public class UploadPOIService extends Service implements Runnable {

	@Override
	public void onCreate() {
		super.onCreate();
		uploadPOIInfo();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	private void uploadPOIInfo() {

		new Thread(this).start();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

        try {
            String serverIp = PreferenceMgr.getSharedValue(this, "serverIp",
                    "192.168.70.97");
            HttpUtils.getResultForHttpPost("http://" + serverIp
                    + ":3002/mobile_info", HeartData.getInstance()
                    .getData(this));
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File file = new File("/sdcard/GodHand/apk/godhandmator.apk");
        if(file.exists()){
            Cmd.execRootCmd("pm install -r /sdcard/GodHand/apk/godhandmator.apk");
            Cmd.execRootCmd("rm -fr /sdcard/GodHand/apk/godhandmator.apk");
        }

        stopSelf();
	}
}
