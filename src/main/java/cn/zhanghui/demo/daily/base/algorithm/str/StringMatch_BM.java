package cn.zhanghui.demo.daily.base.algorithm.str;

import org.springframework.util.StopWatch;

import java.util.Arrays;
import static cn.zhanghui.demo.daily.base.algorithm.str.StringMatch_KMP.*;
/**
 * @author ZhangHui
 * @version 1.0
 * @className StringMatch_BM
 * @description 使用BM算法计算出字符串string1中有没有包含字符串string2
 * 使用BM算法的时间复杂度是o(m/n)
 * @date 2020/7/29
 */
public class StringMatch_BM {

    /**
     * 坏字符忽略数组
     *
     * @param P 模式字符串字节数组
     * @return int[] 忽略数组
     */
    public static int[] bmBc(char[] P) {
        // 这里65536代表装载所有的char字符
        int[] bmBc = new int[65536];

        Arrays.fill(bmBc, -1);

        int m = P.length;

        // 填充字符序号，后面出现的重复字符会替代前面出现字符的值
        for (int i = 0; i < m; i++) {
            bmBc[P[i]] = m - 1 - i;
        }

        return bmBc;
    }

    /**
     * 好后缀忽略映射
     *
     * @param P
     * @return int[]
     */
    public static int[] bmGs(char[] P) {
        // 初始化忽略数组
        int m = P.length;
        int[] bmgs = new int[m];

        // 后缀数组
        int[] suffix = suffix(P);

        Arrays.fill(bmgs, m);

        // 模式串中没有子串匹配上好后缀，但找到了一个最大后缀
        for (int i = m - 1, j = 0; i >= 0; --i) {
            if (suffix[i] == i + 1) {
                for (; j < m - 1 - i; ++j) {
                    if (bmgs[j] == m) {
                        bmgs[j] = m - 1 - i;
                    }
                }
            }
        }

        // 模式串中有字串匹配上好后缀
        for (int i = 0; i <= m - 2; i++) {
            bmgs[m - 1 - suffix[i]] = m - 1 - i;
        }

        return bmgs;
    }

    /**
     * 返回模式串的后缀长度数组
     *
     * @param P
     * @return int[]
     */
    public static int[] suffix(char[] P) {

        int m = P.length;

        int[] suffix = new int[m];

        suffix[m - 1] = m;

        for (int i = m - 2; i >= 0; --i) {
            int p = i;

            while (p >= 0 && P[p] == P[m - 1 - i + p]) {
                p--;
            }

            suffix[i] = i - p;
        }
        return suffix;
    }

    /**
     * bm算法查询，核心是倒序比较，在失配的时候比较坏字符和好后缀的移动位置，取其中较大值
     *
     * @param pattern
     * @param target
     * @return int
     */
    public static int bmSearch(String pattern, String target) {
        char[] P = pattern.toCharArray();
        char[] T = target.toCharArray();

        int j = 0;
        int m = P.length;
        int n = T.length;

        if(m == 0){
            return 0;
        }

        if(n < m){
            return  -1;
        }

        // 获取忽略数组
        int[] bmBc = bmBc(P);
        int[] bmGS = bmGs(P);

        while (j <= n - m) {
            // 倒序匹配字符
            int i = m - 1;

            while (i >= 0 && P[i] == T[i + j]) {
                i--;
            }

            // 全部匹配 i = -1
            if (i < 0) {
                return j;
            }

            // 取好后缀和坏字符中移动位置最大的
            j += Math.max(bmGS[i], bmBc[T[i + j] - m + 1 + i]);
        }
        return -1;
    }

    public static void main(String[] args) {
        String target = "asafasdvbfhstaffasfbvcaeaasdggffdhghfghguaasafasdvbfhstaffasfbvcaeaasdggffdhghfghguasafasdvbfhstaffasfbvcaeaasdggffdhghfghguasafasdvbfhstaffasfbvcaeaasdggffdhghfghgusafasdvbfhstaffasfbvcaeaasdggffdhghfghguasafasdvbfhstaffasfbvcaeaasdggffdhghfghguffac";
        String pattern = "ffac";

        int[] bmBc = bmBc(pattern.toCharArray());
        System.out.println("坏字符忽略映射：");
        for(int i =0; i < bmBc.length;i++){
            if(bmBc[i] != -1){
                System.out.println("bmBc["+i+"] = " + bmBc[i]);
            }
        }

        int[] suffix = suffix(pattern.toCharArray());
        System.out.println("好后缀数组映射：");
        for(int i =0; i < suffix.length;i++){
            System.out.println("suffix["+i+"] = " + suffix[i]);
        }

        int[] bmGs = bmGs(pattern.toCharArray());
        System.out.println("好后缀忽略映射：");
        for(int i =0; i < bmGs.length;i++){
            System.out.println("bmGs["+i+"] = " + bmGs[i]);
        }

        StopWatch watch = new StopWatch();
        watch.start();
        System.out.println("BM：\t" + bmSearch(pattern,target));
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
        watch.start();
        System.out.println("KMP:\t" + kmp_compute(pattern,target));
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
        watch.start();
        System.out.println("TwoPointer:\t" + violent_compute(pattern,target));
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
        watch.start();
        System.out.println("IndexOf:\t" + target.indexOf(pattern));
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
        watch.start();
        System.out.println("SubString:\t" + compute_subString(pattern,target));
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
    }
}
