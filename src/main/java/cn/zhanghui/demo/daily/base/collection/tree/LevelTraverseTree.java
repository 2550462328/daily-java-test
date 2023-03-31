package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @ClassName LevelTraverseTree
 * @Description: 给你一个二叉树，请你返回其按 层序遍历 得到的节点值。 （即逐层地，从左到右访问所有节点）。
 * <p>
 * 3
 * / \
 * 9  20
 * /  \
 * 15   7
 * <p>
 * 返回其层次遍历结果：
 * <p>
 * [
 * [3],
 * [9,20],
 * [15,7]
 * ]
 * @Author: ZhangHui
 * @Date: 2020/9/29
 * @Version：1.0
 */
public class LevelTraverseTree {

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

    /**
     * 使用广度遍历进行遍历
     */
    public List<List<Integer>> levelOrderWithBFS(TreeNode root) {

        List<List<Integer>> resultList = new LinkedList<>();

        if (root == null) return resultList;

        Queue<TreeNode> nodeQueue = new LinkedList<>();

        nodeQueue.offer(root);

        while (!nodeQueue.isEmpty()) {

            List<Integer> levelList = new LinkedList<>();

            int size = nodeQueue.size();

            while (size > 0) {

                TreeNode curr = nodeQueue.poll();

                levelList.add(curr.val);

                if (curr.left != null) nodeQueue.add(curr.left);

                if (curr.right != null) nodeQueue.add(curr.right);

                size--;
            }
            resultList.add(levelList);
        }
        return resultList;

    }
}
