package com.rzx.godhandmator;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

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
     * @param scale scale the screenshot down if needed; 1.0f for original size
     * @param quality quality of the PNG compression; range: 0-100
     * @return true if screen shot is created successfully, false otherwise
     * @since API Level 17
     */
    public static Boolean takeScreenshot(File storePath, Float scale, int quality) {
        if(uiDevice == null)
            return false;

        return uiDevice.takeScreenshot(storePath, scale.floatValue(), quality);
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
     * @param text
     * @param index
     * @return true if successful, else return false
     * @throws UiObjectNotFoundException
     */
    public static Boolean clickByText(String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));
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
     * @param text
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean longClickByText(String text, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));

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
     * @param text
     * @param timeout
     * @return
     */
    public static Boolean waitNewWindowByText(String text, int timeout){
        if(uiDevice == null)
            return false;

        return uiDevice.wait(Until.hasObject(By.textContains(text)), timeout);
    }

    /**
     * @param text
     * @param textContent
     * @param index
     * @return
     * @throws UiObjectNotFoundException
     */
    public static Boolean setTextByText(String text, String textContent, int index) throws UiObjectNotFoundException {
        if(uiDevice == null)
            return false;

//        List<UiObject2> ret = new ArrayList<UiObject2>();
//
//        List<UiObject2> lst =  uiDevice.findObjects(By.textContains(text));
//        if(lst.size() <= index)
//            return false;
//
//        lst.get(index).setText(textContent);

        UiObject object = new UiObject(new UiSelector().textContains(text).instance(index));
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

        executeShellCommand("ime set com.rzx.godhandmator/.ime.RzxInputService");
        String cmd = "am startservice  -a android.view.InputMethod -n com.rzx.godhandmator/.ime.RzxInputService" +
                " --es text \"" + str +
                "\" --es keyCode 0";
        Log.i(TAG, cmd);
        executeShellCommand(cmd);
        return true;
    }
}
