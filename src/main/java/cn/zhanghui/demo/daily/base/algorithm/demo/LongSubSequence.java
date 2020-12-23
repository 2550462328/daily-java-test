package cn.zhanghui.demo.daily.base.algorithm.demo;

/**
 * @author ZhangHui
 * @version 1.0
 * @className LongSubSequence
 * @description 设有由n个不相同的整数组成的数列, 记为:b(1)、b(2)、……、b(n)且b(i)<>b(j) (i<>j),若存在i1<i2<i3< … < ie 且有b(i1)<b(i2)< … <b(ie)则称为长度为e的不下降序列。程序要求,当原数列出之后,求出最长的不下降序列。
 * <p>
 * 例如13,7,9,16,38,24,37,18,44,19,21,22,63,15。
 * <p>
 * 例中13,16,18,19,21,22,63就是一个长度为7的不下降序列,
 * 同时也有7 ,9,16,18,19,21,22,63长度为8的不下降序列。
 * @date 2020/8/5
 */
public class LongSubSequence {

    /**
     * 顺推
     * 从i=1出发，对于i，在0~i之间找出最长的不下降序列，并记录在i的时候，最长的不下降队列最后一个元素的下标
     *
     * @param n 待分析数组
     */
    public static void getLongSequenceASC(int[] n) {
        int len = n.length;

        // 记录每个i的最长不下降序列的长度
        int[] maxLArr = new int[len];
        // 这个是记录对于i来讲，它的上一个最长的不下降序列最后一个元素的下标，核心在于在0~i上找最大的不下降序列（也就是比较maxLArr[i]）
        int[] maxIArr = new int[len];

        // 这两个记录是用来在0~i上查找最长的不下降序列，比如遍历到k，发现maxLArr[k]是最大的，那么这个k就是currMaxL,k的下标就是curreMaxI
        int currMaxL, currMaxI;

        //边界值
        maxLArr[0] = 1;

        for (int i = 1; i < len; i++) {
            currMaxL = 0;
            currMaxI = 0;
            for (int j = 0; j < i; j++) {
                // 求解核心 找到在i之前的，上一个最长的不下降序列的位置，前提是这个位置的值要小于i所在的值，不然没有意义
                if (n[j] <= n[i] && currMaxL < maxLArr[j]) {
                    currMaxL = maxLArr[j];
                    currMaxI = j;
                }
            }
            // 记录i处的最长不下降序列的长度，+1是加上i所在值
            maxLArr[i] = currMaxL + 1;
            maxIArr[i] = currMaxI;
        }

        int maxLength = 0;
        int k = 0;

        for (int i = 0; i < len; i++) {
            if (maxLength < maxLArr[i]) {
                maxLength = maxLArr[i];
                k = i;
            }
        }

        System.out.println("The longest sequence`s length is " + maxLength);

        System.out.print("And as for this seuqence the arr is [");

        while (k != 0) {
            System.out.print(n[k] + " ");
            k = maxIArr[k];
        }

        System.out.print("]");
    }

    /**
     * 逆推
     * 思路和顺路差不多
     *
     * 只是我们的i是从n~0，而j是在i~n区间查找最长不下降序列
     *
     * @param n 待分析数组
     */
    public static void getLongSequenceDESC(int[] n) {

        int len = n.length;

        int[] maxLArr = new int[len];
        int[] maxIArr = new int[len];

        maxLArr[len - 1] = 1;

        int currMaxL, currMaxI;

        for (int i = len - 2; i >= 0; i--) {
            currMaxI = 0;
            currMaxL = 0;

            for (int j = i + 1; j < len; j++) {
                if(n[j] >= n[i] && currMaxL < maxLArr[j]){
                    currMaxL = maxLArr[j];
                    currMaxI = j;
                }
            }

            maxLArr[i] = currMaxL + 1;
            maxIArr[i] = currMaxI;
        }

        int maxLength = 0;
        int k = 0;

        for (int i = 0; i < len; i++) {
            if (maxLength < maxLArr[i]) {
                maxLength = maxLArr[i];
                k = i;
            }
        }

        System.out.println("The longest sequence`s length is " + maxLength);

        System.out.print("And as for this seuqence the arr is [");

        while (k != 0) {
            System.out.print(n[k] + " ");
            k = maxIArr[k];
        }

        System.out.print("]");

    }

    public static void main(String[] args) {
        int[] n = {200, 7, 9, 16, 38, 24, 37, 18, 44, 19, 21, 22, 63, 15};
        getLongSequenceDESC(n);
    }
}
