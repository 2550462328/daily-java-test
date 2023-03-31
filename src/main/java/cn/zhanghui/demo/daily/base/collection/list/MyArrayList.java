package cn.zhanghui.demo.daily.base.collection.list;

import java.util.*;

/**
 * @ClassName: MyArrayList.java
 * @Description: 这是一个自定义的ArrayList 顺序表，底层维护一个数据，长度可以动态变化
 * @author: ZhangHui
 * @date: 2019年7月22日 下午12:36:38
 */
public class MyArrayList implements List {

    transient Object[] elementData;

    private int size;

    public MyArrayList(int initCapablity) {
        elementData = new Object[initCapablity];
    }

    public MyArrayList() {
        this(4);
    }

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
        return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return null;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return null;
    }

    @Override
    public boolean add(Object e) {
        ensureCapablity();
        elementData[size++] = e;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) {
                    remove(i);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (o.equals(elementData[i])) {
                    remove(i);
                }
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection c) {
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public Object get(int index) {
        if (index < 0 || index >= size) {
            throw new RuntimeException("数组索引越界异常！");
        }
        return elementData[index];
    }

    @Override
    public Object set(int index, Object element) {
        return null;
    }

    @Override
    public void add(int index, Object element) {
        ensureCapablity();
        for (int j = size; j > index; j--) {
            elementData[j] = elementData[j - 1];
        }
        elementData[index] = element;
        size++;
    }

    @Override
    public Object remove(int index) {
        Object object = elementData[index];
        for (int j = index; j < size - 1; j++) {
            elementData[j] = elementData[j + 1];
        }
        elementData[size - 1] = null;
        size--;
        return object;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
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

    @Override
    public String toString() {
        return Arrays.toString(elementData);
    }

    public void ensureCapablity() {
        if (size == elementData.length) {
            grow();
        }
    }

    private void grow() {
        elementData = Arrays.copyOf(elementData, size + (size >> 1));
    }

    public static void main(String[] args) throws ClassNotFoundException {
        ArrayList<String> list = new ArrayList<String>();
        list.add("a");
        list.add("b");
        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("a");
        System.out.println(list.removeAll(list1));
    }
}
