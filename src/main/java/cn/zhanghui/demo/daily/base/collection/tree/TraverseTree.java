package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.*;

/**
 * @ClassName TraverseTree
 * @Description: 使用不同方式遍历树
 * @Author: ZhangHui
 * @Date: 2020/9/27
 * @Version：1.0
 */
public class TraverseTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    private static TreeNode root = new TreeNode(0);


    //先序遍历----------------------------------------------------------------------

    /**
     * 递归实现
     */
    public static void preOrderRe(TreeNode root) {
        System.out.println(root.val);
        TreeNode leftTree = root.left;
        if (leftTree != null) {
            preOrderRe(leftTree);
        }
        TreeNode rightTree = root.right;
        if (rightTree != null) {
            preOrderRe(rightTree);
        }
    }

    /**
     * 非递归实现
     */
    public static void preOrder(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                System.out.println(root.val);
                stack.push(root);
                root = root.left;
            }
            if (!stack.isEmpty()) {
                root = stack.pop();
                root = root.right;
            }
        }
    }

    //中序遍历---------------------------------------------------------------------

    /**
     * 中序遍历递归实现
     */
    public static void midOrderRe(TreeNode root) {
        if (root == null)
            return;
        else {
            midOrderRe(root.left);
            System.out.println(root.val);
            midOrderRe(root.right);
        }
    }

    /**
     * 中序遍历非递归实现
     */
    public static void midOrder(TreeNode root) {
        LinkedList<TreeNode> stack = new LinkedList<>();
        while (root != null || !stack.isEmpty()) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            if (!stack.isEmpty()) {
                root = stack.poll();
                System.out.println(root.val);
                root = root.right;
            }
        }
    }

    //后序遍历---------------------------------------------------------------------

    /**
     * 后序遍历递归实现
     */
    public static void postOrderRe(TreeNode root) {
        if (root == null)
            return;
        else {
            postOrderRe(root.left);
            postOrderRe(root.right);
            System.out.println(root.val);
        }
    }

    /**
     * 后序遍历非递归实现
     */
    public static void postOrder(TreeNode root) {

        LinkedList<TreeNode> stack = new LinkedList<>();

        TreeNode curr, prev = null;

        stack.push(root);
        while (!stack.isEmpty()) {
            curr = stack.peek();
            if ((curr.left == null && curr.right == null) || (prev != null && (prev == curr.left || prev == curr.right))) {
                System.out.println(curr.val + " -> ");
                stack.poll();
                prev = curr;
            } else {
                if (curr.right != null) {
                    stack.push(curr.right);
                }
                if (curr.left != null) {
                    stack.push(curr.left);
                }
            }
        }
    }
}
