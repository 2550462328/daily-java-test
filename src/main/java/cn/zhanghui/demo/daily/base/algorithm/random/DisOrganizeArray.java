package cn.zhanghui.demo.daily.base.algorithm.random;

import java.util.*;

/**
 * 打乱一个没有重复元素的数组。
 *
 * 示例:
 *
 * // 以数字集合 1, 2 和 3 初始化数组。
 * int[] nums = {1,2,3};
 * Solution solution = new Solution(nums);
 *
 * // 打乱数组 [1,2,3] 并返回结果。任何 [1,2,3]的排列返回的概率应该相同。
 * solution.shuffle();
 *
 * // 重设数组到它的初始状态[1,2,3]。
 * solution.reset();
 *
 * // 随机返回数组[1,2,3]打乱后的结果。
 * solution.shuffle();
 *
 * @author: ZhangHui
 * @date: 2020/10/15 8:53
 * @version：1.0
 */
public class DisOrganizeArray {

    private int[] nums_original;

    private int[] nums_copy;

    private final Random random;

    public DisOrganizeArray(int[] nums) {
        nums_original = nums;
        nums_copy = Arrays.copyOf(nums, nums.length);
        random = new Random();
    }

    /**
     * Resets the array to its original configuration and return it.
     */
    public int[] reset() {
        nums_original = nums_copy;
        nums_copy = nums_copy.clone();
        return nums_original;
    }

    /**
     * Returns a random shuffling of the array.
     * 设计思路就是使用一个list来装nums的下标，每次都从list中随机获取一个下标 来重组nums
     * 最后获得的nums就是打乱的内容
     */
    public int[] shuffle() {
        int len = nums_original.length;

        List<Integer> numsList = new LinkedList<>();
        for (int i = 0; i < len; i++) {
            numsList.add(i);
        }

        for (int j = 0; j < len; j++) {
            nums_original[j] = nums_copy[getRandomNoRepeat(numsList)];
        }

        return nums_original;
    }

    /**
     * 从numsList中获取随机数并移除它
     * @param numsList
     * @return int
     */
    private int getRandomNoRepeat(List<Integer> numsList) {
        int index = random.nextInt(numsList.size());

        return numsList.remove(index);
    }

    /**
     * 有一种优化思路就是不需要借助List来保持随机性和不重复性
     * 我们直接遍历的时候将当前元素和后面的元素做随机调换 ,需要注意的是当前元素只能和后面的元素调换（包括自己）
     *
     * @param
     * @return int[]
     */
    public int[] shuffle_advanced() {
        int len = nums_original.length;

        for(int i = 0; i < len; i++){
            int replace_index = getRandomThanMin(i,len);
            int temp = nums_original[i];
            nums_original[i] = nums_original[replace_index];
            nums_original[replace_index] = temp;
        }
        return nums_original;
    }
    /**
     * 获取大于min的随机数
     */
    private int getRandomThanMin(int min, int max){
        return random.nextInt((max-min)) + min;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 4, 5, 6};

        DisOrganizeArray disOrganizeArray = new DisOrganizeArray(nums);

        System.out.println(Arrays.toString(disOrganizeArray.reset()));

        System.out.println(Arrays.toString(disOrganizeArray.shuffle_advanced()));

        System.out.println(Arrays.toString(disOrganizeArray.reset()));

        System.out.println(Arrays.toString(disOrganizeArray.shuffle_advanced()));

        System.out.println(Arrays.toString(disOrganizeArray.shuffle_advanced()));

        System.out.println(Arrays.toString(disOrganizeArray.shuffle_advanced()));

        System.out.println(Arrays.toString(disOrganizeArray.shuffle_advanced()));
    }
}
