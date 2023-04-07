//给定两个大小分别为 m 和 n 的正序（从小到大）数组 nums1 和 nums2。请你找出并返回这两个正序数组的 中位数 。 
//
// 算法的时间复杂度应该为 O(log (m+n)) 。 
//
// 
//
// 示例 1： 
//
// 
//输入：nums1 = [1,3], nums2 = [2]
//输出：2.00000
//解释：合并数组 = [1,2,3] ，中位数 2
// 
//
// 示例 2： 
//
// 
//输入：nums1 = [1,2], nums2 = [3,4]
//输出：2.50000
//解释：合并数组 = [1,2,3,4] ，中位数 (2 + 3) / 2 = 2.5
// 
//
// 
//
// 
//
// 提示： 
//
// 
// nums1.length == m 
// nums2.length == n 
// 0 <= m <= 1000 
// 0 <= n <= 1000 
// 1 <= m + n <= 2000 
// -10⁶ <= nums1[i], nums2[i] <= 10⁶ 
// 
//
// Related Topics 数组 二分查找 分治 👍 6435 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution {
    public static void main(String[] args) {
        int[] nums1 = {1, 3};
        int[] nums2 = {2, 7};
        System.out.println(new Solution().findMedianSortedArrays(nums1, nums2));
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length == 0) {
            int length = nums2.length;
            return length % 2 == 0 ? (nums2[length / 2] + nums2[length / 2 - 1]) / 2.0 : nums2[length / 2];
        }
        if (nums2.length == 0) {
            int length = nums1.length;
            return length % 2 == 0 ? (nums1[length / 2] + nums1[length / 2 - 1]) / 2.0 : nums1[length / 2];
        }

        int total = nums1.length + nums2.length;
        int idx1 = 0, idx2 = 0;
        int prev = 0, cur = 0;
        boolean isEven = total % 2 == 0;

        while (idx1 + idx2 <= total / 2) {
            prev = cur;
            if(idx1 >= nums1.length){
                cur = nums2[idx2++];
            }else if(idx2 >= nums2.length){
                cur = nums1[idx1++];
            }else if(nums1[idx1] < nums2[idx2]){
                cur = nums1[idx1++];
            }else{
                cur = nums2[idx2++];
            }
        }

        if(isEven){
            return (prev + cur) / 2.0;
        }else{
            return cur;
        }
    }
}
//leetcode submit region end(Prohibit modification and deletion)
