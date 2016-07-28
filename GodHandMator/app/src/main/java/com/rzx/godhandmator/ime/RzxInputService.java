package com.rzx.godhandmator.ime;

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by rzx on 2016/7/5.
 */
public class RzxInputService extends InputMethodService{
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String text = "";

        try {
            FileInputStream fin = new FileInputStream("/mnt/sdcard/GodHand/tmp/inputText.txt");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            text = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputConnection ic = getCurrentInputConnection();
        if (ic != null){
            if (text != null) {
                if (text.equals("#Enter")) {
                    sendKeyChar('\n');
                } else {
                    ic.commitText( text, text.length());
                    text = null;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
    }

}
