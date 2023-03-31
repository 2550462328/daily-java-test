package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.Scanner;
import java.util.Stack;

/**
 * @author ZhangHui
 * @version 1.0
 * @className BooleanResult
 * @description 给出一个布尔表达式的字符串，比如：true or false and false，表达式只包含true，false，and和or
 * 现在要对这个表达式进行布尔求值，计算结果为真时输出true、为假时输出false，不合法的表达时输出error（比如：true true）。
 * 表达式求值是注意and 的优先级比 or 要高，比如：true or false and false，等价于 true or (false and false)，计算结果是 true。
 * <p>
 * 输入描述:
 * 输入第一行包含布尔表达式字符串s，s只包含true、false、and、or几个单词（不会出现其它的任何单词），且单词之间用空格分隔。 (1 ≤ |s| ≤ 103).
 * <p>
 * 输出描述:
 * 输出true、false或error，true表示布尔表达式计算为真，false表示布尔表达式计算为假，error表示一个不合法的表达式。
 * <p>
 * 示例1
 * <p>
 * 输入
 * and
 * <p>
 * 输出
 * error
 * <p>
 * 示例2
 * <p>
 * 输入
 * true and false
 * <p>
 * 输出
 * false
 * <p>
 * 示例3
 * <p>
 * 输入
 * true or false and false
 * <p>
 * 输出
 * true
 * @date 2020/7/22
 */
public class BooleanResult {

    private static Stack<String> stack = new Stack();

    public String resolveBoolean(String[] target) {
        int len = target.length;

        int i = 0;
        while (i < len) {
            String str = target[i];

            if (i % 2 == 1 && !("and".equals(str) || "or".equals(str))) {
                return "error";
            }

            if (i % 2 == 0 && !("true".equals(str) || "false".equals(str))) {
                return "error";
            }

            if ("and".equals(str)) {
                String preStr = stack.pop();
                String nextStr = target[i + 1];
                boolean compareResult = compareAdd(preStr, nextStr);
                stack.push(String.valueOf(compareResult));
                i += 2;
            } else {
                stack.push(target[i]);
                i++;
            }
        }

        while (!stack.empty()) {
            if ("true".equals(stack.pop())) {
                return "true";
            }
        }
        return "false";
    }

    private boolean compareAdd(String preStr, String nextStr) {
        return Boolean.valueOf(preStr) && Boolean.valueOf(nextStr);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] target = new String[5];

        for (int i = 0; i < 5; i++) {
            target[i] = scanner.next();
        }

        System.out.println(new BooleanResult().resolveBoolean(target));
    }
}
