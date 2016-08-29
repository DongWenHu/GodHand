require 'GHLib'
require 'Configs'
json = require 'json'

local result = {}
local pic_name = getPath().."/tmp/snapshot.png"

AutomatorApi:takeScreenshot(pic_name)
result = {
	["status"]=2,
	["message"]="Exception error.",
	["snapshot_path"]=AutomatorApi:base64EncodeFile(pic_name)
}

result["task_id"] = SharedDic.get("task_id")
result["guid"] = AutomatorApi:readFile(getPath().."/uuid.txt")

local str_post = json.encode(result)
if g_test_flag == false then
	local headers = 'Content-Type:application/json;Content-Length:'..#str_post

	local repeat_times = 5
	local sRet = ""
	while repeat_times > 0 do
		sRet = AutomatorApi:httpPostWithHeader(g_conf_task_result_url, str_post, headers)
		repeat_times = repeat_times - 1
		if sRet ~= "" then
			break
		end
	end

	if sRet == "" then
		result["snapshot_path"] = ""
		str_post = json.encode(result)
		AutomatorApi:logAppend("exception_err", "OnException: "..str_post)
	else
		AutomatorApi:logAppend("exception_suc", "OnException: "..sRet)
	end
else
	result["snapshot_path"] = ""
	str_post = json.encode(result)
	AutomatorApi:logAppend("exception_err", "OnException: "..str_post)
end