package com.rzx.godhandmator.ime;

import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Created by rzx on 2016/7/5.
 */
public class RzxInputService extends InputMethodService{

    private String text = null;
    private int keyCode = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        text = intent.getExtras().getString("text");
        keyCode = intent.getExtras().getInt("keyCode");

        InputConnection ic = getCurrentInputConnection();
        if (ic != null){
            if (text != null){
                ic.commitText( text, text.length());
                text = null;
            }
            if (keyCode != 0){
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    sendKeyChar('\n');
                } else {
                    sendDownUpKeyEvents(keyCode);
                }
                keyCode = 0;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
    }

}
