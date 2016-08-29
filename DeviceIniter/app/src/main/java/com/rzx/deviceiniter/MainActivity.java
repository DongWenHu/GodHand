package com.rzx.deviceiniter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rzx.deviceiniter.appinstall.AppInstallManager;
import com.rzx.deviceiniter.cmd.RootCmd;
import com.rzx.deviceiniter.ui.FxService;
import com.rzx.deviceiniter.util.Assets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String apkPath = "/mnt/sdcard/tmp/apk/";
    private final static String luaPath = "/mnt/sdcard/GodHand/";
    private final static String binPath = "/mnt/sdcard/tmp/bin/";
    private static final String START_UIAUTOMATOR =
            "am instrument -w -r   -e debug false -e class com.rzx.godhandmator.AutomatorTest com.rzx.godhandmator.test/android.support.test.runner.AndroidJUnitRunner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button initBtn = (Button) findViewById(R.id.btn_init);
        initBtn.setOnClickListener(this);
        TelephonyManager telephonyManager=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String imei=telephonyManager.getDeviceId();
        Toast.makeText(this.getApplicationContext(), imei, Toast.LENGTH_LONG).show();
    }

    private void initPhone() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                AppInstallManager.uninstallAllUser(MainActivity.this);

                // 删除apkPath所有文件
                RootCmd.execRootCmd("rm -fr " + apkPath);
                // apk写入sdcard
                Assets.CopyAssets(MainActivity.this, "apk", apkPath);
                // 安装apk
                AppInstallManager.installDir(apkPath, "apk");

                // 安装busybox等相关文件
                Assets.CopyAssets(MainActivity.this, "bin", binPath);
                AppInstallManager.installDir(binPath, "bin");

                // 脚本 写入sdcard
                Assets.CopyAssets(MainActivity.this, "godhand", luaPath);

                FileWriter writer = null;
                try {
                    File file = new File(luaPath + "tmp");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    writer = new FileWriter(luaPath + "tmp/run_file",  false);
                    writer.write("InitDevice.lua");
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RootCmd.execRootCmd(START_UIAUTOMATOR);
                Intent intent = new Intent(getApplicationContext(), FxService.class);
                stopService(intent);
            }
        }).start();

    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.btn_init:
               Intent intent = new Intent(this, FxService.class);
               startService(intent);
               initPhone();
               break;
       }
    }
}
