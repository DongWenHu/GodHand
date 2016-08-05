package com.rzx.godhand.mark.accessibility;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

@SuppressLint("NewApi")
public class MyAccessibilityService extends AccessibilityService {

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub

		this.processAccessibilityEnvent(event);
	}

	// com.kingroot.kinguser
	// child:android.widget.Button拒绝android.widget.Button允许
	private void processAccessibilityEnvent(AccessibilityEvent event) {

		if (event.getSource() == null) {

		} else {

//			Log.e("zmark_accessibility", event.getClassName() + ":"
//					+ recycle(event.getSource()));
			// 处理root授权
			if (event.getPackageName().toString()
					.equals("com.kingroot.kinguser")) {
				List<AccessibilityNodeInfo> ok_nodes = event.getSource()
						.findAccessibilityNodeInfosByText("允许");
				if (ok_nodes != null && !ok_nodes.isEmpty()) {
					AccessibilityNodeInfo node;
					for (int i = 0; i < ok_nodes.size(); i++) {
						node = ok_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")
								&& node.isEnabled()) {
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							return;
						}
					}
				}
			} else {
				processKillApplication(event);
				return;
			}
		}

	}

	public String recycle(AccessibilityNodeInfo info) {
		if (info == null)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(info.getClassName());
		if (info.getClassName().toString().equals("android.widget.Text")
				|| info.getClassName().toString()
						.equals("android.widget.Button")) {
			sb.append(info.getText());
		}

		for (int i = 0; i < info.getChildCount(); i++) {
			sb.append(recycle(info.getChild(i)));
		}
		return sb.toString();

	}

	@Override
	protected boolean onKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return true;

	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub

	}

	private void processKillApplication(AccessibilityEvent event) {

		if (event.getSource() != null) {
			if (event.getPackageName().equals("com.android.settings")) {
				List<AccessibilityNodeInfo> stop_nodes = event.getSource()
						.findAccessibilityNodeInfosByText("强行停止");
				if (stop_nodes != null && !stop_nodes.isEmpty()) {
					AccessibilityNodeInfo node;
					for (int i = 0; i < stop_nodes.size(); i++) {
						node = stop_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")) {
							if (node.isEnabled()) {
								node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
								return;
							}
						}
					}
				}

				List<AccessibilityNodeInfo> ok_nodes = event.getSource()
						.findAccessibilityNodeInfosByText("确定");
				if (ok_nodes != null && !ok_nodes.isEmpty()) {
					AccessibilityNodeInfo node;
					for (int i = 0; i < ok_nodes.size(); i++) {
						node = ok_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")) {
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							return;
						}
					}

				}
			}
		}
	}

}
