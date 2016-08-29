require 'GHLib'
require 'ProtocolDefine'
require 'Configs'


WechatOps = {strCurUsr = ""}
local ALREAD_REGISTERED = false

--登录
function WechatOps.login(usr, pwd)
	local ret, msg
	ret = true
	msg = usr..' Login succeeded.'
	
	AutomatorApi:executeShellCommand("am force-stop com.tencent.mm")
	AutomatorApi:mSleep(1000)
	local cache_file = getPath().."/res/cache/com.tencent.mm/"..usr
	local tar_file = getPath().."/tmp/gh_task/"..SharedDic.get("task_id").."/"..usr..".tar"
	
	AutomatorApi:executeShellCommand(tar_file)
	if AutomatorApi:fileExists(tar_file) then
		AutomatorApi:executeShellCommand("rm -fr "..cache_file)
		AutomatorApi:executeShellCommand("mkdir -p "..getPath().."/res/cache/com.tencent.mm/")
		AutomatorApi:executeShellCommand("busybox tar -xf "..tar_file.." -C "..getPath().."/res/cache/com.tencent.mm/")
	end
	
	if AutomatorApi:fileExists(cache_file) then
		ret,msg = WechatOps.loginByCache(cache_file, usr, pwd)
		--如果调度频繁出错，然后频繁通过用户密码登录会导致账号挂掉，所以最好不要通过输入方式登录
		if ret == false then
			ret,msg = WechatOps.loginByInput(usr, pwd)
		end
	else
		ret,msg = WechatOps.loginByInput(usr, pwd)
		-- ret = false
		-- msg = "Not found cache file."
	end
	
	if ret then
		WechatOps.strCurUsr = usr
	end
	
	return ret, msg
end

--缓存方式登录
function WechatOps.loginByCache(cache_file, usr, pwd)
	AutomatorApi:toast("loginByCache\n"..usr)
	-- local uid = ""
	-- uid = AutomatorApi:executeShellCommand("busybox ls -l /data/data|grep com.tencent.mm|busybox awk '{print $3}'")
	AutomatorApi:executeShellCommand("rm -fr /data/data/com.tencent.mm/*")
	AutomatorApi:executeShellCommand("cp -a "..cache_file.."/* /data/data/com.tencent.mm")
	-- AutomatorApi:executeShellCommand("busybox chown -R "..uid..":"..uid.." /data/data/com.tencent.mm")
	AutomatorApi:executeShellCommand("am start -n com.tencent.mm/.ui.LauncherUI")

	local timeout = 20000
	while timeout > 0 do
		if AutomatorApi:waitNewWindowByTextEqual('登录', 1) and
		   AutomatorApi:waitNewWindowByTextEqual('注册', 1) or 
		   AutomatorApi:waitNewWindowByTextEqual('请输入密码', 1) then
		   break
		end
		
		if AutomatorApi:waitNewWindowByTextEqual('发现', 1) and 
		   AutomatorApi:waitNewWindowByTextEqual('通讯录', 1) and 
		   AutomatorApi:waitNewWindowByTextEqual('微信', 1) then
			if AutomatorApi:waitNewWindowByTextEqual('提示', 5000) then
				break
			end
			local net_avaliable = check_network_avaliable()
			if net_avaliable == false then
				return false, usr..' Login failed. Network is not avaliable.'
			end
			return true, usr..' Login succeeded.'
		end
		timeout = timeout - 2000
		AutomatorApi:mSleep(500)
	end
	
	return false, usr..' Login failed. unknown error.'
end

