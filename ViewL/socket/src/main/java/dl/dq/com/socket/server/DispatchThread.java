package dl.dq.com.socket.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import dl.dq.com.socket.model.MessageProto;

/**
 * @author liqing on 18-3-27.
 */

public class DispatchThread implements Runnable {
    /**
     * Lock held on access to clientsMap put
     */
    private final ReentrantLock mainLock = new ReentrantLock();
    private Socket mSocket;
    private Map<String,Socket> clientsMap;
    public DispatchThread(Socket socket, Map map){
        mSocket = socket;
        clientsMap = map;
    }
    @Override
    public void run() {
        try {
            // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
            InputStream inputStream = mSocket.getInputStream();
            MessageProto.Message message;
            if ((message = MessageProto.Message.parseDelimitedFrom(inputStream)) != null){
                clientsMap.put(message.getUserID(),mSocket);
            }

            while (true) {
                if ((message = MessageProto.Message.parseDelimitedFrom(inputStream)) == null){
                    break;
                }
                Socket socket = clientsMap.get(message.getTargetID());
                if (socket != null){
                    message.writeDelimitedTo(socket.getOutputStream());
                }
            }
            inputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
