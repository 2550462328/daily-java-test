package cn.zhanghui.demo.daily.base.algorithm.demo;

/**
 * 给定一个整数，写一个函数来判断它是否是 3 的幂次方。
 * <p>
 * 示例 1:
 * 输入: 27
 * 输出: true
 * <p>
 * 示例 2:
 * 输入: 0
 * 输出: false
 *
 * @author: ZhangHui
 * @date: 2020/10/20 10:04
 * @version：1.0
 */
public class IsNumberPower {

    public static void main(String[] args) {
//        System.out.println(new IsNumberPower().isPowerOfThree(0));
//        System.out.println(Integer.toHexString(10));
//        System.out.println(Integer.toBinaryString(10));
//        System.out.println(Integer.toOctalString(10));
//        System.out.println(Integer.highestOneBit(10));
//        System.out.println(Integer.lowestOneBit(10));
//        System.out.println(Integer.toString(10,3));

        System.out.println(Math.log10(2187) / Math.log10(3));
        System.out.println(Math.log(2187) / Math.log(3));
    }

    /**
     * 暴力求解法，依次对3求除，如果是3的幂次方最后会剩下3
     */
    public boolean isPowerOfThree(int n) {
        if (n == 1) return true;

        if (n % 2 == 0 || n % 3 != 0) return false;

        double d = n;
        while (d > 3) {
            d = d / 3;
        }
        return d == 3;
    }

    /**
     * 通过进制转换，将n转换成3进制，如果是3的幂次方的数用3进制表示就是 10...开头这种
     * 因此只要使用正则判断n的3进制形式就可以了
     */
    public boolean isPowerOfThree_radixexchange(int n) {
        return Integer.toString(n, 3).matches("^10*$");
    }

    /**
     * 套用公式判断，3^i = n -> i = log3(n) -> logb(n)/logb(3)
     * 因此只要logb(n) / logb(3)的结果是一个整数就可以了
     */
    public boolean isPowerOfThree_formula(int n) {
        // 基于double带来的误差，比如结果10.0 / 2的结果可能是4.99999或者5.00000001
        return (Math.log10(n) / Math.log10(3)) % 1 == 0;
        // 所以替换成这种写法，借助epsilon jdk11引入的常量
//        return (Math.log10(n)/Math.log10(3) + epsilon) % 1 <= 2* epsilon;
    }

    /**
     * 如果n是3的幂次方，3是质数，那么n的除数也只能是3的幂次方
     * 基于int的最大值2^32 -1，那么3的最大幂次方数是3^19，也就是1162261467
     * 因为只要3^19 % n == 0，那么n就是3的幂次方
     * 性能最好 时间和空间复杂度都是在常数值O(1)
     */
    public boolean isPowerOfThree_primedivide(int n) {
        return n > 0 && 1162261467 % n == 0;
    }
}