--输入方式登录
function WechatOps.loginByInput(usr, pwd)
	AutomatorApi:toast("loginByInput\n"..usr)
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.ui.account.LoginUI")
	ret = AutomatorApi:waitNewWindowByTextContain("登录微信", 10000)
	if(ret == false) then return false,usr.." Login failed. Can't find login UI." end
	AutomatorApi:setTextByClass("android.widget.EditText", usr, 0)
	AutomatorApi:setTextByClass("android.widget.EditText", pwd, 1)
	AutomatorApi:clickByTextEqual("登录", 0)
	
	local timeout = 20000
	while timeout > 0 do
		if AutomatorApi:waitNewWindowByTextContain("帐号或密码错误", 1) then return false, usr.." Login failed. User or password wrong." end
		if AutomatorApi:waitNewWindowByTextContain("通过短信验证身份", 1) then return false, usr.." Login failed. Need sms verify." end
		if AutomatorApi:waitNewWindowByTextContain("申请解封", 1) then return false, "Account locked. Need unlock." end
		
		--好友验证
		if AutomatorApi:waitNewWindowByTextContain("你登录的微信需要进行好友验证", 1) then
			AutomatorApi:clickByTextEqual("确定", 0)
			x,y = findMultiColorInRegionFuzzyInTime( 0x04be02, "-303|-7|0x04be02,317|2|0x04be02,34|-237|0x7cb550,22|66|0xefeff4", 90, 0, 0, 719, 1279, 6000)
			if x ==-1 or y == -1 then
				return loginByInput(usr, pwd)
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
			return true, usr..' Login succeeded.'
		end
		timeout = timeout - 2000
	end
	return false, usr..' Login failed. Unknown error.'
end

-- --解封账号
-- function WechatOps.unlockAccount(country_code, phone_num)
	-- local ret = true
	-- local msg = "Unlock account succeeded."
	-- local x,y
	
	-- repeat
		-- ----------------------------------1--------------------------------------
		-- ret = AutomatorApi:waitNewWindowByTextEqual('帐号处罚说明', 5000)
		-- if ret == false then
			-- msg = "Unlock account failed. Can't open first UI."
			-- break
		-- end
		
		-- x,y = findMultiColorInRegionFuzzyInTime( 0x04be02, "-307|-20|0x04be02,315|-18|0x04be02,291|40|0x04be02,-234|38|0x04be02", 90, 0, 0, 719, 1279, 25000)
		-- if x == -1 or y == -1 then
			-- msg = "Unlock account failed. Can't open home UI."
			-- break
		-- end
		-- AutomatorApi:click(x,y)
		
		-- ----------------------------------2--------------------------------------
		-- ret = AutomatorApi:waitNewWindowByTextEqual('自助解封', 5000)
		-- if ret == false then
			-- msg = "Unlock account failed. Can't open second UI."
			-- break
		-- end
		
		-- x,y = findMultiColorInRegionFuzzyInTime( 0x04be02, "-291|-13|0x04be02,296|-5|0x04be02,288|48|0x04be02,-291|45|0x04be02", 90, 0, 0, 719, 1279, 25000)
		-- if x == -1 or y == -1 then
			-- msg = "Unlock account failed. Can't open second UI."
			-- break
		-- end
		
		-- AutomatorApi:click(243,275) --点击选择区号
		-- AutomatorApi:mSleep(2000)
		-- AutomatorApi:swipe(719,1096,719,150,1)
		-- AutomatorApi:mSleep(500)
		-- AutomatorApi:swipe(719,1096,719,150,1)
		-- AutomatorApi:mSleep(500)
		-- AutomatorApi:swipe(719,1096,719,150,1)
		-- AutomatorApi:mSleep(500)
		-- AutomatorApi:swipe(719,1096,719,150,1)
		-- AutomatorApi:mSleep(500)
		-- AutomatorApi:click(349, 1240) --香港区号
		-- AutomatorApi:mSleep(1000)
		-- AutomatorApi:click(344, 352)
		-- AutomatorApi:inputText(phone_num)
		-- AutomatorApi:mSleep(200)
		-- AutomatorApi:click(x,y)
		
		

	-- until (true)
-- end


