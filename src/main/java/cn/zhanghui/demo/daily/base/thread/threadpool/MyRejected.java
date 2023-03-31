package cn.zhanghui.demo.daily.base.thread.threadpool;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 简单的自定义拒绝策略
 *
 * @param null
 * @author ZhangHui
 * @date 2020/1/2
 * @return
 */
public class MyRejected implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        //这里获取到被处理的线程，继承Runnable接口，所以如果我们想获得更多的信息，就要就要写详细r.toString()，最好封装程一个json，将线程的详细信息写在里面
        //处理的时候可以调http请求返回，报告线程未执行，或者做个任务表，将拒绝的任务放在里面，或者写到日志里面
        System.out.println("自定义处理..当前被拒绝的任务为:" + r.toString());
    }
}
