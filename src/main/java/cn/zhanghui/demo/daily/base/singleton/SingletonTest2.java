package cn.zhanghui.demo.daily.base.singleton;

/**
 * @ClassName: SingletonTest.java
 * @Description: 单例模式在多线程下的问题和解决方法
 * 使用静态内部类来解决
 * @author: ZhangHui
 * @date: 2019年5月24日 上午11:20:43
 */
public class SingletonTest2 {

    public static void main(String[] args) {
        Thread thread1 = new Thread(new ThreadA());
        Thread thread2 = new Thread(new ThreadA());
        Thread thread3 = new Thread(new ThreadA());
        thread1.start();
        thread2.start();
        thread3.start();
    }
}

class UniqueObject2 {

    private static class staticObject {
        private static UniqueObject2 object = new UniqueObject2();
    }

    public static UniqueObject2 getInstance() {
        return staticObject.object;
    }
}

class ThreadB implements Runnable {
    @Override
    public void run() {
        UniqueObject2 object = UniqueObject2.getInstance();
        System.out.println(object.hashCode());
    }
}