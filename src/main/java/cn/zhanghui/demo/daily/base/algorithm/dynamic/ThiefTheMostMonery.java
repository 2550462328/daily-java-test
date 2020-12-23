package cn.zhanghui.demo.daily.base.algorithm.dynamic;

/**
 * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
 * <p>
 * 给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额。
 * <p>
 * 示例 1：
 * <p>
 * 输入：[1,2,3,1]
 * 输出：4
 * 解释：偷窃 1 号房屋 (金额 = 1) ，然后偷窃 3 号房屋 (金额 = 3)。
 * 偷窃到的最高金额 = 1 + 3 = 4 。
 * <p>
 * 示例 2：
 * <p>
 * 输入：[2,7,9,3,1]
 * 输出：12
 * 解释：偷窃 1 号房屋 (金额 = 2), 偷窃 3 号房屋 (金额 = 9)，接着偷窃 5 号房屋 (金额 = 1)。
 * 偷窃到的最高金额 = 2 + 9 + 1 = 12 。
 *
 * @author: ZhangHui
 * @date: 2020/10/13 13:54
 * @version：1.0
 */
public class ThiefTheMostMonery {

    public static void main(String[] args) {
        int[] nums = {2, 1, 1, 2};
        System.out.println(new ThiefTheMostMonery().rob_recur(nums));
    }

    /**
     * 使用动态规划
     * 核心就是比较 dp[i - 1], dp[i - 2] + nums[i]这两个值的大小
     * 因为到达i的方式只有这两种
     *
     * @param nums
     * @return int
     * @author ZhangHui
     * @date 2020/10/13
     */
    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];

        int[] dp = new int[nums.length];

        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }
        return dp[nums.length - 1];
    }


    /**
     * 使用递归实现，一如既往的超时
     *
     * @param nums
     * @return int
     * @author ZhangHui
     * @date 2020/10/13
     */
    public int rob_recur(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        return getMoney(nums, nums.length - 1);
    }

    private int getMoney(int[] nums, int i) {
        if (i == 0) return nums[0];
        if (i == 1) return Math.max(nums[0], nums[1]);

        return Math.max(getMoney(nums, i - 1), getMoney(nums, i - 2) + nums[i]);
    }
}
