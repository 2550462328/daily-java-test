package cn.zhanghui.demo.daily.base.collection.list;

/**
 * @ClassName IsRingLinkList
 * @Description: 给定一个链表，判断链表中是否有环。
 * <p>
 * 如果链表中存在环，则返回 true 。 否则，返回 false 。
 * <p>
 * 你能用 O(1)（即，常量）内存解决此问题吗？
 * @Author: ZhangHui
 * @Date: 2020/9/23
 * @Version：1.0
 */
public class IsRingLinkList {

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
        IsRingLinkList isRingLinkList = new IsRingLinkList();

        System.out.println(isRingLinkList.hasCycle(head));
    }

    /**
     * 用快慢指针，它们迟早会相遇
     * 还有一种用哈希存放，如果有重复节点了，不就有环形了么
     */
    public boolean hasCycle(ListNode head) {

        if(head == null){
            return false;
        }

        ListNode dummy1 = new ListNode(0);
        ListNode dummy2 = new ListNode(0);

        dummy1.next = head;
        dummy2.next = head;

        ListNode fast = dummy1;
        ListNode slow = dummy2;

        while(fast != null && fast.next != null){
            if(fast == slow){
                return true;
            }

            fast = fast.next.next;
            slow = slow.next;
        }
        return false;
    }


}
