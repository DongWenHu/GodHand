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

    ret = AutomatorApi:findMultiColorInRegionFuzzy( 0x000000, "317|63|0xd4d4d4,394|90|0x2cadf1,79|201|0x40b5f2,109|200|0x777777,-47|174|0x191919,8|271|0xffffff", 90, 0, 0, 719, 1279)

    AutomatorApi:inputText(ret)
end


