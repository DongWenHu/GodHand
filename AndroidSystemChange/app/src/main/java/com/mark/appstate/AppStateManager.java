package com.mark.appstate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

@SuppressLint("InlinedApi")
public class AppStateManager {

	public static void startAPP(Context context, String appPackageName) {
		try {
			Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(appPackageName);
			context.startActivity(intent);
		} catch (Exception e) {

		}
	}

	public static void killApp(Context context, String appPackageName) {

		Intent killIntent = new Intent(
				Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri packageURI = Uri.parse("package:" + appPackageName);
		killIntent.setData(packageURI);
		killIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		context.startActivity(killIntent);

	}
}
