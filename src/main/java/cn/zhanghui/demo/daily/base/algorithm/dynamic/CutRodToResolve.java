package cn.zhanghui.demo.daily.base.algorithm.dynamic;

/**
 * @author ZhangHui
 * @version 1.0
 * @className CutRodToResolve
 * @description 这是一个经典的切割绳子的问题，已知绳子长度n,和对应每块绳子长度(1~n)的价格，问怎么切割可以让这块绳子卖出的价格最高？
 * <p>
 * 比如绳子的长度是5，我可以切成1块 + 4块，或者2块 + 3块，价格是不一样的
 * <p>
 * 解决方案：以下分别使用递归和动态规划进行求解
 * @date 2020/8/4
 */
public class CutRodToResolve {

    /**
     * 使用递归方式解决
     * 递归方法的核心是切割length长度的绳子，然后对切割的部分根据价格求和
     * 这种方式的核心问题就是会不断的求重复解，比如第一次我切1段，剩下5段求最优解，第二次我切2段，剩下4段求最优解，但是5段求最优解肯定包含了4段求最优解，所以导致重复求解
     *
     * @param values 长度和价格对比表
     * @param length 待切割总长度
     * @return int 返回最佳切割方式的绳子价格总和
     */
    public static int cutByRecur(int[] values, int length) {

        if (length <= 0) {
            return 0;
        }
        int tempMax = -1;

        // i是每次尝试切割长度
        for (int i = 0; i < length; i++) {
            // 对首次切割后剩余长度求解（递归）
            int sum = values[i] + cutByRecur(values, length - (i + 1));
            if (sum > tempMax) {
                tempMax = sum;
            }
        }
        return tempMax;
    }

    /**
     * 这里使用动态规划的思想实现
     * 记录在不同切割长度下的最优解，这里注意i从1开始的，因为0的话也就是n =1的时候没有切割意义，所以要注意动态规划的边界问题
     * <p>
     * 这里为什么bestResolve的长度是length + 1？
     * <p>
     * 我理解的就是8块砖头，从0~8对应着9个最优解，分别是bestResolve[0]~bestResolve[8]
     *
     * @param values
     * @param length
     * @return int
     */
    public static int cutByDynamic(int[] values, int length) {
        // 对应每段长度的最优解
        int[] bestResolve = new int[length + 1];

        if (length <= 0) {
            return 0;
        }

        // 这里i是尝试切割的长度（+1）
        // j是在i长度下的游码，计算着在这段的长度下在哪里继续分割可以使分割下来的段价格最高
        for (int i = 1; i <= length; i++) {
            int tempMax = -1;
            for (int j = 0; j < i; j++) {
                int sum = values[j] + bestResolve[i - (j + 1)];
                if (sum > tempMax) {
                    tempMax = sum;
                }
            }
            bestResolve[i] = tempMax;
        }
        return bestResolve[length];
    }

    public static void main(String[] args) {
        int[] values = new int[]{3, 7, 1, 3, 9};
        int length = values.length;

        System.out.println(cutByRecur(values, length));
        System.out.println(cutByDynamic(values, length));

    }
}
