package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 在杨辉三角中，每个数是它左上方和右上方的数的和。
 * <p>
 * 示例:
 * <p>
 * 输入: 5
 * 输出:
 * [
 * [1],
 * [1,1],
 * [1,2,1],
 * [1,3,3,1],
 * [1,4,6,4,1]
 * ]
 *
 * @author: ZhangHui
 * @date: 2020/11/3 9:30
 * @version：1.0
 */
public class PascalTriangle {

    public static void main(String[] args) {
        PascalTriangle pascalTriangle = new PascalTriangle();

        System.out.println(pascalTriangle.generate(5));
        System.out.println(pascalTriangle.generate(4));
        System.out.println(pascalTriangle.generate(3));
        System.out.println(pascalTriangle.generate(2));
        System.out.println(pascalTriangle.generate(1));
    }

    List<List<Integer>> cacheList = new ArrayList<>();

    {
        List<Integer> zeroList = new ArrayList<>(1);
        zeroList.add(1);
        cacheList.add(zeroList);
    }

    /**
     * 基于缓存和暴力的计算方式
     */
    public List<List<Integer>> generate(int numRows) {

        List<List<Integer>> resultList = new ArrayList<>();

        if (cacheList.size() >= numRows) {
            for (int i = 0; i < numRows; i++) {
                resultList.add(cacheList.get(i));
            }
        } else {
            for (int i = 0; i < cacheList.size(); i++) {
                resultList.add(cacheList.get(i));
            }
            for (int j = cacheList.size(); j < numRows; j++) {
                List<Integer> next = buildNext(resultList.get(j - 1), j + 1);
                resultList.add(next);
                cacheList.add(next);
            }
        }
        return resultList;
    }

    /**
     * 根据上一行计算出下一行
     */
    private List<Integer> buildNext(List<Integer> prev, int size) {
        List<Integer> buildList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            if (i == 0 || i == size - 1) {
                buildList.add(1);
            } else {
                buildList.add(prev.get(i - 1) + prev.get(i));
            }
        }
        return buildList;
    }

    /**
     * 找规律的计算方式
     * 下一行的值等于上一行前面加一个0 和上一行 后面加一个0 将这两行合并
     * 其实也没什么优化，一个计算下一行的不同方式而已
     */
    public List<List<Integer>> generate_rule(int numRows) {
        List<List<Integer>> res = new ArrayList<>();
        if (numRows == 0) return res;
        List<Integer> firstRow = new ArrayList<>();
        firstRow.add(1);
        res.add(new ArrayList(firstRow));
        int size = res.size();
        while (size < numRows) {
            LinkedList<Integer> first = new LinkedList<>();
            first.addFirst(0);
            LinkedList<Integer> second = new LinkedList<>();
            second.addLast(0);
            for (int x : res.get(size - 1)) {
                first.addFirst(x);
                second.addLast(x);
            }
            List<Integer> newRow = new ArrayList<>();
            for (int i = 0; i < first.size(); i++) {
                newRow.add(first.get(i) + second.get(i));
            }
            res.add(newRow);
            size++;
        }
        return res;
    }
}
