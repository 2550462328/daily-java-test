package cn.zhanghui.demo.daily.base.collection.array;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * 给定一个字符串数组，将字母异位词组合在一起。字母异位词指字母相同，但排列不同的字符串。
 *
 * 示例:
 *
 * 输入: ["eat", "tea", "tan", "ate", "nat", "bat"]
 * 输出:
 * [
 *   ["ate","eat","tea"],
 *   ["nat","tan"],
 *   ["bat"]
 * ]
 *
 * 说明：
 *
 * 	所有输入均为小写字母。
 * 	不考虑答案输出的顺序。
 *
 * @author: ZhangHui
 * @date: 2020/11/12 8:57
 * @version：1.0
 */
public class FindAnagramAndGroup {

    /**
     * 对strs中每个元素按照字母表顺序排序，然后在map中查找
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> existsMap = new HashMap<>(strs.length);

        for(String str : strs){
            char[] chars = str.toCharArray();
            Arrays.sort(chars);

            String newStr = new String(chars);
            if(existsMap.containsKey(newStr)){
                existsMap.get(newStr).add(str);
            }else{
                List<String> list = new ArrayList<>();
                list.add(str);
                existsMap.put(newStr,list);
            }
        }
        return new ArrayList<>(existsMap.values());
    }


    /**
     * 对strs中每个元素按照字母表顺序排序，然后在map中查找
     * 效率较差，当然还有其他可以作为map的key的方式
     */
    public List<List<String>> groupAnagrams_hashCode(String[] strs) {
        Map<Integer, List<String>> existsMap = new HashMap<>(strs.length);

        for(String str : strs){
            int hashCode =getHashCode(str);
            if(existsMap.containsKey(hashCode)){
                existsMap.get(hashCode).add(str);
            }else{
                List<String> list = new ArrayList<>();
                list.add(str);
                existsMap.put(hashCode,list);
            }
        }
        return new ArrayList<>(existsMap.values());
    }

    private int getHashCode(String str){
        List<Character> list = new ArrayList<>();

        char[] chars = str.toCharArray();

        for(int i = 0; i < chars.length; i++){
            list.add(chars[i]);
        }
        Collections.sort(list);
        return list.hashCode();
    }

}
