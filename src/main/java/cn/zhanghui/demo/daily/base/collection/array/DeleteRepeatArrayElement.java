package cn.zhanghui.demo.daily.base.collection.array;

/**
 * @author ZhangHui
 * @version 1.0
 * @className DeleteRepeatArrayElement
 * @description 给定一个排序数组，你需要在 原地 删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。
 * <p>
 * 不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。
 * <p>
 * 示例 1:
 * <p>
 * 给定数组 nums = [1,1,2],
 * <p>
 * 函数应该返回新的长度 2, 并且原数组 nums 的前两个元素被修改为 1, 2。
 * <p>
 * 你不需要考虑数组中超出新长度后面的元素。
 * @date 2020/8/27
 */
public class DeleteRepeatArrayElement {

    public static void main(String[] args) {
        int[] target = {-3,-1,-1,0,2,0,0,0,2};
        int len = getNoRepeatArrayLen_twoPointer(target);

        for (int k = 0; k < len; k++) {
            System.out.print(target[k] + " ");
        }

    }

    /**
     * 使用位算法（Not_Work）
     * 核心思想利用num1 | num2 & num2  = num2
     * 但问题也在这儿，因为这个公式不是一定成立的，存在num3 & num2 = num2 （偶然事件）
     */
    public static int getNoRepeatArrayLen(int[] nums) {
        if(nums == null || nums.length == 0){
            return 0;
        }

        int numToSum = nums[0] * 29 + 1;
        int tempLen = nums.length;

        int i = 1;

        while (i < tempLen) {

            int temp = nums[i] * 29 + 1;

            if ((numToSum & temp) == temp) {
                for (int j = i; j < tempLen - 1; j++) {
                    nums[j] = nums[j + 1];
                }
                tempLen--;
            } else {
                numToSum |= temp;
                i++;
            }
        }

        return tempLen;
    }

    /**
     * 使用双指针，一个遍历数组，一个用来替换重复数组
     * 这是官方的答案，但是这种方式只能解决重复数据连续出现的场景
     */
    public static int getNoRepeatArrayLen_twoPointer(int[] nums) {
        if(nums == null || nums.length == 0){
            return 0;
        }

        int i = 0;

        for(int j = 1; j < nums.length; j++){
            if(nums[j] != nums[i]){
                nums[++i] = nums[j];
            }
        }

        return i + 1;
    }
}
