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

import com.rzx.godhand.R;
import com.rzx.godhand.mark.cmd.Cmd;
import com.rzx.godhand.mark.timer.service.UploadPOIService;
import com.rzx.godhand.mark.timer.utils.Constants;
import com.rzx.godhand.mark.timer.utils.ServiceUtil;
import com.rzx.godhand.mark.util.PreferenceMgr;
import com.rzx.godhand.mark.util.ThreadPool;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";

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

		serviceButton = (Button) findViewById(R.id.serviceButton);
		serviceButton.setOnClickListener(this);

		RequestURL = PreferenceMgr.getSharedValue(this, "serverIp",
				"192.168.70.97");
		if (!ServiceUtil.isServiceRunning(this, Constants.POI_SERVICE)) {
			ServiceUtil.invokeTimerService(this, UploadPOIService.class, Constants.POI_SERVICE_ACTION);
		}

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        registerVolumeChangeReceiver();
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
		case R.id.serviceButton:
			Intent serviceIntent = new Intent(
					Settings.ACTION_ACCESSIBILITY_SETTINGS);
			startActivity(serviceIntent);
			break;

		default:
			break;
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

                if ((System.currentTimeMillis() - mTimeout) > 500) {
                    if (currVolume == 0 || currVolume < mOldVolume) {
                        if (mIsAlarmStarted) {
                            toast("请按音量+键关闭正在循环执行的脚本");
                        } else {
                            if (!mIsStarted) {
                                toast("启动脚本");
                                Runnable runner = new Runnable() {
                                    @Override
                                    public void run() {
//                                        Cmd.execRootCmd("echo main.lua > " + luaPath + "/tmp/run_file");
                                        Cmd.execRootCmd(START_UIAUTOMATOR);
                                        mIsStarted = false;
                                        toast("脚本执行结束");
                                    }
                                };

                                ThreadPool.add(runner);
                            } else {
                                toast("停止脚本");
                                Runnable runner = new Runnable() {
                                    @Override
                                    public void run() {
                                        Cmd.execRootCmd(STOP_UIAUTOMATOR1);
                                        Cmd.execRootCmd(STOP_UIAUTOMATOR2);
                                    }
                                };

                                ThreadPool.add(runner);
                            }
                            mIsStarted = !mIsStarted;
                        }
                    }
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