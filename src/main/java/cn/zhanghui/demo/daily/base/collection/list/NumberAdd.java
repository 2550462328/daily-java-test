package cn.zhanghui.demo.daily.base.collection.list;

/**
 * @author ZhangHui
 * @version 1.0
 * @className NumberAdd
 * @description 给定两个非空链表来表示两个非负整数。位数按照逆序方式存储，它们的每个节点只存储单个数字。将两数相加返回一个新的链表。
 * 你可以假设除了数字 0 之外，这两个数字都不会以零开头。
 *
 * 示例：
 * 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
 * 输出：7 -> 0 -> 8
 * 原因：342 + 465 = 807
 *
 * 求解思路：我们使用原始的加法器，从末尾开始加，和大于10产生进位，下一位的和需要加上进位
 * 需要考虑两数为空、两数位数不同和溢出一位的情况（比如99 + 1 = 100）
 *
 * 为什么不直接将链表转为具体的long或者int，在进行计算后转换成链表输出呢？
 *
 * 因为我们考虑的情况是链表的长度是趋向于很大的，会出现long装不住的情况，有人说用BigInteger，那就是较真了
 *
 * 为什么不直接设置一个Head等于计算结果的头节点呢？而要设置一个dummyHead.next = Head？
 *
 *小技巧：对于链表问题，返回结果为头结点时，通常需要先初始化一个预先指针 pre，该指针的下一个节点指向真正的头结点head。
 * 使用预先指针的目的在于链表初始化时无可用节点值，而且链表构造过程需要指针移动，进而会导致头指针丢失，无法返回结果。
 *
 * 如果没pre节点的话，则必须编写额外的条件语句来初始化表头的值。
 *
 * @date 2020/8/4
 */
public class NumberAdd {
    private static class ListNode {
        int val;
        ListNode next;

        ListNode(){};
        ListNode(int val) {
            this.val = val;
        }

        ListNode(int item, ListNode next) {
            this.val = item;
            this.next = next;
        }
    }

    /**
     * 模拟加法运算
     */
    private static ListNode getListNodeSum(ListNode node1_head, ListNode node2_head) {
        ListNode dummyHead = new ListNode(0);
        // 进位
        int carry = 0;
        ListNode pNode1 = node1_head, pNode2 = node2_head, currNode = dummyHead;

        while (pNode1 != null || pNode2 != null) {
            int p1_val = pNode1 == null ? 0 : pNode1.val;
            int p2_val = pNode2 == null ? 0 : pNode2.val;

            int sum = p1_val + p2_val+ carry;

            int curr_val = sum % 10;
            carry = sum / 10;

            currNode.next = new ListNode(curr_val);
            currNode = currNode.next;

            if(pNode1 != null) {
                pNode1 = pNode1.next;
            }

            if(pNode2 != null) {
                pNode2 = pNode2.next;
            }
        }

        if(carry > 0){
            currNode.next = new ListNode(carry);
        }

        return dummyHead.next;
    }

    public static void main(String[] args) {
        ListNode node1_head = new ListNode(0);
        ListNode node2_head = new ListNode(0);

        ListNode currNode1 = node1_head;
        ListNode currNode2 = node2_head;

//        int[] arr1 = {2,4,3};
//        int[] arr2 = {5,6,4};

//        int[] arr1 = {0,1};
//        int[] arr2 = {0,1,2};

//        int[] arr1 = {};
//        int[] arr2 = {0,1};

        int[] arr1 = {9,9};
        int[] arr2 = {1};

        int max = Math.max(arr1.length,arr2.length);

        for(int i = 0; i < max; i++){
            if(i < arr1.length) {
                currNode1.next = new ListNode(arr1[i]);
                currNode1 = currNode1.next;
            }

            if(i < arr2.length) {
                currNode2.next = new ListNode(arr2[i]);
                currNode2 = currNode2.next;
            }
        }

        ListNode resultNode = getListNodeSum(node1_head,node2_head);

        while(resultNode.next != null){
            System.out.println(resultNode.next.val);
            resultNode = resultNode.next;
        }
    }
}