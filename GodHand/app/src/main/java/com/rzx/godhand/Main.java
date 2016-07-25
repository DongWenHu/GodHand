package com.rzx.godhand;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rzx.godhand.msg.AutomatorRequest;
import com.rzx.godhand.msg.AutomatorResponse;

import org.keplerproject.luajava.JavaFunction;
import org.keplerproject.luajava.LuaException;
import org.keplerproject.luajava.LuaState;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main extends Activity implements OnClickListener, OnLongClickListener {

    private static final String TAG = "com.rzx.godhand.Main";
	private static final int MSG_TOAST = 0x1000001;


	private Button execute;
	// public so we can play with these from Lua
	public EditText source;
	public TextView status;

	final StringBuilder output = new StringBuilder();
	private final Handler msgHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
				case MSG_TOAST:
					Toast.makeText(getApplicationContext(), msg.getData().getString("text"), Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        execute = (Button) findViewById(R.id.executeBtn);
		execute.setOnClickListener(this);

		source = (EditText) findViewById(R.id.source);
		source.setOnLongClickListener(this);
//        execRootCmd("reboot");
        source.setText("require 'import'\nrequire 'luatouch'\nos.execute('am start --activity-no-history com.tencent.mm/.ui.account.LoginUI')\ntoast(\"aaa\")");

		status = (TextView) findViewById(R.id.statusText);
		status.setMovementMethod(ScrollingMovementMethod.getInstance());

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Socket socket = new Socket("127.0.0.1", 12580);
					socket.setSoTimeout(5000);
					AutomatorRequest as = new AutomatorRequest();
					as.setMethod("openQuickSettings");
//                    Object[] objects = new Object[1];
//                    objects[0] = "ls";
//					as.setArgs(objects);
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(as);
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					AutomatorResponse response = (AutomatorResponse) in.readObject();
					if(response != null) {
						toast(response.getResponse() == null?"null":response.getResponse().toString());
					}
					socket.close();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

    private void setLuaState(LuaState L) {
        L.openLibs();

        try {
            L.pushJavaObject(this);
            L.setGlobal("activity");

            JavaFunction print = new JavaFunction(L) {
                @Override
                public int execute() throws LuaException {
                    for (int i = 2; i <= L.getTop(); i++) {
                        int type = L.type(i);
                        String stype = L.typeName(type);
                        String val = null;
                        if (stype.equals("userdata")) {
                            Object obj = L.toJavaObject(i);
                            if (obj != null)
                                val = obj.toString();
                        } else if (stype.equals("boolean")) {
                            val = L.toBoolean(i) ? "true" : "false";
                        } else {
                            val = L.toString(i);
                        }
                        if (val == null)
                            val = stype;
                        output.append(val);
                        output.append("\t");
                    }
                    output.append("\n");
                    return 0;
                }
            };
            print.register("print");

            JavaFunction assetLoader = new JavaFunction(L) {
                @Override
                public int execute() throws LuaException {
                    String name = L.toString(-1);

                    AssetManager am = getAssets();
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
            String customPath = getFilesDir() + "/?.lua";
            L.pushString(";" + customPath);    // package path custom
            L.concat(2);                       // package pathCustom
            L.setField(-2, "path");            // package
            L.pop(1);
        } catch (Exception e) {
            status.setText("Cannot override print");
        }
    }

    String evalLua(LuaState L, String src) throws LuaException {
		L.setTop(0);
		int ok = L.LloadString(src);
		if (ok == 0) {
			L.getGlobal("debug");
			L.getField(-1, "traceback");
			L.remove(-2);
			L.insert(-2);
			ok = L.pcall(0, 0, -2);
			if (ok == 0) {
				String res = output.toString();
				output.setLength(0);
				return res;
			}
		}
		throw new LuaException(errorReason(ok) + ": " + L.toString(-1));
		//return null;

	}

	public void onClick(View view) {
		final String src = source.getText().toString();
		status.setText("");
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

	public boolean onLongClick(View view) {
		source.setText("");
		return true;
	}

	public void toast(String text){
		Message msg = msgHandler.obtainMessage();
		msg.arg1 = MSG_TOAST;
		Bundle bundle = new Bundle();
		bundle.putString("text", text);
		msg.setData(bundle);
		msgHandler.sendMessage(msg);
	}

	private static byte[] readAll(InputStream input) throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
		byte[] buffer = new byte[4096];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toByteArray();
	}

	public static String execRootCmd(String cmd) {
		String result = "";
		DataOutputStream dos = null;
		DataInputStream dis = null;

		try {
			Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
			dos = new DataOutputStream(p.getOutputStream());
			dis = new DataInputStream(p.getInputStream());

			Log.i(TAG, cmd);
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
}