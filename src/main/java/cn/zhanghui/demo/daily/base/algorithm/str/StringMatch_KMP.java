package cn.zhanghui.demo.daily.base.algorithm.str;

import java.util.Arrays;

/**
 * @author ZhangHui
 * @version 1.0
 * @className StringMatch_KMP
 * @description 使用KMP算法计算出字符串string1中有没有包含字符串string2
 *              使用KMP算法的时间复杂度是o(m+n)，传统暴力求解算法复杂度是o(m(n-m))
 * @date 2020/7/29
 */
public class StringMatch_KMP {

    /**
     * 使用subString的方式判断是否匹配
     * @author ZhangHui
     * @date 2020/9/15
     * @param pattern
     * @param target
     * @return int
     */
    public static int compute_subString(String pattern, String target){
        int len1 = target.length();
        int len2 = pattern.length();
        if(len2 == 0 || pattern == null) return 0;
        if(len1==0|| len1<len2) return -1;

        for(int i =0;i<=len1-len2;i++){
            if(target.substring(i,i+len2).equals(pattern)){
                return i;
            }
        }
        return -1;

    }

    /**
     * 传统暴力回溯求解
     * @param pattern 字符串格式
     * @param target 待匹配字符串
     * @return int 当匹配的时候返回匹配点 否则返回 -1
     */
    public static int violent_compute(String pattern, String target){

        int pLen = pattern.length();
        int tLen = target.length();

        if(tLen < pLen){
            return  -1;
        }

        // i是target上的游标 j是pattern上的游标 k是匹配进度条
        int i = 0, j = 0, k = 0;

        while(k <= (tLen - pLen)){
            if(target.charAt(i) == pattern.charAt(j)){
                if(++j == pLen){
                    return k;
                }
                i++;
            }else {
                i = ++k;
                j = 0;
            }
        }
        return -1;
    }


    /**
     * 使用KMP算法求解
     * @param pattern 字符串格式
     * @param target 待匹配字符串
     * @return int 当匹配的时候返回匹配点 否则返回 -1
     */
    public static int kmp_compute(String pattern, String target){
        int pLen = pattern.length();
        int tLen = target.length();

        if(pLen == 0){
            return 0;
        }

        if(tLen < pLen){
            return  -1;
        }

        // i是target上的游标 j是pattern上的游标 k是匹配进度条
        int i = 0, j = 0, k = 0;

        int[] nextIndexs = get_nextIndex(pattern);

        while(k <= (tLen - pLen)){
            if(target.charAt(i) == pattern.charAt(j)){
                if(++j == pLen){
                    return k;
                }
                i++;
            }else {
                if(j != 0){
                    k = k + (j - nextIndexs[j]);
                }else{
                    k = k +1;
                }
                i = k;
                j = 0;
            }
        }
        return -1;
    }

    /**
     * 获取next数组
     * @param pattern
     * @return int[]
     */
    private static int[] get_nextIndex(String pattern){
        char[] pChars = pattern.toCharArray();

        int i = 1, j = 0,pLen = pChars.length;

        if(pLen < 2){
            return new int[]{};
        }
        int[] nextIndexs = new int[pLen];

        nextIndexs[1] = 0;

        while(i + 1 < pLen){
            if(j ==0 || pChars[i] == pChars[j]) {
                if(pChars[i+1] == pChars[j+1]){
                    nextIndexs[++i] = nextIndexs[++j];
                }else {
                    nextIndexs[++i] = ++j;
                }
            }else{
                j = nextIndexs[j];
            }
        }
        return nextIndexs;
    }


    public static void main(String[] args) {
        String target = "aaa";
        String pattern = "aaa";

//        System.out.println(violent_compute(pattern,target));

        System.out.println(Arrays.toString(get_nextIndex(pattern)));

        System.out.println(kmp_compute(pattern,target));
    }

}
