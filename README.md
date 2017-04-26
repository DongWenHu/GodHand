# GodHand
根据uiautomator框架实现类似按键精灵，触动精灵的程序，支持lua语言, 需root<br>
只支持android4.4 以上版本.

## 使用方法
> 1、编译程序，生成一个主apk(app\build\outputs\apk\app-debug.apk)和一个单元测试apk(app\build\outputs\apk\app-debug-androidTest-unaligned.apk).<br>
> 2、安装上面两个程序.<br>
> 3、写入指定的文件运行名到 "/mnt/sdcard/GodHand/tmp/run_file", 不写默认为main.lua.<br>
> 4、运行adb shell am instrument -w -r   -e debug false -e class com.rzx.godhandmator.AutomatorTest com.rzx.godhandmator.test/android.support.test.runner.AndroidJUnitRunner.<br>

<br>

## Example
```lua
  require 'import'
  import 'com.rzx.godhandmator.AutomatorApi'
  
  AutomatorApi:toast("任务执行开始")
  AutomatorApi:mSleep(2000)
  AutomatorApi:swipe(719,500,100,500,50)
  AutomatorApi:toast("任务执行结束")
  AutomatorApi:mSleep(2000)
```

## 日志
>出错日志查看/mnt/sdcard/GodHand/log/system.log
