package cn.zhanghui.demo.daily.base.algorithm.demo;

/**
 * 两个整数之间的汉明距离指的是这两个数字对应二进制位不同的位置的数目。
 * <p>
 * 给出两个整数 x 和 y，计算它们之间的汉明距离。
 * <p>
 * 注意：
 * 0 ≤ x, y < 2 ^ 31
 * <p>
 * 示例:
 * <p>
 * 输入: x = 1, y = 4
 * <p>
 * 输出: 2
 *
 * @author: ZhangHui
 * @date: 2020/10/23 8:51
 * @version：1.0
 */
public class GetHammingDistance {

    public static void main(String[] args) {
        System.out.println(new GetHammingDistance().hammingDistance(1, 4));
    }

    public int hammingDistance_violent(int x, int y) {
        int count = 0;

        for (int i = 0; i < 32; i++) {
            if ((x & 1) != (y & 1)) {
                count++;
            }
            x = x >> 1;
            y = y >> 1;
        }

        return count;
    }

    public int hammingDistance(int x, int y) {
        // 下面一段代码等同于Integer.toBitCount(x ^ y);
        int z = x ^ y;

        int count = 0;

        while (z != 0) {
            if ((z & 1) != 0) {
                count++;
            }
            z = z >> 1;
        }
        return count;
    }

    /**
     * 上述中对于z，我们是希望查找出z中有多少个位数是1的
     * 而对于上述那种解法，将z一步步向右移，判断最后一个位数是否为1，在这种情况下我们可能会依次遍历很多个0
     * 那有没有办法跳过这些0呢？
     * 我们发现，只要将z & (z - 1)的话，每次会消除z最右边的位数1
     * <p>
     * 0010 1000   z
     * &
     * 0010 0111   z-1
     * =
     * 0010 0000    消除了原先z中最右边的1
     * <p>
     * 因此我们只需要不断的将z & (z - 1)即可知道z中有几个1
     */
    public int hammingDistance_advanced(int x, int y) {
        // 下面一段代码等同于Integer.toBitCount(x ^ y);
        int z = x ^ y;

        int count = 0;

        while (z != 0) {
            z = z & (z - 1);
            count++;
        }
        return count;
    }
}
