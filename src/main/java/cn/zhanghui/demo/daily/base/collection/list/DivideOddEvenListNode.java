package cn.zhanghui.demo.daily.base.collection.list;


/**
 * 给定一个单链表，把所有的奇数节点和偶数节点分别排在一起。请注意，这里的奇数节点和偶数节点指的是节点编号的奇偶性，而不是节点的值的奇偶性。
 * <p>
 * 请尝试使用原地算法完成。你的算法的空间复杂度应为 O(1)，时间复杂度应为 O(nodes)，nodes 为节点总数。
 * <p>
 * 示例 1:
 * <p>
 * 输入: 1->2->3->4->5->NULL
 * 输出: 1->3->5->2->4->NULL
 *
 * @author: ZhangHui
 * @date: 2020/12/2 9:37
 * @version：1.0
 */
public class DivideOddEvenListNode {

    public static void main(String[] args) {
        new DivideOddEvenListNode().test();
    }

    public void test() {
        ListNode head = new ListNode(1);
        ListNode dummy = head;
        dummy.next = new ListNode(2);
        dummy = dummy.next;
        dummy.next = new ListNode(3);
        dummy = dummy.next;
        dummy.next = new ListNode(4);
        dummy = dummy.next;
        dummy.next = new ListNode(5);

        ListNode resultHead = oddEvenList(head);

        while (resultHead != null) {
            System.out.print(resultHead.val + " -> ");
            resultHead = resultHead.next;
        }
    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    /**
     * 解决思路就是分别建造奇偶节点链表，然后奇链表的最后节点指向偶链表
     */
    public ListNode oddEvenList(ListNode head) {

        if (head == null || head.next == null) {
            return head;
        }
        int index = 2;

        ListNode evenHead = head.next;
        ListNode evenNode = evenHead, oddNode = head;
        ListNode next = evenNode.next;

        while (next != null) {
            if ((++index & 1) != 0) {
                oddNode.next = next;
                oddNode = oddNode.next;
            } else {
                evenNode.next = next;
                evenNode = evenNode.next;
            }
            next = next.next;
        }
        oddNode.next = evenHead;

        if (evenNode != null) {
            evenNode.next = null;
        }
        return head;
    }

    /**
     * 相比较上一种解法会更优雅一点，没有对奇偶性进行判断，而是直接让奇偶链表交叉遍历即可
     */
    public ListNode oddEvenList_advanced(ListNode head) {
        if (head == null) {
            return null;
        }

        ListNode evenHead = head.next;

        ListNode oddNode = head, evenNode = evenHead;

        while (evenNode != null && evenNode.next != null) {
            oddNode.next = evenNode.next;
            oddNode = oddNode.next;
            evenNode.next = oddNode.next;
            evenNode = evenNode.next;
        }
        oddNode.next = evenHead;
        return head;
    }
}
