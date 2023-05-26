package cn.zhanghui.demo.daily.leetcode.editor.cn;//给你一个按照非递减顺序排列的整数数组 nums，和一个目标值 target。请你找出给定目标值在数组中的开始位置和结束位置。
//
// 如果数组中不存在目标值 target，返回 [-1, -1]。 
//
// 你必须设计并实现时间复杂度为 O(log n) 的算法解决此问题。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [5,7,7,8,8,10], target = 8
//输出：[3,4] 
//
// 示例 2： 
//
// 
//输入：nums = [5,7,7,8,8,10], target = 6
//输出：[-1,-1] 
//
// 示例 3： 
//
// 
//输入：nums = [], target = 0
//输出：[-1,-1] 
//
// 
//
// 提示： 
//
// 
// 0 <= nums.length <= 10⁵ 
// -10⁹ <= nums[i] <= 10⁹ 
// nums 是一个非递减数组 
// -10⁹ <= target <= 10⁹ 
// 
//
// Related Topics 数组 二分查找 👍 2310 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution34 {

    /**
     * 两次折半查找 一次缩小左包围圈获取最小坐标 一次缩小右包围圈获取最大坐标
     *
     * @param nums
     * @param target
     * @return
     */
    public int[] searchRange2(int[] nums, int target) {
        int leftIdx = binarySearch(nums, target, true);
        int rightIdx = binarySearch(nums, target, false) - 1;
        if (leftIdx <= rightIdx && rightIdx < nums.length && nums[leftIdx] == target && nums[rightIdx] == target) {
            return new int[]{leftIdx, rightIdx};
        }
        return new int[]{-1, -1};
    }

    public int binarySearch(int[] nums, int target, boolean lower) {
        int left = 0, right = nums.length - 1, ans = nums.length;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (nums[mid] > target || (lower && nums[mid] >= target)) {
                right = mid - 1;
                ans = mid;
            } else {
                left = mid + 1;
            }
        }
        return ans;
    }

    /**
     * 想法是先折半查找找到一个最小范围 然后在这个最小范围做两次折半查找
     * 在某些特定场景下性能优于上面
     *
     * @param nums
     * @param target
     * @return
     */
    public int[] searchRange(int[] nums, int target) {
        int[] result = {-1, -1};
        if (nums.length < 1) return result;
        int left = 0, right = nums.length - 1;
        if (nums[left] == target && nums[right] == target) {
            result[0] = left;
            result[1] = right;
        } else if (nums[left] == target) {
            result[0] = left;
            result[1] = left;
            binarySearch(left, right, nums, target, result, 2);
        } else if (nums[right] == target) {
            result[0] = right;
            result[1] = right;
            binarySearch(left, right, nums, target, result, 1);
        } else {
            binarySearch(0, nums.length - 1, nums, target, result, 0);
        }
        return result;
    }

    private int binarySearch(int left, int right, int[] nums, int target, int[] result, int state) {
        if (right <= left + 1) return -1;
        int middle = (left + right) / 2;
        int r = -1;
        if (nums[middle] == target) {
            r = middle;
        } else if (nums[middle] > target) {
            right = middle;
        } else {
            left = middle;
        }
        if (r == -1) {
            r = binarySearch(left, right, nums, target, result, state);
        }
        if (r == -1) return r;
        if (state == 0) {
            result[0] = middle;
            result[1] = middle;

            binarySearch(left, middle, nums, target, result, 1);
            binarySearch(middle, right, nums, target, result, 2);
        } else if (state == 1) {
            result[0] = r;
            binarySearch(left, r, nums, target, result, 1);
        } else {
            result[1] = r;
            binarySearch(r, right, nums, target, result, 2);
        }
        return -1;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
