package cn.zhanghui.demo.daily.base.thread.lock.aqs;

import java.util.concurrent.Phaser;

/**
 * @author ZhangHui
 * @version 1.0
 * @className PhaserTest
 * @description phaser测试
 * @date 2020/8/7
 */
public class PhaserTest {

    private Phaser phaser = new MyPhaser();

    public static void main(String[] args) throws Exception {
        new PhaserTest().start();
    }

    public void start() throws Exception {
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] =new TeachProcess(i+"");
            phaser.register();
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("这就是中国式的教育~");
    }

    class TeachProcess extends Thread {

        public TeachProcess(String name){
            setName(name);
        }

        @Override
        public void run() {
            int score = Integer.valueOf(getName());
            if(score < 2){
                System.out.println(getName() + "没考上小学");
                phaser.arriveAndDeregister();
            }else {
                System.out.println(getName() + "考上小学");
                phaser.arriveAndAwaitAdvance();
                if(score < 5){
                    System.out.println(getName() + "没考上中学");
                    phaser.arriveAndDeregister();
                }else{
                    System.out.println(getName() + "考上中学");
                    phaser.arriveAndAwaitAdvance();
                    if(score < 8){
                        System.out.println(getName() + "没考上大学");
                        phaser.arriveAndDeregister();
                    }else{
                        System.out.println(getName() + "考上大学");
                        phaser.arriveAndAwaitAdvance();
                    }
                }
            }
        }
    }
}

class MyPhaser extends Phaser {
    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
        switch (phase) {
            case 0:
                primarySchool();
                return false;
            case 1:
                middleSchool();
                return false;
            case 2:
                seniorSchool();
                return true;
            default:
                return true;
        }
    }

    private void primarySchool() {
        System.out.println("总共有"+getRegisteredParties()+"人考上了小学");
    }

    private void middleSchool() {
        System.out.println("总共有"+getRegisteredParties()+"人考上了中学");
    }

    private void seniorSchool() {
        System.out.println("总共有"+getRegisteredParties()+"人考上了大学");
    }
}