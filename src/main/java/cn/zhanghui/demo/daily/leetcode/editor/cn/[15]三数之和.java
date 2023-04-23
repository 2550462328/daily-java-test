//给你一个整数数组 nums ，判断是否存在三元组 [nums[i], nums[j], nums[k]] 满足 i != j、i != k 且 j != 
//k ，同时还满足 nums[i] + nums[j] + nums[k] == 0 。请 
//
// 你返回所有和为 0 且不重复的三元组。 
//
// 注意：答案中不可以包含重复的三元组。 
//
// 
//
// 
//
// 示例 1： 
//
// 
//输入：nums = [-1,0,1,2,-1,-4]
//输出：[[-1,-1,2],[-1,0,1]]
//解释：
//nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0 。
//nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0 。
//nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0 。
//不同的三元组是 [-1,0,1] 和 [-1,-1,2] 。
//注意，输出的顺序和三元组的顺序并不重要。
// 
//
// 示例 2： 
//
// 
//输入：nums = [0,1,1]
//输出：[]
//解释：唯一可能的三元组和不为 0 。
// 
//
// 示例 3： 
//
// 
//输入：nums = [0,0,0]
//输出：[[0,0,0]]
//解释：唯一可能的三元组和为 0 。
// 
//
// 
//
// 提示： 
//
// 
// 3 <= nums.length <= 3000 
// -10⁵ <= nums[i] <= 10⁵ 
// 
//
// Related Topics 数组 双指针 排序 👍 5890 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.*;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {

    public static void main(String[] args) {
        int[] nums = {0, 0, 0};
        System.out.println(new Solution().threeSum(nums));
    }

    public List<List<Integer>> threeSum(int[] nums) {
        int n = nums.length;
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        // 枚举 a
        for (int first = 0; first < n; ++first) {
            // 需要和上一次枚举的数不相同
            if (first > 0 && nums[first] == nums[first - 1]) {
                continue;
            }
            // c 对应的指针初始指向数组的最右端
            int third = n - 1;
            int target = -nums[first];
            // 枚举 b
            for (int second = first + 1; second < n; ++second) {
                // 需要和上一次枚举的数不相同
                if (second > first + 1 && nums[second] == nums[second - 1]) {
                    continue;
                }
                // 需要保证 b 的指针在 c 的指针的左侧
                while (second < third && nums[second] + nums[third] > target) {
                    --third;
                }
                // 如果指针重合，随着 b 后续的增加
                // 就不会有满足 a+b+c=0 并且 b<c 的 c 了，可以退出循环
                if (second == third) {
                    break;
                }
                if (nums[second] + nums[third] == target) {
                    List<Integer> list = new ArrayList<Integer>();
                    list.add(nums[first]);
                    list.add(nums[second]);
                    list.add(nums[third]);
                    ans.add(list);
                }
            }
        }
        return ans;
    }

    /**
     * 在算法复杂度是 n ^ 3的情况下，使用 n ^2 排序算法先排序再计算也未尝不可
     *
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum1(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        if (nums == null || nums.length < 3) {
            return result;
        }

        Arrays.sort(nums);

        if (nums[0] > 0) {
            return result;
        }

        int mid = 0;

        while (mid < nums.length - 2) {
            int left = mid + 1, right = nums.length - 1;
            int surplus = -nums[mid];
            while (left < right) {
                if (nums[left] + nums[right] == surplus) {
                    int temp1 = nums[left];
                    int temp2 = nums[right];
                    while (left < right && nums[left] == temp1) {
                        left++;
                    }
                    while (right > left && nums[right] == temp2) {
                        right--;
                    }
                    List<Integer> findOne = new ArrayList<>();
                    findOne.add(nums[mid]);
                    findOne.add(temp1);
                    findOne.add(temp2);

                    result.add(findOne);
                } else if (nums[right] + nums[left] > surplus) {
                    int temp = nums[right];
                    while (right > left && nums[right] == temp) {
                        right--;
                    }
                } else {
                    int temp = nums[left];
                    while (left < right && nums[left] == temp) {
                        left++;
                    }
                }
            }
            int temp = nums[mid];
            while (mid < nums.length - 2 && nums[mid] == temp) {
                mid++;
            }
        }
        return result;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
