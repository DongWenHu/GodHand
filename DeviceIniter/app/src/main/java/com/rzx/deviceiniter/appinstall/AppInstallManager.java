package com.rzx.deviceiniter.appinstall;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.rzx.deviceiniter.cmd.RootCmd;

import java.io.File;
import java.util.List;

public class AppInstallManager {

	public static void installDir(String dirPath, String type) {

		File dir = new File(dirPath);
		if (dir.isDirectory()) {
			File[] fileList = dir.listFiles();
			if (fileList == null)
				return;
			for (int i = 0; i < fileList.length; i++) {
                if (type.equals("apk")) {
                    installApk(fileList[i]);
                }
                if (type.equals("bin")){
                    installBin(fileList[i]);
                }
			}
		}
	}

	public static void installApk(File apk) {
		// TODO Auto-generated method stub
//		RootCmd.execRootCmd("mount -o rw,remount /system");
//		RootCmd.execRootCmd("cp -a "+ apk.getPath() + " /system/app/" + apk.getName());
		RootCmd.execRootCmd("pm install -r " + apk.getPath());
//        RootCmd.execRootCmd("mount -o ro,remount /system");
	}

    public static void installBin(File bin){
        RootCmd.execRootCmd("mount -o rw,remount /system");
        RootCmd.execRootCmd("cp -a "+ bin.getPath() + " /system/xbin/");
        RootCmd.execRootCmd("chown 0:0 /system/xbin/" + bin.getName());
        RootCmd.execRootCmd("chmod 6755 /system/xbin/" + bin.getName());
        RootCmd.execRootCmd("mount -o ro,remount /system");
    }

	public static void uninstallApk(String packageName) {
		Log.e("zmark", "uninstallApk:" + packageName);
		RootCmd.execRootCmd("pm uninstall " + packageName);
	}

	public static void uninstallAllUser(Context context) {
		// TODO Auto-generated method stub
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);

			// 判断是否系统应用
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					&& !packageInfo.packageName
							.equals(context.getPackageName())
					&& !packageInfo.packageName.equals("com.kingroot.kinguser")) {
				// 非系统应用
				uninstallApk(packageInfo.packageName);
			} else {
				// 系统应用　　　　　　　　

			}
		}

	}

	// 判断是否安装应用 是否要求版本强制更新
	public static boolean checkPackage(Context context, String packageName,
			String version, boolean update)

	{

		if (packageName == null || "".equals(packageName))
			return false;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					packageName, 0);

			if (!update
					|| version
							.equals(info.versionName + ":" + info.versionCode))
				return true;
			return false;

		} catch (NameNotFoundException e) {
			return false;
		}

	}
}
