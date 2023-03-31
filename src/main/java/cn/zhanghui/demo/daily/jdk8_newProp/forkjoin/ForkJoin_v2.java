package cn.zhanghui.demo.daily.jdk8_newProp.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @ClassName: ForkJoin_v2.java
 * @Description: 这是jdk1.7 使用fork/join框架实现对数组排序
 * @author: ZhangHui
 * @date: 2019年11月6日 下午5:16:17
 */
public class ForkJoin_v2 {
    public static void parallelMergeSort(int[] list) {
        RecursiveAction mainTask = new SortTask(list);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(mainTask);
    }
}

class SortTask extends RecursiveAction {
    private static final long serialVersionUID = 1L;
    private final int THRESHOLD = 500;
    private int[] list;

    SortTask(int[] list) {
        this.list = list;
    }

    @Override
    protected void compute() {
        if (list.length < THRESHOLD)
            java.util.Arrays.sort(list);
        else {
            // Obtain the first half
            int[] firstHalf = new int[list.length / 2];
            System.arraycopy(list, 0, firstHalf, 0, list.length / 2);

            // Obtain the second half
            int secondHalfLength = list.length / 2;
            int[] secondHalf = new int[secondHalfLength];
            System.arraycopy(list, list.length / 2, secondHalf, 0, secondHalfLength);

            // Recursively sort the two halves
            invokeAll(new SortTask(firstHalf), new SortTask(secondHalf));

            // Merge firstHalf with second
            ForkJoin_v1.merge(list, firstHalf, secondHalf);
        }
    }
}