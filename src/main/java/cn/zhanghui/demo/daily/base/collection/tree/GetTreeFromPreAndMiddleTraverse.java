package cn.zhanghui.demo.daily.base.collection.tree;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Description:
 * 根据一棵树的前序遍历与中序遍历构造二叉树。
 *
 * 注意:
 * 你可以假设树中没有重复的元素。
 *
 * 例如，给出
 *
 * 前序遍历 preorder = [3,9,20,15,7]
 * 中序遍历 inorder = [9,3,15,20,7]
 *
 * 返回如下的二叉树：
 *
 *     3
 *    / \
 *   9  20
 *     /  \
 *    15   7
 *
 * @author createdBy huizhang43.
 * @date createdAt 2020/12/23 9:20
 **/
public class GetTreeFromPreAndMiddleTraverse {

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    private Map<Integer, Integer> indexMap;

    /**
     * preorder的首位是根节点，在此基础上，可以在中序遍历（前中后的顺序）中查找出它左子树节点个数和右子树节点个数
     * 以及确定左节点和右节点的位置，左节点在根节点 + 1 的位置，右节点在根节点 + 根节点左子树节点个数 + 1的位置
     * 再依次递归到左子树和右子树的建造
     * 递归的结束条件就是对于一个节点，它的左边界 大于 右边界了
     * @param preorder
     * @param preorder_left
     * @param preorder_right
     * @param inorder_left
     * @return
     */
    public TreeNode myBuildTree(int[] preorder, int preorder_left, int preorder_right, int inorder_left) {
        // 递归结束条件
        if (preorder_left > preorder_right) {
            return null;
        }

        // 前序遍历中的第一个节点就是根节点
        int preorder_root = preorder_left;
        // 在中序遍历中定位根节点
        int inorder_root = indexMap.get(preorder[preorder_root]);

        // 先把根节点建立出来
        TreeNode root = new TreeNode(preorder[preorder_root]);
        // 得到左子树中的节点数目
        int size_left_subtree = inorder_root - inorder_left;
        // 递归地构造左子树，并连接到根节点
        root.left = myBuildTree(preorder, preorder_left + 1, preorder_left + size_left_subtree, inorder_left);
        // 递归地构造右子树，并连接到根节点
        root.right = myBuildTree(preorder, preorder_left + size_left_subtree + 1, preorder_right, inorder_root + 1);
        return root;
    }

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        int n = preorder.length;
        // 构造哈希映射，帮助我们快速定位根节点
        indexMap = new HashMap<>(inorder.length);
        for (int i = 0; i < n; i++) {
            indexMap.put(inorder[i], i);
        }
        return myBuildTree(preorder, 0, n - 1, 0);
    }

    public static void main(String[] args) {
        int[] preorder = {3,9,20,15,7};
        int[] inorder = {9,3,15,20,7};

        System.out.println(new GetTreeFromPreAndMiddleTraverse().buildTree(preorder,inorder));
    }

}
