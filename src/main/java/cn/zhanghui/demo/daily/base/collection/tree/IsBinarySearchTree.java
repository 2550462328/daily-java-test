package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @ClassName IsBinarySearchTree
 * @Description: 给定一个二叉树，判断其是否是一个有效的二叉搜索树。
 *
 * 假设一个二叉搜索树具有如下特征：
 *
 * 	节点的左子树只包含小于当前节点的数。
 * 	节点的右子树只包含大于当前节点的数。
 * 	所有左子树和右子树自身必须也是二叉搜索树。
 *
 * @Author: ZhangHui
 * @Date: 2020/9/25
 * @Version：1.0
 */
public class IsBinarySearchTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    private static TreeNode root = new TreeNode(3);

    static{
        TreeNode left1 = new TreeNode(2);
        TreeNode right1 = new TreeNode(5);

        TreeNode left11 = new TreeNode(1);
//        TreeNode left12 = new TreeNode(4);

        TreeNode right11 = new TreeNode(4);
        TreeNode right12 = new TreeNode(6);

        left1.left = left11;
//        left1.right = left12;

        right1.left = right11;
        right1.right = right12;

        root.left = left1;
        root.right = right1;
    }

    public static void main(String[] args) {
        IsBinarySearchTree isBinarySearchTree = new IsBinarySearchTree();

        System.out.println(isBinarySearchTree.isValidBSTWithDFS(root));
    }

    /**
     * 使用中序遍历，如果是二叉搜索树，中序遍历后必定是有序的
     */
    public boolean isValidBSTWithMiddle(TreeNode root) {
        Deque<TreeNode> stack = new LinkedList<TreeNode>();
        int preOrder = Integer.MIN_VALUE;

        while(!stack.isEmpty() || root != null){
            while(root != null){
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();

            System.out.println(root.val);

            if(root.val <= preOrder){
                return false;
            }
            preOrder = root.val;
            root = root.right;
        }

        return true;

    }

    /**
     * 使用广度搜索解决
     */
    public boolean isValidBSTWithBFS(TreeNode root) {

        if(root == null) return true;
        Queue<TreeNode> nodeQueue = new LinkedList<>();

        nodeQueue.add(root);

        int left_max, right_min;

        while(!nodeQueue.isEmpty()){
            TreeNode curr = nodeQueue.poll();

            if(!isValid(curr)){
                return false;
            }

            if(curr.left != null){
                nodeQueue.add(curr.left);
            }
            if(curr.right != null){
                nodeQueue.add(curr.right);
            }
        }
        return true;
    }

    /**
     * 使用深度递归方式解决
     */
    public boolean isValidBSTWithDFS(TreeNode root) {

//        if(root == null) return true;
//
//        boolean isValidLeft = isValidBSTWithDFS(root.left);
//
//        boolean isValidRight = isValidBSTWithDFS(root.right);
//
//        return isValid(root) && isValidLeft && isValidRight;

        return isValid(root,null,null);
    }

    /**
     * 核心是此判断规则 原始暴力判断
     */
    private boolean isValid(TreeNode root){

        if(root.left != null){
            if(root.val <= root.left.val) {
                return false;
            }
            if(root.left.right != null) {
                if (root.val <= root.left.right.val) {
                    return false;
                }
                if(root.left.right.right != null && root.val <= root.left.right.right.val){
                    return false;
                }
            }
        }
        if(root.right != null){
            if(root.val >= root.right.val) {
                return false;
            }
            if(root.right.left != null){
                if(root.val >= root.right.left.val){
                    return false;
                }
                if(root.right.left.left != null && root.val >= root.right.left.left.val){
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * 对判断规则进行优化
     * 加入low和hight参数限制root节点左子树的最大值和右子树的最小值
     */
    private boolean isValid(TreeNode root,Integer low,Integer high){
        if(root== null) return true;

        int val = root.val;

        if(low != null && val <= low){
            return false;
        }
        if(high != null && val >= high){
            return false;
        }

        if(root.left != null && !isValid(root.left,low,val)){
            return false;
        }
        if(root.right != null && !isValid(root.right,val,high)){
            return false;
        }
        return true;
    }
}
