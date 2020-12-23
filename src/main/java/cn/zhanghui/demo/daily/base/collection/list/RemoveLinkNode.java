package cn.zhanghui.demo.daily.base.collection.list;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangHui
 * @version 1.0
 * @className RemoveLinkNode
 * @description 删除链表中的节点
 * @date 2020/9/17
 */
public class RemoveLinkNode {

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
    }

    public static void main(String[] args) {
        RemoveLinkNode removeLinkNode = new RemoveLinkNode();

        removeLinkNode.removeNthFromEndMap(head,1);

        ListNode temp = head;

        while(temp != null){
            System.out.println(temp.val);
            temp = temp.next;
        }
    }

    /**
     * 使用HashMap来计算倒数第n个数
     * @author ZhangHui
     * @date 2020/9/18
     */
    public ListNode removeNthFromEndMap(ListNode head, int n) {

        Map<Integer,ListNode> map = new HashMap<>();

        ListNode temp = head;

        int index = 0;

        while(head != null){
            map.put(index++,head);
            head = head.next;
        }

        ListNode node = map.get(map.size() - n);

        if(node.next != null){
            node.val = node.next.val;
            node.next = node.next.next;
        }else{
            ListNode preNode = map.get(map.size() - n - 1);
            if(preNode == null){
                temp = null;
            }else{
                preNode.next = null;
            }
        }

        return temp;
    }

    /**
     * 借助双指针，两指针间隔n，当第一个指针到底终点的时候，第二个指针指向的就是我们要移除的节点
     */
    public ListNode removeNthFromEndTwoPointer(ListNode head, int n) {

        ListNode dummy = new ListNode(0);

        dummy.next = head;

        ListNode preNNode = dummy;
        ListNode nextNNode = dummy;

        for (int i = 0; i <= n; i++) {
            nextNNode = nextNNode.next;
        }

        while(nextNNode != null){
            preNNode = preNNode.next;
            nextNNode = nextNNode.next;
        }

        preNNode.next = preNNode.next.next;
        return dummy.next;
    }
}
