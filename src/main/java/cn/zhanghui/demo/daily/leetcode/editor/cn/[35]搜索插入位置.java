package cn.zhanghui.demo.daily.leetcode.editor.cn;//给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
//
// 请必须使用时间复杂度为 O(log n) 的算法。 
//
// 
//
// 示例 1: 
//
// 
//输入: nums = [1,3,5,6], target = 5
//输出: 2
// 
//
// 示例 2: 
//
// 
//输入: nums = [1,3,5,6], target = 2
//输出: 1
// 
//
// 示例 3: 
//
// 
//输入: nums = [1,3,5,6], target = 7
//输出: 4
// 
//
// 
//
// 提示: 
//
// 
// 1 <= nums.length <= 10⁴ 
// -10⁴ <= nums[i] <= 10⁴ 
// nums 为 无重复元素 的 升序 排列数组 
// -10⁴ <= target <= 10⁴ 
// 
//
// Related Topics 数组 二分查找 👍 2026 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution35 {
    public static void main(String[] args) {
        int[] nums = {1, 3, 5, 6};
        System.out.println(new Solution35().searchInsert(nums, 7));
    }

    /**
     * 折半查找法不一定要用递归等固定形式，需要根据实际情况来
     * 比如这里我每次手动给left + (right-left)/2的距离
     * 如果mid 大 就right -1
     * 如果mid 小 就left + (right-left)/2
     *
     * @param nums
     * @param target
     * @return
     */
    public int searchInsert(int[] nums, int target) {
        int n = nums.length;
        int left = 0, right = n - 1, ans = n;
        while (left <= right) {
            int mid = ((right - left) >> 1) + left;
            if (target <= nums[mid]) {
                ans = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return ans;
    }

    public int searchInsert1(int[] nums, int target) {
        int r = binarySearch(0, nums.length - 1, nums, target);
        if (nums[r] == target) {
            return r;
        }
        while (r < nums.length && nums[r] < target) {
            r++;
        }
        return r;
    }

    private int binarySearch(int left, int right, int[] nums, int target) {
        if (right <= left) return left;
        int middle = (left + right) / 2;
        if (nums[middle] == target) {
            return middle;
        } else if (nums[middle] > target) {
            right = middle;
        } else {
            left = middle + 1;
        }
        return binarySearch(left, right, nums, target);
    }
}
//leetcode submit region end(Prohibit modification and deletion)
