package com.rzx.godhand.mark.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rzx.godhand.mark.appinstall.AppInstallManager;
import com.rzx.godhand.mark.cmd.RootCmd;
import com.rzx.godhand.R;
import com.rzx.godhand.mark.timer.service.RunScriptService;
import com.rzx.godhand.mark.timer.service.UploadPOIService;
import com.rzx.godhand.mark.timer.utils.Constants;
import com.rzx.godhand.mark.timer.utils.ServiceUtil;
import com.rzx.godhand.mark.util.Assets;
import com.rzx.godhand.mark.util.PreferenceMgr;
import com.rzx.godhand.mark.util.ThreadPool;
import com.rzx.godhand.ui.service.FxService;

import java.io.File;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";

	Button initButton;
	Button serviceButton;

    private static final int MSG_TOAST = 0x1000001;
    private static final String START_UIAUTOMATOR =
            "am instrument -w -r   -e debug false -e class com.rzx.godhandmator.AutomatorTest com.rzx.godhandmator.test/android.support.test.runner.AndroidJUnitRunner";
    private static final String STOP_UIAUTOMATOR1 =
            "am force-stop com.rzx.godhandmator";
    private static final String STOP_UIAUTOMATOR2 =
            "am force-stop com.rzx.godhandmator.test";

    private AudioManager mAudioManager;
    private VolumeReceiver mVolumeReceiver;
    private long mTimeout = 0;
    private int mOldVolume = -1;
    private volatile boolean mIsStarted = false;
    private volatile boolean mIsAlarmStarted = false;

    private final Handler msgHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case MSG_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("text"), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String s =Settings.System.ANDROID_ID;
		setContentView(R.layout.activity_main);
		initButton = (Button) findViewById(R.id.initButton);
		serviceButton = (Button) findViewById(R.id.serviceButton);
		initButton.setOnClickListener(this);
		serviceButton.setOnClickListener(this);

		RequestURL = PreferenceMgr.getSharedValue(this, "serverIp",
				"172.16.21.193");
		if (!ServiceUtil.isServiceRunning(this, Constants.POI_SERVICE)) {
			ServiceUtil.invokeTimerService(this, UploadPOIService.class, Constants.POI_SERVICE_ACTION);
		}

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        registerVolumeChangeReceiver();
		// Log.e("zmark_HeartData", "HeartData:"
		// + HeartData.getInstance().getData(this));
		//
		// RootCmd.execRootCmd("settings put system screen_off_timeout 2147483647");
	}

    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if(digital < 0) {
                digital += 256;
            }
            if(digital < 16){
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
    }

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.initButton:
			Intent intent = new Intent(this, FxService.class);
			startService(intent);
			initPhone();


			break;
		case R.id.serviceButton:
			Intent serviceIntent = new Intent(
					Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(serviceIntent);
			break;

		default:
			break;
		}
	}

	private final static String apkPath = "/mnt/sdcard/markapk/";
	private final static String luaPath = "/mnt/sdcard/GodHand/";
    private final static String binPath = "/mnt/sdcard/tmp/";

	public void initPhone() {

		ThreadPool.add(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// if (FirstPreferences.isFirstRun(this))
				// return;
				// 删除所有用户apk
				AppInstallManager.uninstallAllUser(MainActivity.this);

                // 删除apkPath所有文件
                RootCmd.execRootCmd("rm -fr " + apkPath);
                // apk写入sdcard
				Assets.CopyAssets(MainActivity.this, "apk", apkPath);
				// 安装apk
				AppInstallManager.installDir(apkPath);

                // 安装busybox等相关文件
                Assets.CopyAssets(MainActivity.this, "bin", binPath);
                RootCmd.execRootCmd("mount -o rw,remount /system");
                RootCmd.execRootCmd("cp -a "+binPath + "busybox /system/xbin");
                RootCmd.execRootCmd("chmod 777 /system/xbin/busybox");
                RootCmd.execRootCmd("mount -o ro,remount /system");

				// 脚本 写入sdcard
				Assets.CopyAssets(MainActivity.this, "godhand", luaPath);

				// 设置手机永不锁屏脚本实现 或者代码实现
//				try {
//					TouchScript.doRunScript(MainActivity.this,
//							"init_device.lua");
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}d
                RootCmd.execRootCmd("echo InitDevice.lua > " + luaPath + "/tmp/run_file");
                RootCmd.execRootCmd(START_UIAUTOMATOR);
                Intent intent = new Intent(getApplicationContext(), FxService.class);
                stopService(intent);
			}
		});

	}

	// 递归删除文件夹
	private void deleteFile(File file) {
		if (file.exists()) {// 判断文件是否存在
			if (file.isFile()) {// 判断是否是文件
				file.delete();// 删除文件
			} else if (file.isDirectory()) {// 否则如果它是一个目录
				File[] files = file.listFiles();// 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
					this.deleteFile(files[i]);// 把每个文件用这个方法进行迭代
				}
				file.delete();// 删除文件夹
			}
		} else {
			System.out.println("所删除的文件不存在");
		}
	}

	private static String RequestURL = "";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub

		final View textEntryView = LayoutInflater.from(this).inflate(
				R.layout.dialog, null);
		final EditText urlEdit = (EditText) textEntryView
				.findViewById(R.id.seturl_Edit);
		urlEdit.setText(RequestURL);
		AlertDialog dlg = new AlertDialog.Builder(this)
				.setTitle("Server Ip")
				.setView(textEntryView)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						RequestURL = urlEdit.getText().toString();
						PreferenceMgr.setLbsPage(MainActivity.this, "serverIp",
								RequestURL);
						Toast.makeText(MainActivity.this, "服务器地址设置成功",
								Toast.LENGTH_SHORT).show();

					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

							}
						}).create();
		dlg.show();

		return super.onMenuItemSelected(featureId, item);
	}

    private class VolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {

                int type = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", AudioManager.STREAM_MUSIC);

                //EXTRA_VOLUME_STREAM_VALUE
                //EXTRA_PREV_VOLUME_STREAM_VALUE
                int currVolume = mAudioManager.getStreamVolume(type);
                int maxVolume = mAudioManager.getStreamMaxVolume(type);
                if (mOldVolume < 0) {
                    mOldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", currVolume);
                }

                if (currVolume == 0 || currVolume < mOldVolume) {
                    if (mIsAlarmStarted) {
                        toast("请按音量+键关闭正在循环执行的脚本");
                    } else {
                        if ((System.currentTimeMillis() - mTimeout) > 300) {
                            if (!mIsStarted) {
                                toast("启动脚本");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        RootCmd.execRootCmd("echo main.lua > " + luaPath + "/tmp/run_file");
                                        RootCmd.execRootCmd(START_UIAUTOMATOR);
                                        mIsStarted = false;
                                        toast("脚本执行结束");
                                    }
                                }).start();
                            } else {
                                toast("停止脚本");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        RootCmd.execRootCmd(STOP_UIAUTOMATOR1);
                                        RootCmd.execRootCmd(STOP_UIAUTOMATOR2);
                                    }
                                }).start();
                            }
                            mIsStarted = !mIsStarted;
                        }
                    }
                } else if(currVolume == maxVolume || currVolume > mOldVolume) {
                    if (mIsAlarmStarted) {
                        toast("关闭循环执行脚本");
                        ServiceUtil.cancleAlarmManager(context, RunScriptService.class, Constants.RUN_SCRIPT_SERVICE_ACTION);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                RootCmd.execRootCmd(STOP_UIAUTOMATOR1);
                                RootCmd.execRootCmd(STOP_UIAUTOMATOR2);
                            }
                        }).start();
                    }
                    else{
                        toast("开启循环执行脚本");
//                        RootCmd.execRootCmd("echo main.lua > " + luaPath + "/tmp/run_file");
                        ServiceUtil.invokeTimerService(context, RunScriptService.class, Constants.RUN_SCRIPT_SERVICE_ACTION);
                    }
                    mIsAlarmStarted = !mIsAlarmStarted;
                }

                mOldVolume = currVolume;
                mTimeout = System.currentTimeMillis();
            }
        }
    }

    private void registerVolumeChangeReceiver()
    {
        mVolumeReceiver = new VolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVolumeReceiver, filter);
    }

    public void toast(String text){
        Message msg = msgHandler.obtainMessage();
        msg.arg1 = MSG_TOAST;
        Bundle bundle = new Bundle();
        bundle.putString("text", text);
        msg.setData(bundle);
        msgHandler.sendMessage(msg);
    }
}