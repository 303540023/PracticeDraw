package dl.dq.com.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dl.dq.com.socket.server.DispatchThread;

/**
 * @author liqing on 18-3-26.
 */

public class SocketServer {
    public static void main(String args[]) {
        // 监听指定的端口
        int port = 55533;
        ServerSocket server = null;
        Map<String,Socket> map = new HashMap<String, Socket>();

        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // server将一直等待连接的到来
        System.out.println("server将一直等待连接的到来");

        //如果使用多线程，那就需要线程池，防止并发过高时创建过多线程耗尽资源
        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        while (true) {
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            threadPool.submit(new DispatchThread(socket,map));
        }
    }
}
