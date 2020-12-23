package cn.zhanghui.demo.daily.base.thread.lock.cas;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author ZhangHui
 * @version 1.0
 * @className CLHLock
 * @description 使用链表来实现自旋锁的公平性
 *              原理是每个线程进来都创建一个节点，然后监听当前链表中前一个节点lock的状态，如果lock=false表示获取到了锁
 *              每个线程进来都会获取当前的尾节点，并将自己设置为尾节点， 所以每个线程只知道自己的前置节点是谁和监听它
 * @date 2020/5/11
 */
public class CLHLock {

    static class ClhNode{
        private Boolean isLock  = true;
    }

    private volatile ClhNode tail;

    private ThreadLocal<ClhNode> threadNode = new ThreadLocal<>();

    // 用来同步的更新tail节点，尽管它是valatile
    private AtomicReferenceFieldUpdater<CLHLock, ClhNode> updater = AtomicReferenceFieldUpdater.newUpdater(CLHLock.class,ClhNode.class,"tail");

    public void lock(){
        ClhNode currentNode = new ClhNode();
        threadNode.set(currentNode);

        // preNode是当前线程节点的前置节点
        ClhNode preNode = updater.getAndSet(this,currentNode);

        // 如果preNode不为空，说明锁在被用着
        if(preNode != null){
            while(preNode.isLock){
                //DO NOTHING
            }
            preNode = null;
            return;
        }
    }

    public void unlock(){
       ClhNode currentNode = threadNode.get();

        // 如果tail节点等于node，则将tail节点更新为null，同时将node的lock状态职位false，表示当前线程释放了锁
       if(updater.compareAndSet(this,currentNode,null)){
           currentNode.isLock = false;
       }
       currentNode = null;
    }
}
