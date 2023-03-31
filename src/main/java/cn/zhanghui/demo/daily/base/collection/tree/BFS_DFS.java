package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.*;

/**
 * @author ZhangHui
 * @version 1.0
 * @className BFSTree
 * @description 使用广度遍历法和深度遍历法遍历树和图
 * @date 2020/7/22
 */
public class BFS_DFS {

    TreeNode tRoot;
    GraphNode gRoot;

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    class GraphNode {
        int val;

        List<GraphNode> childNodes;

        public GraphNode(int val, int size) {
            this.val = val;
            this.childNodes = new ArrayList<>(size);
        }
    }

    /**
     * 使用广度遍历树，核心在于使用queue
     */
    public void bfsIterator_tree() {
        Queue<TreeNode> treeNodeQueue = new LinkedList();
        if (tRoot != null) {
            treeNodeQueue.add(tRoot);
        }

        while (!treeNodeQueue.isEmpty()) {
            TreeNode currentNode = treeNodeQueue.poll();

            System.out.println(currentNode.val);

            if (currentNode.left != null) {
                treeNodeQueue.add(currentNode.left);
            }
            if (currentNode.right != null) {
                treeNodeQueue.add(currentNode.right);
            }
        }
    }

    /**
     * 使用递归的方式实现树的深度遍历
     */
    public void dfsIterator_recursive_tree(TreeNode root) {

        if (root.left != null) {
            dfsIterator_recursive_tree(root.left);
        }
        if (root.right != null) {
            dfsIterator_recursive_tree(root.right);
        }
    }

    /**
     * 使用栈的方式实现树的深度遍历
     */
    public void dfsIterator_stack_tree() {

        Stack<TreeNode> treeNodeStack = new Stack();

        if (tRoot != null) {
            treeNodeStack.push(tRoot);
        }

        while (!treeNodeStack.empty()) {
            TreeNode currentNode = treeNodeStack.pop();

            if (currentNode.right != null) {
                treeNodeStack.push(currentNode.right);
            }

            if (currentNode.left != null) {
                treeNodeStack.push(currentNode.left);
            }
        }
    }

    /**
     * 使用队列实现图的广度遍历
     * 图的广度和深度遍历相对比树来说，主要就是图有可能会重复遍历节点
     * 所以我们使用一个Set来装载已经遍历过的节点，在queue保证不加入重复的节点。
     */
    public void dfsIterator_recursive_graph() {
        Queue<GraphNode> graphQueue = new LinkedList();

        Set<GraphNode> visitedNode = new HashSet<>();

        GraphNode root = gRoot;

        if (root != null) {
            graphQueue.add(root);
        }

        while (!graphQueue.isEmpty()) {
            GraphNode currentNode = graphQueue.poll();
            visitedNode.add(root);

            for (GraphNode graphNode : currentNode.childNodes) {
                if (graphNode != null && !visitedNode.contains(graphNode)) {
                    graphQueue.add(graphNode);
                }
            }
        }

    }


}
