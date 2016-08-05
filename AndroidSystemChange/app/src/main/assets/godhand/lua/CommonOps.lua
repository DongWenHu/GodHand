CommonOps = {}

function CommonOps.resetImei(imei)
	local file = io.open(getPath().."/res/imei.txt", "wb")
	if file then
		file:write(imei)
		file:close()
	else
		return false, "open imei file failed";
	end
	
	android_id = AutomatorApi:Md5_16(imei)
	AutomatorApi:executeShellCommand("settings put secure android_id "..android_id)
	return true, "Reset imei succeeded"
end

function CommonOps.doTask(data)
	local ret, msg
	local cmd = data[1]
	if cmd == CMD_RESET_IMEI then
		ret, msg = CommonOps.resetImei(data[2])
	end
	
	return ret, msg
end