package cn.zhanghui.demo.daily.base.collection.list;

/**
 * @ClassName CombineTwoLinkList
 * @Description: 将两个升序链表合并为一个新的 升序 链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
 * <p>
 * 示例：
 * <p>
 * 输入：1->2->4, 1->3->4
 * 输出：1->1->2->3->4->4
 * @Author: ZhangHui
 * @Date: 2020/9/22
 * @Version：1.0
 */
public class CombineTwoLinkList {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    private static ListNode head1 = new ListNode(1);

    private static ListNode head2 = new ListNode(1);

    static {
        head1.next = new ListNode(2);
        head1.next.next = new ListNode(4);
//        head1.next.next.next = new ListNode(4);
//        head1.next.next.next.next = new ListNode(5);

        head2.next = new ListNode(3);
        head2.next.next = new ListNode(4);
//        head2.next.next.next = new ListNode(4);
//        head2.next.next.next.next = new ListNode(5);
    }

    public static void main(String[] args) {
        CombineTwoLinkList combineTwoLinkList = new CombineTwoLinkList();

        ListNode newHead = combineTwoLinkList.mergeTwoListsWithRecursive(head1, head2);

        while (newHead != null) {
            System.out.println(newHead.val);
            newHead = newHead.next;
        }
    }

    /**
     * 使用迭代解决
     * 优化1 ： 采用newHead 直接指向l1或者l2，不需要判断l1和l2的大小
     * 优化2： 不需要事先判断l1和l2的null值，最后直接返回newHead的next即可
     * 优化3： 最后l1和l2肯定会有一个先迭代完成，我们让newHead直接指向剩下的链表即可
     */
    public ListNode mergeTwoListsWithIterator(ListNode l1, ListNode l2) {

        ListNode newHead = new ListNode(-1);

        ListNode tempHead = newHead;

        while (l1 != null && l2 != null) {
            if (l1.val < l2.val) {
                tempHead.next = l1;
                l1 = l1.next;
            } else {
                tempHead.next = l2;
                l2 = l2.next;
            }
            tempHead = tempHead.next;
        }

        tempHead.next = l1 == null ? l2 : l1;

        return newHead.next;
    }

    /**
     * 使用递归解决
     */
    public ListNode mergeTwoListsWithRecursive(ListNode l1, ListNode l2) {

        if (l1 == null) {
            return l2;
        } else if (l2 == null) {
            return l1;
        } else if (l1.val < l2.val) {
            l1.next = mergeTwoListsWithIterator(l1.next, l2);
            return l1;
        } else {
            l2.next = mergeTwoListsWithIterator(l1, l2.next);
            return l2;
        }
    }
}
