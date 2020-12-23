package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author ZhangHui
 * @version 1.0
 * @className OrderDispatch
 * @description 打车派单场景, 假定有N个订单， 待分配给N个司机。每个订单在匹配司机前，会对候选司机进行打分，打分的结果保存在N*N的矩阵A， 其中Aij 代表订单i司机j匹配的分值。
 * 假定每个订单只能派给一位司机，司机只能分配到一个订单。求最终的派单结果，使得匹配的订单和司机的分值累加起来最大，并且所有订单得到分配。
 *
 * 解决思路：采用回溯的思想（循环 + 递归），对于n * n 的情况， 记录每一次遍历的顺序，计算每一次遍历顺序下的总和，返回最大值
 *
 * 核心思想还是这个在循环里递归的情况，遍历到第n层后往回调，调到n-1层，把n-1层所有情况尝试后再回调了n-2层，直到第一层
 * @date 2020/7/23
 */
public class OrderDispatch {
    private static List<Integer> lastOrder;

    private static double lastSum;

    /**
     * 功能描述
     * @param sum 当前遍历的n * n矩阵
     * @param n 矩阵的宽度
     * @param curOrder 当前的序列号
     * @return void
     */
    public static void backTrack(double[][] sum, int n, List<Integer> curOrder) {
        //遍历到n的底层了，计算当前遍历顺序下的总和
        if (curOrder.size() == n) {

            double curSum = calculate(sum, curOrder);

            if (curSum > lastSum) {
                lastSum = curSum;
                lastOrder = new ArrayList<>(curOrder);
            }
            return;
        }
        // 理论上每一层都有n种可能性，但是实际上这个订单被人抢了就不能用了，所有每一层的可能性是n--的
        // 但是我们依旧按每一层是n来算，只不过如果有重复的我们跳过这一循环即可
        // 所以循环递归的本质就是遍历每一层的可能性，直到这一层用完了，遍历到n了，再回调上一层
        for (int i = 0; i < n; i++) {
            // 第i列有没有遍历过
            if (curOrder.contains(i)) {
                continue;
            }
            // 当前列数加进去
            curOrder.add(i);
            // 回溯开始
            backTrack(sum, n, curOrder);
            // 在backTrack return 回来的时候说明到了底层了，所有列数都使用完毕，我们清除掉最后一列，这时候回调到上一层的时候会多一个列选择可能，直到上一层所有可能性遍历完了再回调到上上层
            curOrder.remove(curOrder.size() - 1);
        }
    }

    public static void main(String[] args) {
        int n;
        List<Integer> curOrder = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();

        double[][] num = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                num[i][j] = scanner.nextDouble();
            }
        }

//        for (int i = 0; i < num.length; i++) {
//            System.out.println(Arrays.toString(num[i]));
//        }

        backTrack(num, n, curOrder);

        System.out.printf("%.2f\n",lastSum);
        for(int i = 0; i < n; i++){
            System.out.println(i+1 + " " + (lastOrder.get(i) + 1));
        }
    }


    private static double calculate(double[][] num, List<Integer> order) {
        double sum = 0.0;
        for (int i = 0; i < num.length; i++) {
            sum += num[i][order.get(i)];
        }
        return sum;
    }
}
