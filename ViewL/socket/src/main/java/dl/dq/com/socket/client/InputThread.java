package dl.dq.com.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import dl.dq.com.socket.model.MessageProto.Message;

/**
 * @author liqing on 18-3-27.
 */

public class InputThread implements Runnable {
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private Socket socket;
    private Message.Builder builder;
    public InputThread(Socket socket,String uID,String tID){
        this.socket = socket;

        builder = Message.newBuilder();
        builder.setContent("");
        builder.setUserID(uID);
        builder.setTargetID(tID);
    }

    private void addMessage(String message){
        builder.setContent(message);
        builder.setMessageID(System.currentTimeMillis());
        blockingQueue.add(builder);
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            builder.build().writeDelimitedTo(outputStream);
            while (true){
                addMessage(getMessage());
                Message.Builder builder = (Message.Builder) blockingQueue.take();
                builder.build().writeDelimitedTo(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("IOException");
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.print("InterruptedException");
        } finally {
            try {
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getMessage(){
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //java.io.InputStreamReader继承了Reader类
        String read = null;
        System.out.print("输入数据：");
        try {
            read = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read;
    }
}