function WechatOps._alreadyRegisteredConfirmClicked(phone_num, password)
	local ret = false
	local msg = "Register failed. Unknown error."
	
	local time_out2 = 25000
	while time_out2 > 0 do
		if AutomatorApi:waitNewWindowByTextContain("申请解封", 1) then
			return false, "Register failed. Account locked."
		end
		if AutomatorApi:waitNewWindowByTextContain("长期未登录", 1) then
			return false, "Register failed. Account locked. Not login for a long time."
		end
		
		if AutomatorApi:waitNewWindowByTextContain("已经删除", 1) then
			AutomatorApi:clickByTextEqual("确定", 0)
			if AutomatorApi:waitNewWindowByTextContain('该手机号已经绑定如上微信', 2000) then
				AutomatorApi:clickByTextEqual("不是我的，继续注册", 0)
			end
		end
	
		if AutomatorApi:waitNewWindowByTextContain("你登录的微信需要进行好友验证", 1) then
			AutomatorApi:clickByTextEqual("确定", 0)
			x,y = findMultiColorInRegionFuzzyInTime( 0x04be02, "561|3|0x04be02,270|-228|0x7bb44f,147|-273|0xefeff4,251|-146|0x000000", 90, 0, 0, 719, 1279, 25000)
			if x ~= -1 and y ~= -1 then
				AutomatorApi:click(48, 99)
			end
			
			if AutomatorApi:waitNewWindowByTextContain('该手机号已经绑定如上微信', 2000) then
				AutomatorApi:clickByTextEqual("是我的，立刻登录", 0)
			end
		end

		
		--提示'看看手机通讯录里谁在使用微信'
		if AutomatorApi:waitNewWindowByTextContain('看看手机通讯录里谁在使用微信', 1) then
			AutomatorApi:clickByTextEqual("否", 0)
			
			local time_out3 = 25000
			while time_out3 > 0 do
				if AutomatorApi:waitNewWindowByTextEqual('设置密码', 1) then
					AutomatorApi:mSleep(1000)
					AutomatorApi:setTextByClass("android.widget.EditText", password, 0)
					AutomatorApi:setTextByClass("android.widget.EditText", password, 1)
					AutomatorApi:clickByTextEqual("完成", 0)
					
					if AutomatorApi:waitNewWindowByTextContain('看看手机通讯录里谁在使用微信', 20000) then
						AutomatorApi:clickByTextEqual("否", 0)
					end
				end
				
				if AutomatorApi:waitNewWindowByTextEqual('发现', 1) then
					ALREAD_REGISTERED = true
					WechatOps.strCurUsr = phone_num
					return true, "Register reset. This phone has been registerd."
				end
				
				time_out3 = time_out3 - 1500
			end
			
		end
		time_out2 = time_out2 - 3000
	end
	
	return ret, msg
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
		ret = AutomatorApi:waitNewWindowByTextEqual('填写手机号', 25000)
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
		ret = AutomatorApi:waitNewWindowByTextEqual('确认手机号码', 35000)
		if ret == false then
			msg = "Register failed. Connect failed."
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
		ret, sms = getSms(AutomatorApi:readFile(getPath().."/uuid.txt"), SharedDic.get("task_id"), phone_num, 120000)
		if ret == false then
			ret = false
			msg = "Register failed. "..sms
			break
		end
		
		AutomatorApi:click(286, 407)
		AutomatorApi:inputText(sms)
		AutomatorApi:mSleep(2000)
		AutomatorApi:clickByTextEqual("下一步", 0)
		
		local time_out = 30000
		while time_out > 0 do
			if AutomatorApi:waitNewWindowByTextContain('验证码不正确', 1) then
				return false, "Register failed. Verify code wrong."
			end
		
			--进入查找朋友界面
			if AutomatorApi:waitNewWindowByTextEqual('查找你的微信朋友', 1) then
				AutomatorApi:clickByTextEqual("好", 0)
				AutomatorApi:waitNewWindowByTextEqual('发现', 20000)
				break
			end
			
			if AutomatorApi:waitNewWindowByTextContain('该手机号码已经注册', 1) then
				AutomatorApi:clickByTextEqual("确定", 0)
				return WechatOps._alreadyRegisteredConfirmClicked(phone_num, password)
				-- --提示'看看手机通讯录里谁在使用微信'
				-- if AutomatorApi:waitNewWindowByTextContain('看看手机通讯录里谁在使用微信', 25000) then
					-- AutomatorApi:clickByTextEqual("否", 0)
					-- AutomatorApi:waitNewWindowByTextEqual('发现', 20000)
					-- ALREAD_REGISTERED = true
					-- WechatOps.strCurUsr = phone_num
				-- end
				-- return true, "Register failed. This phone has been registerd."
			end
			
			if AutomatorApi:waitNewWindowByTextContain('该手机号已经绑定如上微信', 1) then
				AutomatorApi:clickByTextEqual("是我的，立刻登录", 0)
				return WechatOps._alreadyRegisteredConfirmClicked(phone_num, password)
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
		ret = AutomatorApi:waitNewWindowByTextEqual('添加', 6000)
		if ret == false then break end
		
		AutomatorApi:clickByTextEqual("添加", 0)
		if AutomatorApi:waitNewWindowByTextEqual('验证申请', 15000) then
			AutomatorApi:inputText("Hi~ 您好~")
			AutomatorApi:clickByTextEqual("确定", 0)
			AutomatorApi:mSleep(5000)
			break
		end
	until(true)
	return true, "Add "..alias.." succeeded."
