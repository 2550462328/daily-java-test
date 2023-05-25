package cn.zhanghui.demo.daily.leetcode.editor.cn;//整数数组 nums 按升序排列，数组中的值 互不相同 。
//
// 在传递给函数之前，nums 在预先未知的某个下标 k（0 <= k < nums.length）上进行了 旋转，使数组变为 [nums[k], nums[
//k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]（下标 从 0 开始 计数）。例如， [0,1,2
//,4,5,6,7] 在下标 3 处经旋转后可能变为 [4,5,6,7,0,1,2] 。 
//
// 给你 旋转后 的数组 nums 和一个整数 target ，如果 nums 中存在这个目标值 target ，则返回它的下标，否则返回 -1 。 
//
// 你必须设计一个时间复杂度为 O(log n) 的算法解决此问题。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [4,5,6,7,0,1,2], target = 0
//输出：4
// 
//
// 示例 2： 
//
// 
//输入：nums = [4,5,6,7,0,1,2], target = 3
//输出：-1 
//
// 示例 3： 
//
// 
//输入：nums = [1], target = 0
//输出：-1
// 
//
// 
//
// 提示： 
//
// 
// 1 <= nums.length <= 5000 
// -10⁴ <= nums[i] <= 10⁴ 
// nums 中的每个值都 独一无二 
// 题目数据保证 nums 在预先未知的某个下标上进行了旋转 
// -10⁴ <= target <= 10⁴ 
// 
//
// Related Topics 数组 二分查找 👍 2625 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
class Solution33 {
    public static void main(String[] args) {
        int[] nums = {5, 1, 3};
        System.out.println(new Solution33().search(nums, 1));
    }

    public int search(int[] nums, int target) {
        if (target >= nums[0]) {
            if (target == nums[0]) return 0;
            return binarySearchFromLeft(0, nums.length - 1, nums.length - 1, nums, target);
        } else {
            if (target == nums[nums.length - 1]) return nums.length - 1;
            return binarySearchFromRight(0, nums.length - 1, 0, nums, target);
        }
    }

    private int binarySearchFromLeft(int left, int right, int last, int[] nums, int target) {
        if (nums[right] == target) return right;
        if (right == left) return -1;
        if (nums[right] < nums[0] || nums[right] > target) {
            last = right;
            right = (left + right) / 2;
        } else {
            left = right;
            right = last;
        }
        return binarySearchFromLeft(left, right, last, nums, target);
    }

    private int binarySearchFromRight(int left, int right, int last, int[] nums, int target) {
        if (nums[left] == target) return left;
        if (right <= left + 1) return -1;
        if (nums[left] > nums[nums.length - 1] || nums[left] < target) {
            last = left;
            left = (left + right) / 2;
        } else {
            right = left;
            left = last;
        }
        return binarySearchFromRight(left, right, last, nums, target);
    }
}
//leetcode submit region end(Prohibit modification and deletion)
