package cn.zhanghui.demo.daily.base.collection.array;

/**
 * 给定一个未排序的数组，判断这个数组中是否存在长度为 3 的递增子序列。
 * <p>
 * 数学表达式如下:
 * <p>
 * 如果存在这样的 i, j, k,  且满足 0 ≤ i < j < k ≤ n-1，
 * 使得 arr[i] < arr[j] < arr[k] ，返回 true ; 否则返回 false 。
 * <p>
 * 说明: 要求算法的时间复杂度为 O(n)，空间复杂度为 O(1) 。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [1,2,3,4,5]
 * 输出: true
 *
 * @author: ZhangHui
 * @date: 2020/11/20 10:05
 * @version：1.0
 */
public class HasSubSequenceASC {

    public static void main(String[] args) {
        int[] nums = {1,2,-10,-8,-7};
        System.out.println(new HasSubSequenceASC().increasingTriplet(nums));
    }

    /**
     * 使用双指针
     * 核心思路是分别两个指针从左到右和从右到左 寻找小值和大值
     * 最后遍历中间节点，只要找到值大于小值且小于大值即可
     */
    public boolean increasingTriplet(int[] nums) {

        if(nums.length < 3){
            return false;
        }
        // 遍历的指针
        int i = 0, k = nums.length - 1;
        // 记录小值和大值，这里不一定是最小值和最大值
        int min = nums[i], max = nums[k];
        // 记录小值和大值的下标
        int min_index = i, max_index = k;

        while (min_index < max_index && k >= 0 && i < nums.length) {
            // 寻找小值
            if (nums[i] <= min) {
                min = nums[i];
                min_index = i;
                i++;
                continue;
            }

            // 寻找大值
            if (nums[k] >= max) {
                if (nums[k] > min) {
                    max = nums[k];
                    max_index = k;
                }
                k--;
                continue;
            }

            // 遍历中间节点
            if (hasElementBetween(nums, min_index, max_index)) {
                return true;
            } else {
                // 尝试寻找新的小值和大值
                i++;
                k--;
            }
        }
        return false;
    }

    private boolean hasElementBetween(int[] nums, int min_index, int max_index) {
        for (int j = min_index + 1; j < max_index; j++) {
            if (nums[j] > nums[min_index] && nums[j] < nums[max_index]) {
                return true;
            }
        }
        return false;
    }

    public boolean increasingTriplet_easy(int[] nums) {
        if(nums.length < 3){
            return false;
        }
        // 保证second无论如何都可以赋一个比first大的值
        int first = nums[0], second = Integer.MAX_VALUE;

        for(int i = 0; i < nums.length; i++){
            if(nums[i] <= first){
                first = nums[i];
            }else if(nums[i] <= second){
                // 虽然second的有效值可能在first的前面 但是要知道如果second成功赋值了，说明前面必有一节是second > first的
                // 所以重点在下面的判断 nums[i] > second 因为second肯定 大于 first ，所以只要有值是大于second的就可以了
                second = nums[i];
            }else{ // 出现值大于first和second 说明是有递增子序列的
                return true;
            }
        }
        return false;
    }

}
