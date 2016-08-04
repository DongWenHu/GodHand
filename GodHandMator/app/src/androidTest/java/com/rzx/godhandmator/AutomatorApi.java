package com.rzx.godhandmator;

import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/24/024.
 */
public class AutomatorApi {
    private static final String TAG = "AutomatorApi";
    /**
     * UiDevice provides access to state information about the device.
     * You can also use this class to simulate user actions on the device,
     * such as pressing the d-pad or pressing the Home and Menu buttons.
     */
    public static UiDevice uiDevice = null;
    public static Context context = null;
    public static UiAutomation uiAutomation = null;

    /**
     * Set init UiDevice
     * @param dev
     */
    public static void setUiDevice(UiDevice dev){
        uiDevice = dev;
    }

    /**
     * @param con
     */
    public static void setContext(Context con){
        context = con;
    }

    /**
     * @param uia
     */
    public static void setUiAutomation(UiAutomation uia){
        uiAutomation = uia;
    }


    /**
     * Simulates a short press on the MENU button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressMenu(){
        if(uiDevice == null)
            return false;

        return uiDevice.pressMenu();
    }

    /**
     * Simulates a short press on the BACK button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressBack() {
        if(uiDevice == null)
            return false;

        return uiDevice.pressBack();
    }

    /**
     * Simulates a short press on the HOME button.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressHome() {
        if(uiDevice == null)
            return false;

        return uiDevice.pressHome();
    }

    /**
     * Simulates a short press on the ENTER key.
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressEnter() {
        if(uiDevice == null)
            return false;

        return uiDevice.pressEnter();
    }

    /**
     * Simulates a short press using a key code.
     *
     * See {@link KeyEvent}
     * @return true if successful, else return false
     * @since API Level 16
     */
    public static Boolean pressKeyCode(int keyCode) {
        if(uiDevice == null)
            return false;

        return uiDevice.pressKeyCode(keyCode);
    }

    /**
     * Opens the notification shade.
     *
     * @return true if successful, else return false
     * @since API Level 18
     */
    public static Boolean openNotification() {
        if(uiDevice == null)
            return false;

        return uiDevice.openNotification();
    }

    /**
     * Opens the Quick Settings shade.
     *
     * @return true if successful, else return false
     * @since API Level 18
     */
    public static Boolean openQuickSettings() {
        if(uiDevice == null)
            return false;

        return uiDevice.openQuickSettings();
    }

    /**
     * Perform a click at arbitrary coordinates specified by the user
     *
     * @param x coordinate
     * @param y coordinate
     * @return true if the click succeeded else false
     * @since API Level 16
     */
    public static Boolean click(int x, int y) {
        if(uiDevice == null)
            return false;

        return uiDevice.click(x, y);
    }

    /**
     * Performs a swipe from one coordinate to another using the number of steps
     * to determine smoothness and speed. Each step execution is throttled to 5ms
     * per step. So for a 100 steps, the swipe will take about 1/2 second to complete.
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param steps is the number of move steps sent to the system
     * @return false if the operation fails or the coordinates are invalid
     * @since API Level 16
     */
    public static Boolean swipe(int startX, int startY, int endX, int endY, int steps) {
        if(uiDevice == null)
            return false;

        return uiDevice.swipe(startX,
                startY,
                endX,
                endY,
                steps);
    }

    /**
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
     * @return true if swipe is performed, false if the operation fails
     * or the coordinates are invalid
     * @since API Level 18
     */
    public static Boolean drag(int startX, int startY, int endX, int endY, int steps) {
        if(uiDevice == null)
            return false;

        return uiDevice.drag(startX,
                startY,
                endX,
                endY,
                steps);
    }

    /**
     * Retrieves the last activity to report accessibility events.
     * @return String name of activity
     * @since API Level 16
     */
    public static String getCurrentActivityName() {
        if(uiDevice == null)
            return null;

        return uiDevice.getCurrentActivityName();
    }

