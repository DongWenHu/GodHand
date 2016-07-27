package com.rzx.godhandmator;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiDevice;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * Basic sample for unbundled UiAutomator.
 */
public class AutomatorTest {
    private static final String TAG = "AutomatorTest";

    private static Context context = null;
    private static UiDevice mDevice = null;

    @Before
    public void setUp() throws IOException, InterruptedException, ClassNotFoundException, RemoteException, LuaException {
        // Initialize Context and UiDevice instance
        context = InstrumentationRegistry.getContext();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        AutomatorApi.setUiDevice(mDevice);
        AutomatorApi.setContext(context);

        Configurator.getInstance().setActionAcknowledgmentTimeout(1);

//        AutomatorServer as = AutomatorServer.getInstance();
//        as.start();
        // Start from the home screen
//        mDevice.pressHome();

//        // Wait for launcher
//        final String launcherPackage = getLauncherPackageName();
//        Assert.assertThat(launcherPackage, CoreMatchers.notNullValue());
//        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
//
//        // Launch the blueprint app
//        Intent intent = new Intent();
//        ComponentName cn = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.account.LoginUI");
//        intent.setComponent(cn);
////        final Intent intent = context.getPackageManager()
////                .getLaunchIntentForPackage("com.tencent.mm");
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//
//        // Wait for the app to appear

//        mDevice.sleep();
        LuaState L = LuaStateFactory.newLuaState();
        setLuaState(L);
        evalLua(L, "require 'greet'\ntest()\n");
    }

    @Test
    public void testCheckPreconditions() throws InterruptedException, LuaException {
        Assert.assertThat(mDevice, CoreMatchers.notNullValue());



    }

//    @Test
//    public void testChangeText_sameActivity() {
//        // Type text and then press the button.
//        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "editTextUserInput"))
//                .setText(STRING_TO_BE_TYPED);
//        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "changeTextBt"))
//                .click();
//
//        // Verify the test is displayed in the Ui
//        UiObject2 changedText = mDevice
//                .wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "textToBeChanged")),
//                        500 /* wait 500ms */);
//        assertThat(changedText.getText(), is(equalTo(STRING_TO_BE_TYPED)));
//    }
//
//    @Test
//    public void testChangeText_newActivity() {
//        // Type text and then press the button.
//        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "editTextUserInput"))
//                .setText(STRING_TO_BE_TYPED);
//        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "activityChangeTextBtn"))
//                .click();
//
//        // Verify the test is displayed in the Ui
//        UiObject2 changedText = mDevice
//                .wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "show_text_view")),
//                        500 /* wait 500ms */);
//        assertThat(changedText.getText(), is(equalTo(STRING_TO_BE_TYPED)));
//    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
//    private String getLauncherPackageName() {
//        // Create launcher Intent
//        final Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//
//        // Use PackageManager to get the launcher package name
//        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
//        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        return resolveInfo.activityInfo.packageName;
//    }


    private static byte[] readAll(InputStream input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    private static void setLuaState(LuaState L) {
        L.openLibs();

        try {
            L.pushJavaObject(context);
            L.setGlobal("context");
            L.pushJavaObject(mDevice);
            L.setGlobal("uiDevice");

            JavaFunction assetLoader = new JavaFunction(L) {
                @Override
                public int execute() throws LuaException {
                    String name = L.toString(-1);

                    AssetManager am = context.getAssets();
                    try {
                        InputStream is = am.open(name + ".lua");
                        byte[] bytes = readAll(is);
                        L.LloadBuffer(bytes, name);
                        return 1;
                    } catch (Exception e) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        e.printStackTrace(new PrintStream(os));
                        L.pushString("Cannot load module "+name+":\n"+os.toString());
                        return 1;
                    }
                }
            };

            L.getGlobal("package");            // package
            L.getField(-1, "loaders");         // package loaders
            int nLoaders = L.objLen(-1);       // package loaders

            L.pushJavaFunction(assetLoader);   // package loaders loader
            L.rawSetI(-2, nLoaders + 1);       // package loaders
            L.pop(1);                          // package

            L.getField(-1, "path");            // package path
            String customPath = context.getFilesDir() + "/?.lua";
            L.pushString(";" + customPath);    // package path custom
            L.concat(2);                       // package pathCustom
            L.setField(-2, "path");            // package
            L.pop(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean evalLua(LuaState L, String src) throws LuaException {
        L.setTop(0);
        int ok = L.LloadString(src);

        if (ok == 0) {
            L.getGlobal("debug");
            L.getField(-1, "traceback");
            L.remove(-2);
            L.insert(-2);
            ok = L.pcall(0, 0, -2);
            if (ok == 0) {
                return true;
            }
        }
        throw new LuaException(errorReason(ok) + ": " + L.toString(-1));

    }

    private String errorReason(int error) {
        switch (error) {
            case 4:
                return "Out of memory";
            case 3:
                return "Syntax error";
            case 2:
                return "Runtime error";
            case 1:
                return "Yield error";
        }
        return "Unknown error " + error;
    }
}
