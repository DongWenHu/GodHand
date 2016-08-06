require 'import'
import 'com.rzx.godhandmator.AutomatorApi'

function setXposedInstaller()
	local ret
	AutomatorApi:executeShellCommand("am force-stop de.robv.android.xposed.installer")
	AutomatorApi:mSleep(1000)
	AutomatorApi:executeShellCommand("am start de.robv.android.xposed.installer/.WelcomeActivity")
	ret = AutomatorApi:waitNewWindowByTextEqual("Xposed Installer", 5000)
	if ret ==false then
		return false
	end
	AutomatorApi:clickByTextEqual("模块", 0)
	
	ret = AutomatorApi:waitNewWindowByTextEqual("HookSystemParameter", 5000)
	if ret ==false then
		return false
	end
	AutomatorApi:clickByClass("android.widget.CheckBox", 0)
	
	AutomatorApi:click(65,93)
	AutomatorApi:mSleep(1000)
	AutomatorApi:clickByTextEqual("设置", 0)
	AutomatorApi:mSleep(1000)
	AutomatorApi:click(617,1013)
	AutomatorApi:click(65,93)
	AutomatorApi:mSleep(1000)
	AutomatorApi:clickByTextEqual("框架", 0)

	if AutomatorApi:waitNewWindowByTextEqual("小心！", 5000) then
		AutomatorApi:clickByTextEqual("确定", 0)
	end
	
	AutomatorApi:clickByTextEqual("安装/更新", 0)
	if AutomatorApi:waitNewWindowByTextEqual("确定", 5000) then
		AutomatorApi:clickByTextEqual("确定", 0)
	end
	return true
end

function setLockClose()
	local ret
	AutomatorApi:executeShellCommand("am start --activity-no-history com.android.settings/.SecuritySettings")
	ret = AutomatorApi:waitNewWindowByTextEqual("安全与锁屏", 5000)
	if ret ==false then
		return false
	end
	AutomatorApi:clickByTextEqual("屏幕锁定", 0)
	ret = AutomatorApi:waitNewWindowByTextEqual("选择屏幕锁定方式", 5000)
	if ret ==false then
		return false
	end
	AutomatorApi:clickByTextEqual("无", 0)
	return true
end


function main()
	AutomatorApi:executeShellCommand("settings put system screen_off_timeout 2147483647")
	AutomatorApi:executeShellCommand("settings put system screen_brightness 100")
		
	setLockClose()
	setXposedInstaller()
end

main()