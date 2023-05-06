//给你一个链表，两两交换其中相邻的节点，并返回交换后链表的头节点。你必须在不修改节点内部的值的情况下完成本题（即，只能进行节点交换）。 
//
// 
//
// 示例 1： 
// 
// 
//输入：head = [1,2,3,4]
//输出：[2,1,4,3]
// 
//
// 示例 2： 
//
// 
//输入：head = []
//输出：[]
// 
//
// 示例 3： 
//
// 
//输入：head = [1]
//输出：[1]
// 
//
// 
//
// 提示： 
//
// 
// 链表中节点的数目在范围 [0, 100] 内 
// 0 <= Node.val <= 100 
// 
//
// Related Topics 递归 链表 👍 1810 👎 0


//leetcode submit region begin(Prohibit modification and deletion)
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

package cn.zhanghui.demo.daily.leetcode.editor.cn;

import cn.zhanghui.demo.daily.leetcode.editor.cn.component.ListNode;

import java.util.LinkedList;

class Solution24 {
    public ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) return head;

        // 这里也可以直接用个节点当中转站即可
        LinkedList<ListNode> stack = new LinkedList<>();

        ListNode next = head.next;
        ListNode root = next;

        stack.add(head);
        ListNode lastExchange = null;
        while (stack.size() > 0 || next != null) {
            if (next == null) break;
            ListNode temp = next.next;
            if (stack.size() > 0) {
                ListNode poll = stack.poll();
                next.next = poll;
                poll.next = temp;
                if (lastExchange == null) {
                    lastExchange = poll;
                } else {
                    lastExchange.next = next;
                    lastExchange = poll;
                }
            } else {
                stack.add(next);
            }
            next = temp;
        }
        return root;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
