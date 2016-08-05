package com.mark.timer.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import com.mark.timer.service.UploadPOIService;

/**
 * Created by coder80 on 2014/10/31.
 */

public class ServiceUtil {

	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager
				.getRunningServices(Constants.RETRIVE_SERVICE_COUNT);

		if (null == serviceInfos || serviceInfos.size() < 1) {
			return false;
		}

		for (int i = 0; i < serviceInfos.size(); i++) {
			if (serviceInfos.get(i).service.getClassName().contains(className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static void invokeTimerService(Context context, Class<?> clzz, String action) {
		PendingIntent alarmSender = null;
		Intent startIntent = new Intent(context, clzz);
		startIntent.setAction(action);
		try {
			alarmSender = PendingIntent.getService(context, 0, startIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		AlarmManager am = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), Constants.ELAPSED_TIME, alarmSender);
	}

	public static void cancleAlarmManager(Context context, Class<?> clzz, String action) {
	
		Intent intent = new Intent(context, clzz);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
	}
}
