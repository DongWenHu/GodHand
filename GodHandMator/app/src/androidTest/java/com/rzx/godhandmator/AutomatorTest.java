package com.rzx.godhandmator;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Basic sample for unbundled UiAutomator.
 */
public class AutomatorTest {
    private static final String TAG = "test.AutomatorTest";
    private static final Context context = InstrumentationRegistry.getContext();

    private UiDevice mDevice;

    @Before
    public void setUp() throws IOException, InterruptedException {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        AutomatorApi.setUiDevice(mDevice);

        AutomatorServer as = AutomatorServer.getInstance();
        as.start();
        // Start from the home screen
//        mDevice.pressHome();

//        // Wait for launcher
//        final String launcherPackage = getLauncherPackageName();
//        Assert.assertThat(launcherPackage, CoreMatchers.notNullValue());
//        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
//
//        // Launch the blueprint app
//        final Intent intent = context.getPackageManager()
//                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
//        context.startActivity(intent);
//
//        // Wait for the app to appear
//        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
//        contextMain = ShareSource.getInstance().getContext();
    }

    @Test
    public void testCheckPreconditions() throws InterruptedException {
        Assert.assertThat(mDevice, CoreMatchers.notNullValue());

//        Intent intent = new Intent(context, RzxGhService.class);
//        context.startService(intent);

//
//        final String src = "require 'import'\nrequire 'luatouch'\nos.execute('am start --activity-no-history com.tencent.mm/.ui.account.LoginUI')\n";
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                LuaState L = LuaStateFactory.newLuaState();
//                setLuaState(L);
//                try {
//                    evalLua(L, src);
//                    mDevice.wait(Until.hasObject(By.pkg("com.tencent.mm").depth(0)), 5000);
//
//                } catch(LuaException e) {
////                    Looper.prepare();
////                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
////                    Looper.loop();
//                    Log.e(TAG, e.getMessage());
//                }
//            }
//        });
//
//        thread.start();
//        thread.join();
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

}
