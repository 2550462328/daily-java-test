//给你一个由 n 个整数组成的数组 nums ，和一个目标值 target 。请你找出并返回满足下述全部条件且不重复的四元组 [nums[a], nums[
//b], nums[c], nums[d]] （若两个四元组元素一一对应，则认为两个四元组重复）： 
//
// 
// 0 <= a, b, c, d < n 
// a、b、c 和 d 互不相同 
// nums[a] + nums[b] + nums[c] + nums[d] == target 
// 
//
// 你可以按 任意顺序 返回答案 。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [1,0,-1,0,-2,2], target = 0
//输出：[[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
// 
//
// 示例 2： 
//
// 
//输入：nums = [2,2,2,2,2], target = 8
//输出：[[2,2,2,2]]
// 
//
// 
//
// 提示： 
//
// 
// 1 <= nums.length <= 200 
// -10⁹ <= nums[i] <= 10⁹ 
// -10⁹ <= target <= 10⁹ 
// 
//
// Related Topics 数组 双指针 排序 👍 1578 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution18 {

    public static void main(String[] args) {
        int[] nums = {2,2,2,2,2};
        System.out.println(new Solution18().fourSum(nums,8));
    }

    /**
     * 基于三数之和进行计算
     *
     * 有个优化点就是 我们可以提前判断当前轮次是否有必要进行
     * 比如 我的最小值是否比target大 和 我的最大值是否比target小
     *
     * @param nums
     * @param target
     * @return
     */
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums == null || nums.length < 4) {
            return result;
        }

        Arrays.sort(nums);
        int mid = 0;
        while (mid < nums.length - 3) {
            while(mid > 0 && mid < nums.length - 3 && nums[mid] == nums[mid - 1]){
                mid++;
            }
            if(mid == nums.length - 3){
                break;
            }
            if((long)nums[mid] + nums[mid+1] + nums[mid+2] + nums[mid+3] > target){
                break;
            }
            if((long)nums[mid] + nums[nums.length-1] + nums[nums.length-2] + nums[nums.length-3] < target){
                mid++;
                continue;
            }

            threeSum(mid, nums, target - nums[mid], result);
            mid++;
        }
        return result;
    }

    public void threeSum(int start, int[] nums, long target, List<List<Integer>> result) {
        int mid = start + 1;
        while (mid < nums.length - 2) {
            while(mid > start + 1 & mid < nums.length - 2 && nums[mid] == nums[mid - 1]){
                mid++;
            }
            if(mid == nums.length - 2){
                break;
            }
            if((long)nums[mid] + nums[mid+1] + nums[mid+2] > target){
                break;
            }
            if((long)nums[mid] + nums[nums.length-1] + nums[nums.length-2] < target){
                mid++;
                continue;
            }
            int left = mid + 1, right = nums.length - 1;
            while (left < right) {
                long surplus = (long)nums[left] + nums[right] + nums[mid] - target;
                if (surplus == 0) {
                    List<Integer> findOne = new ArrayList<>();
                    findOne.add(nums[mid]);
                    findOne.add(nums[left]);
                    findOne.add(nums[right]);
                    findOne.add(nums[start]);

                    result.add(findOne);

                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else if (surplus > 0) {
                    right--;
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else {
                    left++;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                }
            }
            mid++;
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)
