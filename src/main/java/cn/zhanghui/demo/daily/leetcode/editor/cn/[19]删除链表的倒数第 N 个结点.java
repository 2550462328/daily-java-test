//给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。 
//
// 
//
// 示例 1： 
// 
// 
//输入：head = [1,2,3,4,5], n = 2
//输出：[1,2,3,5]
// 
//
// 示例 2： 
//
// 
//输入：head = [1], n = 1
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：head = [1,2], n = 1
//输出：[1]
// 
//
// 
//
// 提示： 
//
// 
// 链表中结点的数目为 sz 
// 1 <= sz <= 30 
// 0 <= Node.val <= 100 
// 1 <= n <= sz 
// 
//
// 
//
// 进阶：你能尝试使用一趟扫描实现吗？ 
//
// Related Topics 链表 双指针 👍 2513 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
package cn.zhanghui.demo.daily.leetcode.editor.cn;

import cn.zhanghui.demo.daily.leetcode.editor.cn.component.ListNode;

/**
 * Definition for singly-linked list.
 * public class ListNode {
 * int val;
 * ListNode next;
 * ListNode() {}
 * ListNode(int val) { this.val = val; }
 * ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution19 {
    /**
     * 双节点追逐法
     *
     * @param head
     * @param n
     * @return
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        if(head == null || head.next == null){
            return null;
        }
        ListNode l1 = head;
        ListNode l2 = head;
        int i = 0;
        while (l1 != null) {
            l1 = l1.next;
            if (i++ > n) {
                l2 = l2.next;
            }
        }

        if(l2 == head && i <= n) {
            return l2.next;
        }
        ListNode next = l2.next;
        if (next != null) {
            l2.next = next.next;
        }

        return head;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
