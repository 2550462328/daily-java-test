package cn.zhanghui.demo.daily.base.algorithm.demo;

/**
 * 给定一个包含 [0, n] 中 n 个数的数组 nums ，找出 [0, n] 这个范围内没有出现在数组中的那个数。
 * <p>
 * 进阶：
 * <p>
 * 你能否实现线性时间复杂度、仅使用额外常数空间的算法解决此问题?
 * <p>
 * 示例 1：
 * <p>
 * 输入：nums = [3,0,1]
 * 输出：2
 * 解释：n = 3，因为有 3 个数字，所以所有的数字都在范围 [0,3] 内。2 是丢失的数字，因为它没有出现在 nums 中。
 *
 * @author: ZhangHui
 * @date: 2020/11/4 18:45
 * @version：1.0
 */
public class LostNumber {

    public static void main(String[] args) {
        int[] nums = {3, 0, 1};
        System.out.println(new LostNumber().missingNumber_advanced(nums));
    }

    /**
     * 计数
     */
    public int missingNumber(int[] nums) {

        int size = nums.length;

        int[] baseNums = new int[size + 1];

        for (int i : nums) {
            baseNums[i]++;
        }

        for (int j = 0; j < baseNums.length; j++) {
            if (baseNums[j] == 0) {
                return j;
            }
        }
        return 0;
    }

    /**
     * 取巧
     */
    public int missingNumber_advanced(int[] nums) {

        int size = nums.length;
        int sum = (size * (size + 1)) / 2;

        int missSum = 0;

        for (int i : nums) {
            missSum += i;
        }

        if (missSum == sum) {
            return 0;
        } else {
            return sum - missSum;
        }
    }
}
