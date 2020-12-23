package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.*;

/**
 * @ClassName IsSymmetricTree
 * @Description: 给定一个二叉树，检查它是否是镜像对称的。
 * <p>
 * 例如，二叉树 [1,2,2,3,4,4,3] 是对称的。
 * 1
 * / \
 * 2   2
 * / \ / \
 * 3  4 4  3
 * @Author: ZhangHui
 * @Date: 2020/9/27
 * @Version：1.0
 */
public class IsSymmetricTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    private static TreeNode root = new TreeNode(1);

    static {
        TreeNode left1 = new TreeNode(2);
        TreeNode right1 = new TreeNode(2);

        TreeNode left11 = new TreeNode(3);
        TreeNode left12 = new TreeNode(4);

        TreeNode right11 = new TreeNode(4);
        TreeNode right12 = new TreeNode(3);

        left1.left = left11;
        left1.right = left12;

        right1.left = right11;
        right1.right = right12;

        root.left = left1;
        root.right = right1;
    }

    public static void main(String[] args) {
        IsSymmetricTree isSymmetricTree = new IsSymmetricTree();

        Integer[] arr = {1, null, null, 1};

        System.out.println(isSymmetricTree.isReverseArray(arr));

        System.out.println(isSymmetricTree.isSymmetricWithRecursive(root));
    }

    /**
     * 使用广度遍历解决，比较每一层的节点是不是回文串
     */
    public boolean isSymmetricWithBFS(TreeNode root) {

        if (root == null) return true;

        Queue<TreeNode> nodeQueue = new LinkedList<>();

        nodeQueue.add(root);

        while (!nodeQueue.isEmpty()) {
            int size = nodeQueue.size();

            Integer[] array = new Integer[size];

            for (int i = 0; i < size; i++) {
                TreeNode curr = nodeQueue.poll();
                array[i] = curr == null ? null : curr.val;

                if (curr != null) {
                    nodeQueue.add(curr.left);
                    nodeQueue.add(curr.right);
                }
            }

            if (!isReverseArray(array)) {
                return false;
            }
            // 可以优化
        }
        return true;
    }

    private boolean isReverseArray(Integer[] arr) {
        int i = 0, j = arr.length - 1;

        while (i < j) {

            if (!Objects.equals(arr[i], arr[j])) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }

    /**
     * 使用递归解决，基于当前层，递归下一层是否还是对称tree
     */
    public boolean isSymmetricWithRecursive(TreeNode root) {

        if (root == null) return true;

        List<TreeNode> list = new ArrayList<>();

        list.add(root);

        return traverseTree(list);
    }

    private boolean traverseTree(List<TreeNode> list) {

        if (list.size() == 0) return true;

        int i = 0, j = list.size() - 1;
        List<TreeNode> nextList = new ArrayList<>();

        while (i < j) {
            if (!equals(list.get(i), list.get(j))) {
                return false;
            }
            i++;
            j--;
        }

        for (TreeNode treeNode : list) {
            if (treeNode != null) nextList.add(treeNode.left);
            if (treeNode != null) nextList.add(treeNode.right);
        }
        list.clear();
        return traverseTree(nextList);
    }

    private boolean equals(TreeNode a, TreeNode b) {
        return a == b || (a != null && b != null && a.val == b.val);
    }

    // 下面是官方的递归和迭代

    /**
     * 该方法效率最高
     * 这里使用递归解决，假设有两个指针从root出发，一个向左，一个向右
     * 那判断一个tree是否是对称的，只需要递归比较指针A的左节点是否和指针B的右节点相等，指针A的右节点是否和指针B的左节点相等
     */
    public boolean isSymmetricWithRecursiveInAdvanced(TreeNode root) {
        return check(root, root);
    }


    private boolean check(TreeNode nodeLeft, TreeNode nodeRight) {

        if (nodeLeft == null && nodeRight == null) {
            return true;
        }

        if (nodeLeft == null || nodeRight == null) {
            return false;
        }

        return nodeLeft.val == nodeRight.val && check(nodeLeft.left, nodeRight.right) && check(nodeLeft.right, nodeRight.left);
    }

    public boolean isSymmetricWithIteratorInAdvanced(TreeNode root) {
        return check(root, root);
    }

    /**
     * 对上述递归方法判断的改写，使用queue装载每次递归的节点值，因为两个指针是交叉进行的，所以queue中相怜的元素肯定是相等的，如果不相等就不是对称tree
     */
    private boolean checkTree(TreeNode nodeLeft, TreeNode nodeRight) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(nodeLeft);
        queue.offer(nodeRight);

        while (!queue.isEmpty()) {

            TreeNode nodeA = queue.poll();
            TreeNode nodeB = queue.poll();

            if (nodeA == null && nodeB == null) {
                continue;
            }

            if (nodeA == null || nodeB == null || nodeA.val != nodeB.val) {
                return false;
            }

            queue.offer(nodeA.left);
            queue.offer(nodeB.right);

            queue.offer(nodeA.right);
            queue.offer(nodeB.left);
        }
        return true;
    }
}
