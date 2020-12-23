package cn.zhanghui.demo.daily.base.collection.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindHasRepeatedElement
 * @description 给定一个整数数组，判断是否存在重复元素。
 * 如果任意一值在数组中出现至少两次，函数返回 true 。如果数组中每个元素都不相同，则返回 false 。
 * @date 2020/9/1
 */
public class FindHasRepeatedElement {

    public static void main(String[] args) {
        FindHasRepeatedElement findHasRepeatedElement = new FindHasRepeatedElement();

        int[] nums = {1,1,2,3};

        System.out.println(findHasRepeatedElement.containsDuplicateHashSet(nums));
    }

    /**
     * NotWorked
     * 暴力求解，超出时间限制
     */
    public boolean containsDuplicateViolent(int[] nums) {
        int len;
        if(nums == null || (len = nums.length) == 0){
            return false;
        }

        for(int i = 0; i < len - 1; i++){
            for(int j = i + 1; j < len; j++){
                if(nums[i] == nums[j]){
                    return true;
                }
            }
        }

        return false;
    }

    /** 效率最高
     * 对原数组进行排序，这样相同的元素就会相邻
     */
    public boolean containsDuplicateSort(int[] nums) {
        int len;
        if(nums == null || (len = nums.length) == 0){
            return false;
        }

        Arrays.sort(nums);

        for(int i = 0; i < len - 1; i++){
            if(nums[i] == nums[i+1]){
                return true;
            }
        }
        return false;
    }

    /**
     * 使用一个不能有重复元素的集合去装数组中的元素，然后比较集合和数组的大小就可以知道有没有重复元素
     */
    public boolean containsDuplicateHashSet(int[] nums) {
        int len;
        if(nums == null || (len = nums.length) == 0){
            return false;
        }

        Set<Integer> numsSet = new HashSet<>();

        int i = 0;

        while(i < len && numsSet.size() == i){
            numsSet.add(nums[i++]);
        }

        return numsSet.size() != len;
    }

    /**
     * NotWorked
     * 使用一个[0-9]的数组集合来装，任一个下标的元素值 > 1代表出现了重复
     * 仅针对数组元素值限制在0~9之间的
     */
    public boolean containsDuplicateArray(int[] nums) {
        int len;
        if(nums == null || (len = nums.length) == 0){
            return false;
        }

        int[] tempArray = new int[10];

        for(int i = 0; i < len; i++){
            if(++tempArray[nums[i]] > 1){
                return true;
            }
        }
        return false;
    }
}
