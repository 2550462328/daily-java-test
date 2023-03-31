package cn.zhanghui.demo.daily.base.algorithm.demo;

/**
 * @author: ZhangHui
 * @date: 2020/10/27 8:50
 * @version：1.0
 */
public class ReverseBits {

    public static void main(String[] args) {
        System.out.println(Integer.toBinaryString(43261596));
        System.out.println(new ReverseBits().reverseBits(43261596));
        System.out.println(new ReverseBits().reverseBits_advanced(43261596));
        System.out.println(new ReverseBits().reverseBits_divide(43261596));
    }

    public int reverseBits(int n) {

        char[] chs = new char[Integer.SIZE];
        for (int i = 0; i < Integer.SIZE; i++) {
            chs[i] = (char) ((n >> i & 1) + '0');
        }
        return Integer.parseUnsignedInt(new String(chs), 2);
    }

    /**
     * 取模运算
     * 与反转十进制整数使用取模除十累加的方法类似，
     * <p>
     * 十进制：ans = ans * 10 + n % 10; n = n / 10;
     * 二进制：ans = ans * 2 + n % 2; n = n / 2;
     * <p>
     * 但这里使用二进制取模运算需要考虑到溢出和前导零的问题
     */
    public int reverseBits_advanced(int n) {

        int result = 0;

        for (int i = 0; i < Integer.SIZE; i++) {
            result = (result << 1) + (n & 1);
            n >>= 1;
        }
        return result;
    }

    /**
     * 分治法
     * 对于int 32位
     * 将前16位和后16位调换 -> 16位中前8位和后9位调换 -> 8位中前4位和后4位调换 -> 4位中前2位和后2位调换 -> 2位中前一位和后一位调换
     * <p>
     * 例子如下
     * <p>
     * // 原数字43261596
     * 0000 ‭0010 1001 0100 _ 0001 1110 1001 1100‬
     * // 反转左右16位：
     * ‭ 0001 1110 1001 1100 _ 0000 0010 1001 0100‬
     * // 继续分为8位一组反转：
     * 1001 1100 0001 1110 _ 1001 0100 0000 0010
     * // 4位一组反转：
     * 1100 1001 1110 0001 _ 0100 1001 0010 0000‬
     * // 2位一组反转：
     * 0011 0110 1011 0100 _ 0001 0110 1000 0000
     * // 每两位再反转一下
     * ‭ 0011 1001 0111 1000 _ 0010 1001 0100 0000‬‬
     * // 这就是43261596反转后的结果：‭964176192
     *
     * @param n
     * @return int
     * @author ZhangHui
     * @date 2020/10/28
     */
    public int reverseBits_divide(int n) {

        n = (n >>> 16) | (n << 16);
        n = ((n & 0xff00ff00) >>> 8) | ((n & 0x00ff00ff) << 8);
        n = ((n & 0xf0f0f0f0) >>> 4) | ((n & 0x0f0f0f0f) << 4);
        n = ((n & 0xcccccccc) >>> 2) | ((n & 0x33333333) << 2);
        n = ((n & 0xaaaaaaaa) >>> 1) | ((n & 0x55555555) << 1);
        return n;
    }
}
