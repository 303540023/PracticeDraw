package dl.dq.com.socket;

import java.io.IOException;

/**
 * @author liqing on 18-3-28.
 */

public class CMD {
    private static Runtime runtime = Runtime.getRuntime();
    public static void excuteProtoc(String fileName){
        try {
            Process process = runtime.exec("/home/liqing/work/tool/protoc/bin/protoc.sh");
            int length;
            while ((length = process.getInputStream().read()) != -1){
                byte[] bytes = new byte[length];
                process.getInputStream().read(bytes);
               System.out.print(new String(bytes,"utf-8"));
            }
            process.getInputStream().read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
