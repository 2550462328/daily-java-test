package cn.zhanghui.demo.daily.base.collection.array;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindArrayTwoElementsEqualsTargetValue
 * @description 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 * <p>
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
 * <p>
 * 示例:
 * <p>
 * 给定 nums = [2, 7, 11, 15], target = 9
 * <p>
 * 因为 nums[0] + nums[1] = 2 + 7 = 9
 * 所以返回 [0, 1]
 * @date 2020/9/7
 */
public class FindArrayTwoElementsEqualsTargetValue {

    /**
     * 效率较差
     * 通过数组的双重循环解决
     */
    public int[] twoSum_recycle(int[] nums, int target) {
        int i = 0;

        if (nums.length < 2) {
            return new int[0];
        }

        while (i < nums.length) {
            int surplus = target - nums[i];

            for (int j = i; j < nums.length; j++) {
                if (nums[j] == surplus) {
                    return new int[]{i, j};
                }
            }
            i++;
        }
        return new int[0];
    }

    /**
     * 性能最好
     * 通过hash解决
     */
    public int[] twoSum_hash(int[] nums, int target) {
        int i = 0;

        if (nums.length < 2) {
            return new int[0];
        }

        Map<Integer, Integer> hashMap = new HashMap<>();

        while (i < nums.length) {
            int surplus = target - nums[i];

            if (hashMap.containsKey(surplus)) {
                return new int[]{i, hashMap.get(surplus)};
            } else {
                hashMap.put(nums[i], i);
            }
            i++;
        }
        return new int[0];
    }
}
