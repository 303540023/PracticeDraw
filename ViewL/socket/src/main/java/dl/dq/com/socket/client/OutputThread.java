package dl.dq.com.socket.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import dl.dq.com.socket.model.MessageProto;

/**
 * @author liqing on 18-3-27.
 */

public class OutputThread implements Runnable {
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private Socket socket;

    public OutputThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        InputStream inputStream ;
        StringBuilder sb = new StringBuilder("\n");
        try {
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            inputStream = socket.getInputStream();
            while (true) {
                MessageProto.Message message;
                if ((message = MessageProto.Message.parseDelimitedFrom(inputStream)) == null){
                    break;
                }
                sb.append(message.getUserID());
                sb.append(": ");
                sb.append(message.getContent());
                sb.append("\n\n");
                System.out.print(sb);
                System.out.print("输入数据：\n");
                sb.delete(1,sb.length());
            }
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
