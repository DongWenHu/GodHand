require 'GHLib'
require 'ProtocolDefine'
require 'Configs'


WechatOps = {strCurUsr = ""}

--登录
function WechatOps.login(usr, pwd)
	local ret, msg
	ret = true
	msg = 'Login succeeded.'
	
	AutomatorApi:executeShellCommand("am force-stop com.tencent.mm")
	AutomatorApi:mSleep(1000)
	local cache_file = getPath().."/res/cache/com.tencent.mm/"..usr
	
	if AutomatorApi:fileExists(cache_file) then
		ret,msg = WechatOps.loginByCache(cache_file, usr, pwd)
	else
		ret,msg = WechatOps.loginByInput(strCurUsr, strCurPwd)
	end
	
	if ret then
		WechatOps.strCurUsr = usr
	end
	
	return ret, msg
end

--缓存方式登录
function WechatOps.loginByCache(cache_file, usr, pwd)
	local uid = ""
	uid = AutomatorApi:executeShellCommand("busybox ls -l /data/data|grep com.tencent.mm|busybox awk '{print $3}'")
	AutomatorApi:executeShellCommand("rm -fr /data/data/com.tencent.mm/*")
	AutomatorApi:executeShellCommand("cp -a "..cache_file.."/* /data/data/com.tencent.mm")
	AutomatorApi:executeShellCommand("busybox chown -R "..uid..":"..uid.." /data/data/com.tencent.mm")
	AutomatorApi:executeShellCommand("am start -n com.tencent.mm/.ui.LauncherUI")

	local timeout = 20000
	while timeout > 0 do
		if AutomatorApi:waitNewWindowByTextEqual('登录', 1) and
		   AutomatorApi:waitNewWindowByTextEqual('注册', 1) then
		   return WechatOps.loginByInput(usr, pwd)
		end
		
		if AutomatorApi:waitNewWindowByTextEqual('发现', 1) and 
		   AutomatorApi:waitNewWindowByTextEqual('通讯录', 1) and 
		   AutomatorApi:waitNewWindowByTextEqual('微信', 1) then
			return true, 'Login succeeded.'
		end
		timeout = timeout - 500
		AutomatorApi:mSleep(500)
	end
	
	return false, 'Login failed. unknown error.'
end

--输入方式登录
function WechatOps.loginByInput(usr, pwd)
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.ui.account.LoginUI")
	ret = AutomatorApi:waitNewWindowByTextContain("登录微信", 6000)
	if(ret == false) then return false,"Login failed. Can't find login UI." end
	AutomatorApi:setTextByClass("android.widget.EditText", usr, 0)
	AutomatorApi:setTextByClass("android.widget.EditText", pwd, 1)
	AutomatorApi:clickByTextEqual("登录", 0)
	
	local timeout = 20000
	while timeout > 0 do
		if AutomatorApi:waitNewWindowByTextContain("帐号或密码错误", 1) then return false, "Login failed. User or password wrong." end
		if AutomatorApi:waitNewWindowByTextContain("通过短信验证身份", 1) then return false, "Login failed. Need sms verify." end
		
		--好友验证
		if AutomatorApi:waitNewWindowByTextContain("你登录的微信需要进行好友验证", 1) then
			AutomatorApi:clickByTextEqual("确定", 0)
			x,y = findMultiColorInRegionFuzzyInTime( 0x04be02, "-303|-7|0x04be02,317|2|0x04be02,34|-237|0x7cb550,22|66|0xefeff4", 90, 0, 0, 719, 1279, 6000)
			if x ==-1 or y == -1 then
				return false,"Login failed. Friend verify failed."
			end
			AutomatorApi:click(x, y)
			return WechatOps.login()
		end
		
		--提示'看看手机通讯录里谁在使用微信'
		if AutomatorApi:waitNewWindowByTextContain('看看手机通讯录里谁在使用微信', 1) then
			AutomatorApi:clickByTextEqual("否", 0)
		end
		
		if AutomatorApi:waitNewWindowByTextEqual('发现', 1) and 
		   AutomatorApi:waitNewWindowByTextEqual('通讯录', 1) and 
		   AutomatorApi:waitNewWindowByTextEqual('微信', 1) then
			return true, 'Login succeeded.'
		end
		timeout = timeout - 4000
	end
	return false, 'Login failed. unknown error.'
