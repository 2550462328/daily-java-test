package cn.zhanghui.demo.daily.base.algorithm.demo;


import java.util.*;

/**
 * @author ZhangHui
 * @version 1.0
 * @className GraphTest
 * @description 场景：找出距离targetNode的路径为times的点，要求点不能重复，比如路径5的点又在路径6出现了，那么不能算距离6的点
 * 解决思想，在包装的GraphNode中新增tempDistance属性，记录每次广度遍历的距离，同时在遍历中使用Set集合保存已经出现的点
 * @date 2020/7/22
 */
public class GraphTest {

    public List<GraphNode> findByTimes(int times, GraphNode targetNode) {

        List<GraphNode> resultNodes = new ArrayList<>();

        Queue<GraphNode> nodeQueue = new LinkedList<>();

        Set<GraphNode> visitedSet = new HashSet<>();

        if (targetNode != null) {
            targetNode.tempDistance = 0;
            nodeQueue.add(targetNode);
        }

        while (!nodeQueue.isEmpty() && times != 0) {

            GraphNode currentNode = nodeQueue.poll();

            if (currentNode.tempDistance == times) {
                break;
            }

            visitedSet.add(currentNode);

            for (GraphNode childNode : currentNode.childrenNode) {
                if (childNode != null && !visitedSet.contains(childNode)) {
                    if ((childNode.tempDistance = currentNode.tempDistance + 1) == times) {
                        resultNodes.add(childNode);
                    }
                    nodeQueue.add(childNode);
                }
            }
        }
        return resultNodes;
    }


    public static void main(String[] args) {
        new GraphTest().test();
    }


    private void test() {
        GraphNode node1 = new GraphNode("node1", 10);

        GraphNode node21 = new GraphNode("node21", 10);
        GraphNode node22 = new GraphNode("node22", 10);
        GraphNode node23 = new GraphNode("node23", 10);

        List<GraphNode> node2List = new ArrayList<>();
        node2List.add(node21);
        node2List.add(node22);
        node2List.add(node23);

        GraphNode node31 = new GraphNode("node31", 10);
        GraphNode node32 = new GraphNode("node32", 10);
        GraphNode node33 = new GraphNode("node33", 10);
        List<GraphNode> node3List = new ArrayList<>();
        node3List.add(node31);
        node3List.add(node32);
        node3List.add(node33);
        node3List.add(node22);

        node23.childrenNode = node3List;
        node1.childrenNode = node2List;

        System.out.println(findByTimes(2, node1));

    }

    class GraphNode {
        Object obj;

        int tempDistance;

        List<GraphNode> childrenNode;

        public GraphNode(Object obj, int intialize) {
            this.obj = obj;
            this.childrenNode = new ArrayList<>(intialize);
        }

        @Override
        public boolean equals(Object o) {
            return obj == o;
        }

        @Override
        public int hashCode() {
            return obj.hashCode();
        }

        @Override
        public String toString() {
            return "GraphNode{" +
                    "obj=" + obj +
                    '}';
        }
    }
}
