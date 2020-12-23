package cn.zhanghui.demo.daily.base.thread.lock.cas;

import java.util.concurrent.Exchanger;

/**
 * @author ZhangHui
 * @version 1.0
 * @className ExchangerTest
 * @description exchanger底层是CAS操作 + unsafe的park和unpark
 *              exchanger是分单槽和多槽的情况的
 *              在没有竞争的情况下，多线程在单槽上完成数据交换，线程A进入exchange，如果单槽为空，将自身Node放入槽中并unsafe.park；线程B进入exchange，如果单槽不为空，交换数据，并唤醒线程A
 *              在有竞争的情况下，有竞争发生在线程B进入槽中发现单槽不为空，开开心心的准备交换自己的数据之前先将槽子置为空，但是这个过程失败了，说明槽中的数据已经被人取走了，只能走多槽模式
 *              在多槽模式下，会有一个index记录槽的下标，index默认为0，如果槽0有竞争，那么线程B会去槽1进行交换或者等待交换，在多次失败或者其他情况后，index会往回走，也就是往槽0靠
 *
 *              目前源码阅读遇到问题：
 *                      1、自旋的次数,spin和hash的关系？
 *                      2、多槽的长度怎么定义的，bound、seq、collides的关系？
 *                      3、index的运动轨迹是怎样的？
 * @date 2020/8/6
 */
public class ExchangerTest {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        Thread thread1 = new Thread(()->{

            String s1 = "法师权杖";

            try {
                System.out.println(Thread.currentThread().getName()+" : 我要交换我的 [ " + s1 + " ]");
                s1 = exchanger.exchange(s1);

                System.out.println(Thread.currentThread().getName()+" : 我交换到的装备是 [ " + s1 + " ]");
            } catch (InterruptedException ignored) {
            }

        },"地下道里的战士");

        Thread thread2 = new Thread(()->{

            String s2 = "战士盔甲";

            try {
                System.out.println(Thread.currentThread().getName()+" : 我要交换我的 [ " + s2 + " ]");
                s2 = exchanger.exchange(s2);

                System.out.println(Thread.currentThread().getName()+" : 我交换到的装备是 [ " + s2 + " ]");
            } catch (InterruptedException ignored) {
            }

        },"一名落魄的法师");

        thread1.start();
        thread2.start();
    }
}
