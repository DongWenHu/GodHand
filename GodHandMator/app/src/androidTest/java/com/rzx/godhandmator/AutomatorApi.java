package com.rzx.godhandmator;

import android.os.RemoteException;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.view.KeyEvent;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/7/24/024.
 */
public class AutomatorApi {
    /**
     * UiDevice provides access to state information about the device.
     * You can also use this class to simulate user actions on the device,
     * such as pressing the d-pad or pressing the Home and Menu buttons.
     */
    public static UiDevice uiDevice = null;

    /**
     * Set init UiDevice
     * @param dev
     */
    public static void setUiDevice(UiDevice dev){
        uiDevice = dev;
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
    public static Boolean pressKeyCode(Integer keyCode) {
        if(uiDevice == null)
            return false;

        return uiDevice.pressKeyCode(keyCode.intValue());
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
    public static Boolean click(Integer x, Integer y) {
        if(uiDevice == null)
            return false;

        return uiDevice.click(x.intValue(), y.intValue());
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
    public static Boolean swipe(Integer startX, Integer startY, Integer endX, Integer endY, Integer steps) {
        if(uiDevice == null)
            return false;

        return uiDevice.swipe(startX.intValue(),
                startY.intValue(),
                endX.intValue(),
                endY.intValue(),
                steps.intValue());
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
    public static Boolean drag(Integer startX, Integer startY, Integer endX, Integer endY, Integer steps) {
        if(uiDevice == null)
            return false;

        return uiDevice.drag(startX.intValue(),
                startY.intValue(),
                endX.intValue(),
                endY.intValue(),
                steps.intValue());
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
     * Waits for a window content update event to occur.
     *
     * If a package name for the window is specified, but the current window
     * does not have the same package name, the function returns immediately.
     *
     * @param packageName the specified window package name (can be <code>null</code>).
     *        If <code>null</code>, a window update from any front-end window will end the wait
     * @param timeout the timeout for the wait
     *
     * @return true if a window update occurred, false if timeout has elapsed or if the current
     *         window does not have the specified package name
     * @since API Level 16
     */
    public static Boolean waitForWindowUpdate(String packageName, Long timeout) {
        if(uiDevice == null)
            return false;

        return uiDevice.waitForWindowUpdate(packageName, timeout.longValue());
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
    public static Boolean takeScreenshot(File storePath, Float scale, Integer quality) {
        if(uiDevice == null)
            return false;

        return uiDevice.takeScreenshot(storePath, scale.floatValue(), quality.intValue());
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

        return uiDevice.executeShellCommand(cmd);
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
    public static Boolean wakeUp() throws RemoteException {
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
    public static Boolean sleep() throws RemoteException {
        if(uiDevice == null)
            return false;

        uiDevice.sleep();
        return true;
    }

    /**
     * Find object by text, and click the object if the object is clickable.
     * @param text
     * @return true if successful, else return false
     */
    public static Boolean clickByText(String text){
        if(uiDevice == null)
            return false;

        UiObject2 object = uiDevice.findObject(By.text(text));
        if(object == null)
            return false;
        object.click();
        return true;
    }

    /**
     * Find object by resource, and click the object if the object is clickable.
     * @param resPackage
     * @param resId
     * @return
     */
    public static Boolean clickByRes(String resPackage, String resId){
        if(uiDevice == null)
            return false;

        UiObject2 object = uiDevice.findObject(By.res(resPackage, resId));
        if(object == null)
            return false;
        object.click();
        return true;
    }

    /**
     * @param x
     * @param y
     * @param time
     * @return
     */
    public static Boolean longClick(Integer x, Integer y, Integer time){
        if(uiDevice == null)
            return false;

        return uiDevice.swipe(x, y, x, y, time.intValue()/5);
    }

    /**
     * @param text
     * @return
     */
    public static Boolean longClickByText(String text){
        if(uiDevice == null)
            return false;

        UiObject2 object = uiDevice.findObject(By.text(text));
        if(object == null)
            return false;
        object.longClick();
        return true;
    }

    /**
     * @param resPackage
     * @param resId
     * @return
     */
    public static Boolean longClickByRes(String resPackage, String resId){
        if(uiDevice == null)
            return false;

        UiObject2 object = uiDevice.findObject(By.res(resPackage, resId));
        if(object == null)
            return false;
        object.longClick();
        return true;
    }

}
