package cn.zhanghui.demo.daily.leetcode.editor.cn.component;

/**
 * Description:
 *
 * @author createdBy huizhang43.
 * @date createdAt 2023/4/3 11:12
 **/
public class ListNode {
    public int val;
    public ListNode next;

    public ListNode() {
    }

    public ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
