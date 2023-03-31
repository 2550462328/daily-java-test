package cn.zhanghui.demo.daily.base.algorithm.demo;


import java.util.Scanner;

/**
 * @author ZhangHui
 * @version 1.0
 * @className OrderShortest
 * @description 2110年美团外卖火星第3000号配送站点有26名骑手，分别以大写字母A-Z命名，因此可以称呼这些骑手为黄家骑士特工A，黄家骑士特工B…黄家骑士特工Z
 * 某美团黑珍珠餐厅的外卖流水线上会顺序产出一组包裹，美团配送调度引擎已经将包裹分配到骑手，并在包裹上粘贴好骑手名称，如RETTEBTAE代表一组流水线包裹共9个，同时分配给了名字为A B E R T的5名骑手。
 * 请在不打乱流水线产出顺序的情况下，把这组包裹划分为尽可能多的片段，同一个骑手只会出现在其中的一个片段，返回一个表示每个包裹片段的长度的列表。
 * @date 2020/7/24
 */
public class OrderShortest {

    /**
     * 使用递归求解
     *
     * @param target
     */
    public static void getShortesOrder_recursive(String target) {
        if (target == null || target.isEmpty()) {
            return;
        }

        int len = target.length();
        char first = target.charAt(0);
        int end, temp = 1;

        if ((end = target.lastIndexOf(first)) != 0 && end != len - 1) {
            while (temp < end) {
                char inner = target.charAt(temp);
                end = Math.max(end, target.lastIndexOf(inner));
                temp++;
            }
            System.out.print((end + 1) + " ");
            getShortesOrder_recursive(target.substring(end + 1, len));
        } else if (end == len - 1) {
            System.out.print(len + " ");
        } else {
            System.out.print(1 + " ");
            getShortesOrder_recursive(target.substring(1, len));
        }
    }

    /**
     * 通过滑轮解决
     *
     * @param target
     */
    public static void getShortesOrder_wheel(String target) {

        // 这里采用滑轮的思想，j是滑轮，pre是上一次滑轮停留的位置，i是介于pre~j之间的值，至于为什么会有i，看下面解释
        int i = 0, j = 0, len = target.length();

        while (j < len) {
            int pre = j;
            char first = target.charAt(pre);
            int tail = target.lastIndexOf(first);
            i = j + 1;
            j = tail;
            // 这里解释为什么要有i，因为pre~j中间的值可能在后面一段可能j之后是存在的，但是题目的要求是必须一个字母只能出现在一段中，所以我们需要遍历pre~j之间的值进行排查，于是就有了i
            // 解决方法就是尽可能小把j往后挪一挪，包装pre~j之间的值在后面是没有重复出现的，也是这段代码的精彩之处
            while (i < j) {
                char temp = target.charAt(i);
                j = Math.max(j, target.lastIndexOf(temp));
                i++;
            }
            j++;
            System.out.print((j - pre) + " ");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String target = scanner.next();

        getShortesOrder_recursive(target);
    }


}
