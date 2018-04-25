package dl.dq.com.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import dl.dq.com.socket.client.InputThread;
import dl.dq.com.socket.client.OutputThread;
import dl.dq.com.socket.client.ThreadPoolManager;

/**
 * @author liqing on 18-3-26.
 */

public class Client {
    private static byte[] request = null;
    static {
        StringBuffer temp = new StringBuffer();
        temp.append("GET https://www.baidu.com:80\r\n");
        temp.append("Host: www.baidu.com:80\r\n");
        temp.append("Connection: keep-alive\r\n");
        temp.append("Cache-Control: max-age=0\r\n");
        temp.append("User-Agent: Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.47 Safari/536.11\r\n");
        temp.append("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n");
        temp.append("Accept-Encoding: gzip,deflate,sdch\r\n");
        temp.append("Accept-Language: zh-CN,zh;q=0.8\r\n");
        temp.append("Accept-Charset: GBK,utf-8;q=0.7,*;q=0.3\r\n");
        temp.append("\r\n");
        request = temp.toString().getBytes();
    }

    public static void main(String args[]) throws Exception {
        // 要连接的服务端IP地址和端口 //127.0.0.1 13.124.203.151
        String host = "127.0.0.1";
        int port = 55533;
        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        // 与服务端建立连接
        SocketChannel sc = SocketChannel.open();
        //设置为非阻塞
        sc.configureBlocking(false);
        //连接服务器地址和端口
        sc.connect(new InetSocketAddress(host, port));
        //打开选择器
        Selector selector = Selector.open();
        //注册连接服务器socket的动作
        sc.register(selector, SelectionKey.OP_CONNECT);
        final Charset charset = Charset.forName("GBK");// 创建GBK字符集
        while (true) {
            //选择一组键，其相应的通道已为 I/O 操作准备就绪。
            //此方法执行处于阻塞模式的选择操作。
            System.out.println("select...");
            selector.select();
            //返回此选择器的已选择键集。
            Set<SelectionKey> keys = selector.selectedKeys();
            System.out.println("keys=" + keys.size());
            Iterator<SelectionKey> keyIterator = keys.iterator();
            Scanner scanner = new Scanner(System.in);
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();
                // 判断此通道上是否正在进行连接操作。
                if (key.isConnectable()) {
                    sc.finishConnect();
                    sc.register(selector, SelectionKey.OP_WRITE);
                    System.out.println("server connected...");
                    break;
                } else if (key.isWritable()) { //写数据
                    System.out.print("please input message:");
                    String message = scanner.nextLine();
                    //ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes());
                    writeBuffer.clear();
                    writeBuffer.put(ByteBuffer.wrap(request));
                    //将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
                    writeBuffer.flip();
                    sc.write(writeBuffer);

                    //注册写操作,每个chanel只能注册一个操作，最后注册的一个生效
                    //如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来
                    //int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
                    //使用interest集合
                    sc.register(selector, SelectionKey.OP_READ);
//                    sc.register(selector, SelectionKey.OP_WRITE);
//                    sc.register(selector, SelectionKey.OP_READ);

                } else if (key.isReadable()){//读取数据
                    System.out.print("receive message:\n");

                    int numRead;

                    while ((numRead = sc.read(readBuffer)) != -1) {
                        if (numRead == 0) {
                            break;
                        }

                        readBuffer.flip();// flip方法在读缓冲区字节操作之前调用。
                        System.out.println(charset.decode(readBuffer));
                        // 使用Charset.decode方法将字节转换为字符串
                        readBuffer.clear();// 清空缓冲
                    }

                    //注册读操作，下一次读取
                    sc.register(selector, SelectionKey.OP_WRITE);
                }
            }
        }
        //ThreadPoolManager.getExecutor().execute(new InputThread(socket, String.valueOf(0), String.valueOf(0)));
        //ThreadPoolManager.getExecutor().execute(new OutputThread(socket));
    }
}
