package cn.zhanghui.demo.daily.jdk8_newProp.lambda_test;

import java.util.Arrays;
import java.util.List;

/**
 * @author: ZhangHui
 * @description: lambda在并发流操作中的应用
 * @date: 2019/6/27
 */
public class ParallelStream {
    public static void main(String[] args) {
        int[] numArr = {13, 23, 11, 2, 343, 32};
        Arrays.parallelSort(numArr);
        System.out.println(numArr[0]);

        List<String> numList = Arrays.asList("22", "32", "13", "43", "13");
        int sum = numList.parallelStream()
                .mapToInt(i -> Integer.parseInt(i))
                .sum();
        System.out.println(sum);
    }
}