end

--注册
function WechatOps.register(nick, country_code, phone_num, password)
	local ret, msg
	ret = true
	msg = 'Register succeeded.'
	
	AutomatorApi:executeShellCommand("am force-stop com.tencent.mm")
	AutomatorApi:mSleep(2000)
	AutomatorApi:executeShellCommand("rm -fr /data/data/com.tencent.mm/*")
	
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.ui.account.RegByMobileRegAIOUI")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('填写手机号', 6000)
		if ret == false then
			msg = "Register failed. Can't open register window."
			break
		end
		AutomatorApi:clickByClass("android.widget.EditText", 0)
		AutomatorApi:inputText(nick)
		AutomatorApi:setTextByClass("android.widget.EditText", country_code, 1)
		AutomatorApi:setTextByClass("android.widget.EditText", phone_num, 2)
		AutomatorApi:setTextByClass("android.widget.EditText", password, 3)
		AutomatorApi:clickByTextEqual("注册", 0)
		
		--确认手机号码提示框
		ret = AutomatorApi:waitNewWindowByTextEqual('确认手机号码', 15000)
		if ret == false then
			msg = "Register failed. Can't show prompt dialog."
			break
		end
		AutomatorApi:clickByTextEqual("确定", 0)
		
		if AutomatorApi:waitNewWindowByTextContain('操作太频繁', 10000) then
			ret = false
			msg = "Register failed. Too frequent operation."
			break
		end
		
		--填写验证码界面
		ret = AutomatorApi:waitNewWindowByTextEqual('填写验证码', 30000)
		if ret == false then
			msg = "Register failed. Can't go to sns verify input window."
			break
		end
		
		
		--获取验证码并输入验证码
		local sms
		ret, sms = getSms(AutomatorApi:readFile(getPath().."/uuid.txt"), g_task_id, phone_num, 60000)
		if ret == false then
			ret = false
			msg = "Register failed. "..sms
			break
		end
		
		AutomatorApi:mSleep(2000)
		AutomatorApi:setTextByClass("android.widget.EditText", sms, 0)
		AutomatorApi:clickByTextEqual("下一步", 0)
		
		local time_out = 30000
		while time_out > 0 do
			if AutomatorApi:waitNewWindowByTextContain('验证码不正确', 1) then
				return false, "Register failed. Verify code wrong."
			end
		
			--进入查找朋友界面
			if AutomatorApi:waitNewWindowByTextEqual('查找你的微信朋友', 1) then
				AutomatorApi:clickByTextEqual("好", 0)
				AutomatorApi:waitNewWindowByTextEqual('发现', 10000)
				break
			end
			
			if AutomatorApi:waitNewWindowByTextContain('该手机号码已经注册', 1) then
				return false, "Register failed. This phone has been registerd."
			end
			
			if AutomatorApi:waitNewWindowByTextContain('该手机号已经绑定如上微信', 1) then
				return false, "Register failed. This phone has been registerd."
			end
			
			time_out = time_out - 2000
		end
		
		if time_out <= 0 then
			ret =false
			msg = "Register failed. Unknown error."
			break
		end
		
		WechatOps.strCurUsr = phone_num
	until(true)
	return ret, msg
end

