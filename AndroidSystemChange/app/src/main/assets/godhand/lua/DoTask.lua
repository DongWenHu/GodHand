require 'CommonOps'
require 'WechatOps'

DoTask = {}

function DoTask.dispatchTask(data)
	local ret, result
	local cmd = data[1]
	
	--取高四位确定哪个应用
	local high = string.sub(cmd, 1, 4)
	
	--全局命令
	if high == "9000" then
		ret, result = CommonOps.doTask(data)
	end
	
	--微信应用
	if high == "0000" then
		ret, result = WechatOps.doTask(data) 
	end
	
	return ret, result
end


function DoTask.doTask(tb_data)
	local jsdata = AutomatorApi:readFile(getPath().."/tmp/aaa.json")
	tb_data = json.decode(jsdata)
	local task_id = tb_data["task_id"]
	local task_type = tb_data["task_type"]
	local guid = AutomatorApi:readFile(getPath().."/uuid.txt")
	local var
	local ret, msg
	local str_post
	local tb_task = {}
	
	tb_task = tb_data["cmd"]
	for var = 1, #tb_task do
		ret, msg = DoTask.dispatchTask(tb_task[var]["op"])
		if ret == false then
			break
		end
	end

	ret = ret and 1 or 2
	local pic_name = getPath().."/tmp/snapshot.png"
	local result 
	if ret == 2 then
		--如果执行失败保存当前页面截图
		AutomatorApi:takeScreenshot(pic_name)
		result = {
			["status"]=ret,
			["message"]=msg,
			["snapshot_path"]=AutomatorApi:base64EncodeFile(pic_name)
		}
	else
		result = {
			["status"]=ret
		}
	end
	
	result["task_id"] = task_id
	result["guid"] = guid
	if task_type == "wx_vote" and result["status"] == 1 then
		result["snapshot_path"]=AutomatorApi:base64EncodeFile(g_conf_wx_vote_s_snapshot_path)
	end
	if task_type == "wx_register" and result["status"] == 1 then
		result["registAccount"] = WechatOps.strCurUsr
	end

	str_post = json.encode(result)
	local headers = 'Content-Type:application/json;Content-Length:'..#str_post
	sRet = AutomatorApi:httpPostWithHeader(g_conf_task_result_url, str_post, headers)
	-- sRet = AutomatorApi:executeShellCommand("curl -d '"..str_post
				-- .." -H 'Content-Type: application/json'"
				-- .." -H 'Content-Length: "..#str_post.."' "
				-- ..g_conf_task_result_url)
	sRet = sRet~="" and sRet or "post failed"
	AutomatorApi:log("main", "post result status("..tostring(result["status"]).."): "..sRet)
end

return DoTask