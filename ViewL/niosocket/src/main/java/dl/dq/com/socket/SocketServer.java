package dl.dq.com.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dl.dq.com.socket.server.DispatchThread;

/**
 * @author liqing on 18-3-26.
 */

public class SocketServer {
    Selector selector ;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    public void start()throws IOException{
        // 监听指定的端口
        int port = 55533;
        ServerSocketChannel server = null;
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
        // server将一直等待连接的到来
        System.out.println("server将一直等待连接的到来");
        selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            System.out.println("111 ok ok");
            selector.select();
            System.out.println("ok ok");
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator =  keys.iterator();
            while (keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    System.out.println("accept");
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
                keyIterator.remove(); //该事件已经处理，可以丢弃
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = ssc.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("a new client connected "+clientChannel.getRemoteAddress());
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        this.readBuffer.clear();
//        readBuffer.flip();
        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();

            return;
        }

        String str = new String(readBuffer.array(), 0, numRead);
        System.out.println(str);
        socketChannel.register(selector, SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        System.out.println("write:");
        String str = new String(readBuffer.array(), 0, 9);
        System.out.println(str);
        readBuffer.flip();
        channel.write(readBuffer);
        channel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String args[]) {
        try {
            new SocketServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
