require 'GHLib'
require 'Configs'
json = require 'json'

local task_file_recv_path = getPath().."/tmp/task_file.tar"
local task_file_extract_path = getPath().."/tmp/gh_task"
local update_file_recv_path = getPath().."/tmp/ts_code_update.tar"
g_task_id = ""

function check_update()
	local version = AutomatorApi:readFile(getPath().."/VERSION")
	local res = AutomatorApi:httpGet(g_conf_check_update_url, "version="..version)
	if res ~= "" then
		if res == '{"success":true}' then
			local ret = httpGet(g_conf_update_url, "")
			if ret ~= "" then
				AutomatorApi:writeFile(update_file_recv_path, ret)
				AutomatorApi:executeShellCommand("tar -xf .."..update_file_recv_path.." -C "..getPath())
			else
				AutomatorApi:log("main", "Get upgrade file failed. Failed connect to http host.")
			end
		end
	else
		AutomatorApi:log("main", "Check upgrade failed. Failed connect to http host.")
	end
end

function check_log_size()
	local file_list = getFileList(getPath().."/log")
	for k,v in pairs(file_list) do
		if(lengthOfFile(v) > 1024*500) then
			os.remove(v)
		end
	end
end

function main()
	--检查日志大小,超过500KB就删除文件
	check_log_size()
	--检查脚本文件更新
	check_update()
	
	--获取任务文件
	local guid = AutomatorApi:readFile(getPath().."/uuid.txt")
	local res = AutomatorApi:httpGet(g_conf_task_file_url, "guid="..guid)
	if res == "" then
		AutomatorApi:log("main", "Get task file failed. Failed connect to http host.")
		return
	else
		if string.len(res) == 17 and res == '{"success":false}' then
			AutomatorApi:log("main", "Get task file failed. Host return false.")
			return
		end
	end

	AutomatorApi:writeFile(task_file_recv_path, res)
	AutomatorApi:executeShellCommand("mkdir -p "..task_file_extract_path)
	AutomatorApi:executeShellCommand("rm -fr "..task_file_extract_path.."/*")
	AutomatorApi:executeShellCommand("tar -xf .."..task_file_recv_path.." -C "..task_file_extract_path)


	g_task_id = AutomatorApi:executeShellCommand("ls "..task_file_extract_path)
	g_task_id = string.gsub(g_task_id, "\n", "")
	if g_task_id == nil or g_task_id == "" then
		AutomatorApi:log("main", "Get task file failed. Error tar file")
		return
	end
	
	--通知下载任务完成
	AutomatorApi:httpGet(g_conf_download_complete_url, "guid="..guid)

	local jsdata = AutomatorApi:readFile(task_file_extract_path.."/"..g_task_id.."/script")
	local tbl_task = json.decode(jsdata)
	local task_type = tbl_task["task_type"]
	tbl_task["task_id"] = g_task_id

	if task_type == "wx_vote" then
		AutomatorApi:executeShellCommand("cp -a "..task_file_extract_path.."/"..g_task_id.."/main.lua "..getPath().."/lua/wx_vote.lua")
	end

	dt = dofile(getPath().."/lua/DoTask.lua")
	dt.doTask(tbl_task)
end

main()

