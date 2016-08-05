package com.mark.cmd;

import android.content.Context;
import android.widget.Toast;

public class ReBootCmd {

	public static void reboot(Context context) {
		if (!RootCmd.haveRoot()) {
			Toast.makeText(context, "手机未root,不能获取最佳体验效果", Toast.LENGTH_LONG)
					.show();
			return;
		}

		RootCmd.execRootCmd("reboot");

	}

}
