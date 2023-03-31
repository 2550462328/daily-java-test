package cn.zhanghui.demo.daily.base.algorithm.dynamic;

/**
 * @ClassName JumpToFloor
 * @description 已经楼梯的高度是n, 我们上楼梯一次只能上1阶和2阶，问上到n阶有多少种方式？
 * <p>
 * 解决思想：使用动态规划，因为上到n阶有两种方式，一种是n-1阶 + 1，一种n-2阶 + 2，所以核心还是求解n-1阶 + n-2阶上楼方式的和
 * @Author: ZhangHui
 * @Date: 2020/10/12
 * @Version：1.0
 */
public class JumpToFloor {

    public static void main(String[] args) {
        System.out.println(new JumpToFloor().climbStairs(45));
        System.out.println(new JumpToFloor().climbStairs_dp(45));
    }

    /**
     * 使用递归解决
     */
    public int climbStairs(int n) {
        if (n == 1 || n == 2) return n;

        return climbStairs(n - 1) + climbStairs(n - 2);
    }

    /**
     * 动态规划
     */
    public int climbStairs_dp(int n) {
        if (n == 1 || n == 2) return n;

        int[] dp = new int[n];

        dp[0] = 1;
        dp[1] = 2;

        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n - 1];
    }
}
