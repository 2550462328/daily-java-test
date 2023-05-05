//给你一个链表数组，每个链表都已经按升序排列。 
//
// 请你将所有链表合并到一个升序链表中，返回合并后的链表。 
//
// 
//
// 示例 1： 
//
// 输入：lists = [[1,4,5],[1,3,4],[2,6]]
//输出：[1,1,2,3,4,4,5,6]
//解释：链表数组如下：
//[
//  1->4->5,
//  1->3->4,
//  2->6
//]
//将它们合并到一个有序链表中得到。
//1->1->2->3->4->4->5->6
// 
//
// 示例 2： 
//
// 输入：lists = []
//输出：[]
// 
//
// 示例 3： 
//
// 输入：lists = [[]]
//输出：[]
// 
//
// 
//
// 提示： 
//
// 
// k == lists.length 
// 0 <= k <= 10^4 
// 0 <= lists[i].length <= 500 
// -10^4 <= lists[i][j] <= 10^4 
// lists[i] 按 升序 排列 
// lists[i].length 的总和不超过 10^4 
// 
//
// Related Topics 链表 分治 堆（优先队列） 归并排序 👍 2427 👎 0


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

class Solution23 {
    class Status implements Comparable<Status> {
        int val;
        ListNode ptr;

        Status(int val, ListNode ptr) {
            this.val = val;
            this.ptr = ptr;
        }

        public int compareTo(Status status2) {
            return this.val - status2.val;
        }
    }

    PriorityQueue<Status> queue = new PriorityQueue<Status>();

    /**
     * 构造优先队列 从大到小存放节点 最后弹出形成新的链表
     *
     * @param lists
     * @return
     */
    public ListNode mergeKLists(ListNode[] lists) {
        for (ListNode node : lists) {
            if (node != null) {
                queue.offer(new Status(node.val, node));
            }
        }
        ListNode head = new ListNode(0);
        ListNode tail = head;
        while (!queue.isEmpty()) {
            Status f = queue.poll();
            tail.next = f.ptr;
            tail = tail.next;
            if (f.ptr.next != null) {
                queue.offer(new Status(f.ptr.next.val, f.ptr.next));
            }
        }
        return head.next;
    }

    /**
     * 推荐 分治法  基于数组偏移量
     *
     * @param lists
     * @return
     */
    public ListNode mergeKLists2(ListNode[] lists) {
        return merge(lists, 0, lists.length - 1);
    }

    public ListNode merge(ListNode[] lists, int l, int r) {
        if (l == r) {
            return lists[l];
        }
        if (l > r) {
            return null;
        }
        int mid = (l + r) >> 1;
        return mergeTwoLists(merge(lists, l, mid), merge(lists, mid + 1, r));
    }

    public ListNode mergeTwoLists(ListNode a, ListNode b) {
        if (a == null || b == null) {
            return a != null ? a : b;
        }
        ListNode head = new ListNode(0);
        ListNode tail = head, aPtr = a, bPtr = b;
        while (aPtr != null && bPtr != null) {
            if (aPtr.val < bPtr.val) {
                tail.next = aPtr;
                aPtr = aPtr.next;
            } else {
                tail.next = bPtr;
                bPtr = bPtr.next;
            }
            tail = tail.next;
        }
        tail.next = (aPtr != null ? aPtr : bPtr);
        return head.next;
    }

    /**
     * 分治法 基于数组复制 效率微差
     *
     * @param lists
     * @return
     */
    public ListNode mergeKLists1(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;
        if (lists.length == 1) return lists[0];

        int mid = lists.length / 2;
        ListNode[] l1 = new ListNode[mid];
        ListNode[] l2 = new ListNode[lists.length - mid];
        System.arraycopy(lists, 0, l1, 0, mid);
        System.arraycopy(lists, mid, l2, 0, lists.length - mid);

        return mergeSubList(l1, l2);
    }

    private ListNode mergeSubList(ListNode[] l1, ListNode[] l2) {
        ListNode h1, h2;

        if (l1 == null || l1.length == 0) {
            h1 = null;
        } else if (l1.length == 1) {
            h1 = l1[0];
        } else {
            int mid = l1.length / 2;
            ListNode[] l11 = new ListNode[mid];
            ListNode[] l12 = new ListNode[l1.length - mid];
            System.arraycopy(l1, 0, l11, 0, mid);
            System.arraycopy(l1, mid, l12, 0, l1.length - mid);

            h1 = mergeSubList(l11, l12);
        }

        if (l2 == null || l2.length == 0) {
            h2 = null;
        } else if (l2.length == 1) {
            h2 = l2[0];
        } else {
            int mid = l2.length / 2;
            ListNode[] l21 = new ListNode[mid];
            ListNode[] l22 = new ListNode[l2.length - mid];
            System.arraycopy(l2, 0, l21, 0, mid);
            System.arraycopy(l2, mid, l22, 0, l2.length - mid);

            h2 = mergeSubList(l21, l22);
        }
        if (h1 == null && h2 == null) {
            return null;
        } else if (h1 == null) {
            return h2;
        } else if (h2 == null) {
            return h1;
        } else {
            return mergeTwoLists(h1, h2);
        }
    }

    public ListNode mergeTwoLists1(ListNode list1, ListNode list2) {

        if (list1 == null) {
            return list2;
        }
        if (list2 == null) {
            return list1;
        }
        ListNode head;

        if (list1.val < list2.val) {
            head = new ListNode(list1.val);
            list1 = list1.next;
        } else {
            head = new ListNode(list2.val);
            list2 = list2.next;
        }
        ListNode next = head;
        while (list1 != null && list2 != null) {
            if (list2.val < list1.val) {
                next.next = new ListNode(list2.val);
                list2 = list2.next;
            } else {
                next.next = new ListNode(list1.val);
                list1 = list1.next;
            }
            next = next.next;
        }
        if (list1 != null) {
            next.next = list1;
        }
        if (list2 != null) {
            next.next = list2;
        }
        return head;
    }
}
//leetcode submit region end(Prohibit modification and deletion)
