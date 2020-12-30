package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.LinkedList;

/**
 * Description:
 * <p>
 * 给定一个 完美二叉树 ，其所有叶子节点都在同一层，每个父节点都有两个子节点。二叉树定义如下：
 * <p>
 * struct Node {
 * int val;
 * Node *left;
 * Node *right;
 * Node *next;
 * }
 * <p>
 * 填充它的每个 next 指针，让这个指针指向其下一个右侧节点。如果找不到下一个右侧节点，则将 next 指针设置为 NULL。
 * <p>
 * 初始状态下，所有 next 指针都被设置为 NULL。
 * <p>
 * 进阶：
 * <p>
 * 你只能使用常量级额外空间。
 * 使用递归解题也符合要求，本题中递归程序占用的栈空间不算做额外的空间复杂度。
 *
 * @author createdBy huizhang43.
 * @date createdAt 2020/12/29 20:15
 **/
public class FillTreeNodeNextPointer {

    static class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {
        }

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, Node _left, Node _right, Node _next) {
            val = _val;
            left = _left;
            right = _right;
            next = _next;
        }
    }

    /**
     * 使用队列进行层次遍历
     * @param root
     * @return
     */
    public Node connectByQueue(Node root) {

        if (root == null) {
            return root;
        }
        LinkedList<Node> queue = new LinkedList<>();

        queue.addLast(root);

        int i = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            Node head = null;
            while (i < size) {
                Node currNode = queue.pollFirst();
                if (head == null) {
                    head = currNode;
                } else {
                    head.next = currNode;
                    head = head.next;
                }
                if (currNode.left != null) {
                    queue.addLast(currNode.left);
                }
                if (currNode.right != null) {
                    queue.addLast(currNode.right);
                }
                i++;
            }
            i = 0;
        }
        return root;
    }

    /**
     * 使用动态规划 将Tree转换成二维数组
     * @param root
     * @return
     */
    public Node connectByRecursive(Node root) {

        if(root == null){
            return root;
        }
        int h = getTreeHeight(root);

        Node[][] dp = new Node[h + 1][1 << h];
        dp[0][0] = root;
        for (int i = 1; i < h + 1; i++) {
            for (int j = (1 << i) -1; j >= 0; j--) {
                if ((j & 1) == 0) {
                    dp[i][j] = dp[i - 1][j / 2].left;
                } else {
                    dp[i][j] = dp[i - 1][j / 2].right;
                }
                if(j + 1 >= 1 << i){
                    dp[i][j].next = null;
                }else{
                    dp[i][j].next = dp[i][j+1];
                }
            }
        }

        return root;
    }

    private int getTreeHeight(Node root) {
        Node dump = root;
        int height = 0;
        while (dump.left != null) {
            height++;
            dump = dump.left;
        }
        return height;
    }

    /**
     * 逻辑上对节点分配next节点
     * 左节点的next指向右节点 右节点的next指向父节点next节点的左节点
     * @param root
     * @return
     */
    public Node connect(Node root) {
        if(root == null){
            return root;
        }

        Node farLeft = root;

        while(farLeft.left != null){

            Node head = farLeft;

            while(head != null){

                head.left.next = head.right;

                if(head.next != null){
                    head.right.next = head.next.left;
                }
                head = head.next;
            }
            farLeft = farLeft.left;
        }

        return root;
    }

    public static void main(String[] args) {
        Node root = new Node(1);
        Node root_left = new Node(2);
        Node root_right = new Node(3);
        Node root_left_left = new Node(4);
        Node root_left_right = new Node(5);
        Node root_right_left = new Node(6);
        Node root_right_right = new Node(7);

        root_left.left = root_left_left;
        root_left.right = root_left_right;
        root_right.left = root_right_left;
        root_right.right = root_right_right;

        root.left = root_left;
        root.right = root_right;

//        new FillTreeNodeNextPointer().connectByQueue(root);
        new FillTreeNodeNextPointer().connectByRecursive(root);

    }

}
