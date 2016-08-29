require 'CommonOps'
require 'WechatOps'

DoTask = {}

function DoTask.dispatchTask(data)
	local ret, result
	local cmd = data[1]
	
	--取高四位确定哪个应用
	local high = string.sub(cmd, 1, 4)
	
	
	if high == "9000" then
		--全局命令
		ret, result = CommonOps.doTask(data)
	elseif high == "0000" then
		--微信应用
		ret, result = WechatOps.doTask(data) 
	elseif high == "0001" then
		--facebook应用
		ret, result = WechatOps.doTask(data) 
	end
	
	return ret, result
end


function DoTask.doTask(tb_data)
	if g_test_flag then
		local jsdata = AutomatorApi:readFile(getPath().."/tmp/test.json")
		tb_data = json.decode(jsdata)
		--test begin
		local test_count = {
			-- {"Lldu61332943", "sdef90832", "866980024308805"},
			-- {"XAWR61331085", "tszergy5s", "866980029485806"},
			-- {"dUmD59100650", "tszskygy123", "866980021377118"},
			{"jQNN59100782", "ta9686sd", "866980020381723"},
			{"saCP59100587", "myta1216", "866980026177216"}
		}
		
		math.randomseed(os.time())
		local rand_count = math.random(2)
		tb_task[1]["op"][2] = test_count[rand_count][3]
		tb_task[2]["op"][2] = test_count[rand_count][1]
		tb_task[2]["op"][3] = test_count[rand_count][2]
		--test end
		AutomatorApi:toast(tb_task[1]["op"][2].."\n"..tb_task[2]["op"][2].."\n"..tb_task[2]["op"][3])
	end
	
	SharedDic.set('task_id', tb_data["task_id"])
	SharedDic.set('task_type', tb_data["task_type"])
	local task_id = tb_data["task_id"]
	local task_type = tb_data["task_type"]
	local guid = AutomatorApi:readFile(getPath().."/uuid.txt")
	local var
	local ret
	local str_post
	local tb_task = {}
	local msg_tmp = ""
	local msg = ""
	
	tb_task = tb_data["cmd"]
	
	local check_times = 10
	local network_avaliable
	while check_times > 0 do
		network_avaliable = check_network_avaliable()
		if network_avaliable == true then
			break
		end
		check_times = check_times - 1
	end
	if network_avaliable == false then
		AutomatorApi:log("main", "Network is not avaliable.")
		ret = false
		msg = "Network is not avaliable."
	else
		for var = 1, #tb_task do
			local iRet, sRet = pcall(function()	
				ret, msg_tmp = DoTask.dispatchTask(tb_task[var]["op"])
			end)
			if iRet == false then
				ret = false
				msg = msg..'|'..'Exception error.'..sRet
			else
				msg = msg..'|'..msg_tmp
			end
			if ret == false then
				if tb_task[var]["can_ignore"] == nil or tb_task[var]["can_ignore"] == 0 then
					break
				end
			end
		end
	end

	ret = ret and 1 or 2
	local pic_name = getPath().."/tmp/snapshot.png"
	local result 

	--保存当前页面截图
	AutomatorApi:log("takeScreenshot", "before takeScreenshot")
	AutomatorApi:takeScreenshot(pic_name)
	AutomatorApi:log("takeScreenshot", "after takeScreenshot")
	result = {
		["status"]=ret,
		["message"]=msg,
		["snapshot_path"]=AutomatorApi:base64EncodeFile(pic_name)
	}

	result["task_id"] = task_id
	result["guid"] = guid
	result["registAccount"] = WechatOps.strCurUsr


	str_post = json.encode(result)
	if g_test_flag == false then
		local headers = 'Content-Type:application/json;Content-Length:'..#str_post
		
		local repeat_times = 5
		local sRet = ""
		while repeat_times > 0 do
			AutomatorApi:log("test_http_post", "before post")
			sRet = AutomatorApi:httpPostWithHeader(g_conf_task_result_url, str_post, headers)
			AutomatorApi:log("test_http_post", "after post")
			repeat_times = repeat_times - 1
			if sRet ~= "" then
				break
			end
		end
		
		if sRet == "" then
			result["snapshot_path"] = ""
			str_post = json.encode(result)
			AutomatorApi:logAppend("http_err", "DoTask.doTask: "..str_post)
		else
			AutomatorApi:logAppend("http_suc", "DoTask.doTask: "..result["message"])
		end
	else
		if ret == 2 then
			result["snapshot_path"] = ""
			str_post = json.encode(result)
			AutomatorApi:logAppend("http_err", "DoTask.doTask: "..str_post)
		end
	end
end