package cn.zhanghui.demo.daily.my_jedis;

/**
 * 连接池契约
 * @author ZhangHui
 * @date 2020/8/20
 */
public interface Pool<T> {
    /**
     * 线程池初始化
     * @param maxSize 最大连接数
     * @param maxIdle 最长等待时间
     */
    void init(int maxSize, long maxIdleTimeMills);

    /**
     * 获取jedis连接
     * @return cn.zhanghui.demo.daily.my_jedis.Jedis 返回jedis客户端
     */
    Jedis getResource() throws Exception;

    /**
     * 释放连接
     */
    void release(T t);
}
