package cn.zhanghui.demo.daily.base.algorithm.dynamic;

/**
 * 给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
 * <p>
 * 示例:
 * <p>
 * 输入: [-2,1,-3,4,-1,2,1,-5,4]
 * 输出: 6
 * 解释: 连续子数组 [4,-1,2,1] 的和最大，为 6。
 *
 * @author: ZhangHui
 * @date: 2020/10/13 8:50
 * @version：1.0
 */
public class BigestSubSequenceSum {

    public static void main(String[] args) {
        int[] nums = {-2,1,-3,4,-1,2,1,-5,4};
        System.out.println(new BigestSubSequenceSum().maxSubArray(nums));
    }

    /**
     * 动态规划求解
     * 状态方程：f(i)=max{f(i−1)+ai,ai}
     * 核心就是比较前面的和加上ai元素与ai本身元素值比较
     */
    public int maxSubArray(int[] nums) {

        int pre = 0, maxSum = nums[0];

        for (int i : nums) {
            pre = Math.max(pre + i, i);
            maxSum = Math.max(maxSum, pre);
        }
        return maxSum;
    }

    /**
     * 从头部和尾部同时进行寻找最大值，最后的最大值要么在左边，要么在右边，要么在中间
     * @author ZhangHui
     * @date 2020/10/13
     * @param nums
     * @return int
     */
    public int maxSubArray_violent(int[] nums) {
        if (nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int l = 0, r = nums.length - 1;
        int headSum = nums[l], tailSum = nums[r];

        int prel = 0, prer = 0;

        // 分别求出左右的最大值
        while (l < r) {
            prel = Math.max(prel + nums[l], nums[l]);
            headSum = Math.max(headSum, prel);

            prer = Math.max(prer + nums[r], nums[r]);
            tailSum = Math.max(tailSum, prer);

            l++;
            r--;
        }

        int maxSum = Math.max(headSum, tailSum);

        // 要做奇偶值判断，偶数不用处理
        if (nums.length % 2 == 0) {
            maxSum = Math.max(maxSum, prel + prer);
        } else { // 奇数情况需要考虑中间的那个节点

            maxSum = Math.max(maxSum, nums[l]);
            maxSum = Math.max(maxSum, Math.max(prel + nums[l], prer + nums[l]));
            maxSum = Math.max(maxSum, prel + prer + nums[l]);
        }
        return maxSum;
    }

}
