package cn.zhanghui.demo.daily.base.collection.array;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindMoreConsistentNumber
 * @description 给定一个数组，它的第 i 个元素是一支给定股票第 i 天的价格。
 * <p>
 * 设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）。
 * <p>
 * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
 * <p>
 * 示例 1:
 * <p>
 * 输入: [7,1,5,3,6,4]
 * 输出: 7
 * 解释: 在第 2 天（股票价格 = 1）的时候买入，在第 3 天（股票价格 = 5）的时候卖出, 这笔交易所能获得利润 = 5-1 = 4 。
 * 随后，在第 4 天（股票价格 = 3）的时候买入，在第 5 天（股票价格 = 6）的时候卖出, 这笔交易所能获得利润 = 6-3 = 3 。
 * @date 2020/8/28
 */
public class FindMoreConsistentNumber {

    public static void main(String[] args) {
        int[] prices = {7,6,4,3,1};

        System.out.println(maxProfit(prices));
    }

    /**
     * 最简单的思想，因为我们在买入和卖出之间必然是一段上升曲线，既然这样，有什么必要去找最高点和最低点呢？
     * 只要下一个比前一个数值大，就可以算为利润，因为这一段必然在上升曲线的一段
     */
    public int maxProfit_easy(int[] prices) {
        int maxprofit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1])
                maxprofit += prices[i] - prices[i - 1];
        }
        return maxprofit;
    }

    /**
     * 这个是上面解法的复杂型
     * 它面向解决最低点和最高点的问题
     */
    public static int maxProfit(int[] prices) {

        if (prices == null || prices.length == 0) {
            return 0;
        }

        // 总利润
        int sumProfit = 0;

        // m是最高点，n是最低点
        int clen = 0, m = 0, n = 0;

        int plen = prices.length;

        for (int i = 1; i < plen; i++) {
            if (prices[i] > prices[i - 1]) {
                if(i == plen -1){
                    sumProfit += (prices[i] - prices[n]);
                }
                clen++;
            }else if(clen == 0){
                n = i;
            }else{
                sumProfit += (prices[m] - prices[n]);
                n = i;
                clen = 0;
            }
            m = i;
        }
        return sumProfit;
    }
}