--添加微信号（用户或者公众号）
function WechatOps.addAlias(alias)
	local ret
	AutomatorApi:executeShellCommand("am start --activity-no-history -n com.tencent.mm/.ui.chatting.ChattingUI --es Chat_User '"..alias.."'")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('添加', 2000)
		if ret == false then break end
		
		AutomatorApi:clickByTextEqual("添加", 0)
		if AutomatorApi:waitNewWindowByTextEqual('验证申请', 4000) then
			AutomatorApi:inputText("Hi~ 您好~")
			AutomatorApi:clickByTextEqual("确定", 0)
			break
		end
	until(true)
	return true, "Add "..alias.." succeeded."
end

--发送单聊
function WechatOps.sendMsg(who, message)
	local ret,msg
	ret = true
	msg = 'Send message succeeded.'
	AutomatorApi:executeShellCommand("am start --activity-no-history -n com.tencent.mm/.ui.chatting.ChattingUI --es Chat_User '"..who.."'")
	
	repeat
		ret = AutomatorApi:waitNewWindowByDescEqual('表情', 2000)
		if ret == false then 
			ret = false
			msg = "Send message failed. Can't find chat dialog." 
			break
		end
		
		AutomatorApi:clickByClass("android.widget.EditText", 0)
		AutomatorApi:setTextByClass("android.widget.EditText", "", 0)
		AutomatorApi:inputText(message)
		AutomatorApi:clickByTextEqual("发送", 0)
	until(true)
	
	return ret,msg
end

--发带图片朋友圈
function WechatOps.sendSnsWithPicture(content, picPath)
	local ret,msg
	ret = true
	msg = "Send sns with picture succeeded."
	
	AutomatorApi:executeShellCommand("am start --activity-no-history -n com.tencent.mm/.plugin.sns.ui.SnsUploadUI"
		.." --es Kdescription '"..content.."'"
		.." --es sns_kemdia_path '"..picPath.."'"
		.." --ei Ksnsupload_type 0")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual("谁可以看", 3000)
		if ret == false then
			msg = "Send sns with picture failed. Can't open the plugin.sns.ui.SnsUploadUI."
			break
		end
		
		--AutomatorApi:clickByTextEqual("发送", 0)
	until(true)
	return ret,msg
end

--仅发文字朋友圈
function WechatOps.sendSnsJustText(content)
	local ret,msg
	ret = true
	msg = "Send sns with picture succeeded."
	
	AutomatorApi:executeShellCommand("am start --activity-no-history -n com.tencent.mm/.plugin.sns.ui.SnsUploadUI"
		.." --es Kdescription '"..content.."'"
		.." --ei Ksnsupload_type 4")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual("谁可以看", 3000)
		if ret == false then
			msg = "Send sns with picture failed. Can't open the plugin.sns.ui.SnsUploadUI."
			break
		end
		
		AutomatorApi:clickByTextEqual("发送", 0)
	until(true)
	return ret,msg
end

--添加附近的人
function WechatOps.addNearFriend()
	local ret,msg
	ret = true
	msg = "Add near friend succeeded."
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.plugin.nearby.ui.NearbyFriendsUI")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual("附近的人", 15000)
		if ret == false then
			msg = "Add near friend failed. Can't open the plugin.nearby.ui.NearbyFriendsUI."
			break
		end

		AutomatorApi:swipe(719,1096,719,150,math.random(5,100))
		AutomatorApi:mSleep(2000)
		AutomatorApi:click(math.random(9,700), math.random(253,1274))
		ret = AutomatorApi:waitNewWindowByTextEqual("打招呼", 2000)
		if ret == false then
			msg = "Add near friend failed. Can't open contact info UI."
			break
		end
		AutomatorApi:clickByTextEqual("打招呼", 0)
		AutomatorApi:waitNewWindowByTextEqual("向TA说句话打个招呼", 2000)
		AutomatorApi:inputText("Hi~ 您好~")
		AutomatorApi:clickByTextEqual("发送", 0)
	until(true)
	
	return ret,msg
end

