require 'import'
import 'com.rzx.godhandmator.AutomatorApi'

--获取滚动条控件
function getScrollable()
	local uiSelector,uiScrollable
	uiSelector = luajava.newInstance('android.support.test.uiautomator.UiSelector')
	uiSelector = uiSelector:scrollable(true)
	if uiSelector ~= nil then
		uiScrollable = luajava.newInstance('android.support.test.uiautomator.UiScrollable', uiSelector)
	end
	return uiScrollable
end

--返回程序HOME目录
function getPath()
	return '/mnt/sdcard/GodHand'
end

--字符串分割
function string.split(str, delimiter)
	if str==nil or str=='' or delimiter==nil then
		return nil
	end
	
    local result = {}
    for match in (str..delimiter):gmatch("(.-)"..delimiter) do
        table.insert(result, match)
    end
    return result
end

--找色
function findMultiColorInRegionFuzzy(beginColor, otherColors, sim, x1, y1, x2, y2)
	local result
	result = AutomatorApi:findMultiColorInRegionFuzzy(beginColor, otherColors, sim, x1, y1, x2, y2)
	result = string.split(result, ',')
	return tonumber(result[1]),tonumber(result[2])
end

--规定时间内完成找色
function findMultiColorInRegionFuzzyInTime(p1,p2,p3,p4,p5,p6,p7,p8)
	local var
	local count
	local x,y
	count = p8/500
	if count == 0 then count = 1 end
	for var = 1, count do
		AutomatorApi:mSleep(500)
		x,y = findMultiColorInRegionFuzzy( p1,p2,p3,p4,p5,p6,p7)
		if x ~= -1 and y ~= -1 then
			return x, y
		end
	end
	return x,y
end

--计算文件长度
function lengthOfFile(filename)
	if AutomatorApi:fileExists(filename) == false then
		return -1
	end
	
	local fh = io.open(filename, "rb")
	local len = fh:seek("end")
	fh:close()
	return len
end

--获取目录下文件列表
function getFileList(dir)
	local file_list = {}
	local cmd = "ls "..dir.." > /sdcard/GodHand/tmp/file_list"
	AutomatorApi:executeShellCommand(cmd)
	local s = io.open("/sdcard/GodHand/tmp/file_list")
	
	local start_pos, end_pos
	local line
	for line in s:lines() do 
		table.insert(file_list, dir.."/"..line)
	end
	return file_list
end

--获取最上层Activity
function getTopActivity()
	local iRet, sRet = pcall(function()
		local path = "/sdcard/tmp/getTopActivity"
		os.execute("dumpsys activity top >" .. path)
		mSleep(500)
		return string.match(readFileString(path),"ACTIVITY ([^ ]+)")
	end)
	if iRet == true then
		return sRet
	else
		return ""
	end
end

--获取图片验证码
function getVcode(guid, task_id, file, timeout)
	local str_post = {
			["guid"] = guid,
			["task_id"] = task_id,
			["snapshot_path"] = AutomatorApi:base64EncodeFile(file)
		}
		
	str_post = json.encode(str_post)
	local sRet = AutomatorApi:httpPost(g_conf_put_vcode_url, str_post)
	if sRet == '{"success":true}' then
		str_post = {
			["guid"] = guid,
			["task_id"] = task_id
		}
		
		str_post = json.encode(str_post)
		while timeout > 0 do
			sRet = AutomatorApi:httpPost(g_conf_get_vcode_url, str_post)
			if sRet ~= "" then
				local iRet, sRet = pcall(function()
						return json.decode(sRet)
				end)
				
				if iRet == true then
					if sRet["success"] == true then
						local code = sRet["vcode"]
						return true, code
					elseif sRet["success"] == false then
						AutomatorApi:mSleep(2000)
						timeout = timeout - 2000
						if timeout <= 0 then
							return false, "getVcode time out."..sRet["message"]
						end
					end
				else
					return false, "Get vcode failed. Json decode error."
				end
			else
				AutomatorApi:log("main", "Get vcode failed. Post failed.")
				return false, "Get vcode failed. Post failed."
			end
		end
	else
		return false, "Put vcode failed."
	end
end

--获取短信验证码
function getSms(guid, task_id, phone_num, timeout)
	local http_result_file = getPath().."/tmp/http_result.txt"
	local str_post = {
			["guid"] = guid,
			["task_id"] = task_id,
			["phone"] = phone_num
		}
		
	str_post = json.encode(str_post)
	AutomatorApi:log("test", "curl -d '"..str_post
		.."' -o "..http_result_file
		.." -H 'Content-Type: application/json'"
		.." -H 'Content-Length: "..#str_post.."' "
		..g_conf_send_phone_url)
	AutomatorApi:executeShellCommand("curl -d '"..str_post
		.."' -o "..http_result_file
		.." -H 'Content-Type: application/json'"
		.." -H 'Content-Length: "..#str_post.."' "
		..g_conf_send_phone_url)
	local sRet = AutomatorApi:executeShellCommand("cat "..http_result_file)
	-- local sRet = AutomatorApi:httpPost(g_conf_send_phone_url, str_post)
	if sRet == '{"success":true}' then
		str_post = {
			["guid"] = guid,
			["task_id"] = task_id
		}
		
		str_post = json.encode(str_post)
		while timeout > 0 do
			sRet = AutomatorApi:executeShellCommand("curl -d '"..str_post
				.."' -o "..http_result_file
				.." -H 'Content-Type: application/json'"
				.." -H 'Content-Length: "..#str_post.."' "
				..g_conf_get_sms_url)
			sRet = AutomatorApi:readFile(http_result_file)
			-- sRet = AutomatorApi:httpPost(g_conf_get_sms_url, str_post)
			if sRet ~= "" then
				local iRet, sRet = pcall(function()
						return json.decode(sRet)
				end)
				
				if iRet == true then
					if sRet["success"] == true then
						local sms = sRet["sms"]
						return true, sms
					elseif sRet["success"] == false then
						AutomatorApi:mSleep(2000)
						timeout = timeout - 2000
						if timeout <= 0 then
							return false, "getSms time out."..sRet["message"]
						end
					end
				else
					AutomatorApi:log("main", "getSms().get_sms failed. Json decode error.")
					return false, "Get vcode failed. Json decode error."
				end
			else
				AutomatorApi:log("main", "getSms().get_sms failed. Post failed.")
				return false, "Get sms failed. Post failed."
			end
		end
	else
		AutomatorApi:log("main", "getSms().send_phone failed."..sRet)
		return false, "Send phone failed."
	end
end
	