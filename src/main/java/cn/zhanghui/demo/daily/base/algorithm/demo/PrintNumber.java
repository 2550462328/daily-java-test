package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.*;

/**
 * 写一个程序，输出从 1 到 n 数字的字符串表示。
 *
 * 1. 如果 n 是3的倍数，输出“Fizz”；
 *
 * 2. 如果 n 是5的倍数，输出“Buzz”；
 *
 * 3.如果 n 同时是3和5的倍数，输出 “FizzBuzz”。
 *
 * 试试想象规则更复杂的情况下应该怎么做？
 *
 * @author: ZhangHui
 * @date: 2020/10/19 8:48
 * @version：1.0
 */
public class PrintNumber {

    private final String THREE_MULTIPLE = "Fizz";
    private final String FIVE_MULTIPLE = "Buzz";
    private final String FIFTEEN_MULTIPLE = "FizzBuzz";

    private Map<Integer, String> map = new HashMap<>();

    {
        map.put(3, THREE_MULTIPLE);
        map.put(5, FIVE_MULTIPLE);
    }

    /**
     * 最原始，最暴力
     */
    public List<String> fizzBuzz(int n) {
        List<String> list = new LinkedList<>();

        for (int i = 1; i <= n; i++) {
            if (i % 15 == 0) {
                list.add(FIFTEEN_MULTIPLE);
            } else if (i % 3 == 0) {
                list.add(THREE_MULTIPLE);
            } else if (i % 5 == 0) {
                list.add(FIVE_MULTIPLE);
            } else {
                list.add(String.valueOf(i));
            }
        }
        return list;
    }

    /**
     * 优雅一点，在规则复杂的情况下避免套用太多的if -else
     */
    public List<String> fizzBuzz_graceful(int n) {
        List<String> list = new LinkedList<>();

        for (int i = 1; i <= n; i++) {
            String number = "";
            if (i % 3 == 0) {
                number += THREE_MULTIPLE;
            }
            if (i % 5 == 0) {
                number += FIVE_MULTIPLE;
            }
            if (Objects.equals("", number)) {
                number += i;
            }
            list.add(number);
        }
        return list;
    }

    /**
     * 在2的基础提高扩展性
     * 我们可以基于map进行扩展规则
     */
    public List<String> fizzBuzz_extension(int n) {
        List<String> list = new LinkedList<>();

        for (int i = 1; i <= n; i++) {
            String number = "";
            Iterator<Integer> iterator = map.keySet().iterator();

            while (iterator.hasNext()) {
                Integer next = iterator.next();

                if (i % next == 0) {
                    number += map.get(next);
                }
            }
            if (Objects.equals("", number)) {
                number += i;
            }
            list.add(number);
        }
        return list;
    }
}
