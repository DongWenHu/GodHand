package test;

import android.util.Log;

import com.rzx.godhand.msg.AutomatorRequest;
import com.rzx.godhand.msg.AutomatorResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/7/24/024.
 */
public class AutomatorServer {
    private static AutomatorServer automatorServer = new AutomatorServer();
    private ServerSocket serverSocket = null;
    private ExecutorService executorService = null; // Thread pool
    private List<Socket> clientSockets = new ArrayList<Socket>();

    private static final String TAG = "AutomatorServer";

    private AutomatorServer(){

    }

    public static AutomatorServer getInstance(){
        return automatorServer;
    }

    public void start() throws IOException {
        if(serverSocket != null){
            Log.w(TAG, "Already a server started!");
            return;
        }

        serverSocket = new ServerSocket(12580); //  create a server and bind to 12580
        executorService = Executors.newCachedThreadPool();  // create a thread pool

        while (true) {
            Socket client = serverSocket.accept();
            clientSockets.add(client);
            executorService.execute(new Service(client));
        }
    }

    public class Service implements Runnable{
        private Socket socket;
        private ObjectInputStream in = null;
        private ObjectOutputStream out = null;
        private AutomatorResponse response = new AutomatorResponse();

        public Service(Socket socket) throws IOException {
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                Object obj = in.readObject();
                AutomatorRequest msg = (AutomatorRequest)obj;
                dealMessage(msg);
            } catch (IOException e) {
                System.out.println("断开了一个客户端链接");
                clientSockets.remove(this.socket);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                try {
                    response.setResponse(e.getMessage());
                    out.writeObject(response);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                try {
                    response.setResponse(e.getMessage());
                    out.writeObject(response);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                try {
                    response.setResponse(e.getMessage());
                    out.writeObject(response);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (InvocationTargetException e) {
                try {
                    response.setResponse(e.getMessage());
                    out.writeObject(response);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        private void dealMessage(AutomatorRequest msg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
            Class apiClass = test.AutomatorApi.class;
            Class[] argsClass = new Class[msg.getArgs().length];
            for (int i = 0, j = msg.getArgs().length; i < j; i++) {
                argsClass[i] = msg.getArgs()[i].getClass();
            }

            Method method = apiClass.getDeclaredMethod(msg.getMethod(), argsClass);
            Object result = method.invoke(null, msg.getArgs());
            out.writeObject(result);
        }
    }
}
