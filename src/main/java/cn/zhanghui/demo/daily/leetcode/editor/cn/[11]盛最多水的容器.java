//给定一个长度为 n 的整数数组 height 。有 n 条垂线，第 i 条线的两个端点是 (i, 0) 和 (i, height[i]) 。 
//
// 找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。 
//
// 返回容器可以储存的最大水量。 
//
// 说明：你不能倾斜容器。 
//
// 
//
// 示例 1： 
//
// 
//
// 
//输入：[1,8,6,2,5,4,8,3,7]
//输出：49 
//解释：图中垂直线代表输入数组 [1,8,6,2,5,4,8,3,7]。在此情况下，容器能够容纳水（表示为蓝色部分）的最大值为 49。 
//
// 示例 2： 
//
// 
//输入：height = [1,1]
//输出：1
// 
//
// 
//
// 提示： 
//
// 
// n == height.length 
// 2 <= n <= 10⁵ 
// 0 <= height[i] <= 10⁴ 
// 
//
// Related Topics 贪心 数组 双指针 👍 4251 👎 0
package cn.zhanghui.demo.daily.leetcode.editor.cn;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution11 {
    /**
     * 左右摇晃法 随着x轴越来越小 要寻找的y轴得越来越大 才有必要进行计算体积
     * @param height
     * @return
     */
    public int maxArea(int[] height) {
        int max = 0, min = 0;
        int xi = 0, yi = height.length - 1;

        while (yi > xi) {
            if (height[xi] < height[yi]) {
                if(height[xi] > min){
                    min = height[xi];
                    max = Math.max((yi - xi) * height[xi], max);
                }
                xi++;
            } else {
                if (height[yi] > min) {
                    min = height[yi];
                    max = Math.max((yi - xi) * height[yi], max);
                }
                yi--;
            }
        }
        return max;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
