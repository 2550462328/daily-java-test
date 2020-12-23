package cn.zhanghui.demo.daily.base.collection.list;

/**
 * 编写一个程序，找到两个单链表相交的起始节点。
 * 如下面的两个链表：
 *
 * 4 -> 1 -> 8 -> 2 -> 1 -> NULL
 * 1 -> 1 -> 8 -> 2 -> 1 -> NULL
 *
 * 输出节点 1
 *
 * 在第2个节点开始相交。
 *
 * @author: ZhangHui
 * @date: 2020/12/3 17:57
 * @version：1.0
 */
public class FindTwoLinkListIntersectionNode {

    public static class ListNode {
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

    private static ListNode node1 = new ListNode(1);

    private static ListNode headA = new ListNode(4);
    private static ListNode headB = new ListNode(5);

//    private static ListNode headA = node1;
//    private static ListNode headB = node1;

    static {
        ListNode node1 = new ListNode(8);
        ListNode node2 = new ListNode(4);
        ListNode node3 = new ListNode(5);

        headA.next = new ListNode(1);
        headA.next.next = node1;
        headA.next.next.next = node2;
        headA.next.next.next.next = node3;

        headB.next = new ListNode(6);
        headB.next.next = new ListNode(1);
        headB.next.next.next = node1;
        headB.next.next.next.next = node2;
        headB.next.next.next.next.next = node3;
    }
    
    public static void main(String[] args) {
        ListNode resultNode = new FindTwoLinkListIntersectionNode().getIntersectionNode_advanced(headA,headB);
        System.out.println(resultNode.val);
        pringLinkList(headA);
        System.out.println();
        pringLinkList(headB);
    }

    /**
     * 将链表A翻转，然后比较原链表B和现链表B
     */
    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {

        if(headA == null || headB == null){
            return null;
        }

        ListNode headB_copy = new ListNode(headB.val);
        ListNode listNodeB_copy = headB_copy,listNodeB = headB;

        // 记录原链表B的值
        while(listNodeB != null && listNodeB.next != null){
            listNodeB_copy.next = new ListNode(listNodeB.next.val);
            listNodeB = listNodeB.next;
            listNodeB_copy = listNodeB_copy.next;
        }

        // 翻转链表A
        ListNode reverseHeadA = reverseLinkList(headA);

        // 比较现链表B和原链表B的节点值
        ListNode intersectionNode = null;
        ListNode listNodeB2 = headB;
        ListNode listNodeA = headA;
        ListNode prev = null;
        while(listNodeB2 != null){

            //这里考虑到链表B是链表A的子串
            if(headB_copy == null || headB_copy.val != listNodeB2.val){
                intersectionNode = prev;
                break;
            }else if(listNodeA == listNodeB2){ // 这里考虑到链表A是链表B的子串
                intersectionNode = listNodeA;
                break;
            }

            // 记录上一个相等的值
            prev = listNodeB2;
            listNodeB2 = listNodeB2.next;
            headB_copy = headB_copy.next;
        }

        // 最后将链表A还原
        reverseLinkList(reverseHeadA);
        return intersectionNode;
    }

    private ListNode reverseLinkList(ListNode head){

        ListNode prev = null;

        ListNode curr = head;

        while(curr != null){
            ListNode tempNode = curr.next;
            curr.next = prev;
            prev = curr;
            curr = tempNode;
        }

        return prev;
    }

    /**
     * 双指针算法，nodeA从链表A头节点出发，nodeB从链表B头节点出发
     * 假如将链表A和链表B的长度连在一起，那么链表A和链表B有交点的话，那么nodeA和nodeB到交点的距离是一样的
     * 所以 我们只需要在nodeA遍历完链表A，然后指向链表B的头节点即可，同理nodeB也是
     * 算法就是 l1 + all + l2 = l2 + all + l1
     */
    public ListNode getIntersectionNode_advanced(ListNode headA, ListNode headB) {

        if (headA == null || headB == null) {
            return null;
        }

        ListNode nodeA = headA;
        ListNode nodeB = headB;

        while(nodeA != nodeB){

            nodeA = nodeA.next;
            nodeB = nodeB.next;

            if(nodeA == null && nodeB == null){
                return null;
            }
            if(nodeA == null){
                nodeA = headB;
            }
            if(nodeB == null){
                nodeB = headA;
            }
        }

        return nodeA;
    }

    private static void pringLinkList(ListNode head){
        while(head != null){
            System.out.print(head.val + " -> " );
            head = head.next;
        }
    }
}
