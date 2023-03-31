package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @ClassName CalculateTreeHeight
 * @Description: 给定一个二叉树，找出其最大深度。
 * 二叉树的深度为根节点到最远叶子节点的最长路径上的节点数。
 * @Author: ZhangHui
 * @Date: 2020/9/24
 * @Version：1.0
 */
public class CalculateTreeHeight {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    private static TreeNode root = new TreeNode(0);

    static {
        TreeNode left1 = new TreeNode(1);
        TreeNode right1 = new TreeNode(2);

        TreeNode left11 = new TreeNode(3);
        TreeNode left12 = new TreeNode(4);

        TreeNode right11 = new TreeNode(5);
        TreeNode right12 = new TreeNode(6);

        left1.left = left11;
        left1.right = left12;

        right1.left = right11;
        right1.right = right12;

        root.left = left1;
        root.right = right1;
    }

    public static void main(String[] args) {
        CalculateTreeHeight calculateTreeHeight = new CalculateTreeHeight();

        System.out.println(calculateTreeHeight.maxDepthWithBFSInAdvanced(root));
    }

    /**
     * 采用递归进行求解
     * 求基于root的最大深度就是求 Math.max(roor.left.maxDepth, root.right.maxDepth) + 1，所以依次递归下去
     */
    public int maxDepthWithRecursive(TreeNode root) {
        if (root == null) {
            return 0;
        } else {
            int leftHeight = maxDepthWithRecursive(root.left);
            int rightgHeight = maxDepthWithRecursive(root.right);
            return Math.max(leftHeight, rightgHeight) + 1;
        }
    }

    /**
     * 采用广度优先搜索方法进行搜索
     * 不同于原来的每次从Queue中取出一个TreeNode，然后再把它的Left和Right放到Queue的end；我们现在需要每次拿走一层的节点
     */
    public int maxDepthWithBFS(TreeNode root) {

        if (root == null) return 0;
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<TreeNode> tempQueue = new LinkedList<>();

        nodeQueue.add(root);
        int depth = 0;

        while (!nodeQueue.isEmpty()) {

            while (nodeQueue.size() != 1) {
                tempQueue.add(nodeQueue.poll());
            }

            tempQueue.add(nodeQueue.poll());

            depth++;

            while (!tempQueue.isEmpty()) {
                TreeNode curr = tempQueue.poll();

                if (curr.left != null) {
                    nodeQueue.add(curr.left);
                }
                if (curr.right != null) {
                    nodeQueue.add(curr.right);
                }
            }
        }

        return depth;
    }

    /**
     * 采用广度优先搜索方法进行搜索优化
     * 取消tempQueue，在一个Queue中解决，我们没有必要将nodeQueue清空后再放入新的left和right
     * 我们在放入下一层节点之前先查询当前层有几个节点，给它们poll出去就完事了
     */
    public int maxDepthWithBFSInAdvanced(TreeNode root) {

        if (root == null) return 0;
        Queue<TreeNode> nodeQueue = new LinkedList<>();

        nodeQueue.offer(root);

        int depth = 0;

        while (!nodeQueue.isEmpty()) {

            int size = nodeQueue.size();

            while (size > 0) {
                TreeNode curr = nodeQueue.poll();

                if (curr.left != null) {
                    nodeQueue.offer(curr.left);
                }
                if (curr.right != null) {
                    nodeQueue.offer(curr.right);
                }
                size--;
            }
            depth++;
        }

        return depth;
    }

}
