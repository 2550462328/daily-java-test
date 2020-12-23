package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Description:
 * 给定一个二叉树，返回其节点值的锯齿形层次遍历。（即先从左往右，再从右往左进行下一层遍历，以此类推，层与层之间交替进行）。
 * <p>
 * 例如：
 * 给定二叉树 [3,9,20,null,null,15,7],
 * <p>
 * 3
 * / \
 * 9  20
 * /  \
 * 15   7
 * <p>
 * 返回锯齿形层次遍历如下：
 * <p>
 * [
 * [3],
 * [20,9],
 * [15,7]
 * ]
 *
 * @author createdBy huizhang43.
 * @date createdAt 2020/12/21 9:46
 **/
public class ZigzagLevelOrder {

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    /**
     * 使用LinkList，一种既可以做栈又可以做队列的数据结构
     * 借助LinkList两头通的数据结构，在从左向右和从右向左遍历采用不同的方式
     * @param root
     * @return
     */
    public List<List<Integer>> solveByQueueWithStack(TreeNode root) {

        LinkedList<TreeNode> queueOrStack = new LinkedList<>();

        if(root != null) {
            queueOrStack.add(root);
        }

        List<List<Integer>> resultList = new ArrayList<>();

        // 是否从右边开始遍历
        boolean isRight = false;
        // 每一层的节点编号
        int levelNums = 0;

        while (!queueOrStack.isEmpty()) {
            // 记录当前层的节点数
            int levelSize = queueOrStack.size();
            List<Integer> levelList = new ArrayList<>(levelSize);
            while (levelNums < levelSize) {

                // 从右边进行遍历
                if (isRight) {
                    TreeNode currNode = queueOrStack.pollFirst();
                    levelList.add(currNode.val);
                    if (currNode.right != null) {
                        queueOrStack.addLast(currNode.right);
                    }
                    if (currNode.left != null) {
                        queueOrStack.addLast(currNode.left);
                    }
                  // 从左边进行遍历
                } else {
                    TreeNode currNode = queueOrStack.pollLast();
                    levelList.add(currNode.val);
                    if (currNode.left != null) {
                        queueOrStack.addFirst(currNode.left);
                    }
                    if (currNode.right != null) {
                        queueOrStack.addFirst(currNode.right);
                    }
                }
                levelNums++;
            }
            isRight = !isRight;
            levelNums = 0;
            resultList.add(levelList);
        }
        return resultList;

    }

}
