package cn.zhanghui.demo.daily.base.collection.tree;

/**
 * @author ZhangHui
 * @version 1.0
 * @className FindArrayLongestCommonPrefix
 * @description 查找字符串数组的最长公共前缀
 * <p>
 * 提供基于前缀树实现的用例
 * <p>
 * 此外暴力求解法这里不列举了
 * <p>
 * 比如横向求解 两两求解公共最大前缀后再和下一个对比
 * 比如纵向求解，依次比较每个字符串第i个字符，不一样即可输出
 * @date 2020/9/16
 */
public class FindArrayLongestCommonPrefix {

    public static void main(String[] args) {
        FindArrayLongestCommonPrefix findArrayLongestCommonPrefix = new FindArrayLongestCommonPrefix();

        String[] strs = {"aa", "a"};

        System.out.println(findArrayLongestCommonPrefix.longestCommonPrefix(strs));
    }

    public String longestCommonPrefix(String[] strs) {
        for (String str : strs) {
            if (str.length() == 0) {
                return "";
            }
            insertNode(str);
        }
        return get();
    }

    private Node head = new Node();

    private boolean isInit = false;

    public void insertNode(String str) {

        Node temp = head;

        if (!isInit) {
            for (int i = 0; i < str.length(); i++) {
                temp.childNode = new Node(str.charAt(i));
                temp = temp.childNode;
            }
            isInit = true;
        } else {
            int index = 0;

            while (temp.childNode != null && index < str.length()) {
                if (temp.childNode.value != str.charAt(index)) {
                    temp.childNode = null;
                    break;
                }
                temp = temp.childNode;
                if (++index == str.length()) {
                    temp.childNode = null;
                }
            }
        }
    }

    public String get() {
        Node temp = head;
        StringBuilder sb = new StringBuilder();

        while (temp.childNode != null) {
            sb.append(temp.childNode.value);
            temp = temp.childNode;
        }
        return sb.toString();
    }

    class Node {
        private Character value;

        private Node childNode;

        public Node(Character value) {
            this.value = value;
        }

        public Node() {
        }
    }
}