end

--根据手机号、QQ号、微信号添加好友
function WechatOps.addFriend(usr, hi_words)
	local ret = true
	local msg = "Add "..usr.." succeeded."
	
	if usr == nil or usr == "" then
		return false, 'Add friend failed. Account is null.'
	end
	
	AutomatorApi:executeShellCommand("am start --activity-no-history -n com.tencent.mm/.plugin.search.ui.FTSAddFriendUI")
	repeat 
		AutomatorApi:mSleep(1000)
		ret = AutomatorApi:waitNewWindowByTextEqual('搜索', 5000)
		if ret == false then
			msg = "Add friend "..usr.." failed. Can't go to .plugin.search.ui.FTSAddFriendUI"
			break
		end
		
		AutomatorApi:setTextByClass("android.widget.EditText", usr, 0)
		AutomatorApi:inputText("#Enter")
		
		local time_out = 30000
		while time_out > 0 do
			if AutomatorApi:waitNewWindowByTextEqual('该用户不存在', 1) then
				return false, "Add friend "..usr.." failed. This user doesn't exist."
			end
			
			if waitNewWindow('com.tencent.mm/.plugin.search.ui.FTSAddFriendUI', 1) then
				break
			end
			time_out = time_out - 500
		end
	
		ret = AutomatorApi:waitNewWindowByTextEqual('添加到通讯录', 5000)
		if ret == false then
			msg = "Add friend "..usr.." failed. Can't find add button."
			break
		end
		AutomatorApi:clickByTextEqual('添加到通讯录', 0)
		ret = waitNewWindow('com.tencent.mm/.plugin.profile.ui.ContactInfoUI', 25000)
		if ret == false then
			msg = "Add friend "..usr.." failed. Network is not avaliable."
			break
		end
		
		ret = AutomatorApi:waitNewWindowByTextContain('你需要发送验证申请', 5000)
		if ret == false then
			msg = "Add friend "..usr.." failed. Can't go to say hi UI."
			break
		end
		
		if hi_words ~= nil and hi_words ~= "" then
			AutomatorApi:click(640, 294)
			AutomatorApi:inputText(hi_words)
			AutomatorApi:mSleep(500)
		end		
		AutomatorApi:clickByTextEqual('发送', 0)
		
		ret = waitNewWindow('com.tencent.mm/.plugin.profile.ui.SayHiWithSnsPermissionUI', 25000)
		if ret == false then
			msg = "Add friend "..usr.." failed. Network is not avaliable."
			break
		end
	until(true)
	
	return ret,msg
end

