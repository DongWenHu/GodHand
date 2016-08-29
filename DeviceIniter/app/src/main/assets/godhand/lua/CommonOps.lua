CommonOps = {}

function CommonOps.resetImei(imei)
	AutomatorApi:writeFile(getPath().."/res/imei.txt", imei)
	
	android_id = AutomatorApi:Md5_16(imei)
	AutomatorApi:executeShellCommand("settings put secure android_id "..android_id)
	return true, "Reset imei succeeded"
end

function CommonOps.sleep(ms)
	if ms == nil then
		return false, "Sleep failed. Parameter is null."
	end
	AutomatorApi:mSleep(ms)

	return true, "Ok, I was awake."
end

function CommonOps.doTask(data)
	local ret, msg
	local cmd = data[1]
	if cmd == CMD_RESET_IMEI then
		ret, msg = CommonOps.resetImei(data[2])
	elseif cmd == CMD_SLEEP then
		ret, msg = CommonOps.sleep(data[2])
	end
	
	return ret, msg
end