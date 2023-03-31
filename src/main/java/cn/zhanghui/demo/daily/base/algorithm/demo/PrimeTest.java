package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 统计所有小于非负整数 n 的质数的数量。
 * <p>
 * 示例 1：
 * <p>
 * 输入：n = 10
 * 输出：4
 * 解释：小于 10 的质数一共有 4 个, 它们是 2, 3, 5, 7 。
 *
 * @author: ZhangHui
 * @date: 2020/10/19 13:51
 * @version：1.0
 */
public class PrimeTest {


    public static void main(String[] args) {
        System.out.println(new PrimeTest().countPrimes_acvanced(10000));
    }

    private List<Integer> primeList = new LinkedList<>();

    /**
     * 性能较差
     * 在传统暴力法上做了优化
     * 1、基于所有自然数由质数组成这一定律，缓存质数，对质数求余
     * 2、质数除了2以外不可能是偶数
     * 3、如果一个数不是质数，那么它最多只能被它的平方值除尽，也就是判断边界只要到一个数的平方值就可以了（包括平方值）
     */
    public int countPrimes(int n) {
        int count = n > 2 ? 1 : 0;

        for (int i = 3; i < n; i++) {
            if (isPrime(i)) {
                count++;
                primeList.add(i);
            }
        }
        return count;
    }

    private boolean isPrime(int num) {

        // num & 1  == 0 等于 num % 2 == 0
        if ((num & 1) == 0) return false;
        for (int i : primeList) {
            // i*i > num判断比i > Math.sqrt(num)要快
            if (i * i > num) break;
            if (num % i == 0) return false;
        }

        return true;
    }

    /**
     * 使用厄拉多塞筛法进行求解
     * 例如当我们知道2是质数的时候，我们遍历n，将2的倍数全部置为非质数，依次到n-1
     * 耗时最短
     */
    public int countPrimes_acvanced(int n) {
        boolean[] sign = new boolean[n];

        int count = 0;

        for (int i = 2; i < n; i++) {
            if (!sign[i - 1]) {
                count++;
                for (int j = i * 2; j < n; j += i) {
                    sign[j - 1] = true;
                }
            }
        }
        return count;
    }

    /**
     * 基于方案2对内存进行优化的方案1
     * 内存是优化下来了，但是时间上去了
     */
    public int countPrimes_acvanced_memory1(int n) {

        if(n < 2) return 0;

        BitSet bitSet = new BitSet(n);

        for (int i = 2; i * i < n; i++) {
            if (!bitSet.get(i - 1)) {
                for (int j = i * 2; j < n; j += i) {
                    bitSet.set(j - 1, true);
                }
            }
        }
        return n-2-bitSet.cardinality();
    }

    /**
     * 基于方案2对内存进行优化的方案2
     * 同理，内存是优化下来了，但是时间上去了
     */
    public int countPrimes_acvanced_memory2(int n) {
        int count = 0;
        //一个 int 变量占用 32 字节
        //在C#中，提供了点阵列（BitArray）数组，用这玩意可读性一定会好于我写的代码。
        int[] signs = new int[n / 32 + 1];
        for (int i = 2; i < n; i++) {
            //将元素和需确定得数字经行按位或运算，如果值改变，说明不存在该数字（未登记该数字），则其为质数。
            //(当某个数为 2 的 n 次方时（n为自然数），其 & (n - 1) 所得值将等价于取余运算所得值)
            //*如果 x = 2^n ，则 x & (n - 1) == x % n
            //下面判断可以写成
            //if ((signs[i / size] & (1 << (i % 32))) == 0)
            if ((signs[i / 32] & (1 << (i & 31))) == 0) {
                count++;
                for (int j = i + i; j < n; j += i) {
                    //登记该数字
                    signs[j / 32] |= 1 << (j & 31);
                }
            }
        }
        return count;
    }

}
