package cn.zhanghui.demo.daily.base.collection.list;

/**
 * @ClassName ReverseLinkList
 * @Description: 反转一个单链表。
 * 示例:
 * 输入: 1->2->3->4->5->NULL
 * 输出: 5->4->3->2->1->NULL
 * @Author: ZhangHui
 * @Date: 2020/9/21
 * @Version：1.0
 */
public class ReverseLinkList {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    private static ListNode head = new ListNode(1);

    static {
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);
    }

    public static void main(String[] args) {
        ReverseLinkList reverseLinkList = new ReverseLinkList();

        ListNode newHead = reverseLinkList.reverseListIterator(head);

        while (newHead != null) {
            System.out.println(newHead.val);
            newHead = newHead.next;
        }
    }

    /**
     * 通过迭代的方式解决
     */
    public ListNode reverseListIterator(ListNode head) {

        ListNode curr = head;
        ListNode prev = null;

        while (curr != null) {
            ListNode tempNode = curr.next;
            curr.next = prev;
            prev = curr;
            curr = tempNode;
        }
        return prev;
    }

    /**
     * 通过递归的方式解决
     */
    public ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode p = reverseListRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return p;
    }
}
