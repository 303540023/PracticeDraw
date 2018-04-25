package dl.dq.com.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.LockSupport;

import dl.dq.com.socket.model.MessageProto.Message;

/**
 * @author liqing on 18-3-27.
 */

public class InputThread implements Runnable {
    private BlockingQueue blockingQueue = new LinkedBlockingQueue();
    private Socket socket;
    public InputThread(Socket socket,String uID,String tID){
        this.socket = socket;
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        final int sleep_time=0;
        try {

            outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
                writer.print("H");
                LockSupport.parkNanos(sleep_time);
                writer.print("e");
                LockSupport.parkNanos(sleep_time);
                writer.print("l");
                LockSupport.parkNanos(sleep_time);
                writer.print("l");
                LockSupport.parkNanos(sleep_time);
                writer.print("o");
                LockSupport.parkNanos(sleep_time);
                writer.print("!");
                LockSupport.parkNanos(sleep_time);
                writer.println();
                writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("IOException");
        } finally {
            try {
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
