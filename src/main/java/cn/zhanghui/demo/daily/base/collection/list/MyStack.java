package cn.zhanghui.demo.daily.base.collection.list;

/**
 * @author: ZhangHui
 * @description: 这是一个自定义栈
 * @date: 2019/6/26
 */
public class MyStack<T> {
    private Object[] content;
    private int size = 0;
    private final int DEFAULT_CONTENT_SIZE = 16;
    public MyStack(){
        content = new Object[DEFAULT_CONTENT_SIZE];
    }

    public void push(T t){
        ensureCapablity();
        content[size++] = t;
    }

    public T poll(){
        if(size == 0){
            return null;
        }
        return (T)content[--size];
    }

    public int size(){
        return size;
    }
    public void ensureCapablity(){
        if(size >= content.length - 1){
            content = new Object[2 * content.length - 1];
        }
    }

    public static void main(String[] args) {
        MyStack<Integer> stack = new MyStack<Integer>();
        stack.push(1232132312);
        System.out.println(stack.size());
        System.out.println(stack.poll());
        System.out.println(stack.size());
    }
}
