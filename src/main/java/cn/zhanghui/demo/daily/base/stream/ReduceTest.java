package cn.zhanghui.demo.daily.base.stream;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.common.base.Predicate;

/**
 * @ClassName: ReduceTest.java
 * @Description: 测试Stream中的reduce方法
 * @author: ZhangHui
 * @date: 2019年7月17日 下午3:09:39
 */
public class ReduceTest {
    public static void main(String[] args) {
        //使用iterator初始化无限值得Stream
        Stream<Integer> stream = Stream.iterate(1, new UnaryOperator<Integer>() {
            @Override
            public Integer apply(Integer t) {
                return t + 1;
            }

        }).limit(10);
        stream.map(i -> i + " ").forEach(i -> {
            System.out.print(i);
        });
        System.out.println();

        // 计算总值
        // 原生写法
        int sum = Stream.of(1, 2, 3, 4, 5).reduce(new BinaryOperator<Integer>() {
            @Override
            public Integer apply(Integer i1, Integer i2) {
                return i1 + i2;
            }
        }).get();
        System.out.println(sum);
        // lambda写法
        System.out.println(Stream.of(1, 2, 3, 4, 5).reduce((i1, i2) -> {
            return i1 + i2;
        }).get());

        // 求最大值
        // 原生写法
        int max = Stream.of(1, 2, 3, 4, 5).reduce(new BinaryOperator<Integer>() {
            @Override
            public Integer apply(Integer i1, Integer i2) {
                return i1 > i2 ? i1 : i2;
            }
        }).get();
        System.out.println(max);

        // lambda写法
        System.out.println(Stream.of(1, 2, 3, 4, 5).reduce((i1, i2) -> {
            return i1 > i2 ? i1 : i2;
        }).get());

        // 将String数组中值拼接，并且与初始值拼接
        // 原生写法
        System.out.println(Stream.of("wo", "ai", "ni").reduce("pcc", new BinaryOperator<String>() {
            @Override
            public String apply(String t, String u) {
                return t.concat(u);
            }
        }));

        // lambda写法
        System.out.println(Stream.of("wo", "ai", "ni").reduce("pcc", (t, u) -> t.concat(u)));

        // 非并行下 将Stream中符合条件的元素放到指定list中
        // 原生写法
        Predicate<String> predicate = str -> str.contains("a");
        Stream.of("aa", "ab", "ac", "dd").reduce(new ArrayList<String>(), new BiFunction<ArrayList<String>, String, ArrayList<String>>() {
            @Override
            public ArrayList<String> apply(ArrayList<String> t, String u) {
                if (predicate.apply(u)) {
                    t.add(u);
                }
                return t;
            }
        }, new BinaryOperator<ArrayList<String>>() {
            @Override
            public ArrayList<String> apply(ArrayList<String> t, ArrayList<String> u) {
                return t;
            }
        }).stream().forEach(System.out::println);

        // lambda写法
        Stream.of("aa", "ab", "ac", "dd").reduce(new ArrayList<String>(), (r, t) -> {
                    if (predicate.apply(t)) r.add(t);
                    return r;
                },
                (r1, r2) -> r1).stream().forEach(System.out::println);

        // 在并行情况下将Stream中符合条件的元素放到指定list中
        // 原生写法
        Stream.of("aa", "ab", "ac", "dd").parallel().reduce(new ArrayList<String>(), new BiFunction<ArrayList<String>, String, ArrayList<String>>() {
            @Override
            public ArrayList<String> apply(ArrayList<String> t, String u) {
                if (predicate.apply(u)) {
                    t.add(u);
                }
                return t;
            }
        }, new BinaryOperator<ArrayList<String>>() {
            @Override
            public ArrayList<String> apply(ArrayList<String> t, ArrayList<String> u) {
                System.out.println(t == u); //这里会返回true
                //	t.addAll(u);  //如果加上这句话会跟预期结果不一样
                return t;
            }
        }).stream().forEach(System.out::println);

        // lambda写法
        Stream.of("aa", "ab", "ac", "dd").parallel().reduce(new ArrayList<String>(), (r, t) -> {
                    if (predicate.apply(t)) r.add(t);
                    return r;
                },
                (r1, r2) -> {
                    System.out.println(r1 == r2);
                    return r1;
                }).stream().forEach(System.out::println);

        // 在并行情况下将Stream中符合条件的元素放到指定list中
        // 原生写法
        Stream.of("aa", "ab", "ac", "dd").parallel().collect(() -> new ArrayList<String>(), new BiConsumer<ArrayList<String>, String>() {
            @Override
            public void accept(ArrayList<String> t, String u) {
                if (predicate.apply(u)) {
                    t.add(u);
                }
            }
        }, new BiConsumer<ArrayList<String>, ArrayList<String>>() {
            @Override
            public void accept(ArrayList<String> t, ArrayList<String> u) {
                System.out.println(t == u); //这里会返回false
                t.addAll(u);
            }
        }).stream().forEach(System.out::println);
        // lambda写法
        Stream.of("aa", "ab", "ac", "dd").parallel().collect(() -> new ArrayList<String>(), (r, t) -> {
                    if (predicate.apply(t)) r.add(t);
                },
                (r1, r2) -> {
                    System.out.println(r1 == r2);
                    r1.addAll(r2);
                }).stream().forEach(System.out::println);
    }
}
