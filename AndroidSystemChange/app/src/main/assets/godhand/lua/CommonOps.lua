CommonOps = {}

function CommonOps.resetImei(imei)
	local file = io.open("/sdcard/TouchSprite/res/imei.txt", "wb")
	if file then
		file:write(imei)
		file:close()
	else
		return false, "open imei file failed";
	end
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