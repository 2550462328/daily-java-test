package cn.zhanghui.demo.daily.base.collection.array;

import java.util.Arrays;

/**
 * @author ZhangHui
 * @version 1.0
 * @className ArrayNumberAddOne
 * @description 给定一个由整数组成的非空数组所表示的非负整数，在该数的基础上加一。
 *
 * 最高位数字存放在数组的首位， 数组中每个元素只存储单个数字。
 *
 * 你可以假设除了整数 0 之外，这个整数不会以零开头。
 *
 * 示例 1:
 *
 * 输入: [1,2,3]
 * 输出: [1,2,4]
 * 解释: 输入数组表示数字 123。
 *
 * @date 2020/9/3
 */
public class ArrayNumberAddOne {

    public static void main(String[] args) {
        ArrayNumberAddOne arrayNumberAddOne = new ArrayNumberAddOne();

        int[] digits = {9};

        System.out.println(Arrays.toString(arrayNumberAddOne.plusOne(digits)));
    }

    /**
     * 使用进位标识解决
     */
    public int[] plusOne(int[] digits) {

        int len = digits.length;

        if(len == 0){
            return new int[0];
        }
        int carryFlag = 1;
        int index = len - 1;
        do{
            // 有进位 + 1
            int plusVal = (digits[index] + carryFlag) % 10;
            digits[index --] = plusVal;

            // 没有进位的情况，进位标示为0
            if(digits[index + 1] != 0){
                carryFlag = 0;
            }

            // 有进位且数组遍历到头了，扩容 + 1
            if(index < 0 && carryFlag == 1){
                  digits = Arrays.copyOf(digits,len + 1);
                  index = 0;
            }

        }while(index >= 0 && carryFlag != 0);

        return digits;
    }

    /**
     * 幽雅一点的写法
     * 其实就是套用死规则，不灵活，数值是0就产生进位，遍历到头还没有返回就扩容 + 1
     */
    public int[] plusOneGentle(int[] digits) {

        int len = digits.length;

        if(len == 0){
            return new int[0];
        }

        for(int i = len-1; i >= 0 ;i--){
            digits[i] = digits[i]++ % 10;
            if(digits[i] != 0){
                return digits;
            }
        }

        digits = new int[len + 1];
        digits[0] = 1;

        return digits;
    }
}
