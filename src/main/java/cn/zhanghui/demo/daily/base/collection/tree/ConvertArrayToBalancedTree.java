package cn.zhanghui.demo.daily.base.collection.tree;

/**
 * @ClassName ConvertArrayToBalancedTree
 * @Description: 将一个按照升序排列的有序数组，转换为一棵高度平衡二叉搜索树。
 *
 * 本题中，一个高度平衡二叉树是指一个二叉树每个节点 的左右两个子树的高度差的绝对值不超过 1。
 *
 * 示例:
 * 给定有序数组: [-10,-3,0,5,9],
 * 一个可能的答案是：[0,-3,9,-10,null,5]，它可以表示下面这个高度平衡二叉搜索树：
 *       0
 *      / \
 *    -3   9
 *    /   /
 *  -10  5
 *
 * @Author: ZhangHui
 * @Date: 2020/10/9
 * @Version：1.0
 */
public class ConvertArrayToBalancedTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public static void main(String[] args) {
        ConvertArrayToBalancedTree convertArrayToBalancedTree = new ConvertArrayToBalancedTree();

        int[] nums = {-10, -3, 0, 5, 9};
        TreeNode root = convertArrayToBalancedTree.sortedArrayToBST(nums);

        convertArrayToBalancedTree.postOrderRe(root);

    }

    public TreeNode sortedArrayToBST(int[] nums) {
        int l = 0, r = nums.length - 1;

        return convert(nums, l, r);
    }

    private TreeNode convert(int[] nums, int l, int r) {

        if (l <= r) {
            int mid = l + (r - l + 1) / 2;

            TreeNode currNode = new TreeNode(nums[mid]);

            currNode.left = convert(nums, l, mid - 1);
            currNode.right = convert(nums, mid + 1, r);

            return currNode;
        }

        return null;
    }

    public void postOrderRe(TreeNode root) {
        if (root == null)
            return;
        else {
            postOrderRe(root.left);
            postOrderRe(root.right);
            System.out.print(root.val + " ");
        }
    }
}
