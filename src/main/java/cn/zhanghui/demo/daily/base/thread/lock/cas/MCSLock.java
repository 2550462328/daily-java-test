package cn.zhanghui.demo.daily.base.thread.lock.cas;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MCSLock
 * @description MCSLock和CLHLock的区别在于MCSLock监听的是自身节点,它轮询的是自身的islock状态，而CLH轮询的是前置节点的islock状态
 *              当前节点保留对下一个节点的引用，当当前节点unlock的时候，置下一个节点的islock为false，所以下一个线程节点就相当于获取到了锁
 * @date 2020/5/11
 */
public class MCSLock {

    private static class MCSNode {
        private Boolean islock = true;
        private MCSNode next;
    }

    private volatile MCSNode queue;

    // 同步性的更新queue节点，尽管它是volatile的
    private AtomicReferenceFieldUpdater<MCSLock, MCSNode> updater = AtomicReferenceFieldUpdater.newUpdater(MCSLock.class,MCSNode.class,"queue");

    private ThreadLocal<MCSNode> threadNode = new ThreadLocal<>();

    public void lock(){
        MCSNode currentNode = new MCSNode();
        threadNode.set(currentNode);

        MCSNode preNode = updater.getAndSet(this,currentNode);

        if(preNode != null){
            preNode.next = currentNode;
            //这里监听的是自身的lock状态，也就是我有没有获取到锁
            while(currentNode.islock){
                //DO Nothing
            }
            preNode = null;
            return;
        }
    }


    public void unlock(){
        MCSNode currentNode = threadNode.get();
        //没有线程等待了
        if(currentNode.next == null){
            if(updater.compareAndSet(this,currentNode,null)){
                return;
            }else{ // 来了一个线程在等待了
                while(currentNode.next == null){

                }
            }
        }else{
            // 通知后置节点获取到锁了
            currentNode.next.islock = false;
            currentNode.next = null;
        }
    }

}
