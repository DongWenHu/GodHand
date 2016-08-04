package com.mark.activity;

import java.io.File;

import com.mark.appinstall.AppInstallManager;
import com.mark.luasprite.TouchScript;
import com.rzx.godhand.R;
import com.mark.timer.utils.Constants;
import com.mark.timer.utils.ServiceUtil;
import com.mark.util.Assets;
import com.mark.util.PreferenceMgr;
import com.mark.util.ThreadPool;
import com.ui.service.FxService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Button initButton;
	Button serviceButton;

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
			ServiceUtil.invokeTimerPOIService(this);
		}
		// Log.e("zmark_HeartData", "HeartData:"
		// + HeartData.getInstance().getData(this));
		//
		// RootCmd.execRootCmd("settings put system screen_off_timeout 2147483647");
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

	private final static String apkPath = "mnt/sdcard/markapk/";
	private final static String luaPath = "mnt/sdcard/TouchSprite/";

	public void initPhone() {

		ThreadPool.add(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				// if (FirstPreferences.isFirstRun(this))
				// return;
				// ɾ�������û�apk
				AppInstallManager.uninstallAllUser(MainActivity.this);
				// apkд��sdcard
				Assets.CopyAssets(MainActivity.this, "apk", apkPath);
				// ��װapk
				AppInstallManager.installDir(apkPath);

				// �ж�mnt/sdcard/TouchSprite/�Ƿ����
				File mWorkingPath = new File("mnt/sdcard/TouchSprite");
				// ����ļ�·��������
				if (!mWorkingPath.exists()) {
					// �����ļ���
					mWorkingPath.mkdirs();
				}

				File tmpFile = new File("mnt/sdcard/tmp");
				deleteFile(tmpFile);
				// �ű� д��sdcard
				Assets.CopyAssets(MainActivity.this, "touchscript", luaPath);

				// �����ֻ����������ű�ʵ�� ���ߴ���ʵ��
				try {
					TouchScript.doRunScript(MainActivity.this,
							"init_device.lua");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	// �ݹ�ɾ���ļ���
	private void deleteFile(File file) {
		if (file.exists()) {// �ж��ļ��Ƿ����
			if (file.isFile()) {// �ж��Ƿ����ļ�
				file.delete();// ɾ���ļ�
			} else if (file.isDirectory()) {// �����������һ��Ŀ¼
				File[] files = file.listFiles();// ����Ŀ¼�����е��ļ� files[];
				for (int i = 0; i < files.length; i++) {// ����Ŀ¼�����е��ļ�
					this.deleteFile(files[i]);// ��ÿ���ļ�������������е���
				}
				file.delete();// ɾ���ļ���
			}
		} else {
			System.out.println("��ɾ�����ļ�������");
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
						Toast.makeText(MainActivity.this, "��������ַ���óɹ�",
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

}