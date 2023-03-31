package cn.zhanghui.demo.daily.base.algorithm.dynamic;

/**
 * @author ZhangHui
 * @version 1.0
 * @className PyramidData
 * @description 从最高点到底部任意处结束的路径，使路径经过数字的和最大。每一步可以从当前点走到左下方的点也可以到达右下方的点。
 * <p>
 * 13
 * 11 8
 * 12 7 26
 * 6 14 15 8
 * 12 7 13 24 11
 * <p>
 * 比如上述最大就是13 + 8 + 26 + 15 + 24 = 86
 * <p>
 * 对于动态规划，需要注意
 * 声明数组代表的含义
 * 状态转移方程
 * 边界条件
 * @date 2020/8/4
 */
public class PyramidData {

    /**
     * 采用顺推的方式，不考虑边界情况的话，对于dp[i][j]的值就是在dp[i-1][j-1]、dp[i-1][j]、dp[i-1][j+1]中找最大值
     * <p>
     * 注意这种求解方式仅针对金字塔这种特殊形状的数据
     */
    public static int getBigestSum(int[][] n) {

        int len = n.length;

        int[][] dp = new int[len][len];

        dp[0][0] = n[0][0];

        for (int i = 1; i < len; i++) {
            for (int j = 0; j <= i; j++) {
                // j==0和j==i属于左边界和右边界
                if (j == 0) {
                    if (i == 1) {
                        dp[i][j] = dp[i - 1][j] + n[i][j];
                    } else {
                        dp[i][j] = Math.max(dp[i - 1][j + 1], dp[i - 1][j]) + n[i][j];
                    }
                } else if (j == i) {
                    dp[i][j] = dp[i - 1][j - 1] + n[i][j];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j + 1], Math.max(dp[i - 1][j - 1], dp[i - 1][j])) + n[i][j];
                }
            }
        }

        int tempMax = 0;

        for (int i = 0; i < len; i++) {
            tempMax = Math.max(dp[len - 1][i], tempMax);
        }

        for (int i = 0; i < len; i++) {
            int[] a = dp[i];
        }
        return tempMax;
    }

    public static void main(String[] args) {
        int[][] n = {{13}, {11, 8}, {12, 7, 26}, {6, 14, 15, 8}, {12, 25, 13, 24, 11}};
        System.out.println("\n" + getBigestSum(n));
    }
}
