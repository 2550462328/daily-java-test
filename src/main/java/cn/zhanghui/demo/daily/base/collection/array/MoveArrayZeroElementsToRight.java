package cn.zhanghui.demo.daily.base.collection.array;

import java.util.Arrays;

/**
 * @author ZhangHui
 * @version 1.0
 * @className MoveArrayZeroElementToRight
 * @description 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
 * <p>
 * 示例:
 * <p>
 * 输入: [0,1,0,3,12]
 * 输出: [1,3,12,0,0]
 * @date 2020/9/4
 */
public class MoveArrayZeroElementsToRight {

    public static void main(String[] args) {
        MoveArrayZeroElementsToRight moveArrayZeroElementsToRight = new MoveArrayZeroElementsToRight();

        int[] nums = {0, 1, 0, 3, 12};

        moveArrayZeroElementsToRight.moveZeroesAdvanced(nums);

        System.out.println(Arrays.toString(nums));
    }

    /**
     * 使用双指针的方式去解决
     */
    public void moveZeroes(int[] nums) {

        int len = nums.length;

        if (len == 0) {
            return;
        }

        int i = 0, index = 0;

        while (i < len) {
            if (nums[i] != 0) {
                nums[index++] = nums[i];
            }
            i++;
        }
        for (int j = index; j < len; j++) {
            nums[j] = 0;
        }
    }

    /**
     * 在双指针的基础上进行细节优化
     * 原理在于可以确定双指针之间都是0 所以在i出元素赋值给index处的时候顺便将 i处赋值为0
     * 跟上面的解决区别就在于 省去了第二轮从index ~len之间 赋值零的操作
     */
    public void moveZeroesAdvanced(int[] nums) {

        int len = nums.length;

        if (len == 0) {
            return;
        }

        int i = 0, index = 0;

        while (i < len) {
            if (nums[i] != 0) {
                // 这波交换相当于将nums[index] = nums[i],nus[i] = 0
                int temp = nums[index];
                nums[index] = nums[i];
                nums[i] = temp;
                index++;
            }
            i++;
        }
    }
}
