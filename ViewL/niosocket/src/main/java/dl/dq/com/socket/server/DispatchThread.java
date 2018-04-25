package dl.dq.com.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import dl.dq.com.socket.SocketServer;
import dl.dq.com.socket.model.MessageProto;
import jdk.net.Sockets;

/**
 * @author liqing on 18-3-27.
 */

public class DispatchThread implements Runnable {
    /**
     * Lock held on access to clientsMap put
     */
    private final ReentrantLock mainLock = new ReentrantLock();
    private SocketChannel mSocket;
    private Map<String,SocketChannel> clientsMap;
    private Selector selector;
    public DispatchThread(SocketChannel socket, Map map){
        mSocket = socket;
        clientsMap = map;
    }
    @Override
    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(5);
        try {
            mSocket.configureBlocking(false);
            System.out.println(String.format("accept: %s", mSocket.getRemoteAddress().toString()));
            selector = Selector.open();
            mSocket.register(selector, SelectionKey.OP_ACCEPT);
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            while (true){
                byteBuffer.clear();
                long e = System.currentTimeMillis();
                int read = mSocket.read(byteBuffer);
                if (read <0){
                    break;
                }
                long b = System.currentTimeMillis();
                System.out.println("spend:" + (e - b) + "ms"+new String(byteBuffer.array()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
