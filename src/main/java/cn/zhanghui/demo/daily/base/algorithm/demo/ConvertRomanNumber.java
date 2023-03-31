package cn.zhanghui.demo.daily.base.algorithm.demo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 罗马数字转整数罗马数字包含以下七种字符: I， V， X， L，C，D 和 M。
 *
 * 字符          数值
 * I             1
 * V             5
 * X             10
 * L             50
 * C             100
 * D             500
 * M             1000
 *
 * 例如， 罗马数字 2 写做 II ，即为两个并列的 1。12 写做 XII ，即为 X + II 。 27 写做  XXVII, 即为 XX + V + II 。
 *
 * 通常情况下，罗马数字中小的数字在大的数字的右边。但也存在特例，例如 4 不写做 IIII，而是 IV。数字 1 在数字 5 的左边，所表示的数等于大数 5 减小数 1 得到的数值 4 。同样地，数字 9 表示为 IX。这个特殊的规则只适用于以下六种情况：
 *
 * 	I 可以放在 V (5) 和 X (10) 的左边，来表示 4 和 9。
 * 	X 可以放在 L (50) 和 C (100) 的左边，来表示 40 和 90。
 * 	C 可以放在 D (500) 和 M (1000) 的左边，来表示 400 和 900。
 *
 * 示例 1:
 * 输入: "III"
 * 输出: 3
 *
 * 示例 2:
 * 输入: "MCMXCIV"
 * 输出: 1994
 * 解释: M = 1000, CM = 900, XC = 90, IV = 4
 *
 * @author: ZhangHui
 * @date: 2020/10/21 9:00
 * @version：1.0
 */
public class ConvertRomanNumber {

    private Map<Character,Integer> singleMap = new HashMap<>();
    private Map<String,Integer> doubleMap = new HashMap<>();

    {
        singleMap.put('I',1);
        singleMap.put('V',5);
        singleMap.put('X',10);
        singleMap.put('L',50);
        singleMap.put('C',100);
        singleMap.put('D',500);
        singleMap.put('M',1000);

        doubleMap.put("IV",4);
        doubleMap.put("IX",9);
        doubleMap.put("XL",40);
        doubleMap.put("XC",90);
        doubleMap.put("CD",400);
        doubleMap.put("CM",900);
    }

    public static void main(String[] args) {
        String str = "he";

        System.out.println(str.substring(0,2));
    }

    /**
     * 暴力遍历求解
     */
    public int romanToInt(String s){

        int result = 0;

        for(int i = 0; i < s.length(); i++){
            if(i != s.length() - 1 ){
                String appendString = s.substring(i,i+2);
                if(doubleMap.containsKey(appendString)) {
                    result += doubleMap.get(appendString);
                    i++;
                }else{
                    result += singleMap.get(s.charAt(i));
                }
            }else{
                result += singleMap.get(s.charAt(i));
            }
        }
        return result;
    }

    /**
     * 借助stack
     */
    public int romanToInt_stack(String s){

        int result = 0;

        LinkedList<Character> stack = new LinkedList<>();

        char[] chars = s.toCharArray();

        int i = 0,len = chars.length;

        while(i < len){
            if(!stack.isEmpty()){
                char pre = stack.poll();
                String appendString = String.valueOf(pre) + String.valueOf(chars[i]);
                if(doubleMap.containsKey(appendString)){
                    result += doubleMap.get(appendString);
                    i++;
                    continue;
                }else{
                    result += singleMap.get(pre);
                }
            }
            stack.push(chars[i]);
            i++;
        }

        while(!stack.isEmpty()){
            result += singleMap.get(stack.poll());
        }

        return result;
    }
}
