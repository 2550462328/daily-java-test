//给你一个长度为 n 的整数数组 nums 和 一个目标值 target。请你从 nums 中选出三个整数，使它们的和与 target 最接近。 
//
// 返回这三个数的和。 
//
// 假定每组输入只存在恰好一个解。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [-1,2,1,-4], target = 1
//输出：2
//解释：与 target 最接近的和是 2 (-1 + 2 + 1 = 2) 。
// 
//
// 示例 2： 
//
// 
//输入：nums = [0,0,0], target = 1
//输出：0
// 
//
// 
//
// 提示： 
//
// 
// 3 <= nums.length <= 1000 
// -1000 <= nums[i] <= 1000 
// -10⁴ <= target <= 10⁴ 
// 
//
// Related Topics 数组 双指针 排序 👍 1377 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.Arrays;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution16 {

    /**
     * 在算法复杂度是 n ^ 3的情况下，使用 n ^2 排序算法先排序再计算也未尝不可
     */
    public int threeSumClosest1(int[] nums, int target) {
        if (nums == null || nums.length < 3) {
            return 0;
        }
        Arrays.sort(nums);

        int mid = 0;
        int minValue = 0;
        while (mid < nums.length - 2) {
            int left = mid + 1, right = nums.length - 1;
            int surplus = target - nums[mid];
            while (left < right) {
                int minusValue = nums[left] + nums[right] - surplus;
                if (minusValue == 0) {
                    return target;
                } else if (minusValue > 0) {
                    if (minValue == 0) {
                        minValue = minusValue;
                    } else if (minValue > 0) {
                        minValue = Math.min(minValue, minusValue);
                    } else {
                        minValue = minusValue > -minValue ? minValue : minusValue;
                    }
                    right--;
                } else {
                    if (minValue == 0) {
                        minValue = minusValue;
                    } else if (minValue < 0) {
                        minValue = Math.max(minValue, minusValue);
                    } else {
                        minValue = minusValue > -minValue ? minusValue : minValue;
                    }
                    left++;
                }
            }
            mid++;
            while (mid < nums.length - 2 && nums[mid] == nums[mid - 1]) {
                mid++;
            }
        }
        return target + minValue;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
