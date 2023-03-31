package cn.zhanghui.demo.daily.my_jedis;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Client
 * @description 客户端 给开发人员使用提供API
 * @date 2020/8/20
 */
public class Client {

    private Connection connection;

    public Client(String host, int port) {
        connection = new Connection(host, port);
    }

    public String set(String key, String value) {
        connection.sendCommand(Protocol.Command.SET, key.getBytes(), value.getBytes());
        return connection.getStatus();
    }

    public String get(String key) {
        connection.sendCommand(Protocol.Command.GET, key.getBytes());
        return connection.getStatus();
    }

    public void set(String key, String value, String nx, String ex, int i) {
        connection.sendCommand(Protocol.Command.SET, key.getBytes(), value.getBytes(), nx.getBytes(), ex.getBytes(), String.valueOf(i).getBytes());
    }

    public void append(String key, String value) {
        connection.sendCommand(Protocol.Command.APPEND, key.getBytes(), value.getBytes());
    }

    public void close() {
        connection.close();
    }
}
