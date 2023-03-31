package cn.zhanghui.demo.daily.base.algorithm.demo;


import java.util.*;

/**
 * @author ZhangHui
 * @version 1.0
 * @className GuessOrder
 * @description 已知一种新的火星文的单词由英文字母（仅小写字母）组成，但是此火星文中的字母先后顺序未知。给出一组非空的火星文单词，且此组单词已经按火星文字典序进行好了排序（从小到大），请推断出此火星文中的字母先后顺序。
 * 输入描述:
 * 一行文本，为一组按火星文字典序排序好的单词(单词两端无引号)，单词之间通过空格隔开
 * 输出描述:
 * 按火星文字母顺序输出出现过的字母，字母之间无其他字符，如果无法确定顺序或者无合理的字母排序可能，请输出"invalid"(无需引号)
 * @date 2020/7/24
 */
public class GuessOrder {

    class TreeNode {
        private List<TreeNode> parentsNode = new ArrayList<>();

        private HashMap<Character, TreeNode> childrenNode = new HashMap<>();

        boolean isVisited = false;

        public TreeNode() {
        }

        public TreeNode(boolean isVisited) {
            this.isVisited = isVisited;
        }
    }

    private TreeNode head = new TreeNode(true);

    public void insert(String target) {
        char[] chars = target.toCharArray();
        TreeNode temp = head;

        int len = chars.length;
        for (int i = 0; i < len; i++) {
            TreeNode curNode = findByKey(head, chars[i]);
            if (curNode == temp) {
                continue;
            }
            if (curNode == null) {
                TreeNode tempNode = new TreeNode();
                tempNode.parentsNode.add(temp);
                temp.childrenNode.put(chars[i], tempNode);
            } else {
                curNode.parentsNode.add(temp);
                temp.childrenNode.put(chars[i], curNode);
            }
            temp = temp.childrenNode.get(chars[i]);
        }
    }

    public List<Character> bfs_tree() {
        Queue<TreeNode> queue = new LinkedList();
        queue.add(head);
        List<Character> resultList = new LinkedList<>();

        int queueSize = 0;

        while (!queue.isEmpty()) {
            TreeNode curNode = queue.poll();
            queueSize++;
            if (!curNode.childrenNode.isEmpty()) {
                for (Map.Entry<Character, TreeNode> entry : curNode.childrenNode.entrySet()) {
                    boolean parentVisited = false;
                    TreeNode tempNode = entry.getValue();

                    for (TreeNode treeNode : tempNode.parentsNode) {
                        if (!treeNode.isVisited) {
                            parentVisited = true;
                        }
                    }

                    if (!parentVisited && !tempNode.isVisited) {
                        queue.add(tempNode);
                        resultList.add(entry.getKey());
                        entry.getValue().isVisited = true;
                    }
                }
            }
        }
        if (queueSize == head.childrenNode.size() + 1) {
            return null;
        }
        return resultList;
    }

    public static TreeNode findByKey(TreeNode target, Character key) {

        if (!target.childrenNode.isEmpty()) {
            for (Map.Entry<Character, TreeNode> entry : target.childrenNode.entrySet()) {
                if (Objects.equals(key, entry.getKey())) {
                    return entry.getValue();
                }
                target = entry.getValue();
                if (!target.childrenNode.isEmpty()) {
                    TreeNode resultNode = findByKey(target, key);
                    if (resultNode == null) {
                        continue;
                    } else {
                        return resultNode;
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String target = scanner.nextLine();
        String[] strings = target.split(" ");
        GuessOrder guessOrder = new GuessOrder();

        for (String string : strings) {
            guessOrder.insert(string);
        }

//        System.out.println(findByKey(guessOrder.head,'f'));

        List<Character> list = guessOrder.bfs_tree();

        if (list == null) {
            System.out.println("invalid");
        } else {
            System.out.println(list.toString());
        }
    }
}
