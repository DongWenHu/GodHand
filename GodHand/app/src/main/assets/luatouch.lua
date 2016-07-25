--
-- Created by IntelliJ IDEA.
-- User: rzx
-- Date: 2016/7/20
-- Time: 15:22
-- To change this template use File | Settings | File Templates.
--

require 'import'
import 'android.support.test.uiautomator.*'

-- Retrieves the product name of the device.
--
-- This method provides information on what type of device the test is running on. This value is
-- the same as returned by invoking #adb shell getprop ro.product.name.
--
-- @return product name of the device
function getProductName()
    return uiDevice:getProductName()
end

--Simulates a short press on the MENU button.
function pressMenu()
    uiDevice:pressMenu()
end

--Simulates a short press on the BACK button.
function pressBack()
    uiDevice:pressBack()
end

--Simulates a short press on the HOME button.
function pressHome()
    uiDevice:pressHome()
end

--Simulates a short press on the DELETE key.
function pressDelete()
    uiDevice:pressDelete()
end

--Simulates a short press on the ENTER key.
function pressEnter()
    uiDevice:pressEnter()
end

--Opens the notification shade.
function openNotification()
    uiDevice:openNotification()
end

--Opens the Quick Settings shade.
function openQuickSettings()
    uiDevice:openQuickSettings()
end

--Simulates a short press using a key code.
--@param keyCode key code
function pressKeyCode(keyCode)
    uiDevice:pressKeyCode(keyCode)
end

--Perform a click at arbitrary coordinates specified by the user
--@param x coordinate
--@param y coordinate
function click(x, y)
    uiDevice:click(x, y)
end


-- Performs a swipe from one coordinate to another using the number of steps
-- to determine smoothness and speed. Each step execution is throttled to 5ms
-- per step. So for a 100 steps, the swipe will take about 1/2 second to complete.
--
-- @param startX
-- @param startY
-- @param endX
-- @param endY
-- @param steps is the number of move steps sent to the system
function swipe(startX, startY, endX, endY, steps)
    uiDevice:swipe(startX, startY, endX, endY, steps)
end

-- the same to swipe
function move(startX, startY, endX, endY, steps)
    swipe(startX, startY, endX, endY, steps)
end

--[[
* Performs a swipe from one coordinate to another coordinate. You can control
* the smoothness and speed of the swipe by specifying the number of steps.
* Each step execution is throttled to 5 milliseconds per step, so for a 100
* steps, the swipe will take around 0.5 seconds to complete.
*
* @param startX X-axis value for the starting coordinate
* @param startY Y-axis value for the starting coordinate
* @param endX X-axis value for the ending coordinate
* @param endY Y-axis value for the ending coordinate
* @param steps is the number of steps for the swipe action
]]
function drag(startX, startY, endX, endY, steps)
    uiDevice:drag(startX, startY, endX, endY, steps)
end

--[[
* Waits for the current application to idle.
* @param timeout in milliseconds
]]
function waitForIdle(timeout)
    uiDevice:waitForIdle(timeout)
end

-- Retrieves the name of the last package to report accessibility events.
-- @return String name of package
function getCurrentPackageName()
    return uiDevice:getCurrentPackageName()
end

--[[
* This method simulates pressing the power button if the screen is OFF else
* it does nothing if the screen is already ON.
*
* If the screen was OFF and it just got turned ON, this method will insert a 500ms delay
* to allow the device time to wake up and accept input.
]]
function screenOn()
    uiDevice:wakeUp()
end

--[[
* Checks the power manager if the screen is ON.
*
* @return true if the screen is ON else false
]]
function isScreenOn()
    return uiDevice:isScreenOn()
end

--[[
* This method simply presses the power button if the screen is ON else
* it does nothing if the screen is already OFF.
]]
function screenOff()
    uiDevice:sleep()
end

--[[
* Take a screenshot of current window and store it as PNG
*
* The screenshot is adjusted per screen rotation
*
* Default scale of 1.0f (original size) and 90% quality is used
* The screenshot is adjusted per screen rotation
*
* @param storePath where the PNG should be written to
* @param scale scale the screenshot down if needed; 1.0f for original size
* @param quality quality of the PNG compression; range: 0-100
* @return true if screen shot is created successfully, false otherwise
]]
function takeScreenshot(storePath, scale, quality)
    scale = scale or 1.0
    quality = quality or 90

    uiDevice:takeScreenshot(storePath, scale, quality)
end

import 'android.widget.Toast'
import 'android.os.Looper'
function toast(str)
    Looper:prepare()
    local t = Toast:makeText(activity:getApplicationContext(), str, Toast.LENGTH_SHORT)
    t:show()
    Looper:loop()
end

function os.execute(cmd)
    return activity:execRootCmd(cmd)
end