--设置微信号
function WechatOps.setAlias(alias)
	local ret,msg
	ret = true
	msg = "Set alias succeeded."
	
	if alias == nil then
		alias = WechatOps.strCurUsr
		local chara = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
		for var=1,4 do
			local i = math.random(1, string.len(chara))
			alias = string.sub(chara, i, i)..alias
		end
	end
	
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.plugin.setting.ui.setting.SettingsAliasUI")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('设置微信号', 2000)
		if ret == false then
			msg = "Set alias failed. Can't go to plugin.setting.ui.setting.SettingsAliasUI"
			break
		end
		
		AutomatorApi:setTextByClass("android.widget.EditText", alias, 0)
		AutomatorApi:clickByTextEqual("保存", 0)
		AutomatorApi:mSleep(800)
		AutomatorApi:clickByTextEqual("确定", 0)
		if AutomatorApi:waitNewWindowByTextContain("你操作频率过快", 2000) then
			ret = false
			msg = "Set alias failed. Operating frequency too fast prompt."
			break
		end
		WechatOps.strCurUsr = alias
	until(true)
	
	return ret, msg
end

--设置性别
function WechatOps.setSex(sex)
	local ret,msg
	ret = true
	msg = "Set sex succeeded."
	
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.plugin.setting.ui.setting.SettingsPersonalInfoUI")
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('个人信息', 2000)
		ret = ret and AutomatorApi:waitNewWindowByTextEqual('性别', 2000)
		if ret == false then
			msg = "Set sex failed. Can't go to plugin.setting.ui.setting.SettingsPersonalInfoUI"
			break
		end
		
		AutomatorApi:clickByTextEqual("性别", 0)
		AutomatorApi:mSleep(500)
		if sex == 'm' then
			AutomatorApi:clickByTextEqual("男", 0)
		else
			AutomatorApi:clickByTextEqual("女", 0)
		end
	until(true)
end

--设置昵称
function WechatOps.setNickname(nickname)
	local ret,msg
	ret = true
	msg = "Set nickname succeeded."
	
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.plugin.setting.ui.setting.SettingsModifyNameUI")
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('更改名字', 2000)
		if ret == false then
			msg = "Set nickname failed. Can't go to plugin.setting.ui.setting.SettingsModifyNameUI"
			break
		end
		
		AutomatorApi:setTextByClass("android.widget.EditText", "", 0)
		AutomatorApi:inputText(nickname)
		AutomatorApi:mSleep(500)
		AutomatorApi:clickByTextEqual("保存", 0)
	until(true)
end

--通过微信自带浏览器打开URL
function WechatOps.openUrlByWx(url)
	AutomatorApi:executeShellCommand("am start -n com.tencent.mm/.plugin.webview.ui.tools.WebViewUI -d '"..url.."'")
	return true, "Open url succeeded."
end

--设置地区
function WechatOps.setArea()
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.plugin.setting.ui.setting.SettingsPersonalInfoUI")

	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('个人信息', 2000)
		ret = ret and AutomatorApi:waitNewWindowByTextEqual('地区', 2000)
		if ret == false then
			msg = "Set sex failed. Can't go to plugin.setting.ui.setting.SettingsPersonalInfoUI"
			break
		end
		
		AutomatorApi:clickByTextEqual("地区", 0)
		AutomatorApi:mSleep(4000)
		AutomatorApi:click(229,285)
	until(true)
	return true, "Set area succeeded"
end

--投票
function WechatOps.vote()
	local ret, msg
	func = dofile(getPath().."/lua/wx_vote.lua")
	ret,msg = func()
	if ret then
		AutomatorApi:takeScreenshot(g_conf_wx_vote_s_snapshot_path)
	end
	return ret, msg
end

