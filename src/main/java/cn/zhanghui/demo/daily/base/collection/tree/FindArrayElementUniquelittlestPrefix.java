package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.HashMap;

/**
 * @author ZhangHui
 * @version 1.0
 * @className PreTree
 * @description 前缀树的示例
 * 给出一个字符串数组，求出能代表每个字符串的唯一最小前缀
 * @date 2020/6/9
 */
public class FindArrayElementUniquelittlestPrefix {
    public static void main(String[] args) {
        FindArrayElementUniquelittlestPrefix preTree = new FindArrayElementUniquelittlestPrefix();

        // 最好将待插入的字符串按照个数进行排序
        String s1 = "aqweq";
        String s2 = "aeqec";
        String s3 = "qeqw";
        String s4 = "ab";

        preTree.insertNode(s1);
        preTree.insertNode(s2);
        preTree.insertNode(s3);
        preTree.insertNode(s4);

        System.out.println(preTree.getShortestPre(s1));
        System.out.println(preTree.getShortestPre(s2));
        System.out.println(preTree.getShortestPre(s3));
        System.out.println(preTree.getShortestPre(s4));
    }

    class Node {
        private int preEnd = 0;

        private int size = 0;

        private HashMap<Character, Node> childNode = new HashMap<>();
    }

    private Node head = new Node();

    public void insertNode(String str) {

        char[] strArray = str.toCharArray();

        Node temp = head;

        int end = -1;

        for (int i = 0; i < strArray.length; i++) {
            if (!temp.childNode.containsKey(strArray[i])) {
                temp.childNode.put(strArray[i], new Node());
                if (end == -1) {
                    end = i + 1;
                }
            }
            temp = temp.childNode.get(strArray[i]);
        }
        if (end == -1) {
            end = strArray.length;
        }
        temp.preEnd = end;
        temp.size = strArray.length;
    }

    public String getShortestPre(String str) {
        if (head.childNode.isEmpty()) {
            return "";
        }
        Node temp = head;
        char[] strArray = str.toCharArray();
        int i = 0;

        while (i < strArray.length) {
            temp = temp.childNode.get(strArray[i]);
            if (strArray.length == temp.size) {
                break;
            }
            i++;
        }
        return str.substring(0, temp.preEnd);
    }

}
