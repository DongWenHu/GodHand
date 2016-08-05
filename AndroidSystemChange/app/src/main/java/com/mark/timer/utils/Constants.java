package com.mark.timer.utils;

public class Constants {
	public static final int ELAPSED_TIME = 5 * 1000;
	public static final int RETRIVE_SERVICE_COUNT = 50;
	public static final int ELAPSED_TIME_DELAY = 2 * 60 * 1000;// get GPS
																// delayed
	public static final int BROADCAST_ELAPSED_TIME_DELAY = 10 * 1000;
	public static final String WORKER_SERVICE = "com.mark.timer.service.WorkService";
	public static final String POI_SERVICE = "com.mark.timer.service.UploadPOIService";
	public static final String POI_SERVICE_ACTION = "com.mark.timer.service.UploadPOIService.action";
	public static final String RUN_SCRIPT_SERVICE = "com.mark.timer.service.RunScriptService";
	public static final String RUN_SCRIPT_SERVICE_ACTION = "com.mark.timer.service.RunScriptService.action";

}
