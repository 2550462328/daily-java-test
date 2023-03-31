package cn.zhanghui.demo.daily.base.algorithm.dynamic;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MaxRodLengthPlus
 * @description 给你一根长度为 n 的绳子，请把绳子剪成整数长度的 m 段（m、n都是整数，n>1并且m>1），每段绳子的长度记为 k[0],k[1]...k[m-1] 。请问
 * 可能的最大乘积是多少？例如，当绳子的长度是8时，我们把它剪成长度分别为2、3、3的三段，此时得到的最大乘积是18。
 * <p>
 * 解决思路：我们经过一系列严谨的推导，假设切割长度为m的时候，求得乘积最大，使用求导之后m取3的时候是最大的。
 * <p>
 * 对于一段绳子，如果可以被3整除，就是最大值，否则需要比较不能被整除的部分
 * 譬如n=4的时候，我们可以1+ 3、2 + 2和 3 + 1
 * <p>
 * 所以对于求解长度n的绳子，核心还是比较(n-1)*3、(n-2)*2和(n-3)*1的最大值
 * @date 2020/8/4
 */
public class MaxRodLengthPlus {

    public static int getLongestRodLength(int n) {
        if (n <= 3) return n - 1;

        int[] dp = {1, 2, 3};

        for (int i = 3; i < n; i++) {
            int tempMax = Math.max(dp[2] * 1, Math.max(dp[1] * 2, dp[0] * 3));
            dp[0] = dp[1];
            dp[1] = dp[2];
            dp[2] = tempMax;
        }
        return dp[2];
    }

}
