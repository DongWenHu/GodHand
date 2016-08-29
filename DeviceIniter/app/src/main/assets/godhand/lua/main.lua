require 'GHLib'
require 'Configs'
require 'DoTask'
require 'SharedDic'
json = require 'json'

local task_file_recv_path = getPath().."/tmp/task_file.tar"
local task_file_extract_path = getPath().."/tmp/gh_task"
local update_file_recv_path = getPath().."/tmp/ts_code_update.tar"

function check_update()
	local version = AutomatorApi:readFile(getPath().."/VERSION")
	local res = AutomatorApi:httpGet(g_conf_check_update_url, "version="..version)
	if res ~= "" then
		if res == '{"success":true}' then
			AutomatorApi:executeShellCommand("rm -f "..update_file_recv_path)
			AutomatorApi:executeShellCommand("curl -o "..update_file_recv_path.." "..g_conf_update_url)
			AutomatorApi:executeShellCommand("busybox tar -xf .."..update_file_recv_path.." -C "..getPath())
			return true
		end
	else
		AutomatorApi:log("main", "Check upgrade failed. Failed connect to http host.")
	end
	
	return false
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
	if check_update() then
		return
	end
	
	--获取任务文件
	local guid = AutomatorApi:readFile(getPath().."/uuid.txt")
	AutomatorApi:executeShellCommand("rm -f "..task_file_recv_path)
	AutomatorApi:executeShellCommand("curl -o '"..task_file_recv_path.."' "..g_conf_task_file_url.."?guid="..guid)
	local len_file = lengthOfFile(task_file_recv_path)
	if len_file == -1 then
		AutomatorApi:log("main", "Get task file failed. Failed connect to http host.")
		return
	elseif len_file == 17 then
		local f_data = AutomatorApi:readFile(task_file_recv_path)
		if f_data == '{"success":false}' then
			AutomatorApi:log("main", "Get task file failed. Host return false.")
			return
		end
	end

	AutomatorApi:executeShellCommand("mkdir -p "..task_file_extract_path)
	AutomatorApi:executeShellCommand("rm -fr "..task_file_extract_path.."/*")
	AutomatorApi:executeShellCommand("busybox tar -xf "..task_file_recv_path.." -C "..task_file_extract_path)


	local task_id = AutomatorApi:executeShellCommand("ls "..task_file_extract_path)
	task_id = string.gsub(task_id, "\n", "")
	if task_id == nil or task_id == "" then
		AutomatorApi:log("main", "Get task file failed. Error tar file")
		return
	end
	
	--通知下载任务完成
	AutomatorApi:httpGet(g_conf_download_complete_url, "guid="..guid)

	local jsdata = AutomatorApi:readFile(task_file_extract_path.."/"..task_id.."/script")
	local tbl_task = json.decode(jsdata)
	local task_type = tbl_task["task_type"]
	tbl_task["task_id"] = task_id
	SharedDic.set("task_id", task_id)
	SharedDic.set("task_type", task_type)
	
	local tar_file = getFileList(task_file_extract_path.."/"..task_id.."/*.tar")
	if #tar_file ~= 0 then
		AutomatorApi:executeShellCommand("cp -a "..task_file_extract_path.."/"..task_id.."/"..tar_file[1].." "
			..getPath().."/tmp")
	end
		

	if task_type == "wx_vote" then
		AutomatorApi:executeShellCommand("cp -a "..task_file_extract_path.."/"..task_id.."/main.lua "..getPath().."/lua/wx_vote.lua")
	elseif task_type == "wx_keep_account" then
		AutomatorApi:executeShellCommand("cp -a "..task_file_extract_path.."/"..task_id.."/*.tar "..getPath().."/res/cache/com.tencent.mm/")
	end
	
	
	--设置头像时避免找不到一张图片
	local file_exists = AutomatorApi:fileExists("/storage/sdcard0/tencent/micromsg/weixin/snapshot.png")
	if file_exists == false then
		AutomatorApi:takeScreenshot("/storage/sdcard0/tencent/micromsg/weixin/snapshot.png")
	end
	
	math.randomseed(os.time())
	DoTask.doTask(tbl_task)
	
end

-- AutomatorApi:screenOn()
AutomatorApi:toast("任务执行开始")
AutomatorApi:mSleep(2000)

if g_test_flag then
	DoTask.doTask({})
else
	main()
end
AutomatorApi:toast("任务执行结束")
AutomatorApi:mSleep(2000)
AutomatorApi:executeShellCommand("busybox ps -ef|busybox grep app_process |busybox grep -v 'grep'|busybox grep instrument|busybox awk '{print $1}'|busybox xargs kill -9");
-- AutomatorApi:screenOff()