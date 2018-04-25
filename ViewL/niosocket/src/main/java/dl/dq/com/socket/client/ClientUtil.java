package dl.dq.com.socket.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author liqing on 18-3-27.
 */

public class ClientUtil {
    /**
     * Created by cc
     * 生成Client Uid号
     * uuid: db30cc85-3387-4845-87bc-dc390b55b7ea 前8位 + 201508171108900(yyMMddHHMMSS)
     * 即： db30cc85201508171108900
     */
    public static String getOrderNo() {
        String orderNo = "";
        UUID uuid = UUID.randomUUID();
        String trandNo = String.valueOf((Math.random() * 9 + 1) * 1000000);
        String sdf = new SimpleDateFormat("yyyyMMddHHMMSS").format(new Date());
        orderNo = uuid.toString().substring(0, 8);
        orderNo = orderNo + sdf;
        return orderNo;
    }
}
