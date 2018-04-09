package dl.dq.com.socket;

import java.net.Socket;

import dl.dq.com.socket.client.InputThread;
import dl.dq.com.socket.client.OutputThread;
import dl.dq.com.socket.client.ThreadPoolManager;

/**
 * @author liqing on 18-3-26.
 */

public class Client {
    public static void main(String args[]) throws Exception {
        // 要连接的服务端IP地址和端口 //127.0.0.1 13.124.203.151
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        for (int i = 0; i < 1; i++) {
            Socket socket = new Socket(host, port);
            ThreadPoolManager.getExecutor().execute(new InputThread(socket,String.valueOf(i+1),String.valueOf(i)));
            ThreadPoolManager.getExecutor().execute(new OutputThread(socket));
            System.out.println(i);
        }
    }
}
