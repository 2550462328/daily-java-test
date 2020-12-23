package cn.zhanghui.demo.daily.base.collection.array;

import java.util.Arrays;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindNoRepeatArrayElement
 * @description 给定一个非空整数数组，除了某个元素只出现一次以外，其余每个元素均出现两次。找出那个只出现了一次的元素。
 * 说明：
 * 你的算法应该具有线性时间复杂度。 你可以不使用额外空间来实现吗？
 *
 * @date 2020/9/1
 */
public class FindNoRepeatArrayElement {

    public static void main(String[] args) {

        System.out.println(1 ^ 2 ^ 3 ^ 1 ^ 3);
    }

    /**
     * 对nums进行排序，这样相同的元素就会相邻
     * 对偶数对进行筛选，有不一样的情况即找出不重复元素
     * 需要注意nums长度为1以及不重复元素出现在奇数大小的数组最后面这两种特殊情况
     */
    public int singleNumberSort(int[] nums) {
        int len = nums.length;

        if(len == 1){
            return nums[0];
        }

        Arrays.sort(nums);

        for(int i = 0; i < len - 1; i = i + 2){
            if(nums[i] != nums[i+1]){
                return nums[i];
            }
            if(len % 2 == 1 && i == len -3){
                return nums[len - 1];
            }
        }

        return -1;
    }

    /**
     * 采用位运算
     * 基于 a ^ b ^ a  =  b
     * 异或运算满足交换律和结合律，即 a^b^a=b^a^a=b^(a^a)=b^0=b
     */
    public int singleNumberBit(int[] nums) {
        int len = nums.length;

        int s = 0;
        for(int i = 0; i < len; i++){
            s ^= nums[i];
        }

        return s;
    }
}
