package com.mark.heartdata;

import java.util.UUID;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.mark.util.FileUtils;

public class HeartData {
	private static HeartData instance;

	private HeartData() {

	}

	public static HeartData getInstance() {
		if (instance == null)
			instance = new HeartData();
		return instance;
	}

	// 手机guid
	// 手机型号
	// 手机操作系统
	// IP地址
	// 网关
	// 掩码
	// DNS

	public String getData(Context context) {
		StringBuilder sb = new StringBuilder();

		sb.append("{\"guid\":\"").append(getUUID()).append("\",")
				.append(getDhcpInfo(context)).append(",")
				.append(getPhoneInfo()).append("}");

		return sb.toString();
	}

	private String getDhcpInfo(Context context) {
		WifiManager wifiManager = ((WifiManager) context
				.getSystemService("wifi"));
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		StringBuilder sb = new StringBuilder();
		sb.append("\"ip\":\"").append(intToIp(dhcpInfo.ipAddress))
				.append("\",\"gw\":\"").append(intToIp(dhcpInfo.gateway))
				.append("\",\"nm\":\"").append(intToIp(dhcpInfo.netmask))
				.append("\",\"dns\":\"").append(intToIp(dhcpInfo.dns1))
				.append("\"");
		return sb.toString();
	}

	// 'mobile_type':'', //手机型号

	// 'os':'', //手机操作系统
	private String getPhoneInfo() {
		return "\"mobile_type\":\"" + android.os.Build.MODEL + "\",\"os\":\""
				+ android.os.Build.VERSION.RELEASE + "\"";
	}

	private String intToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "."
				+ (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
	}

	private final static String UUIDFile = "mnt/sdcard/GodHand/uuid.txt";

	private String getUUID() {
		String uuid = FileUtils.getFileBytes(UUIDFile);
		if (uuid == null)
			uuid = getNewUUID();
		return uuid;
	}

	private String getNewUUID() {
		String s = UUID.randomUUID().toString();
		// 去掉“-”符号
		String uuid = s.substring(0, 8) + s.substring(9, 13)
				+ s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
		setSdUUID(uuid);
		return uuid;
	}

	private void setSdUUID(String uuid) {
		FileUtils.saveFile(uuid.getBytes(), UUIDFile);

	}

}
