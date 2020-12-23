package cn.zhanghui.demo.daily.base.collection.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * @ClassName: MyLinkedList.java
 * @Description: 这是一个简单的自定义的LinkedList，单链表
 * @Author: ZhangHui
 * @Date: 2019年7月25日 上午11:11:38
 **/
public class MyLinkedList implements List {

	// 头节点，不存储数据，存储首节点地址
	Node head = new Node();

	private int size;

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Object e) {
		add(size, e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		for (Node x = head; head != null;) {
			Node xNext = x.getNext();
			x.setNext(null);
			x.setData(null);
			x = xNext;
		}
		head = null;
		size = 0;
	}

	@Override
	public Object get(int index) {
		Node p = head;
		// 从头进行查找
		for (int i = 0; i <= index; i++) {
			p = p.getNext();
		}
		return p.getData();
	}

	@Override
	public Object set(int index, Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(int index, Object element) {
		Node p = head;
		for (int i = 0; i < index; i++) {
			p = p.getNext();
		}
		Node newNode = new Node(element, p.getNext());
		p.setNext(newNode);
		size++;
	}

	@Override
	public Object remove(int index) {
		checkIndex(index);
		Node p = head;
		Object tData = null;
		if (index == size - 1) {
			for (int i = 0; i < index; i++) {
				p = p.getNext();
			}
			tData = p.getNext().getData();
			p.setNext(null);

		} else {
			for (int i = 0; i < index; i++) {
				p = p.getNext();
			}
			tData = p.getNext().getData();
			p.setNext(p.getNext().getNext());
		}
		size--;
		return tData;
	}

	@Override
	public int indexOf(Object o) {
		Node p = head;
		if (o == null) {
			for (int i = 0; i < size; i++) {
				p = p.getNext();
				if (p.getData() == null) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				p = p.getNext();
				if (o.equals(p.getData())) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		return 0;
	}

	@Override
	public ListIterator listIterator() {
		return null;
	}

	@Override
	public ListIterator listIterator(int index) {
		return null;
	}

	@Override
	public List subList(int fromIndex, int toIndex) {
		return null;
	}

	private void checkIndex(int index) {
		if (index < 0 || index > size - 1) {
			throw new RuntimeException("IndexOutOfBoundException, aim index " + index + ",now size " + size);
		}
	}

	@Override
	public String toString() {
		if (size == 0)
			return "[]";
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		Node p = head;
		for (int i = 0; i < size; i++) {
			p = p.getNext();
			if (i == size - 1) {
				sb.append(p.getData());
			} else {
				sb.append(p.getData() + ",");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public static void main(String[] args) {
		MyLinkedList list = new MyLinkedList();
		list.add("a");
		list.add("b");
		list.add("c");
		System.out.println(list);
		System.out.println(list.get(0));
		list.remove(3);
		System.out.println(list);
	}

	class Node {
		Object data;
		Node next;

		public Node() {
			super();
		}

		public Node(Object data, Node next) {
			super();
			this.data = data;
			this.next = next;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

		public Node getNext() {
			return next;
		}

		public void setNext(Node next) {
			this.next = next;
		}

	}
}
