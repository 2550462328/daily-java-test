//将一个给定字符串 s 根据给定的行数 numRows ，以从上往下、从左到右进行 Z 字形排列。 
//
// 比如输入字符串为 "PAYPALISHIRING" 行数为 3 时，排列如下： 
//
// 
//P   A   H   N
//A P L S I I G
//Y   I   R 
//
// 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："PAHNAPLSIIGYIR"。 
//
// 请你实现这个将字符串进行指定行数变换的函数： 
//
// 
//string convert(string s, int numRows); 
//
// 
//
// 示例 1： 
//
// 
//输入：s = "PAYPALISHIRING", numRows = 3
//输出："PAHNAPLSIIGYIR"
// 
//
//示例 2：
//
// 
//输入：s = "PAYPALISHIRING", numRows = 4
//输出："PINALSIGYAHRPI"
//解释：
//P     I    N
//A   L S  I G
//Y A   H R
//P     I
// 
//
// 示例 3： 
//
// 
//输入：s = "A", numRows = 1
//输出："A"
// 
//
// 
//
// 提示： 
//
// 
// 1 <= s.length <= 1000 
// s 由英文字母（小写和大写）、',' 和 '.' 组成 
// 1 <= numRows <= 1000 
// 
//
// Related Topics 字符串 👍 2016 👎 0

package cn.zhanghui.demo.daily.leetcode.editor.cn;


//leetcode submit region begin(Prohibit modification and deletion)
class Solution6 {

    public String convert(String s, int numRows) {
        if (numRows == 1 || s.length() <= numRows) {
            return s;
        }
        StringBuilder[] result = new StringBuilder[numRows];
        for (int k = 0; k < result.length; k++) {
            result[k] = new StringBuilder();
        }
        int flag = -1;
        int i = 0;
        for (char c : s.toCharArray()) {
            result[i].append(c);
            if (i == 0 || i == numRows - 1) flag = -flag;
            i += flag;
        }

        for (int j = 1; j < result.length; j++) {
            result[0].append(result[j]);
        }
        return result[0].toString();
    }


    public String convert1(String s, int numRows) {

        if (numRows == 1 || s.length() <= numRows) {
            return s;
        }
        char[] chars = s.toCharArray();
        int gap = numRows * 2 - 2;
        int sLen = chars.length;
        int plus = sLen % gap > numRows ? sLen % gap - numRows + 1 : 1;
        int length = sLen / gap * (numRows - 1) + plus;

        char[][] result = new char[length][numRows];
        int column = 0, row = 0;
        int top = numRows - 1, down = 0;
        for (int i = 0; i < sLen; i++) {
            if (i >= down && i <= top) {
                result[column][row++] = chars[i];
                if (i == top) {
                    down = down + gap;
                    row -= 2;
                }
            } else if (i <= down) {
                result[++column][row--] = chars[i];
                if (i == down) {
                    top = top + gap;
                    row += 2;
                }
            }
        }

        int index = 0;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < length; j++) {
                if (result[j][i] != '\u0000') {
                    chars[index++] = result[j][i];
                }
            }
        }
        return new String(chars);
    }
}
//leetcode submit region end(Prohibit modification and deletion)
