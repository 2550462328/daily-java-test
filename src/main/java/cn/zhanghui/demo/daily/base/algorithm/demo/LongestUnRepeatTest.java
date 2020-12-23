package cn.zhanghui.demo.daily.base.algorithm.demo;

import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author ZhangHui
 * @version 1.0
 * @className LongestUnRepeatTest
 * @description 找出一个字符串中最长的无重复的连续子串
 * @date 2020/8/18
 */
public class LongestUnRepeatTest {

    public static void getSubString(String target) {
        if (StringUtils.isEmpty(target)) {
            return;
        }

        Map<Character, Integer> tempMap = new HashMap();
        int l = 0, r = 0,max = 0;
//        int  maxl = 0, maxr = 0;
        char[] targetChars = target.toCharArray();
        int tLen = targetChars.length;

        for (; r < tLen; r++) {
            if (tempMap.containsKey(targetChars[r])) {
                if (r - l > max) {
                    max = r - l;
//                    maxl = l;
//                    maxr = r;
                }
                int lastIndex = tempMap.get(targetChars[r]);

                for (int i = l; i <= lastIndex; i++) {
                    tempMap.remove(targetChars[i]);
                }
                l = lastIndex + 1;
            }
            tempMap.put(targetChars[r], r);
        }

        System.out.println(target + "最大的无重复子串长度是" + max);
//        return target.substring(maxl, maxr);
    }

    public static void getSubString_advanced(String target) {
        if (StringUtils.isEmpty(target)) {
            return;
        }

        int[] lastIndex = new int[128];

        for(int i = 0; i < 128; i++){
            lastIndex[i] = -1;
        }

        int l = 0, r = 0,maxLen = 0;
//        int  maxl = 0, maxr = 0;
        int tLen = target.length();

        for (; r < tLen; r++) {

            char currVal = target.charAt(r);

            l = Math.max(l,lastIndex[currVal]);

            maxLen = Math.max(maxLen,r -l);

//            if(maxLen == (r -l + 1)){
//                maxl = l;
//                maxr = r;
//            }
            lastIndex[currVal] = r;
        }

        System.out.println(target + "最大的无重复子串长度是" + maxLen);
//        return target.substring(maxl, maxr);
    }

    public static void main(String[] args) {
        String target = "dasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadadadasdabcdefgsagaasdasgagrdasafasvfffffffffffffffadcsadada";
        StopWatch watch = new StopWatch();

        watch.start();
        getSubString(target);
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());

        watch.start();
        getSubString_advanced(target);
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
    }
}
