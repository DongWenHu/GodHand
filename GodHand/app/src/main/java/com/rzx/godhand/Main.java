package com.rzx.godhand;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Main extends Activity implements OnClickListener, OnLongClickListener {

    private static final String TAG = "com.rzx.godhand.Main";
	private static final int MSG_TOAST = 0x1000001;
    private static final String START_UIAUTOMATOR =
            "am instrument -w -r   -e debug false -e class com.rzx.godhandmator.AutomatorTest com.rzx.godhandmator.test/android.support.test.runner.AndroidJUnitRunner";
    private static final String STOP_UIAUTOMATOR =
            "am force-stop com.rzx.godhandmator";


	private Button execute;
	// public so we can play with these from Lua
	public EditText source;
	public TextView status;

	private AudioManager mAudioManager;
	private VolumeReceiver mVolumeReceiver;
	private long mTimeout = 0;
	private int mOldVolume = -1;

	final StringBuilder output = new StringBuilder();
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        execute = (Button) findViewById(R.id.executeBtn);
		execute.setOnClickListener(this);

		source = (EditText) findViewById(R.id.source);
		source.setOnLongClickListener(this);
//        execRootCmd("reboot");
        source.setText("require 'import'\nrequire 'luatouch'\nos.execute('am start --activity-no-history com.tencent.mm/.ui.account.LoginUI')\ntoast(\"aaa\")");

		status = (TextView) findViewById(R.id.statusText);
		status.setMovementMethod(ScrollingMovementMethod.getInstance());

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        registerVolumeChangeReceiver();

//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Socket socket = new Socket("127.0.0.1", 12580);
//					socket.setSoTimeout(5000);
//					AutomatorRequest as = new AutomatorRequest();
//					as.setMethod("openQuickSettings");
////                    Object[] objects = new Object[1];
////                    objects[0] = "ls";
////					as.setArgs(objects);
//					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
//					out.writeObject(as);
//					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
//					AutomatorResponse response = (AutomatorResponse) in.readObject();
//					if(response != null) {
//						toast(response.getResponse() == null?"null":response.getResponse().toString());
//					}
//					socket.close();
//				} catch (UnknownHostException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}).start();
	}

	private void registerVolumeChangeReceiver()
	{
		mVolumeReceiver = new VolumeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.media.VOLUME_CHANGED_ACTION");
		registerReceiver(mVolumeReceiver, filter);
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

				if (currVolume == 0) {
					if ((System.currentTimeMillis() - mTimeout) > 300) {
						// stop
                        execRootCmd(STOP_UIAUTOMATOR);
					}
				} else if (currVolume == maxVolume) {
					if ((System.currentTimeMillis() - mTimeout) > 300) {
						// exec
                        execRootCmd(START_UIAUTOMATOR);
					}
				} else if (currVolume < mOldVolume) {
					// stop
                    execRootCmd(STOP_UIAUTOMATOR);
				} else if (currVolume > mOldVolume){
					// exec
					execRootCmd(START_UIAUTOMATOR);
				} else {
				}

				mOldVolume = currVolume;
				mTimeout = System.currentTimeMillis();
			}
		}
	}

	public void onClick(View view) {
		final String src = source.getText().toString();
		status.setText("");
	}


	public boolean onLongClick(View view) {
		source.setText("");
		return true;
	}

	public void toast(String text){
		Message msg = msgHandler.obtainMessage();
		msg.arg1 = MSG_TOAST;
		Bundle bundle = new Bundle();
		bundle.putString("text", text);
		msg.setData(bundle);
		msgHandler.sendMessage(msg);
	}

	public static String execRootCmd(String cmd) {
		String result = "";
		DataOutputStream dos = null;
		DataInputStream dis = null;

		try {
			Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
			dos = new DataOutputStream(p.getOutputStream());
			dis = new DataInputStream(p.getInputStream());

			Log.i(TAG, cmd);
			dos.writeBytes(cmd + "\n");
			dos.flush();
			dos.writeBytes("exit\n");
			dos.flush();
			String line = null;
			while ((line = dis.readLine()) != null) {
				Log.d("result", line);
				result += line;
			}
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}