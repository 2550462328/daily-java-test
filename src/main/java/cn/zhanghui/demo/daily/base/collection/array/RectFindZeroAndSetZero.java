package cn.zhanghui.demo.daily.base.collection.array;

import java.util.*;

/**
 * 给定一个 m x n 的矩阵，如果一个元素为 0，则将其所在行和列的所有元素都设为 0。请使用原地算法。
 * <p>
 * 示例 1:
 * <p>
 * 输入:
 * [
 * [1,1,1],
 * [1,0,1],
 * [1,1,1]
 * ]
 * <p>
 * 输出:
 * [
 * [1,0,1],
 * [0,0,0],
 * [1,0,1]
 * ]
 * <p>
 * 进阶:
 * <p>
 * 一个直接的解决方案是使用  O(mn) 的额外空间，但这并不是一个好的解决方案。
 * 一个简单的改进方案是使用 O(m + n) 的额外空间，但这仍然不是最好的解决方案。
 * 你能想出一个常数空间的解决方案吗？
 *
 * @author: ZhangHui
 * @date: 2020/11/11 9:20
 * @version：1.0
 */
public class RectFindZeroAndSetZero {

    public static void main(String[] args) {
        int[][] matrix = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
//        new RectFindZeroAndSetZero().setZeroes_violent(matrix);
//        for(int i = 0; i < matrix.length; i++){
//            System.out.println(Arrays.toString(matrix[i]));
//        }
        new RectFindZeroAndSetZero().setZeroes_transfer(matrix);
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
    }

    /**
     * 暴力标记置零法
     * 有个缺点在于我们需要额外的空间去存储元素零的所在的行和列
     * 我们可以不使用额外空间，直接在元素所在的行首和队首置为0，一种特殊的标记方式
     * <p>
     * 还有一种思路是在双重循环的时候就进行置零操作，不过需要对零进行区分，是自带的零还是人为置零，需要一个数组去存储状态
     */
    public void setZeroes_violent(int[][] matrix) {
        Set<Integer> rowSet = new HashSet<>();
        Set<Integer> columnSet = new HashSet<>();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    rowSet.add(i);
                    columnSet.add((j));
                }
            }
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (rowSet.contains(i) || columnSet.contains(j)) {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    /**
     * 将二元数组变成一元数组进行标记置零
     */
    public void setZeroes_transfer(int[][] matrix) {

        int rowLenth = matrix[0].length;
        int len = matrix.length * rowLenth;
        int[] array = new int[len];
        List<Integer> zeroIndex = new ArrayList<>();

        // 转成一元数组
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, array, i * rowLenth, rowLenth);
        }

        for (int j = 0; j < len; j++) {
            if (array[j] == 0) {
                zeroIndex.add(j);
            }
        }

        for (int index : zeroIndex) {

            int preLen = index % rowLenth;

            int nextLen = rowLenth - preLen - 1;

            while (preLen > 0) {
                array[index - preLen] = 0;
                preLen--;
            }

            while (nextLen > 0) {
                array[index + nextLen] = 0;
                nextLen--;
            }

            int columnNextIndex = index % rowLenth;

            while (columnNextIndex < len) {
                array[columnNextIndex] = 0;
                columnNextIndex += rowLenth;
            }
        }

        // 还原成二元数组
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(array, i * rowLenth, matrix[i], 0, rowLenth);
        }
    }


}
