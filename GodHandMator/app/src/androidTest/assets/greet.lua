require 'import'
import 'com.rzx.godhandmator.AutomatorApi'

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

    AutomatorApi:inputText("范德萨范德萨")
end