    /**
     * Retrieves the name of the last package to report accessibility events.
     * @return String name of package
     * @since API Level 16
     */
    public static String getCurrentPackageName() {
        if(uiDevice == null)
            return null;

        return uiDevice.getCurrentPackageName();
    }

    /**
     * Helper method used for debugging to dump the current window's layout hierarchy.
     * Relative file paths are stored the application's internal private storage location.
     *
     * @param fileName
     * @since API Level 16
     */
    public static Boolean dumpWindowHierarchy(String fileName) {
        if(uiDevice == null)
            return false;

        uiDevice.dumpWindowHierarchy(fileName);
        return true;
    }

    /**
     * Take a screenshot of current window and store it as PNG
     *
     * The screenshot is adjusted per screen rotation
     *
     * @param storePath where the PNG should be written to
     * @return true if screen shot is created successfully, false otherwise
     * @since API Level 17
     */
    public static Boolean takeScreenshot(String storePath) {
        if(uiDevice == null)
            return false;

        return uiDevice.takeScreenshot(new File(storePath), 1.0f, 100);
    }

    /**
     * Retrieves default launcher package name
     *
     * @return package name of the default launcher
     */
    public static String getLauncherPackageName() {
        if(uiDevice == null)
            return null;

        return uiDevice.getLauncherPackageName();
    }

    /**
     * Executes a shell command using shell user identity, and return the standard output in string.
     * <p>
     * Calling function with large amount of output will have memory impacts, and the function call
     * will block if the command executed is blocking.
     * <p>Note: calling this function requires API level 21 or above
     * @param cmd the command to run
     * @return the standard output of the command
     * @throws IOException
     * @since API Level 21
     * @hide
     */
    public static String executeShellCommand(String cmd) throws IOException {
        if(uiDevice == null)
            return null;

        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= 21) {
            return uiDevice.executeShellCommand(cmd);
        }

        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * This method simulates pressing the power button if the screen is OFF else
     * it does nothing if the screen is already ON.
     *
     * If the screen was OFF and it just got turned ON, this method will insert a 500ms delay
     * to allow the device time to wake up and accept input.
     * @throws RemoteException
     * @since API Level 16
     */
    public static Boolean screenOn() throws RemoteException {
        if(uiDevice == null)
            return false;

        uiDevice.wakeUp();
        return true;
    }

    /**
     * This method simply presses the power button if the screen is ON else
     * it does nothing if the screen is already OFF.
     * @return true if successful, else return false
     * @throws RemoteException
     */
    public static Boolean screenOff() throws RemoteException {
        if(uiDevice == null)
            return false;

        uiDevice.sleep();
        return true;
    }

    /**
     * Find object by text, and click the object if the object is clickable.
     * @param text contains text
     * @param index
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByTextContain(String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));
        object.click();
        return true;
    }

    /**
     * Find object by text, and click the object if the object is clickable.
     * @param text equal text
     * @param index
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByTextEqual(String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().text(text).instance(index));
        object.click();
        return true;
    }

    /**
     * @param cls
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByClass(String cls, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().className(cls).instance(index));
        object.click();
        return true;
    }

    /**
     * @param x
     * @param y
     * @param time
     * @return
     */
    public static Boolean longClick(int x, int y, int time){
        if(uiDevice == null)
            return false;

        return uiDevice.swipe(x, y, x, y, time/5);
    }

