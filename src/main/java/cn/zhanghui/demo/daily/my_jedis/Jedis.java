package cn.zhanghui.demo.daily.my_jedis;

/**
 * @author ZhangHui
 * @version 1.0
 * @className Jedis
 * @description 对Client类的进一层封装，留给开发人员使用
 * @date 2020/8/20
 */
public class Jedis extends Client {

    public Jedis(String host, int port) {
        super(host, port);
    }

    @Override
    public String set(String key, String value) {
        return super.set(key, value);
    }

    @Override
    public String get(String key) {
        return super.get(key);
    }

    @Override
    public void set(String key, String value, String nx, String ex, int i) {
        super.set(key, value, nx, ex, i);
    }

    @Override
    public void append(String key, String value) {
        super.append(key, value);
    }

    @Override
    public void close() {
        super.close();
    }
}
