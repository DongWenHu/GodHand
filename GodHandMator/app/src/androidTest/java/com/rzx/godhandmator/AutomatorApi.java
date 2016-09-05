package com.rzx.godhandmator;

import android.app.UiAutomation;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Base64;
import android.view.KeyEvent;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

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
     * 通过包含文本内容找到组件并点击
     *
     * @param text 包含文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
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
     * 通过等值文本内容找到组件并点击
     *
     * @param text 等值文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
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
     * 通过类名找到组件找到组件并点击， 类名可通过DDMS获取
     *
     * @param cls 类名
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
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
     * 通过资源ID找到组件找到组件并点击， 资源ID可通过DDMS获取
     *
     * @param resId 资源ID
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByResId(String resId, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().resourceId(resId).instance(index));
        object.click();
        return true;
    }


    /**
     * 通过资源ID找到组件找到组件并获取文本内容， 资源ID可通过DDMS获取
     *
     * @param resId 资源ID
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByResId(String resId, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return "";

        UiObject object = new UiObject(new UiSelector().resourceId(resId).instance(index));
        return object.getText();
    }

    /**
     * 通过类名找到组件并获取文本内容，类名可通过DDMS获取
     *
     * @param cls 类名
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByClass(String cls, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return "";

        UiObject object = new UiObject(new UiSelector().className(cls).instance(index));
        return object.getText();
    }

    /**
     * 通过包含文本内容匹配找到组件并获取文本内容
     *
     * @param str 包含的文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
     * @return 获取到的文本内容
     * @throws UiObjectNotFoundException
     */
    public static String getTextByTextContain(String str, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return "";

        UiObject object = new UiObject(new UiSelector().textContains(str).instance(index));
        return object.getText();
    }

    /**
     * 通过坐标长按点击
     *
     * @param x 坐标x
     * @param y 坐标y
     * @param time 长按时间
     * @return
     */
    public static Boolean longClick(int x, int y, int time){
        if(uiDevice == null)
            return false;

        return uiDevice.swipe(x, y, x, y, time/5);
    }

    /**
     * 通过包含文本内容找到组件并长按
     *
     * @param text 包含匹配文本
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
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
     * 通过等值文本内容找到组件并长按
     *
     * @param text 等值匹配内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
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
     * 通过资源ID找到组件并长按，资源ID可通过DDMS获取
     *
     * @param resId 资源ID
     * @param index 如果找到多个相同项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByResId(String resId, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().resourceId(resId).instance(index));

        object.longClick();
        return true;
    }

    /**
     * 通过包名匹配等待窗口出现，包名可通过DDMS获取
     *
     * @param pkg 包名
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByPkg(String pkg, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.pkg(pkg).depth(0)), timeout);
    }

    /**
     * 通过包含匹配文本内容等待窗口出现，文本内容即界面组件显示的内容或通过DDMS获取
     *
     * @param text 包含匹配文本内容
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByTextContain(String text, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.textContains(text)), timeout);
    }

    /**
     * 通过等值匹配文本内容等待窗口出现，文本内容即界面组件显示的内容或通过DDMS获取
     *
     * @param text 等值匹配文本内容
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByTextEqual(String text, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.text(text)), timeout);
    }

    /**
     * 通过包含匹配描述字段等待窗口出现，描述字段内容可通过DDMS获取
     *
     * @param desc 包含匹配描述字段
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByDescContain(String desc, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.descContains(desc)), timeout);
    }

    /**
     * 通过等值匹配描述字段等待窗口出现，描述字段内容可通过DDMS获取
     *
     * @param desc 等值匹配描述字段
     * @param timeout 超时时间
     * @return
     */
    public static Boolean waitNewWindowByDescEqual(String desc, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.desc(desc)), timeout);
    }

    /**
     * @param resId
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByResId(String resId, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.res(resId)), timeout);
    }

    /**
     * 通过包含匹配文本内容来设置文本框内容，文本即该组件上显示的内容，或者通过DDMS获取.
     *
     * @param text 包含匹配的文本内容
     * @param textContent 需设置的文本内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
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
     * 通过等值匹配文本内容来设置文本框内容，文本即该组件上显示的内容，或者通过DDMS获取.
     *
     * @param text 等值匹配的内容
     * @param textContent 需要设置的内容
     * @param index 如果找到多个相同内容的项，取第几个的索引，从0开始计算。
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
     *  通过类设置文本框内容，类通过DDMS可以获取.
     *
     * @param cls 类名
     * @param text 需要设置的内容
     * @param index 如果找到多个相同类的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByClass(String cls, String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().className(cls).instance(index));
        object.click();
        object.setText(text);

        return true;
    }

    /**
     * 通过资源ID设置文本框内容，资源ID通过DDMS可以获取.
     *
     * @param resId 资源ID
     * @param text 需要设置的内容
     * @param index 如果找到多个相同资源ID的项，取第几个的索引，从0开始计算。
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByResId(String resId, String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().resourceId(resId).instance(index));
        object.click();
        object.setText(text);

        return true;
    }

    /**
     * 在编辑框输入一串字符串。
     *
     * @param str 字符串内容
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
        executeShellCommand("ime disable com.rzx.godhandmator/.ime.RzxInputService");
        return true;
    }

    /**
     * 读一个文件内容。
     *
     * @param fileName 要读的文件名
     * @return 读取后的内容
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
     * 覆盖方式写一个文件，如果文件不存在，将创建一个文件。
     *
     * @param fileName 文件名
     * @param writestr 文件内容
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
     * 以覆盖方式记录日志，日志生成的目录在/mnt/sdcard/GodHand/log/
     *
     * @param file 日志文件名,无需后缀名，自动扩展“.log”。
     * @param content 日志内容
     */
    public static void log(String file, String content) throws IOException {
        File destDir = new File("/mnt/sdcard/GodHand/log");
        if (!destDir.exists()){
            destDir.mkdirs();
        }

        Date date=new Date();

        String logfile = "/mnt/sdcard/GodHand/log/"+file+".log";
        String logContent = String.format("[%tF %tT]: %s\n", date, date, content);
        FileWriter writer = new  FileWriter(logfile,  false);
        writer.write(logContent);
        writer.close();
    }

    /**
     * 以追加方式记录日志，日志生成的目录在/mnt/sdcard/GodHand/log/
     *
     * @param file 日志文件名,无需后缀名，自动扩展“.log”。
     * @param content 日志内容
     */
    public static void logAppend(String file, String content) throws IOException {
        File destDir = new File("/mnt/sdcard/GodHand/log");
        if (!destDir.exists()){
            destDir.mkdirs();
        }

        Date date=new Date();

        String logfile = "/mnt/sdcard/GodHand/log/"+file+".log";
        String logContent = String.format("[%tF %tT]: %s\n", date, date, content);
        FileWriter writer = new  FileWriter(logfile,  true);
        writer.write(logContent);
        writer.close();
    }

    /**
     * 建议使用luasocket插件取代该方法, 更高效，更多的操作方式
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果，如果请求发生错误返回 "" 空字符串
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

            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = connection.getInputStream();

            for (int i; (i = is.read(buf)) != -1;) {
                baos.write(buf, 0, i);
            }
            result = baos.toString("UTF-8");

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
     * 建议使用luasocket插件取代该方法, 更高效，更多的操作方式
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param    Post 内容
     *
     * @return 所代表远程资源的响应结果，如果请求发生错误返回 "" 空字符串
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
     * 建议使用luasocket插件取代该方法, 更高效，更多的操作方式
     * 自定义头部发送Post请求
     *
     * 头部格式: Content-Type:application/json;Content-Length:20
     *          以分号分割多个头部信息, 冒号分割键值。
     *
     * @param url 发送请求的 URL
     * @param postData Post内容
     * @param header 以分号分割多个头部信息, 冒号分割键值。
     * @return 所代表远程资源的响应结果，如果请求发生错误返回 "" 空字符串
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
     * Sleep.
     *
     * @param ms Millisecond.
     */
    public static void mSleep(int ms){
        SystemClock.sleep(ms);
    }

    /**
     * 判断文件是否存在
     * 注意:此方法只能判断sdcard中的文件是否存在，其他目录会没有权限。
     * @param filename
     * @return
     */
    public static boolean fileExists(String filename) throws IOException {
        File file = new File(filename);
        boolean isExists = file.exists();
        return isExists;
    }

    /**
     * Base64 encode for string.
     *
     * @param str
     * @return
     */
    public static String base64Encode(String str){
        return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }

    /**
     *  Base64 decode for string.
     *
     * @param str
     * @return
     */
    public static String base64Decode(String str){
        return String.valueOf(Base64.decode(str, Base64.DEFAULT));
    }

    /**
     * Md5 32 bit, lowercase letters.
     *
     * @param str
     * @return
     */
    public static String Md5_32(String str){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Md5 16 bit, lowercase letters.
     *
     * @param str
     * @return
     */
    public static String Md5_16(String str){
        String ret = Md5_32(str);
        if (ret != null){
            return ret.substring(8, 24);
        }

        return null;
    }

    /**
     * Base64 encode for file.
     *
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

    /**
     * Make toast text.
     *
     * @param str
     */
    public static void toast(String str) throws IOException {
        File destDir = new File("/mnt/sdcard/GodHand/tmp");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        writeFile("/mnt/sdcard/GodHand/tmp/toastText.txt", str);
        executeShellCommand("am startservice -n com.rzx.godhandmator/.services.ActionService");
    }

    /**
     * 删除图片数据库所有图片，对应存储中的图片也会被删除
     *
     */
    public static void delImagesMedia(){
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null);
    }

    /**
     * 通知更新图片数据库中的指定文件的图片
     *
     * @param file 指定要更新的文件
     */
    public static void notifyScanImageFile(String file){
        MediaScannerConnection.scanFile(context, new String[] { file }, null, null);
    }

    ////////////////////////////////////////////////////////////
    public static String getImei(String fileName) {
        HashMap localHashMap1 = loadHashMapFromFile(fileName
                + "/MicroMsg/systemInfo.cfg");
        HashMap localHashMap2 = loadHashMapFromFile(fileName
                + "/MicroMsg/CompatibleInfo.cfg");
        return getIMEI(localHashMap1, localHashMap2);
    }

    public static HashMap loadHashMapFromFile(String paramString) {
        try {
            ObjectInputStream localObjectInputStream = new ObjectInputStream(
                    new FileInputStream(paramString));
            Object localObject = localObjectInputStream.readObject();
            localObjectInputStream.close();
            HashMap localHashMap = (HashMap) localObject;
            return localHashMap;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    private static String getIMEI(HashMap paramHashMap1, HashMap paramHashMap2) {
        String str;
        try {
            boolean bool = paramHashMap1.containsKey(Integer.valueOf(258));
            str = null;
            if (bool)
                str = paramHashMap1.get(Integer.valueOf(258)).toString();
            if ((str == null) || (str.length() == 0))
                str = (String) paramHashMap2.get(Integer.valueOf(258));
            if ((str == null) || (str.length() == 0))
                throw new Exception();
        } catch (Exception localException) {

            localException.printStackTrace();
            str = "";
        }
        return str;
    }
    ///////////////////////////////////////////////////////////
}






