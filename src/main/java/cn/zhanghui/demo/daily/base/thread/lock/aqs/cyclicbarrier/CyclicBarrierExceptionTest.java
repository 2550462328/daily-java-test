package cn.zhanghui.demo.daily.base.thread.lock.aqs.cyclicbarrier;

import java.util.concurrent.*;

/**
 * @ClassName
 * @Description: 测试CyclicBarrier在遇到意外情况的时候会发生什么
 * @Author: ZhangHui
 * @Date: 2020/3/12
 * @Version：1.0
 */
public class CyclicBarrierExceptionTest {
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();

    private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(100,1000,0, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(100));

    private CyclicBarrier barrier = new CyclicBarrier(100,new LastTask());

    public static void main(String[] args){
        new CyclicBarrierExceptionTest().doBarrier();
    }

    /**
     * barrier.await();的阀口开放除了达到阀值外还可以是
     * 1. 有一个线程await超时了
     * 2. 有一个await的线程被Interrupt了，前提是确定这个线程是start的 ，主线程interrupt可不算，这个Interruped的线程会抛出InterruptException并且interrupt状态被clear
     * 3. barrier被reset了，会释放当前所有在await的线程，没有到达await的线程不算
     * 4. barrier被broken了,比如上面的reset就会breakBarrier
     * 上述情况所有线程都抛出BrokenBarrierException
     * 在finalTask中如果抛出异常所有await的线程也会抛出BrokenBarrierException，程序继续进行
     * @author ZhangHui
     * @date 2020/3/12
     * @param
     * @return void
     */
    private void doBarrier(){
            for(int i = 0 ; i < 100; i++){
                Thread thread = new Thread(()->{
                    try {
                        //返回下标，范围0~99
                        int index = barrier.await();
                        System.out.println(Thread.currentThread().getName()+"：我已到达...我是倒数第"+(index + 1)+"个");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"：去出发旅游咯...");
                },"thread"+i);
                threadPool.execute(thread);
            }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Thread threadInterrupted = new Thread(()->{
//            System.out.println(Thread.currentThread().getName()+"：我已到达...");
//            try {
//                barrier.await();
//            } catch (InterruptedException e) {
//                System.out.println("thread-Interrupted " + Thread.currentThread().isInterrupted());
//                e.printStackTrace();
//            } catch (BrokenBarrierException e) {
//                e.printStackTrace();
//            }
//            System.out.println(Thread.currentThread().getName()+"：去出发旅游咯...");
//        },"thread-Interrupted");
//        threadInterrupted.start();
//        threadPool.execute(()->{
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("我要破坏了...");
//            threadInterrupted.interrupt();
//        });
        threadPool.shutdown();
        while(true){
            if(threadPool.isTerminated()){
                System.out.println("线程池已关闭");
                break;
            }
        }
    }

}

/**
 * 执行解除屏障前的操作，当前线程由最后一个进入屏障的线程执行！
 * 
 * @author ZhangHui
 * @date 2020/3/11
 * @returns
 */
class LastTask implements Runnable {
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
//            System.out.println(1/0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "：我是导游，我来分发旅游说明书了...");
    }
}