--所有操作结束后执行保存缓存的操作
function WechatOps.doEnd()
	if WechatOps.strCurUsr ~= "" then
		AutomatorApi:executeShellCommand("am force-stop com.tencent.mm")
		AutomatorApi:mSleep(3000)
		local cache_file = getPath().."/res/cache/com.tencent.mm/"..WechatOps.strCurUsr
		
		AutomatorApi:executeShellCommand("rm -fr "..cache_file)
		AutomatorApi:executeShellCommand("mkdir -p "..cache_file)
		AutomatorApi:executeShellCommand("cp -a /data/data/com.tencent.mm/* "..cache_file)
	end
	return true, "End succeeded."
end

--根据指令执行任务
function WechatOps.doTask(data)
	local cmd = data[1]
	local ret, msg
	
	if cmd == CMD_WX_LOGIN then
		ret, msg = WechatOps.login(data[2], data[3])
	elseif cmd == CMD_WX_REGISTER then
		ret, msg = WechatOps.register(data[2], data[3], data[4], data[5])
	else
		repeat
			if WechatOps.strCurUsr == "" then
				ret, msg = false, "not login!"
				break
			end

			if cmd == CMD_WX_ADD_FRIEND then 
				ret, msg = WechatOps.addAlias(data[2])
			elseif cmd == CMD_WX_SEND_MSG then
				ret, msg = WechatOps.sendMsg(data[2], data[3])
			elseif cmd == CMD_WX_ADD_MP then
				ret, msg = WechatOps.addAlias(data[2])
			elseif cmd == CMD_WX_VOTE then
				ret, msg = WechatOps.vote()
			elseif cmd == CMD_WX_SEND_SNS then
				ret, msg = WechatOps.sendSNS(data[2])
			elseif cmd == CMD_WX_UPDATE_NICKNAME then
				ret, msg = WechatOps.setNickname(data[2])
			elseif cmd == CMD_WX_ADD_NEAR_FRIEND then
				ret, msg = WechatOps.addNearFriend()
			elseif cmd == CMD_WX_UPDATE_SEX then
				ret, msg = WechatOps.setSex(data[2])
			elseif cmd == CMD_WX_RANDOM_SEND_SNS then
				--ret, msg = randomSendSNS()
			elseif cmd == CMD_WX_CLEAR_MSG_RECORD then
				ret, msg = WechatOps.clearRecorder(data[2])
			elseif cmd == CMD_WX_SET_ALIAS then
				for var = 1,5 do
					ret, msg = WechatOps.setAlias(data[2])
					if ret then break end
				end
			elseif cmd == CMD_WX_END then
				ret, msg = WechatOps.doEnd()
			elseif cmd == CMD_WX_OPEN_URL_BY_WX then
				ret, msg = WechatOps.openUrlByWx(data[2])
			elseif cmd == CMD_WX_SET_AREA then
				ret, msg = WechatOps.setArea()
			end
		until (true)
	end
	
	if ret == false and WechatOps.strCurUsr ~= "" then
		WechatOps.doEnd()
	end
	AutomatorApi:log("wechat", msg)

	return ret, msg
end

-- ret, msg = WechatOps.loginByInput("421421421", "2132132131")
-- ret, msg = WechatOps.addAlias('hdw123')
-- ret, msg = WechatOps.sendMsg('hudongwen2012', '发送FSD')
-- ret, msg = WechatOps.sendSnsWithPicture('"t\nest"', '/mnt/sdcard/GodHand/tmp/snapshot.png')
-- ret, msg = WechatOps.sendSnsJustText('"t\nest"')
-- ret, msg = WechatOps.addNearFriend()
-- ret, msg = WechatOps.setAlias()
-- ret, msg = WechatOps.setSex('m')
-- ret, msg = WechatOps.setNickname('胡东文')
-- WechatOps.strCurUsr = 'hudongwen2012'
-- ret, msg = WechatOps.doEnd()
-- ret, msg = WechatOps.login("hudongwen2012", "hdw12345687")
-- msg = AutomatorApi:httpGet(g_conf_task_file_url, 'guid=fdsfdsfds')
-- AutomatorApi:log('wechat', msg)

