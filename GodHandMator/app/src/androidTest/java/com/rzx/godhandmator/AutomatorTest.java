package com.rzx.godhandmator;

import android.app.UiAutomation;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.InstrumentationUiAutomatorBridge;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Basic sample for unbundled UiAutomator.
 */
public class AutomatorTest {
    private static final String TAG = "AutomatorTest";

    private static Context context = null;
    private static UiDevice mDevice = null;
    private static UiAutomation mUiAutomation = null;

    /**
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     * @throws RemoteException
     * @throws LuaException
     */
    @Before
    public void setUp() throws IOException, InterruptedException, ClassNotFoundException, RemoteException, LuaException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        // Initialize Context and UiDevice instance
        context = InstrumentationRegistry.getContext();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Method m1 = mDevice.getClass().getDeclaredMethod("getAutomatorBridge");
        m1.setAccessible(true);
        InstrumentationUiAutomatorBridge ins = (InstrumentationUiAutomatorBridge)m1.invoke(mDevice);
        Field field = ins.getClass().getSuperclass().getDeclaredField("mUiAutomation");
        field.setAccessible(true);
        mUiAutomation = (UiAutomation) field.get(ins);

        AutomatorApi.setUiDevice(mDevice);
        AutomatorApi.setContext(context);
        AutomatorApi.setUiAutomation(mUiAutomation);


        File luaDir = new File("/mnt/sdcard/GodHand/lua");
        File pluginDir = new File("/mnt/sdcard/GodHand/plugin");
        File logDir = new File("/mnt/sdcard/GodHand/log");
        luaDir.mkdirs();
        pluginDir.mkdirs();
        logDir.mkdirs();

        Configurator.getInstance().setActionAcknowledgmentTimeout(1);
    }

    @Test
    public void testCheckPreconditions() throws InterruptedException, LuaException {
        Assert.assertThat(mDevice, CoreMatchers.notNullValue());

        LuaState L = LuaStateFactory.newLuaState();
        setLuaState(L);
        evalLua(L, "require 'greet'\ntest()\n");
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    private static byte[] readAll(InputStream input) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * @param L
     */
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

    /**
     * @param L
     * @param src
     * @return
     * @throws LuaException
     */
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

    /**
     * @param error
     * @return
     */
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
