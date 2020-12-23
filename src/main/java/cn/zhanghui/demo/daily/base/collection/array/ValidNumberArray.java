package cn.zhanghui.demo.daily.base.collection.array;


import java.util.HashMap;
import java.util.Objects;

/**
 * @author ZhangHui
 * @version 1.0
 * @className ValidNumberArray
 * @description 判断一个 9x9 的数独是否有效。只需要根据以下规则，验证已经填入的数字是否有效即可。
 * <p>
 * 数字 1-9 在每一行只能出现一次。
 * 数字 1-9 在每一列只能出现一次。
 * 数字 1-9 在每一个以粗实线分隔的 3x3 宫内只能出现一次。
 * <p>
 * 输入:
 * [
 * ["5","3",".",".","7",".",".",".","."],
 * ["6",".",".","1","9","5",".",".","."],
 * [".","9","8",".",".",".",".","6","."],
 * ["8",".",".",".","6",".",".",".","3"],
 * ["4",".",".","8",".","3",".",".","1"],
 * ["7",".",".",".","2",".",".",".","6"],
 * [".","6",".",".",".",".","2","8","."],
 * [".",".",".","4","1","9",".",".","5"],
 * [".",".",".",".","8",".",".","7","9"]
 * ]
 * 输出: true
 * <p>
 * 说明:
 * <p>
 * 一个有效的数独（部分已被填充）不一定是可解的。
 * 只需要根据以上规则，验证已经填入的数字是否有效即可。
 * 给定数独序列只包含数字 1-9 和字符 '.' 。
 * 给定数独永远是 9 x 9 形式的。
 * @date 2020/9/7
 */
public class ValidNumberArray {

    public static void main(String[] args) {
        char[][] board = {{'5', '3', '.', '.', '7', '.', '.', '.', '.'}, {'6', '.', '.', '1', '9', '5', '.', '.', '.'}, {'.', '9', '8', '.', '.', '.', '.', '6', '.'}, {'8', '.', '.', '.', '6', '.', '.', '.', '3'}, {'4', '.', '.', '8', '.', '3', '.', '.', '1'}, {'7', '.', '.', '.', '2', '.', '.', '.', '6'}, {'.', '6', '.', '.', '.', '.', '2', '8', '.'}, {'.', '.', '.', '4', '1', '9', '.', '.', '5'}, {'.', '.', '.', '.', '8', '.', '.', '7', '9'}};

        ValidNumberArray validNumberArray = new ValidNumberArray();

        System.out.println(validNumberArray.isValidSudokuViolent(board));

    }


    /**
     * 效率较差
     * 传统暴力判断（对题目中的三种情况）
     */
    public boolean isValidSudokuViolent(char[][] board) {
        ArrayMap arrayMap1 = new ArrayMap();
        ArrayMap arrayMap2 = new ArrayMap();
        ArrayMap arrayMap3 = new ArrayMap();

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (!arrayMap1.put(board[i][j])) {
                    return false;
                }
                if (!arrayMap2.put(board[j][i])) {
                    return false;
                }
                if (i % 3 == 0 && j % 3 == 0 && !isPartValid(i, j, board, arrayMap3)) {
                    return false;
                }
            }
        }

        return true;

    }

    private boolean isPartValid(int row, int column, char[][] board, ArrayMap map) {
        for (int i = row; i < row + 3; i++) {
            for (int j = column; j < column + 3; j++) {
                if (!map.put(board[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }


    class ArrayMap extends HashMap {

        int count = 0;

        Object NULL_VALUE = null;

        public boolean put(Object key) {
            if (!Objects.equals(key, '.') && containsKey(key)) {
                return false;
            }

            if (++count >= 9) {
                count = 0;
                clear();
            } else {
                put(key, NULL_VALUE);
            }
            return true;
        }
    }


    /**
     * 效率最好
     * 基于上一步骤的优化
     * 优化1：对小9格的判断不用isPartValid方法，通过推导对于i 和 j应该在第几宫 第几格
     * 第i宫，第j格对应的坐标为 (3∗(i/3)+j/3, 3∗(i%3)+j%3)
     * <p>
     * 优化2：对于元素重复出现的判断不用HashMap，改用位运算
     * 使用9位的二进制记录0~9的值，比如0 1000 0010代表 1和8已经记录过了
     * 判断x是否记录 ：将二进制右移x位和1做位与
     * 将x加入：将1左移x位 和二进制做 异或
     */
    public boolean isValidSudokuBit(char[][] board) {
        for (int i = 0; i < 9; i++) {
            // hori, veti, sqre分别表示行、列、小宫
            int hori = 0, veti = 0, sqre = 0;
            for (int j = 0; j < 9; j++) {
                // 由于传入为char，需要转换为int，减48
                int h = board[i][j] - 48;
                int v = board[j][i] - 48;
                int s = board[3 * (i / 3) + j / 3][3 * (i % 3) + j % 3] - 48;
                // "."的ASCII码为46，故小于0代表着当前符号位"."，不用讨论
                if (h > 0) {
                    hori = sodokuer(h, hori);
                }
                if (v > 0) {
                    veti = sodokuer(v, veti);
                }
                if (s > 0) {
                    sqre = sodokuer(s, sqre);
                }
                if (hori == -1 || veti == -1 || sqre == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    private int sodokuer(int n, int val) {
        return ((val >> n) & 1) == 1 ? -1 : val ^ (1 << n);
    }

}
