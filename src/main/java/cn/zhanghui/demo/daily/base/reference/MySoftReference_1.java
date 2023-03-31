package cn.zhanghui.demo.daily.base.reference;

import java.lang.ref.SoftReference;

/**
 * @ClassName: MySoftReference.java
 * @Description: 当软应用对象被其他对象引用时，那么这个对象就变成了强引用
 * @author: ZhangHui
 * @date: 2019年8月27日 下午3:16:21
 */
public class MySoftReference_1 {
    static class A {
        @Override
        public String toString() {
            return "i am instanceA";
        }
    }

    static class B {
        private A strongRef;

        public void setStrongRef(A ref) {
            this.strongRef = ref;
        }
    }

    public static void main(String[] args) {
        MySoftReference_1.A instanceA = new MySoftReference_1.A();
        SoftReference<MySoftReference_1.A> cache = new SoftReference<MySoftReference_1.A>(instanceA);
        instanceA = null;
        try {
            // instanceA 处于软可达状态，可能在某个时间后被垃圾回收器回收
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MySoftReference_1.B instanceB = new MySoftReference_1.B();
        instanceA = cache.get();
        if (instanceA == null) {
            instanceA = new MySoftReference_1.A();
            cache = new SoftReference<MySoftReference_1.A>(instanceA);
        }
        instanceB.setStrongRef(instanceA);
        instanceA = null;
        // instanceA现在与cache对象存在软引用并且与B对象存在强引用，所以它不会被垃圾回收器回收
    }
}
