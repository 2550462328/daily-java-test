package cn.zhanghui.demo.daily.base.algorithm.dynamic;

/**
 * @ClassName FindMaxProfit
 * @Description: 给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。
 * <p>
 * 如果你最多只允许完成一笔交易（即买入和卖出一支股票一次），设计一个算法来计算你所能获取的最大利润。
 * <p>
 * 注意：你不能在买入股票前卖出股票。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [7,1,5,3,6,4]
 * 输出: 5
 * 解释: 在第 2 天（股票价格 = 1）的时候买入，在第 5 天（股票价格 = 6）的时候卖出，最大利润 = 6-1 = 5 。
 * 注意利润不能是 7-1 = 6, 因为卖出价格需要大于买入价格；同时，你不能在买入前卖出股票。
 * <p>
 * 示例 2:
 * <p>
 * 输入: [7,6,4,3,1]
 * 输出: 0
 * 解释: 在这种情况下, 没有交易完成, 所以最大利润为 0。
 * @Author: ZhangHui
 * @Date: 2020/10/12 13:32
 * @Version：1.0
 */
public class FindMaxProfit {

    public static void main(String[] args) {
        int[] prices = {7, 1, 5, 3, 6, 4};

        System.out.println(new FindMaxProfit().maxProfit(prices));
    }

    /**
     * 维护最低点和最高点
     * 当最低点下标大于最高点下标时，重置最高点
     *
     * @param prices
     * @return int
     * @author ZhangHui
     * @date 2020/10/12
     */
    public int maxProfit(int[] prices) {
        if (prices.length < 2) return 0;

        int min = 0, max = 0;
        int maxProfit = 0;

        for (int i = 1; i < prices.length; i++) {

            int temp_min = min, temp_max = max;

            if (prices[i] > prices[max]) max = i;
            if (prices[i] < prices[min]) min = i;

            if (min > max) {
                maxProfit = Math.max(maxProfit, prices[temp_max] - prices[temp_min]);
                // 重置最高点
                max = min = i;
            }
        }
        maxProfit = Math.max(maxProfit, prices[max] - prices[min]);
        return maxProfit;
    }

    /**
     * 优化版
     * @param prices
     * @return int
     * @author ZhangHui
     * @date 2020/10/12
     */
    public int maxProfit_advanced(int[] prices) {
        int min = 0, maxProfit = 0;

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < prices[min]) {
                min = i;
            } else if (prices[i] - prices[min] > maxProfit) {
                maxProfit = prices[i] - prices[min];
            }
        }
        return maxProfit;
    }


}