function WechatOps.getRandText(filename)
	local s = io.open(filename)
	
	local table_lines = {}
	local line
	for line in s:lines() do 
		table.insert(table_lines, line)
	end
	
	return table_lines[math.random(#table_lines)]
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
		
		if g_test_flag == false then
			message = WechatOps.getRandText(getPath().."/res/sns_msg.txt")
			local date_ = AutomatorApi:executeShellCommand("date")
			AutomatorApi:inputText(message.."\n"..date_)
		else
			AutomatorApi:inputText(message)
		end
		AutomatorApi:clickByTextEqual("发送", 0)
	until(true)
	
	return ret,msg
end

--发带图片朋友圈
function WechatOps.sendSnsWithPicture(content, picPath)
	local ret,msg
	ret = true
	msg = "Send sns with picture succeeded."
	local file_exists = AutomatorApi:fileExists(picPath)
	if file_exists == false then
		return WechatOps.sendSnsJustText(content)
	end
	
	AutomatorApi:executeShellCommand("am start -n com.tencent.mm/.plugin.sns.ui.SnsTimeLineUI")
	AutomatorApi:executeShellCommand("am start --activity-no-history -n com.tencent.mm/.plugin.sns.ui.SnsUploadUI"
		.." --es sns_kemdia_path '"..picPath.."'"
		.." --ei Ksnsupload_type 0")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual("谁可以看", 3000)
		if ret == false then
			msg = "Send sns with picture failed. Can't open the plugin.sns.ui.SnsUploadUI."
			break
		end
		
		AutomatorApi:setTextByClass("android.widget.EditText", "", 0)
		AutomatorApi:inputText(content)
		AutomatorApi:mSleep(1000)
		AutomatorApi:clickByTextEqual("发送", 0)
		
		ret = waitNewWindow("com.tencent.mm/.plugin.sns.ui.SnsUploadUI", 25000)
		if ret == false then
			msg = "Send sns with picture failed. Network is not avaliable."
			break
		end
	until(true)
	return ret,msg
end

--仅发文字朋友圈
function WechatOps.sendSnsJustText(content)
	local ret,msg
	ret = true
	msg = "Send sns with picture succeeded."
	
	AutomatorApi:executeShellCommand("am start -n com.tencent.mm/.plugin.sns.ui.SnsTimeLineUI")
	
	-- AutomatorApi:executeShellCommand("am start --activity-no-history -n com.tencent.mm/.plugin.sns.ui.SnsUploadUI"
		-- .." --ei Ksnsupload_type 9")
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual("朋友圈", 3000)
		if ret == false then
			msg = "Send sns with picture failed. Can't go to .plugin.sns.ui.SnsTimeLineUI."
			break
		end
		
		AutomatorApi:longClick(662, 104, 1000)
	
		ret = waitNewWindow('com.tencent.mm/.plugin.sns.ui.SnsTimeLineUI', 5000)
		if ret == false then
			msg = "Send sns with picture failed. Can't go to next UI."
			break
		end
	
		if AutomatorApi:waitNewWindowByTextEqual('我知道了', 1) then
			AutomatorApi:clickByTextEqual('我知道了', 0)
		end
		
		ret = AutomatorApi:waitNewWindowByTextEqual("谁可以看", 3000)
		if ret == false then
			msg = "Send sns with picture failed. Can't open the plugin.sns.ui.SnsUploadUI."
			break
		end
		
		AutomatorApi:setTextByClass("android.widget.EditText", "", 0)
		AutomatorApi:inputText(content)
		AutomatorApi:mSleep(1000)
		AutomatorApi:clickByTextEqual("发送", 0)
		
		waitNewWindow("com.tencent.mm/.plugin.sns.ui.SnsUploadUI", 15000)
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
		
		waitNewWindow("com.tencent.mm/.ui.contact.SayHiEditUI", 10000)
	until(true)
	
	return ret,msg
end

--设置微信号
function WechatOps.setAlias(alias)
	local ret,msg
	ret = true
	msg = "Set alias succeeded."
	
	if ALREAD_REGISTERED then 
		AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.ui.account.LoginFingerprintUI")
		AutomatorApi:mSleep(1000)
		AutomatorApi:click(623, 1220)
		
		local ret = AutomatorApi:waitNewWindowByTextContain("微信号：", 2000)
		if ret then
			local text = AutomatorApi:getTextByTextContain("微信号：", 0)
			WechatOps.strCurUsr = string.match(text, "微信号：(.*)")
			return true, msg
		end
	end
	
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
		ret = AutomatorApi:waitNewWindowByTextEqual('设置微信号', 5000)
		if ret == false then
			msg = "Set alias failed. Can't go to plugin.setting.ui.setting.SettingsAliasUI"
			break
		end
		
		AutomatorApi:setTextByClass("android.widget.EditText", alias, 0)
		AutomatorApi:mSleep(800)
		AutomatorApi:clickByTextEqual("保存", 0)
		AutomatorApi:mSleep(800)
		AutomatorApi:clickByTextEqual("确定", 0)
		if AutomatorApi:waitNewWindowByTextContain("你操作频率过快", 2000) then
			ret = false
			msg = "Set alias failed. Operating frequency too fast prompt."
			break
		end
		if AutomatorApi:waitNewWindowByTextContain("帐号已经存在", 2000) then
			return WechatOps.setAlias()
		end
		
		ret = waitNewWindow("com.tencent.mm/.plugin.setting.ui.setting.SettingsAliasUI", 25000)
		if ret == false then
			msg = "Set alias failed. Send time out."
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
		ret = AutomatorApi:waitNewWindowByTextEqual('个人信息', 5000)
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
	
	return true, msg
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
		AutomatorApi:mSleep(3000)
		AutomatorApi:swipe(719,1096,719,150,math.random(5,100))
		AutomatorApi:mSleep(2000)
		AutomatorApi:click(281,562)
	until(true)
	return true, "Set area succeeded"
end

--设置头像
function WechatOps.setHead(head_png)
	local ret,msg
	ret = true
	msg = "Set head succeeded."
	
	local file_exists = AutomatorApi:fileExists(head_png)
	if file_exists == false then
		return false, "Set head failed. File not found."
	end
	
	AutomatorApi:executeShellCommand("am start --activity-no-history com.tencent.mm/.ui.account.LoginFingerprintUI")
	AutomatorApi:mSleep(1500)
	AutomatorApi:click(625, 1223)
	AutomatorApi:mSleep(1500)
	AutomatorApi:click(220, 263)
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('个人信息', 5000)
		if ret == false then
			msg = "Set head failed. Can't go to plugin.setting.ui.setting.SettingsPersonalInfoUI"
			break
		end
		
		AutomatorApi:clickByTextEqual("头像", 0)
		ret = AutomatorApi:waitNewWindowByTextEqual('拍摄照片', 5000)
		if ret == false then
			msg = "Set head failed. Next UI not found."
			break
		end
		
		AutomatorApi:mSleep(1000)
		local iRet, sRet
		AutomatorApi:clickByClass("android.widget.ImageView", 2)

		if iRet == false then
			ret = false
			msg = "Set head failed. Can't find any image."
			break
		end
		
		ret = AutomatorApi:waitNewWindowByTextEqual('使用', 5000)
		if ret == false then
			msg = "Set head failed. Next UI not found."
			break
		end
		
		AutomatorApi:executeShellCommand('am start -n com.tencent.mm/.ui.tools.CropImageNewUI  --ei CropImageMode 1 --es CropImage_ImgPath "'..head_png..'"')
		AutomatorApi:mSleep(1000)
		AutomatorApi:clickByTextEqual("使用", 0)
		
		ret = AutomatorApi:waitNewWindowByTextEqual('个人信息', 15000)
		if ret == false then
			msg = "Set head failed. Upload failed."
			break
		end
	until(true)
	
	return true, msg

end

function WechatOps.setSnsBackground(sns_png)
	local ret,msg
	ret = true
	msg = "Set sns background succeeded."
	
	local file_exists = AutomatorApi:fileExists(sns_png)
	if file_exists == false then
		return false, "Set sns background failed. File not found."
	end
	
	AutomatorApi:executeShellCommand('am start -n com.tencent.mm/.plugin.sns.ui.SettingSnsBackgroundUI')
	
	repeat
		ret = AutomatorApi:waitNewWindowByTextEqual('更换相册封面', 10000)
		if ret == false then
			msg = "Set sns background failed. Can't go to .plugin.sns.ui.SettingSnsBackgroundUI."
			break
		end
		
		--从手机相册选择
		AutomatorApi:click(333, 240)
		
		ret = AutomatorApi:waitNewWindowByTextEqual('拍摄照片', 10000)
		if ret == false then
			msg = "Set sns background failed. Next UI not found."
			break
		end
		
		AutomatorApi:mSleep(1000)
		local iRet, sRet
		AutomatorApi:clickByClass("android.widget.ImageView", 2)

		if iRet == false then
			ret = false
			msg = "Set sns background failed. Can't find any image."
			break
		end
		
		ret = AutomatorApi:waitNewWindowByTextEqual('使用', 5000)
		if ret == false then
			msg = "Set sns background failed. Next UI not found."
			break
		end
		
		AutomatorApi:executeShellCommand('am start -n com.tencent.mm/.ui.tools.CropImageNewUI  --ei CropImageMode 1 --es CropImage_ImgPath "'..sns_png..'"')
		AutomatorApi:mSleep(1000)
		AutomatorApi:clickByTextEqual("使用", 0)
		
		local time_out = 15000
		local top_activity = ""
		while time_out > 0 do
			top_activity = getTopActivity()
			if top_activity ~= "com.tencent.mm/.ui.tools.CropImageNewUI" then
				break
			end
			time_out = time_out - 500
		end
	until(true)

	return true, msg
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
		-- AutomatorApi:mSleep(3000)
		-- AutomatorApi:executeShellCommand("am force-stop com.tencent.mm")
		AutomatorApi:mSleep(3000)
		local cache_file = getPath().."/res/cache/com.tencent.mm/"..WechatOps.strCurUsr
		
		AutomatorApi:executeShellCommand("rm -fr "..cache_file.."*")
		AutomatorApi:executeShellCommand("mkdir -p "..cache_file)
		AutomatorApi:executeShellCommand("cp -a /data/data/com.tencent.mm/MicroMsg "..cache_file)
		AutomatorApi:executeShellCommand("cp -a /data/data/com.tencent.mm/files "..cache_file)
		AutomatorApi:executeShellCommand("cp -a /data/data/com.tencent.mm/shared_prefs "..cache_file)
		AutomatorApi:executeShellCommand("busybox tar -cf "..cache_file..".tar "..WechatOps.strCurUsr.." -C "..getPath().."/res/cache/com.tencent.mm/")
	
		if g_test_flag == false then
			local iRet, sRet = pcall(function()	
				local repeat_times = 5
				local curl_ret = ""
				local curl_tmp_file = getPath().."/tmp/curl_tmp.txt"
				local cmd = "curl --connect-timeout 5 -o "..curl_tmp_file.." -F 'filename=@"..cache_file..".tar' "
						..g_conf_upload_file_url.."?task_id="..SharedDic.get("task_id")
				repeat	
					AutomatorApi:executeShellCommand(cmd)
					curl_ret = AutomatorApi:readFile(curl_tmp_file)
					repeat_times = repeat_times - 1
				until (curl_ret == '{"success":true}' and repeat_times > 0)
				
				if curl_ret ~= '{"success":true}' then
					AutomatorApi:logAppend("http_err", "WechatOps.doEnd(): "..cmd)
				else
					AutomatorApi:logAppend("http_suc", "WechatOps.doEnd(): "..curl_ret)
				end
			end)
		end
	end
	return true, "End succeeded."
end

--根据指令执行任务
function WechatOps.doTask(data)
	local cmd = data[1]
	local ret, msg
	ret = false
	msg = ""
	
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
				local ret_tmp, msg_tmp
				local tbl_usrs = string.split(data[2], ',')
				
				if tbl_usrs == nil or #tbl_usrs == 0 then
					msg = "User empty."
				else
					for k,v in pairs(tbl_usrs) do
						ret_tmp, msg_tmp = WechatOps.addFriend(v, data[3])
						msg = msg..'>'..msg_tmp
					end
				end
				ret = true
			elseif cmd == CMD_WX_SEND_MSG then
				ret, msg = WechatOps.sendMsg(data[2], data[3])
			elseif cmd == CMD_WX_ADD_MP then
				ret, msg = WechatOps.addAlias(data[2])
			elseif cmd == CMD_WX_VOTE then
				ret, msg = WechatOps.vote()
			elseif cmd == CMD_WX_SEND_SNS then
				ret, msg = WechatOps.sendSnsJustText(data[2])
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
				ret, msg = WechatOps.setAlias(data[2])
			elseif cmd == CMD_WX_END then
				ret, msg = WechatOps.doEnd()
			elseif cmd == CMD_WX_OPEN_URL_BY_WX then
				ret, msg = WechatOps.openUrlByWx(data[2])
			elseif cmd == CMD_WX_SET_AREA then
				ret, msg = WechatOps.setArea()
			elseif cmd == CMD_WX_SEND_SNS_WITH_PICTURE then
				if data[3] == "" then
					ret, msg = WechatOps.sendSnsJustText(data[2])
				else
					ret, msg = WechatOps.sendSnsWithPicture(data[2], getPath().."/tmp/gh_task/"..SharedDic.get("task_id").."/"..data[3])
				end
			elseif cmd == CMD_WX_SET_HEAD then
				if data[2] ~= "" then
					local head_png = getPath().."/tmp/gh_task/"..SharedDic.get("task_id").."/"..data[2]
					ret, msg = WechatOps.setHead(head_png)
				end
			elseif cmd == CMD_WX_SET_SNS_BACKGROUND then
				if data[2] ~= "" then
					local sns_png = getPath().."/tmp/gh_task/"..SharedDic.get("task_id").."/"..data[2]
					ret, msg = WechatOps.setSnsBackground(sns_png)
				end
			end
		until (true)
	end
	
	AutomatorApi:log("wechat", msg)

	return ret, msg
end

-- ret, msg = WechatOps.loginByInput("421421421", "2132132131")
-- ret, msg = WechatOps.addAlias('hdw123')
-- ret, msg = WechatOps.sendMsg('hudongwen2012', '发送FSD')
-- ret, msg = WechatOps.sendSnsWithPicture('"t\nest"', '/mnt/sdcard/GodHand/tmp/snapshot.png')
-- ret, msg = WechatOps.sendSnsJustText('"t\ntes发射点t"')
-- ret, msg = WechatOps.addNearFriend()
-- ret, msg = WechatOps.setAlias()
-- ret, msg = WechatOps.setSex('m')
-- ret, msg = WechatOps.setNickname('胡东文')
-- ret, msg = WechatOps.setHead('/mnt/sdcard/GodHand/tmp/snapshot.png')
-- ret, msg = WechatOps.setSnsBackground('/mnt/sdcard/GodHand/tmp/snapshot.png')
-- WechatOps.strCurUsr = 'hudongwen2012'
-- ret, msg = WechatOps.doEnd()
-- ret, msg = WechatOps.login("hudongwen2012", "hdw12345687")
-- msg = AutomatorApi:httpGet(g_conf_task_file_url, 'guid=fdsfdsfds')
-- ret, msg = WechatOps.addFriend("hudongwen2012", "你真的好吗？")
-- ret, msg = WechatOps.doTask({'00000004',"hudongwen2012,123,fsdfds", "你真的好吗？"})
-- AutomatorApi:toast(msg)
-- AutomatorApi:mSleep(3000)

