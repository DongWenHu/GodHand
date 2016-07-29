require 'import'
import 'com.rzx.godhandmator.AutomatorApi'
import 'android.util.Log'

function test()
    local res
--    AutomatorApi:executeShellCommand ("am start --activity-no-history com.tencent.mm/.ui.account.LoginUI");
--    res = AutomatorApi:waitNewWindowByText("返回", 6000)
--    if(res == false) then
--        return
--    end

--    AutomatorApi:setTextByText("QQ", "fsdfsf", 0)
--    AutomatorApi:setTextByClass("android.widget.EditText", "11111", 0)
--    AutomatorApi:setTextByClass("android.widget.EditText", "22222", 1)

--    AutomatorApi:inputText("#Enter")
--    AutomatorApi:takeScreenshot("/mnt/sdcard/GodHand/tmp/snapshot.png");

--    ret = AutomatorApi:findMultiColorInRegionFuzzy( 0x8bbf4f, "-67|88|0x202c68,28|201|0x1a305f,199|8|0x0000fd,189|-190|0x44a6c5,-160|-186|0x212221,-344|-173|0x1f0f4d,-368|216|0x00ff00,-352|346|0x10354f", 99, 0, 0, 719, 1279)

--    AutomatorApi:inputText(ret)
    AutomatorApi:log("aaa", "fdsfdsfds");
end


