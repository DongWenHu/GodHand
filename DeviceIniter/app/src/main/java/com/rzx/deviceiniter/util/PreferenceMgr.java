package com.rzx.deviceiniter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceMgr {

	public static String getSharedValue(Context context, String key,
			String value) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString(key, value);

	}

	public static void setLbsPage(Context context, String key, String value) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor pEdit = prefs.edit();
		pEdit.putString(key, value);
		pEdit.commit();
	}

}