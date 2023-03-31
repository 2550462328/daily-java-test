package cn.zhanghui.demo.daily.base.collection.stack;

/**
 * 设计一个支持 push ，pop ，top 操作，并能在常数时间内检索到最小元素的栈。
 * <p>
 * push(x) —— 将元素 x 推入栈中。
 * pop() —— 删除栈顶的元素。
 * top() —— 获取栈顶元素。
 * getMin() —— 检索栈中的最小元素。
 *
 * @author: ZhangHui
 * @date: 2020/10/16 9:25
 * @version：1.0
 */
public class MinStack {

    class Node {
        private int value;

        private Node next;

        public Node(int value) {
            this.value = value;
        }
    }

    public Node root = null;

    /**
     * initialize your data structure here.
     */
    public MinStack() {

    }

    public void push(int x) {
        Node dump = root;

        Node node = new Node(x);
        node.next = dump;

        root = node;
    }

    public void pop() {
        if (root != null) {
            root = root.next;
        }
    }

    public int top() {
        return root == null ? 0 : root.value;
    }

    public int getMin() {

        Node dump = root;

        if (root == null) return 0;

        int min = dump.value;

        while (dump.next != null) {
            Node curr = dump.next;
            min = Math.min(min, curr.value);
            dump = curr;
        }
        return min;
    }

    public static void main(String[] args) {
        MinStack obj = new MinStack();
        obj.push(2);
        obj.pop();
        System.out.println(obj.top());
        System.out.println(obj.getMin());
    }
}