    /**
     * @param text contains text
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByTextContain(String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));

        object.longClick();
        return true;
    }

    /**
     * @param text equals text
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByTextEqual(String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().text(text).instance(index));

        object.longClick();
        return true;
    }

    /**
     * @param pkg
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByPkg(String pkg, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.pkg(pkg).depth(0)), timeout);
    }

    /**
     * @param text Contains text
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByTextContain(String text, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.textContains(text)), timeout);
    }

    /**
     * @param text Equals text
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByTextEqual(String text, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.text(text)), timeout);
    }

    /**
     * @param desc Contains description
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByDescContain(String desc, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.descContains(desc)), timeout);
    }

    /**
     * @param desc Equals description
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByDescEqual(String desc, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.desc(desc)), timeout);
    }

    /**
     * @param text Contains text
     * @param textContent
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByTextContain(String text, String textContent, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));
        object.click();
        object.setText(textContent);
        return true;
    }

    /**
     * @param text Equals text
     * @param textContent
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByTextEqual(String text, String textContent, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().text(text).instance(index));
        object.click();
        object.setText(textContent);
        return true;
    }

    /**
     * @param cls
     * @param text
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByClass(String cls, String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().className(cls).instance(index));
        object.click();
        object.setText(text);

//        List<UiObject2> ret = new ArrayList<UiObject2>();
//
//        List<UiObject2> lst =  uiDevice.findObjects(By.clazz(cls));
//        if(lst.size() <= index)
//            return false;
//
//        lst.get(index).setText(text);
        return true;
    }

    /**
     * @param str
     * @return
     */
    public static Boolean inputText(String str) throws IOException {
        if(uiDevice == null)
            return false;

        File destDir = new File("/mnt/sdcard/GodHand/tmp");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        writeFile("/mnt/sdcard/GodHand/tmp/inputText.txt", str);
        executeShellCommand("ime disable com.rzx.godhandmator/.ime.RzxInputService");
        executeShellCommand("ime enable com.rzx.godhandmator/.ime.RzxInputService");
        executeShellCommand("ime set com.rzx.godhandmator/.ime.RzxInputService");
        executeShellCommand("am startservice  -a android.view.InputMethod -n com.rzx.godhandmator/.ime.RzxInputService");
        return true;
    }

    /**
     * @param fileName
     * @return
     * @throws IOException
     */
    public static String readFile(String fileName) throws IOException{
        String res="";
        FileInputStream fin = new FileInputStream(fileName);
        int length = fin.available();
        byte [] buffer = new byte[length];
        fin.read(buffer);
        res = EncodingUtils.getString(buffer, "UTF-8");
        fin.close();
        return res;
    }

    /**
     * @param fileName
     * @param writestr
     * @throws IOException
     */
    public static void writeFile(String fileName, String writestr) throws IOException{
        FileOutputStream fout = new FileOutputStream(fileName);
        byte [] bytes = writestr.getBytes();
        fout.write(bytes);
        fout.close();
    }

    /**
     * The color in coordinate of (x,y) is similar or equal to the value of rgb.
     *
     * @param pixel To be compared first one.
     * @param rgb   To be compared second one.
     * @param sim   0-100
     * @return
     */
    private static Boolean isPixelSimilar(int pixel, int rgb, int sim){
        sim = (100-sim);
        if (sim == 0){
            return pixel == rgb;
        }

        int red1 = (pixel >> 16) &0xff;
        int green1 = (pixel >> 8) &0xff;
        int blue1 = (pixel & 0xff);

        int red2 =(rgb >> 16) &0xff;
        int green2 = (rgb >> 8) &0xff;
        int blue2 = (rgb & 0xff);

        int redDiff = Math.abs(red1 - red2);
        int greenDiff = Math.abs(green1 - green2);
        int blueDiff = Math.abs(blue1 - blue2);

        //waste time
//        Math.sqrt(redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff)

        return redDiff <= sim && greenDiff <= sim && blueDiff <= sim;
    }

