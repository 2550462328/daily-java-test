package cn.zhanghui.demo.daily.base.singleton;

/**
 * @ClassName: SingletonTest.java
 * @Description: 单例模式在多线程下的问题和解决方法
 * 使用DLK双重检查来解决
 * @author: ZhangHui
 * @date: 2019年5月24日 上午11:08:43
 */
public class SingletonTest {

    public static void main(String[] args) {
        Thread thread1 = new Thread(new ThreadA());
        Thread thread2 = new Thread(new ThreadA());
        Thread thread3 = new Thread(new ThreadA());
        thread1.start();
        thread2.start();
        thread3.start();
    }

}

class UniqueObject {
    private static UniqueObject object;

    public static UniqueObject getInstance() {
        if (object != null) {

        } else {
            synchronized (UniqueObject.class) {
                if (object == null) object = new UniqueObject();
            }
        }
        return object;
    }
}

class ThreadA implements Runnable {
    @Override
    public void run() {
        UniqueObject object = UniqueObject.getInstance();
        System.out.println(object.hashCode());
    }
}