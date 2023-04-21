//罗马数字包含以下七种字符： I， V， X， L，C，D 和 M。 
//
// 
//字符          数值
//I             1
//V             5
//X             10
//L             50
//C             100
//D             500
//M             1000 
//
// 例如， 罗马数字 2 写做 II ，即为两个并列的 1。12 写做 XII ，即为 X + II 。 27 写做 XXVII, 即为 XX + V + 
//II 。 
//
// 通常情况下，罗马数字中小的数字在大的数字的右边。但也存在特例，例如 4 不写做 IIII，而是 IV。数字 1 在数字 5 的左边，所表示的数等于大数 5
// 减小数 1 得到的数值 4 。同样地，数字 9 表示为 IX。这个特殊的规则只适用于以下六种情况： 
//
// 
// I 可以放在 V (5) 和 X (10) 的左边，来表示 4 和 9。 
// X 可以放在 L (50) 和 C (100) 的左边，来表示 40 和 90。 
// C 可以放在 D (500) 和 M (1000) 的左边，来表示 400 和 900。 
// 
//
// 给你一个整数，将其转为罗马数字。 
//
// 
//
// 示例 1: 
//
// 
//输入: num = 3
//输出: "III" 
//
// 示例 2: 
//
// 
//输入: num = 4
//输出: "IV" 
//
// 示例 3: 
//
// 
//输入: num = 9
//输出: "IX" 
//
// 示例 4: 
//
// 
//输入: num = 58
//输出: "LVIII"
//解释: L = 50, V = 5, III = 3.
// 
//
// 示例 5: 
//
// 
//输入: num = 1994
//输出: "MCMXCIV"
//解释: M = 1000, CM = 900, XC = 90, IV = 4. 
//
// 
//
// 提示： 
//
// 
// 1 <= num <= 3999 
// 
//
// Related Topics 哈希表 数学 字符串 👍 1097 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import java.util.HashMap;
import java.util.Map;

//leetcode submit region begin(Prohibit modification and deletion)
class Solution12 {
    public static Map<Integer, String> contentMap = new HashMap<>(16);

    public static int[] arr = {1, 5, 10, 50, 100, 500, 1000};

    static {
        contentMap.put(1, "I");
        contentMap.put(5, "V");
        contentMap.put(10, "X");
        contentMap.put(50, "L");
        contentMap.put(100, "C");
        contentMap.put(500, "D");
        contentMap.put(1000, "M");
        contentMap.put(4, "IV");
        contentMap.put(9, "IX");
        contentMap.put(40, "XL");
        contentMap.put(90, "XC");
        contentMap.put(400, "CD");
        contentMap.put(900, "CM");
    }



    static String[][] c = {
            {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"},
            {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"},
            {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"},
            {"", "M", "MM", "MMM"}
    };

    /**
     * 投机取巧法 针对范围比较小的值可以这么判断 不推荐
     *
     * @param num
     * @return
     */
    public String intToRoman(int num) {
        StringBuilder roman = new StringBuilder();
        roman
                .append(c[3][num / 1000 % 10])
                .append(c[2][num / 100 % 10])
                .append(c[1][num / 10 % 10])
                .append(c[0][num % 10]);
        return roman.toString();
    }

    /**
     * 推荐  不断做减法 从 1000减到 1
     *
     * @param num
     * @return
     */
    public String intToRoman2(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] reps = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        String res = "";
        for (int i = 0; i < 13; i++) {
            while (num >= values[i]) {
                num -= values[i];
                res += reps[i];
            }
        }
        return res;
    }

    /**
     * 暴力破解法 针对每个字符进行解析
     *
     * @param num
     * @return
     */
    public String intToRoman1(int num) {

        int multi = 3;
        int plus;
        int pri = 1000;

        StringBuilder sb = new StringBuilder();

        while (num != 0) {
            plus = num / pri;
            num %= pri;
            if (plus != 0) {
                sb.append(generateStr(plus, multi, pri, contentMap, arr));
            }
            multi--;
            pri /= 10;
        }

        return sb.toString();
    }

    private String generateStr(int plus, int multi, int pri, Map<Integer, String> contentMap, int[] arr) {
        int val = pri * plus;
        StringBuilder sb = new StringBuilder();

        if (contentMap.containsKey(val)) {
            return contentMap.get(val);
        } else if (val > arr[arr.length - 1]) {
            int sur = val / arr[arr.length - 1];
            for (int i = 0; i < sur; i++) {
                sb.append(contentMap.get(arr[arr.length - 1]));
            }
        } else if (val > arr[multi * 2 + 1]) {
            sb.append(contentMap.get(arr[multi * 2 + 1]));
            int sur = (val - arr[multi * 2 + 1]) / arr[multi * 2];
            for (int i = 0; i < sur; i++) {
                sb.append(contentMap.get(arr[multi * 2]));
            }
        } else {
            int sur = val / arr[multi * 2];
            for (int i = 0; i < sur; i++) {
                sb.append(contentMap.get(arr[multi * 2]));
            }
        }
        return sb.toString();
    }
}
//leetcode submit region end(Prohibit modification and deletion)
