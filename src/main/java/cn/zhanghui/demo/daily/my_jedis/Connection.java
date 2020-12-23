package cn.zhanghui.demo.daily.my_jedis;

import redis.clients.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Connect
 * @description
 *  连接类
 *  在这里进行创建连接并处理IO请求，用inputStream进行数据回显，
 *  提供OutputStream给协议层，以便让其给服务端发送命令
 * @date 2020/8/20
 */
public class Connection {
    private String host = "localhost";
    private int port = 6379;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    public Connection() {
    }

    public Connection(String host) {
        this.host = host;
    }

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void connect() {
        try {
            if (socket == null) {
                socket = new Socket(host, port);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disConnect(){
        if(socket != null){
            try {
                outputStream.flush();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(socket);
            }
        }
    }

    public void close(){
        disConnect();
    }

    public Connection sendCommand(Protocol.Command command, byte[]... args) {
        connect();
        Protocol.setCommand(outputStream, command, args);
        return this;
    }

    public String getStatus() {
        byte[] bytes = new byte[1024];

        try {
            socket.getInputStream().read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String(bytes);
    }


}
