--协议固定8个字节
--高四位表示哪个应用相关操作, 低四位表示该应用相关命令, 
--如 "00000001"  
--	  0000 表示微信应用
--    0001 表示登录命令
--"99999999" 为异常错误码
--命令消息客户端发往服务器采用json格式,
--			如微信登录 ["00000001", "wxid_iefihiaixr9352", "bruxzmm"]
--命令消息服务端发往客户端采用"命令码|状态(0代表失败,1代表成功)|返回消息字符串", 
--			如微信登录成功 00000001|1|登录成功
--协议编写必须遵循该规则

----------------------------------------------------------
--异常错误码
CMD_ERROR = "99999999"

----------------------------------------------------------
--全局相关命令
CMD_LUA_EXIT = "90000001"
CMD_UPLOAD_FILE = "90000002"
CMD_DOWNLOAD_FILE = "90000003"
CMD_RESET_IMEI = "90000004"
CMD_SLEEP = "90000005"

----------------------------------------------------------
--微信相关命令
CMD_WX_LOGIN = "00000001"	--登录
CMD_WX_LOGOUT = "00000002"	--登出
--CMD_WX_CHANG_USR = "00000003"	--切换用户
CMD_WX_ADD_FRIEND = "00000004"	--添加好友
CMD_WX_CREATE_GROUP = "00000005"	--创建群聊
CMD_WX_SEND_MSG = "00000006"	--发送单聊
CMD_WX_SEND_GROUP_MSG = "00000007"	--发送群聊
CMD_WX_ADD_MP = "00000008"	--添加公众号
CMD_WX_COMMENT_SNS = "00000009"	--评论朋友圈
CMD_WX_UPVOTE_SNS = "00000010"	--点赞朋友圈
CMD_WX_VOTE = "00000011"	--微信投票
CMD_WX_SEND_SNS = "00000012"	--发朋友圈(仅文字)
CMD_WX_UPDATE_NICKNAME = "00000013"	--更改昵称
CMD_WX_ADD_NEAR_FRIEND = "00000014"	--通过附近的人添加好友
CMD_WX_UPDATE_SEX = "00000015"	--更改性别
CMD_WX_RANDOM_SEND_MSG = "00000016"	--随机给好友发消息
CMD_WX_RANDOM_SEND_SNS = "00000017"	--随机发朋友圈
CMD_WX_CLEAR_MSG_RECORD = "00000018" --清除聊天记录
CMD_WX_REGISTER = "00000019"	--注册微信
CMD_WX_SET_ALIAS = "00000020"	--设置微信号
CMD_WX_CLEAR_CACHE = "00000021"	--清除微信缓存
CMD_WX_END = "00000022"	--微信命令结束标志
CMD_WX_SET_AUTO_DOWNLOAD_APK = "00000023"	--设置自动下载微信安装包
CMD_WX_OPEN_URL_BY_WX = "00000024"	--通过微信自带浏览器打开URL
CMD_WX_SET_AREA = "00000025" --设置地区
CMD_WX_SET_HEAD = "00000026" --设置头像
CMD_WX_SET_SNS_BACKGROUND = "00000027" --设置朋友圈背景
CMD_WX_SEND_SNS_WITH_PICTURE = "00000028"	--发朋友圈(带图片)