    /**
     * Find the point by multiple pixels
     * Example:
     *  findMultiColorInRegionFuzzy( 0xade1f5, "466|29|0x2cadf1,77|192|0x686868,78|193|0x1a1a1a", 90, 0, 0, 719, 1279)
     *
     * @param oriPix Original pixel
     * @param otherPix Other pixels description. Offset in original and value of pixel
     * @param sim Similarity of every pixel, value can be set from 0 to 100.
     * @param startX The start X coordinate of picture.
     * @param startY The start Y coordinate of picture.
     * @param endX  The end X coordinate of picture.
     * @param endY  The end Y coordinate of picture.
     * @return  If find the point then return the point(String), else return "-1,-1".
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static String findMultiColorInRegionFuzzy(
            int oriPix,
            String otherPix,
            int sim,
            int startX,
            int startY,
            int endX,
            int endY)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Bitmap screenshot = uiAutomation.takeScreenshot();

        int width = screenshot.getWidth();
        int height = screenshot.getHeight();
        int[] pixes = new int[width*height];

        oriPix |= 0xFF000000;
        screenshot.getPixels(pixes, 0,
                screenshot.getWidth(),
                0, 0, width, height);

        ArrayList<Integer> xs = new ArrayList<>(10);
        ArrayList<Integer> ys = new ArrayList<>(10);
        int[] rgbs = new int[10];
        int maxX = 0;
        int maxY = 0;
        int minX = 0;
        int minY = 0;
        int i;
        int j;

        if (!otherPix.equals("")) {

            String[] onePix = otherPix.split(",");

            for (i = 0; i < onePix.length; ++i) {
                String[] pros = onePix[i].split("\\|");
                xs.add(i, Integer.parseInt(pros[0]));
                ys.add(i, Integer.parseInt(pros[1]));
                rgbs[i] = Integer.parseInt(pros[2].replaceAll("^0[x|X]", ""), 16) | 0xFF000000;
            }

            maxX = Collections.max(xs);
            maxY = Collections.max(ys);
            minX = Collections.min(xs);
            minY = Collections.min(ys);
        }
        boolean isFind = false;
        for (i = startX; i < endX; i++){
            for (j = startY; j < endY; j++){
                if ((maxX + i) > endX ||
                    (maxY + j) > endY ||
                    (minX + i) < startX ||
                    (minY + j) < startY){
                    continue;
                }

                int n = j*width+i;
                if (isPixelSimilar(pixes[n], oriPix, sim)){
                    isFind = true;
                    for (int k = 0; k < xs.size(); ++k){
                        int m = (j+ys.get(k))*width + (i+xs.get(k));
                        if (!isPixelSimilar(pixes[m], rgbs[k], sim)){
                            isFind = false;
                            break;
                        }
                    }
                    if (isFind){
                        return ""+i+","+j;
                    }
                }
            }
        }

        return "-1,-1";
    }

    /**
     * @param file
     * @param content
     */
    public static void log(String file, String content) throws IOException {
        File destDir = new File("/mnt/sdcard/GodHand/log");
        if (!destDir.exists()){
            destDir.mkdirs();
        }

        Date date=new Date();

        String logfile = "/mnt/sdcard/GodHand/log/"+file+".log";
        String logContent = String.format("[%tF %tT]: %s\n", date, date, content);
        writeFile(logfile, logContent);
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String httpGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = param.equals("")?url:url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *
     * @return 所代表远程资源的响应结果
     */
    public static String httpPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param url
     * @am
     * @return
     */
    public static String httpPostWithHeader(String url, String postData, String header) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            String[] headers = header.split(";");
            for (int k = 0; k < headers.length; ++k){
                String[] kv = headers[k].split(":");
                conn.setRequestProperty(kv[0], kv[1]);
            }

            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(postData);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param ms
     */
    public static void mSleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filename
     * @return
     */
    public static boolean fileExists(String filename) throws IOException {
        String result = executeShellCommand("ls " + filename);
        if(result.contains("No such file or directory")){
            return false;
        }

        return true;
    }

    /**
     * @param str
     * @return
     */
    public static String base64Encode(String str){
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    /**
     * @param str
     * @return
     */
    public static String base64Decode(String str){
        return String.valueOf(Base64.decode(str, Base64.DEFAULT));
    }

    /**
     * @param filename
     * @return
     */
    public static String base64EncodeFile(String filename) throws IOException {
        File file = new File(filename);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return Base64.encodeToString(buffer,Base64.DEFAULT);
    }
}
