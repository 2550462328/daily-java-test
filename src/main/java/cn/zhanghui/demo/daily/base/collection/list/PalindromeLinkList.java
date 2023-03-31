package cn.zhanghui.demo.daily.base.collection.list;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName PalindromeLinkList
 * @Description: 请判断一个链表是否为回文链表。
 * 示例 1:
 * 输入: 1->2
 * 输出: false
 * <p>
 * 示例 2:
 * 输入: 1->2->2->1
 * 输出: true
 * @Author: ZhangHui
 * @Date: 2020/9/22
 * @Version：1.0
 */
public class PalindromeLinkList {

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
        head.next.next = new ListNode(2);
        head.next.next.next = new ListNode(1);
//        head.next.next.next.next = new ListNode(5);
    }

    public static void main(String[] args) {
        PalindromeLinkList palindromeLinkList = new PalindromeLinkList();

        System.out.println(palindromeLinkList.isPalindromeWithReverseLinkList(head));

    }

    /**
     * 使用HashMap装载
     */
    public boolean isPalindromeWithHashMap(ListNode head) {
        Map<Integer, Integer> map = new HashMap<>();

        int index = 0;

        while (head != null) {
            map.put(index++, head.val);
            head = head.next;
        }

        int i = 0, j = map.size() - 1;

        while (i < j) {
            if (!map.get(i).equals(map.get(j))) {
                return false;
            }
            i++;
            j--;
        }
        return true;
    }

    /**
     * 通过反转后半部分链表解决问题
     */
    public boolean isPalindromeWithReverseLinkList(ListNode head) {

        if (head == null) {
            return true;
        }

        ListNode dummy1 = head;
        ListNode dummy2 = head;

        // 寻找后半截链表
        while (dummy2.next != null && dummy2.next.next != null) {
            dummy2 = dummy2.next.next;
            dummy1 = dummy1.next;
        }

        // 反转后半截链表
        ListNode reverseNode = reverseListIterator(dummy1.next);

        ListNode tempNode = reverseNode;
        ListNode tempHead = head;

        boolean isPalindrome = true;

        // 对比反转后的半截链表和原先的前半截
        while (tempNode != null) {
            if (tempNode.val != tempHead.val) {
                isPalindrome = false;
                break;
            }
            tempNode = tempNode.next;
            tempHead = tempHead.next;
        }

        // 还原原先反转的链表
        dummy1.next = reverseListIterator(reverseNode);

        return isPalindrome;
    }

    private ListNode reverseListIterator(ListNode head) {

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
}
