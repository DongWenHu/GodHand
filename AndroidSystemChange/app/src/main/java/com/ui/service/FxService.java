package com.ui.service;

import com.mark.system.R;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;


import android.view.WindowManager.LayoutParams;

import android.widget.RelativeLayout;

@SuppressLint("InflateParams")
public class FxService extends Service {

	// 定义浮动窗口布局

	RelativeLayout mProgressLayout;
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		createProgressBarView();
	}

	@SuppressWarnings("static-access")
	private void createProgressBarView() {
		// TODO Auto-generated method stub
		if (mProgressLayout == null) {
			mWindowManager = (WindowManager) getApplication().getSystemService(
					getApplication().WINDOW_SERVICE);
			wmParams = new WindowManager.LayoutParams();
			// 获取的是WindowManagerImpl.CompatModeWrapper

			// 设置window type
			wmParams.type = LayoutParams.TYPE_PHONE;
			// 设置图片格式，效果为背景透明
			wmParams.format = PixelFormat.RGBA_8888;
			// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
			wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
			// 调整悬浮窗显示的停靠位置为左侧置顶
			wmParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
			// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
			wmParams.x = 0;
			wmParams.y = 0;

			// 设置悬浮窗口长宽数据
			wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
			wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

			LayoutInflater inflater = LayoutInflater.from(getApplication());
			// 获取浮动窗口视图所在布局
			mProgressLayout = (RelativeLayout) inflater.inflate(
					R.layout.progress_layout, null);
			// 添加mProgressLayout
			mWindowManager.addView(mProgressLayout, wmParams);

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (mProgressLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mProgressLayout);
			mProgressLayout = null;

		}
	}

}
