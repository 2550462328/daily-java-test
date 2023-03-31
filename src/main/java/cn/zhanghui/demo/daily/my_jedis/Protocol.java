package cn.zhanghui.demo.daily.my_jedis;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Protocol
 * @description RESP协议 详见； https://redis.io/topics/protocol
 * @date 2020/8/20
 */
public class Protocol {

    // jedis后来将这些常量优化为byte，在os进行写出的时候对其进行char转型
    private static final String DOLLAR_BYTE = "$";
    private static final String ASTERISK_BYTE = "*";
    public static final byte PLUS_BYTE = 43;
    public static final byte MINUS_BYTE = 45;
    public static final byte COLON_BYTE = 58;
    private static final String BLANK_BYTE = "\r\n";

    public static void setCommand(OutputStream os, Protocol.Command cmd, byte[]... args) {
        // 1. 生成协议 *3 $3 SET $3 key $5 value
        StringBuffer sb = new StringBuffer();
        // 1.1 数组长度 *3
        sb.append(ASTERISK_BYTE).append(args.length + 1).append(BLANK_BYTE);
        // 1.2 命令长度 $3
        sb.append(DOLLAR_BYTE).append(cmd.name().length()).append(BLANK_BYTE);
        // 1.3 命令 SET / GET
        sb.append(cmd).append(BLANK_BYTE);

        for (byte[] arg : args) {
            // 1.4 key/value 长度
            sb.append(DOLLAR_BYTE).append(arg.length).append(BLANK_BYTE);
            // 1.5 key/value
            sb.append(new String(arg)).append(BLANK_BYTE);
        }

        try {
            os.write(sb.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义一个枚举类 存放命令
     */
    public static enum Command {
        SET, GET, KEYS, APPEND
    }

}
