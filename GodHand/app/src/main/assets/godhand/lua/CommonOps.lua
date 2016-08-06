CommonOps = {}

function CommonOps.resetImei(imei)
	-- local file = io.open(getPath().."/res/imei.txt", "wb")
	-- local file = io.open("/sdcard/TouchSprite/res/imei.txt", "wb")
	-- if file then
		-- file:write(imei)
		-- file:close()
	-- else
		-- return false, "open imei file failed";
	-- end
	
	-- AutomatorApi:writeFile("/sdcard/TouchSprite/res/imei.txt", imei)
	AutomatorApi:writeFile(getPath().."/res/imei.txt", imei)
	
	android_id = AutomatorApi:Md5_16(imei)
	-- android_id = "2dfe2a50bec50170"
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