package cn.zhanghui.demo.daily.jdk8_newProp.concurrenthashmap;

import sun.misc.Unsafe;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.*;

public class ConcurrentHashMapDebug<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    private static final long serialVersionUID = 7249069246763182397L;

    // map的最大容量
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    // map的初始容量
    private static final int DEFAULT_CAPACITY = 16;

    // 虚拟机限制的最大数组长度，用在Collection.toArray()时限制大小
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    // map默认并发数(兼容jdk1.7)
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    // 扩容因子(兼容jdk1.7),jdk1.8中使用n - ( n >> 2)代替 *0.75f
    private static final float LOAD_FACTOR = 0.75f;

    // 链表转树的临界节点数
    static final int TREEIFY_THRESHOLD = 8;

    // 树转链表的临界节点数
    static final int UNTREEIFY_THRESHOLD = 6;

    // 链表转树时Node[]的长度不小于64
    static final int MIN_TREEIFY_CAPACITY = 64;

    // 每个线程帮助扩容的时候最少要处理的hash桶数，这个值如果太小会导致多线程竞争数过多
    // 在计算的时候 认为一个CPU可以处理8个线程的并发，所以每个线程需要处理的hash桶数是(table.length) / 8 / CPU个数 如果小于 16 就让他等于16
    private static final int MIN_TRANSFER_STRIDE = 16;

    // 每个扩容都唯一的生成戳的数，最小是6
    private static int RESIZE_STAMP_BITS = 16;

    // 最大的扩容线程的数量(2^16 - 1)
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

    // 移位量，把生成戳移位后保存在sizeCtl中当做扩容线程计数的基数，向反方向移位后能够反解出生成戳
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

    // 下面是一些特殊节点的hash值，正常节点hash在spread函数都会生成正数

    // 这个hash值出现在扩容的时候会有一个Forwarding的临时节点，它不存储实际的数据
    // 如果旧数组的一个hash桶中全部的节点都迁移到新数组中，旧数组就在这个hash桶中放置一个ForwardingNode
    // 读操作或者迭代读时碰到ForwardingNode时，将操作转发到扩容后的新的table数组上去执行，写操作碰见它时，则尝试帮助扩容
    static final int MOVED = -1;

    // 这个hash值出现在树的头部节点TreeBin，它指向树的根节点root
    // TreeBin维护了一个简单读写锁
    static final int TREEBIN = -2;

    // ReservationNode的hash值，ReservationNode是一个保留节点，相当于一个预留位置，不会保存实际的数据，正常情况是不会出现的
    static final int RESERVED = -3;

    // 用于和负数hash值进行 & 运算，将其转化为正数（绝对值不相等
    static final int HASH_BITS = 0x7fffffff;

    // 可使用的CPU内核数
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    // 在序列化时使用，这是为了兼容以前的版本
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("segments", Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE),
            new ObjectStreamField("segmentShift", Integer.TYPE)};

    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K, V> next;

        Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return val;
        }

        public final int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        public final String toString() {
            return key + "=" + val;
        }

        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            Object k, v, u;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
                    && (v = e.getValue()) != null && (k == key || k.equals(key)) && (v == (u = val) || v.equals(u)));
        }

        Node<K, V> find(int h, Object k) {
            Node<K, V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType)
                            && ((p = (ParameterizedType) t).getRawType() == Comparable.class)
                            && (as = p.getActualTypeArguments()) != null && as.length == 1 && as[0] == c) // type arg is
                        // c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable class), else
     * 0.
     */
    @SuppressWarnings({"rawtypes", "unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 : ((Comparable) k).compareTo(x));
    }

    // 用来返回节点数组的指定位置的节点的原子操作
    @SuppressWarnings("unchecked")
    static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i) {
        return (Node<K, V>) U.getObjectVolatile(tab, ((long) i << ASHIFT) + ABASE);
    }

    // cas操作，用来在指定位置修改值
    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i, Node<K, V> c, Node<K, V> v) {
        return U.compareAndSwapObject(tab, ((long) i << ASHIFT) + ABASE, c, v);
    }

    // 原子操作，在指定位置设置值
    static final <K, V> void setTabAt(Node<K, V>[] tab, int i, Node<K, V> v) {
        U.putObjectVolatile(tab, ((long) i << ASHIFT) + ABASE, v);
    }

    // 存放node节点的数组
    transient volatile Node<K, V>[] table;

    /** nextTable、sizeCtl、transferIndex与多线程扩容有关
     * baseCount、cellsBusy、counterCells与新的高效的并发计数方式有关
     */

    /**
     * 扩容后的新的table数组，只有在扩容时才有用
     * nextTable != null，说明扩容方法还没有真正退出，一般可以认为是此时还有线程正在进行扩容
     */
    private transient volatile Node<K, V>[] nextTable;

    /**
     * 非常重要的属性
     * sizeCtl = -1，表示有线程正在进行真正的初始化操作
     * sizeCtl = -(1 + nThreads)，表示有nThreads个线程正在进行扩容操作（实际上不是这么计算参与线程的）
     * sizeCtl > 0，表示接下来的真正的初始化操作中使用的容量，或者初始化（table.length）/扩容完成后的threshold（table.length * 3/4）
     * sizeCtl = 0，默认值，此时在真正的初始化操作中使用默认容量
     */
    private transient volatile int sizeCtl;

    /**
     * 调整大小时要分割的下一个表索引(上一个transfer任务的起始下标index 加上1)。
     * transfer时方向是从大到小的，迭代时是下标从小往大，二者方向相反，尽量减少扩容时transefer和迭代两者同时处理一个hash桶的情况
     * 顺序相反时，二者相遇过后，迭代没处理的都是已经transfer的hash桶，transfer没处理的，都是已经迭代的hash桶，冲突会变少
     * 下标在[nextIndex - 实际的stride （下界要 >= 0）, nextIndex - 1]内的hash桶，就是每个transfer的任务区间
     * 每次接受一个transfer任务，都要CAS执行 transferIndex = transferIndex - 实际的stride，保证一个transfer任务不会被几个线程同时获取（相当于任务队列的size减1）
     * 当没有线程正在执行transfer任务时，一定有transferIndex <= 0，这是判断是否需要帮助扩容的重要条件（相当于任务队列为空）
     */
    private transient volatile int transferIndex;

    // 下面三个主要与统计数目有关，可以参考jdk1.8新引入的java.util.concurrent.atomic.LongAdder的源码，帮助理解

    // 计数器基本值，主要在没有碰到多线程竞争时使用，需要通过CAS进行更新
    private transient volatile long baseCount;

    // CAS自旋锁标志位，用于初始化，或者counterCells扩容时
    private transient volatile int cellsBusy;

    // 用于高并发的计数单元，如果初始化了这些计数单元，那么跟table数组一样，长度必须是2^n的形式
    private transient volatile CounterCell[] counterCells;

    // views
    private transient KeySetView<K, V> keySet;
    private transient ValuesView<K, V> values;
    private transient EntrySetView<K, V> entrySet;

    /* ---------------- Public operations -------------- */

    public ConcurrentHashMapDebug() {
    }

    public ConcurrentHashMapDebug(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException();
        int cap = ((initialCapacity >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY
                : tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1));
        this.sizeCtl = cap;
    }

    public ConcurrentHashMapDebug(Map<? extends K, ? extends V> m) {
        this.sizeCtl = DEFAULT_CAPACITY;
        putAll(m);
    }

    public ConcurrentHashMapDebug(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    public ConcurrentHashMapDebug(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        if (initialCapacity < concurrencyLevel) // Use at least as many bins
            initialCapacity = concurrencyLevel; // as estimated threads
        long size = (long) (1.0 + (long) initialCapacity / loadFactor);
        int cap = (size >= (long) MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : tableSizeFor((int) size);
        this.sizeCtl = cap;
    }

    // Original (since JDK1.2) Map methods

    /**
     * {@inheritDoc}
     */
    public int size() {
        long n = sumCount();
        return ((n < 0L) ? 0 : (n > (long) Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) n);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return sumCount() <= 0L; // ignore transient negative values
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if
     * this map contains no mapping for the key.
     *
     * <p>
     * More formally, if this map contains a mapping from a key {@code k} to a value
     * {@code v} such that {@code key.equals(k)}, then this method returns
     * {@code v}; otherwise it returns {@code null}. (There can be at most one such
     * mapping.)
     *
     * @throws NullPointerException if the specified key is null
     */
    public V get(Object key) {
        Node<K, V>[] tab;
        Node<K, V> e, p;
        int n, eh;
        K ek;
        int h = spread(key.hashCode());
        if ((tab = table) != null && (n = tab.length) > 0 && (e = tabAt(tab, (n - 1) & h)) != null) {
            if ((eh = e.hash) == h) {
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;
            } else if (eh < 0)
                return (p = e.find(h, key)) != null ? p.val : null;
            while ((e = e.next) != null) {
                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }

    /**
     * Tests if the specified object is a key in this table.
     *
     * @param key possible key
     * @return {@code true} if and only if the specified object is a key in this
     * table, as determined by the {@code equals} method; {@code false}
     * otherwise
     * @throws NullPointerException if the specified key is null
     */
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * Returns {@code true} if this map maps one or more keys to the specified
     * value. Note: This method may require a full traversal of the map, and is much
     * slower than method {@code containsKey}.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     * @throws NullPointerException if the specified value is null
     */
    public boolean containsValue(Object value) {
        if (value == null)
            throw new NullPointerException();
        Node<K, V>[] t;
        if ((t = table) != null) {
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            for (Node<K, V> p; (p = it.advance()) != null; ) {
                V v;
                if ((v = p.val) == value || (v != null && value.equals(v)))
                    return true;
            }
        }
        return false;
    }

    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    /**
     * Implementation for put and putIfAbsent
     */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null)
            throw new NullPointerException();
        int hash = spread(key.hashCode());  // 计算key的hash值
        int binCount = 0; // 单个链表上元素的个数
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f; // 计算key后下标的Node
            int n, i, fh;  // n是table长度  i是key所在链表在node数组中的下标  fh是Node的hash值
            if (tab == null || (n = tab.length) == 0) // 如果node数组为空，初始化数组
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) { // 如果计算的Key所在的位置为空，直接添加
                if (casTabAt(tab, i, null, new Node<K, V>(hash, key, value, null)))
                    break;
            } else if ((fh = f.hash) == MOVED) // 如果检测到某个节点的hash值是MOVED，则表示正在进行数组扩张的数据复制阶段
                tab = helpTransfer(tab, f);  // 则当前线程也会参与去复制，通过允许多线程复制的功能，一次来减少数组的复制所带来的性能损失
            else { // 如果计算的key所在的位置有值的话
                V oldVal = null;
                synchronized (f) { // 给这个Node加锁
                    if (tabAt(tab, i) == f) { // 再次确认之前计算下标取出的Node还是那个位置
                        if (fh >= 0) { // fh >= 0 时 Node所在的位置是一个链表，fh = -2时是一个树
                            binCount = 1; //自身一个Node，所以所在链表初始长度为1
                            for (Node<K, V> e = f; ; ++binCount) { // 遍历链表
                                K ek;
                                if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) { // 如果取出的Node刚好Key就是要新增的key
                                    oldVal = e.val;
                                    if (!onlyIfAbsent) // 没有存在不可替换选项时，覆盖旧value
                                        e.val = value;
                                    break;
                                }
                                Node<K, V> pred = e;
                                if ((e = e.next) == null) { // 如果取出的Node的Key不是要新增的key
                                    pred.next = new Node<K, V>(hash, key, value, null); // 将新增的node放到尾部
                                    break;
                                }
                            }
                        } else if (f instanceof TreeBin) { // Node所在的位置是一棵树
                            Node<K, V> p;
                            binCount = 2;  // 树的头节点+自身，所以所在链表初始长度为2
                            if ((p = ((TreeBin<K, V>) f).putTreeVal(hash, key, value)) != null) { // 将新节点添加到树中
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD) // 判断是要转换成树还是扩容数组
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal; // 返回的替换前的旧值
                    break;
                }
            }
        }
        addCount(1L, binCount); // 并发计数
        return null;
    }

    /**
     * Copies all of the mappings from the specified map to this one. These mappings
     * replace any mappings that this map had for any of the keys currently in the
     * specified map.
     *
     * @param m mappings to be stored in this map
     */
    public void putAll(Map<? extends K, ? extends V> m) {
        tryPresize(m.size());
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            putVal(e.getKey(), e.getValue(), false);
    }

    /**
     * Removes the key (and its corresponding value) from this map. This method does
     * nothing if the key is not in the map.
     *
     * @param key the key that needs to be removed
     * @return the previous value associated with {@code key}, or {@code null} if
     * there was no mapping for {@code key}
     * @throws NullPointerException if the specified key is null
     */
    public V remove(Object key) {
        return replaceNode(key, null, null);
    }

    /**
     * Implementation for the four public remove/replace methods: Replaces node
     * value with v, conditional upon match of cv if non-null. If resulting value is
     * null, delete.
     */
    final V replaceNode(Object key, V value, Object cv) {
        int hash = spread(key.hashCode());
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0 || (f = tabAt(tab, i = (n - 1) & hash)) == null)
                break;
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                boolean validated = false;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            validated = true;
                            for (Node<K, V> e = f, pred = null; ; ) {
                                K ek;
                                if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    V ev = e.val;
                                    if (cv == null || cv == ev || (ev != null && cv.equals(ev))) {
                                        oldVal = ev;
                                        if (value != null)
                                            e.val = value;
                                        else if (pred != null)
                                            pred.next = e.next;
                                        else
                                            setTabAt(tab, i, e.next);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null)
                                    break;
                            }
                        } else if (f instanceof TreeBin) {
                            validated = true;
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> r, p;
                            if ((r = t.root) != null && (p = r.findTreeNode(hash, key, null)) != null) {
                                V pv = p.val;
                                if (cv == null || cv == pv || (pv != null && cv.equals(pv))) {
                                    oldVal = pv;
                                    if (value != null)
                                        p.val = value;
                                    else if (t.removeTreeNode(p))
                                        setTabAt(tab, i, untreeify(t.first));
                                }
                            }
                        }
                    }
                }
                if (validated) {
                    if (oldVal != null) {
                        if (value == null)
                            addCount(-1L, -1);
                        return oldVal;
                    }
                    break;
                }
            }
        }
        return null;
    }

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        long delta = 0L; // negative number of deletions
        int i = 0;
        Node<K, V>[] tab = table;
        while (tab != null && i < tab.length) {
            int fh;
            Node<K, V> f = tabAt(tab, i);
            if (f == null)
                ++i;
            else if ((fh = f.hash) == MOVED) {
                tab = helpTransfer(tab, f);
                i = 0; // restart
            } else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        Node<K, V> p = (fh >= 0 ? f : (f instanceof TreeBin) ? ((TreeBin<K, V>) f).first : null);
                        while (p != null) {
                            --delta;
                            p = p.next;
                        }
                        setTabAt(tab, i++, null);
                    }
                }
            }
        }
        if (delta != 0L)
            addCount(delta, -1);
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. The set supports element removal, which removes the corresponding
     * mapping from this map, via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear} operations. It does
     * not support the {@code add} or {@code addAll} operations.
     *
     * <p>
     * The view's iterators and spliterators are
     * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
     *
     * <p>
     * The view's {@code spliterator} reports {@link Spliterator#CONCURRENT},
     * {@link Spliterator#DISTINCT}, and {@link Spliterator#NONNULL}.
     *
     * @return the set view
     */
    public KeySetView<K, V> keySet() {
        KeySetView<K, V> ks;
        return (ks = keySet) != null ? ks : (keySet = new KeySetView<K, V>(this, null));
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map. The
     * collection is backed by the map, so changes to the map are reflected in the
     * collection, and vice-versa. The collection supports element removal, which
     * removes the corresponding mapping from this map, via the
     * {@code Iterator.remove}, {@code Collection.remove}, {@code removeAll},
     * {@code retainAll}, and {@code clear} operations. It does not support the
     * {@code add} or {@code addAll} operations.
     *
     * <p>
     * The view's iterators and spliterators are
     * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
     *
     * <p>
     * The view's {@code spliterator} reports {@link Spliterator#CONCURRENT} and
     * {@link Spliterator#NONNULL}.
     *
     * @return the collection view
     */
    public Collection<V> values() {
        ValuesView<K, V> vs;
        return (vs = values) != null ? vs : (values = new ValuesView<K, V>(this));
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map. The set is
     * backed by the map, so changes to the map are reflected in the set, and
     * vice-versa. The set supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear} operations.
     *
     * <p>
     * The view's iterators and spliterators are
     * <a href="package-summary.html#Weakly"><i>weakly consistent</i></a>.
     *
     * <p>
     * The view's {@code spliterator} reports {@link Spliterator#CONCURRENT},
     * {@link Spliterator#DISTINCT}, and {@link Spliterator#NONNULL}.
     *
     * @return the set view
     */
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySetView<K, V> es;
        return (es = entrySet) != null ? es : (entrySet = new EntrySetView<K, V>(this));
    }

    /**
     * Returns the hash code value for this {@link Map}, i.e., the sum of, for each
     * key-value pair in the map, {@code key.hashCode() ^ value.hashCode()}.
     *
     * @return the hash code value for this map
     */
    public int hashCode() {
        int h = 0;
        Node<K, V>[] t;
        if ((t = table) != null) {
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            for (Node<K, V> p; (p = it.advance()) != null; )
                h += p.key.hashCode() ^ p.val.hashCode();
        }
        return h;
    }

    /**
     * Returns a string representation of this map. The string representation
     * consists of a list of key-value mappings (in no particular order) enclosed in
     * braces ("{@code {}}"). Adjacent mappings are separated by the characters
     * {@code ", "} (comma and space). Each key-value mapping is rendered as the key
     * followed by an equals sign ("{@code =}") followed by the associated value.
     *
     * @return a string representation of this map
     */
    public String toString() {
        Node<K, V>[] t;
        int f = (t = table) == null ? 0 : t.length;
        Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Node<K, V> p;
        if ((p = it.advance()) != null) {
            for (; ; ) {
                K k = p.key;
                V v = p.val;
                sb.append(k == this ? "(this Map)" : k);
                sb.append('=');
                sb.append(v == this ? "(this Map)" : v);
                if ((p = it.advance()) == null)
                    break;
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }

    /**
     * Compares the specified object with this map for equality. Returns
     * {@code true} if the given object is a map with the same mappings as this map.
     * This operation may return misleading results if either map is concurrently
     * modified during execution of this method.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    public boolean equals(Object o) {
        if (o != this) {
            if (!(o instanceof Map))
                return false;
            Map<?, ?> m = (Map<?, ?>) o;
            Node<K, V>[] t;
            int f = (t = table) == null ? 0 : t.length;
            Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
            for (Node<K, V> p; (p = it.advance()) != null; ) {
                V val = p.val;
                Object v = m.get(p.key);
                if (v == null || (v != val && !v.equals(val)))
                    return false;
            }
            for (Map.Entry<?, ?> e : m.entrySet()) {
                Object mk, mv, v;
                if ((mk = e.getKey()) == null || (mv = e.getValue()) == null || (v = get(mk)) == null
                        || (mv != v && !mv.equals(v)))
                    return false;
            }
        }
        return true;
    }

    /**
     * Stripped-down version of helper class used in previous version, declared for
     * the sake of serialization compatibility
     */
    static class Segment<K, V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float lf) {
            this.loadFactor = lf;
        }
    }

    /**
     * Saves the state of the {@code ConcurrentHashMap} instance to a stream (i.e.,
     * serializes it).
     *
     * @param s the stream
     * @throws java.io.IOException if an I/O error occurs
     * @serialData the key (Object) and value (Object) for each key-value mapping,
     * followed by a null pair. The key-value mappings are emitted in no
     * particular order.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        // For serialization compatibility
        // Emulate segment calculation from previous version of this class
        int sshift = 0;
        int ssize = 1;
        while (ssize < DEFAULT_CONCURRENCY_LEVEL) {
            ++sshift;
            ssize <<= 1;
        }
        int segmentShift = 32 - sshift;
        int segmentMask = ssize - 1;
        @SuppressWarnings("unchecked")
        Segment<K, V>[] segments = (Segment<K, V>[]) new Segment<?, ?>[DEFAULT_CONCURRENCY_LEVEL];
        for (int i = 0; i < segments.length; ++i)
            segments[i] = new Segment<K, V>(LOAD_FACTOR);
        s.putFields().put("segments", segments);
        s.putFields().put("segmentShift", segmentShift);
        s.putFields().put("segmentMask", segmentMask);
        s.writeFields();

        Node<K, V>[] t;
        if ((t = table) != null) {
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            for (Node<K, V> p; (p = it.advance()) != null; ) {
                s.writeObject(p.key);
                s.writeObject(p.val);
            }
        }
        s.writeObject(null);
        s.writeObject(null);
        segments = null; // throw away
    }

    /**
     * Reconstitutes the instance from a stream (that is, deserializes it).
     *
     * @param s the stream
     * @throws ClassNotFoundException if the class of a serialized object could not be found
     * @throws java.io.IOException    if an I/O error occurs
     */
    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        /*
         * To improve performance in typical cases, we create nodes while reading, then
         * place in table once size is known. However, we must also validate uniqueness
         * and deal with overpopulated bins while doing so, which requires specialized
         * versions of putVal mechanics.
         */
        sizeCtl = -1; // force exclusion for table construction
        s.defaultReadObject();
        long size = 0L;
        Node<K, V> p = null;
        for (; ; ) {
            @SuppressWarnings("unchecked")
            K k = (K) s.readObject();
            @SuppressWarnings("unchecked")
            V v = (V) s.readObject();
            if (k != null && v != null) {
                p = new Node<K, V>(spread(k.hashCode()), k, v, p);
                ++size;
            } else
                break;
        }
        if (size == 0L)
            sizeCtl = 0;
        else {
            int n;
            if (size >= (long) (MAXIMUM_CAPACITY >>> 1))
                n = MAXIMUM_CAPACITY;
            else {
                int sz = (int) size;
                n = tableSizeFor(sz + (sz >>> 1) + 1);
            }
            @SuppressWarnings("unchecked")
            Node<K, V>[] tab = (Node<K, V>[]) new Node<?, ?>[n];
            int mask = n - 1;
            long added = 0L;
            while (p != null) {
                boolean insertAtFront;
                Node<K, V> next = p.next, first;
                int h = p.hash, j = h & mask;
                if ((first = tabAt(tab, j)) == null)
                    insertAtFront = true;
                else {
                    K k = p.key;
                    if (first.hash < 0) {
                        TreeBin<K, V> t = (TreeBin<K, V>) first;
                        if (t.putTreeVal(h, k, p.val) == null)
                            ++added;
                        insertAtFront = false;
                    } else {
                        int binCount = 0;
                        insertAtFront = true;
                        Node<K, V> q;
                        K qk;
                        for (q = first; q != null; q = q.next) {
                            if (q.hash == h && ((qk = q.key) == k || (qk != null && k.equals(qk)))) {
                                insertAtFront = false;
                                break;
                            }
                            ++binCount;
                        }
                        if (insertAtFront && binCount >= TREEIFY_THRESHOLD) {
                            insertAtFront = false;
                            ++added;
                            p.next = first;
                            TreeNode<K, V> hd = null, tl = null;
                            for (q = p; q != null; q = q.next) {
                                TreeNode<K, V> t = new TreeNode<K, V>(q.hash, q.key, q.val, null, null);
                                if ((t.prev = tl) == null)
                                    hd = t;
                                else
                                    tl.next = t;
                                tl = t;
                            }
                            setTabAt(tab, j, new TreeBin<K, V>(hd));
                        }
                    }
                }
                if (insertAtFront) {
                    ++added;
                    p.next = first;
                    setTabAt(tab, j, p);
                }
                p = next;
            }
            table = tab;
            sizeCtl = n - (n >>> 2);
            baseCount = added;
        }
    }

    // ConcurrentMap methods

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key, or {@code null}
     * if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    public V putIfAbsent(K key, V value) {
        return putVal(key, value, true);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the specified key is null
     */
    public boolean remove(Object key, Object value) {
        if (key == null)
            throw new NullPointerException();
        return value != null && replaceNode(key, null, value) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if any of the arguments are null
     */
    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null)
            throw new NullPointerException();
        return replaceNode(key, newValue, oldValue) != null;
    }

    /**
     * {@inheritDoc}
     *
     * @return the previous value associated with the specified key, or {@code null}
     * if there was no mapping for the key
     * @throws NullPointerException if the specified key or value is null
     */
    public V replace(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException();
        return replaceNode(key, value, null);
    }

    // Overrides of JDK8+ Map extension method defaults

    /**
     * Returns the value to which the specified key is mapped, or the given default
     * value if this map contains no mapping for the key.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the value to return if this map contains no mapping for the given
     *                     key
     * @return the mapping for the key, if present; else the default value
     * @throws NullPointerException if the specified key is null
     */
    public V getOrDefault(Object key, V defaultValue) {
        V v;
        return (v = get(key)) == null ? defaultValue : v;
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();
        Node<K, V>[] t;
        if ((t = table) != null) {
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            for (Node<K, V> p; (p = it.advance()) != null; ) {
                action.accept(p.key, p.val);
            }
        }
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null)
            throw new NullPointerException();
        Node<K, V>[] t;
        if ((t = table) != null) {
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            for (Node<K, V> p; (p = it.advance()) != null; ) {
                V oldValue = p.val;
                for (K key = p.key; ; ) {
                    V newValue = function.apply(key, oldValue);
                    if (newValue == null)
                        throw new NullPointerException();
                    if (replaceNode(key, newValue, oldValue) != null || (oldValue = get(key)) == null)
                        break;
                }
            }
        }
    }

    /**
     * If the specified key is not already associated with a value, attempts to
     * compute its value using the given mapping function and enters it into this
     * map unless {@code null}. The entire method invocation is performed
     * atomically, so the function is applied at most once per key. Some attempted
     * update operations on this map by other threads may be blocked while
     * computation is in progress, so the computation should be short and simple,
     * and must not attempt to update any other mappings of this map.
     *
     * @param key             key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the
     * specified key, or null if the computed value is null
     * @throws NullPointerException  if the specified key or mappingFunction is null
     * @throws IllegalStateException if the computation detectably attempts a recursive update to this
     *                               map that would otherwise never complete
     * @throws RuntimeException      or Error if the mappingFunction does so, in which case the
     *                               mapping is left unestablished
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int binCount = 0;
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
                Node<K, V> r = new ReservationNode<K, V>();
                synchronized (r) {
                    if (casTabAt(tab, i, null, r)) {
                        binCount = 1;
                        Node<K, V> node = null;
                        try {
                            if ((val = mappingFunction.apply(key)) != null)
                                node = new Node<K, V>(h, key, val, null);
                        } finally {
                            setTabAt(tab, i, node);
                        }
                    }
                }
                if (binCount != 0)
                    break;
            } else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                boolean added = false;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K, V> e = f; ; ++binCount) {
                                K ek;
                                V ev;
                                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    val = e.val;
                                    break;
                                }
                                Node<K, V> pred = e;
                                if ((e = e.next) == null) {
                                    if ((val = mappingFunction.apply(key)) != null) {
                                        added = true;
                                        pred.next = new Node<K, V>(h, key, val, null);
                                    }
                                    break;
                                }
                            }
                        } else if (f instanceof TreeBin) {
                            binCount = 2;
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> r, p;
                            if ((r = t.root) != null && (p = r.findTreeNode(h, key, null)) != null)
                                val = p.val;
                            else if ((val = mappingFunction.apply(key)) != null) {
                                added = true;
                                t.putTreeVal(h, key, val);
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (!added)
                        return val;
                    break;
                }
            }
        }
        if (val != null)
            addCount(1L, binCount);
        return val;
    }

    /**
     * If the value for the specified key is present, attempts to compute a new
     * mapping given the key and its current mapped value. The entire method
     * invocation is performed atomically. Some attempted update operations on this
     * map by other threads may be blocked while computation is in progress, so the
     * computation should be short and simple, and must not attempt to update any
     * other mappings of this map.
     *
     * @param key               key with which a value may be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException  if the specified key or remappingFunction is null
     * @throws IllegalStateException if the computation detectably attempts a recursive update to this
     *                               map that would otherwise never complete
     * @throws RuntimeException      or Error if the remappingFunction does so, in which case the
     *                               mapping is unchanged
     */
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int delta = 0;
        int binCount = 0;
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null)
                break;
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K, V> e = f, pred = null; ; ++binCount) {
                                K ek;
                                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    val = remappingFunction.apply(key, e.val);
                                    if (val != null)
                                        e.val = val;
                                    else {
                                        delta = -1;
                                        Node<K, V> en = e.next;
                                        if (pred != null)
                                            pred.next = en;
                                        else
                                            setTabAt(tab, i, en);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null)
                                    break;
                            }
                        } else if (f instanceof TreeBin) {
                            binCount = 2;
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> r, p;
                            if ((r = t.root) != null && (p = r.findTreeNode(h, key, null)) != null) {
                                val = remappingFunction.apply(key, p.val);
                                if (val != null)
                                    p.val = val;
                                else {
                                    delta = -1;
                                    if (t.removeTreeNode(p))
                                        setTabAt(tab, i, untreeify(t.first));
                                }
                            }
                        }
                    }
                }
                if (binCount != 0)
                    break;
            }
        }
        if (delta != 0)
            addCount((long) delta, binCount);
        return val;
    }

    /**
     * Attempts to compute a mapping for the specified key and its current mapped
     * value (or {@code null} if there is no current mapping). The entire method
     * invocation is performed atomically. Some attempted update operations on this
     * map by other threads may be blocked while computation is in progress, so the
     * computation should be short and simple, and must not attempt to update any
     * other mappings of this Map.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException  if the specified key or remappingFunction is null
     * @throws IllegalStateException if the computation detectably attempts a recursive update to this
     *                               map that would otherwise never complete
     * @throws RuntimeException      or Error if the remappingFunction does so, in which case the
     *                               mapping is unchanged
     */
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int delta = 0;
        int binCount = 0;
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
                Node<K, V> r = new ReservationNode<K, V>();
                synchronized (r) {
                    if (casTabAt(tab, i, null, r)) {
                        binCount = 1;
                        Node<K, V> node = null;
                        try {
                            if ((val = remappingFunction.apply(key, null)) != null) {
                                delta = 1;
                                node = new Node<K, V>(h, key, val, null);
                            }
                        } finally {
                            setTabAt(tab, i, node);
                        }
                    }
                }
                if (binCount != 0)
                    break;
            } else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K, V> e = f, pred = null; ; ++binCount) {
                                K ek;
                                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    val = remappingFunction.apply(key, e.val);
                                    if (val != null)
                                        e.val = val;
                                    else {
                                        delta = -1;
                                        Node<K, V> en = e.next;
                                        if (pred != null)
                                            pred.next = en;
                                        else
                                            setTabAt(tab, i, en);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null) {
                                    val = remappingFunction.apply(key, null);
                                    if (val != null) {
                                        delta = 1;
                                        pred.next = new Node<K, V>(h, key, val, null);
                                    }
                                    break;
                                }
                            }
                        } else if (f instanceof TreeBin) {
                            binCount = 1;
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> r, p;
                            if ((r = t.root) != null)
                                p = r.findTreeNode(h, key, null);
                            else
                                p = null;
                            V pv = (p == null) ? null : p.val;
                            val = remappingFunction.apply(key, pv);
                            if (val != null) {
                                if (p != null)
                                    p.val = val;
                                else {
                                    delta = 1;
                                    t.putTreeVal(h, key, val);
                                }
                            } else if (p != null) {
                                delta = -1;
                                if (t.removeTreeNode(p))
                                    setTabAt(tab, i, untreeify(t.first));
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    break;
                }
            }
        }
        if (delta != 0)
            addCount((long) delta, binCount);
        return val;
    }

    /**
     * If the specified key is not already associated with a (non-null) value,
     * associates it with the given value. Otherwise, replaces the value with the
     * results of the given remapping function, or removes if {@code null}. The
     * entire method invocation is performed atomically. Some attempted update
     * operations on this map by other threads may be blocked while computation is
     * in progress, so the computation should be short and simple, and must not
     * attempt to update any other mappings of this Map.
     *
     * @param key               key with which the specified value is to be associated
     * @param value             the value to use if absent
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException if the specified key or the remappingFunction is null
     * @throws RuntimeException     or Error if the remappingFunction does so, in which case the
     *                              mapping is unchanged
     */
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (key == null || value == null || remappingFunction == null)
            throw new NullPointerException();
        int h = spread(key.hashCode());
        V val = null;
        int delta = 0;
        int binCount = 0;
        for (Node<K, V>[] tab = table; ; ) {
            Node<K, V> f;
            int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & h)) == null) {
                if (casTabAt(tab, i, null, new Node<K, V>(h, key, value, null))) {
                    delta = 1;
                    val = value;
                    break;
                }
            } else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K, V> e = f, pred = null; ; ++binCount) {
                                K ek;
                                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    val = remappingFunction.apply(e.val, value);
                                    if (val != null)
                                        e.val = val;
                                    else {
                                        delta = -1;
                                        Node<K, V> en = e.next;
                                        if (pred != null)
                                            pred.next = en;
                                        else
                                            setTabAt(tab, i, en);
                                    }
                                    break;
                                }
                                pred = e;
                                if ((e = e.next) == null) {
                                    delta = 1;
                                    val = value;
                                    pred.next = new Node<K, V>(h, key, val, null);
                                    break;
                                }
                            }
                        } else if (f instanceof TreeBin) {
                            binCount = 2;
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> r = t.root;
                            TreeNode<K, V> p = (r == null) ? null : r.findTreeNode(h, key, null);
                            val = (p == null) ? value : remappingFunction.apply(p.val, value);
                            if (val != null) {
                                if (p != null)
                                    p.val = val;
                                else {
                                    delta = 1;
                                    t.putTreeVal(h, key, val);
                                }
                            } else if (p != null) {
                                delta = -1;
                                if (t.removeTreeNode(p))
                                    setTabAt(tab, i, untreeify(t.first));
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    break;
                }
            }
        }
        if (delta != 0)
            addCount((long) delta, binCount);
        return val;
    }

    // Hashtable legacy methods

    /**
     * Legacy method testing if some key maps into the specified value in this
     * table. This method is identical in functionality to
     * {@link #containsValue(Object)}, and exists solely to ensure full
     * compatibility with class {@link java.util.Hashtable}, which supported this
     * method prior to introduction of the Java Collections framework.
     *
     * @param value a value to search for
     * @return {@code true} if and only if some key maps to the {@code value}
     * argument in this table as determined by the {@code equals} method;
     * {@code false} otherwise
     * @throws NullPointerException if the specified value is null
     */
    public boolean contains(Object value) {
        return containsValue(value);
    }

    /**
     * Returns an enumeration of the keys in this table.
     *
     * @return an enumeration of the keys in this table
     * @see #keySet()
     */
    public Enumeration<K> keys() {
        Node<K, V>[] t;
        int f = (t = table) == null ? 0 : t.length;
        return new KeyIterator<K, V>(t, f, 0, f, this);
    }

    /**
     * Returns an enumeration of the values in this table.
     *
     * @return an enumeration of the values in this table
     * @see #values()
     */
    public Enumeration<V> elements() {
        Node<K, V>[] t;
        int f = (t = table) == null ? 0 : t.length;
        return new ValueIterator<K, V>(t, f, 0, f, this);
    }

    // ConcurrentHashMap-only methods

    /**
     * Returns the number of mappings. This method should be used instead of
     * {@link #size} because a ConcurrentHashMap may contain more mappings than can
     * be represented as an int. The value returned is an estimate; the actual count
     * may differ if there are concurrent insertions or removals.
     *
     * @return the number of mappings
     * @since 1.8
     */
    public long mappingCount() {
        long n = sumCount();
        return (n < 0L) ? 0L : n; // ignore transient negative values
    }

    /**
     * Creates a new {@link Set} backed by a ConcurrentHashMap from the given type
     * to {@code Boolean.TRUE}.
     *
     * @param <K> the element type of the returned set
     * @return the new set
     * @since 1.8
     */
    public static <K> KeySetView<K, Boolean> newKeySet() {
        return new KeySetView<K, Boolean>(new ConcurrentHashMapDebug<K, Boolean>(), Boolean.TRUE);
    }

    /**
     * Creates a new {@link Set} backed by a ConcurrentHashMap from the given type
     * to {@code Boolean.TRUE}.
     *
     * @param initialCapacity The implementation performs internal sizing to accommodate this
     *                        many elements.
     * @param <K>             the element type of the returned set
     * @return the new set
     * @throws IllegalArgumentException if the initial capacity of elements is negative
     * @since 1.8
     */
    public static <K> KeySetView<K, Boolean> newKeySet(int initialCapacity) {
        return new KeySetView<K, Boolean>(new ConcurrentHashMapDebug<K, Boolean>(initialCapacity), Boolean.TRUE);
    }

    /**
     * Returns a {@link Set} view of the keys in this map, using the given common
     * mapped value for any additions (i.e., {@link Collection#add} and
     * {@link Collection#addAll(Collection)}). This is of course only appropriate if
     * it is acceptable to use the same value for all additions from this view.
     *
     * @param mappedValue the mapped value to use for any additions
     * @return the set view
     * @throws NullPointerException if the mappedValue is null
     */
    public KeySetView<K, V> keySet(V mappedValue) {
        if (mappedValue == null)
            throw new NullPointerException();
        return new KeySetView<K, V>(this, mappedValue);
    }

    /* ---------------- Special Nodes -------------- */

    /**
     * A node inserted at head of bins during transfer operations.
     */
    static final class ForwardingNode<K, V> extends Node<K, V> {
        final Node<K, V>[] nextTable;

        ForwardingNode(Node<K, V>[] tab) {
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }

        Node<K, V> find(int h, Object k) {
            // loop to avoid arbitrarily deep recursion on forwarding nodes
            outer:
            for (Node<K, V>[] tab = nextTable; ; ) {
                Node<K, V> e;
                int n;
                if (k == null || tab == null || (n = tab.length) == 0 || (e = tabAt(tab, (n - 1) & h)) == null)
                    return null;
                for (; ; ) {
                    int eh;
                    K ek;
                    if ((eh = e.hash) == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                    if (eh < 0) {
                        if (e instanceof ForwardingNode) {
                            tab = ((ForwardingNode<K, V>) e).nextTable;
                            continue outer;
                        } else
                            return e.find(h, k);
                    }
                    if ((e = e.next) == null)
                        return null;
                }
            }
        }
    }

    /**
     * A place-holder node used in computeIfAbsent and compute
     */
    static final class ReservationNode<K, V> extends Node<K, V> {
        ReservationNode() {
            super(RESERVED, null, null, null);
        }

        Node<K, V> find(int h, Object k) {
            return null;
        }
    }

    /* ---------------- Table Initialization and Resizing -------------- */

    /**
     * 返回与扩容有关的一个生成戳rs，每次新的扩容，都有一个不同的n，这个生成戳就是根据n来计算出来的一个数字，n不同，这个数字也不同
     * 保证 rs << RESIZE_STAMP_SHIFT（16） 必须是负数
     * numberOfLeadingZeros方法返回无符号整型i的最高非零位前面的0的个数，包括符号位在内，如果是负数直接返回0
     */
    static final int resizeStamp(int n) {
        return Integer.numberOfLeadingZeros(n) | (1 << (RESIZE_STAMP_BITS - 1));
    }

    /**
     * 初始化数组table，
     * 如果sizeCtl小于0，说明别的数组正在进行初始化，则让出执行权
     * 如果sizeCtl大于0的话，则初始化一个大小为sizeCtl的数组
     * 否则的话初始化一个默认大小(16)的数组
     * 然后设置sizeCtl的值为数组长度的3/4
     */
    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0) //小于0的时候表示在别的线程在初始化表或扩展表，要保证单线程扩容，进行线程礼让
                Thread.yield();
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) { //尝试cas修改sizeCtl(类似加锁) 设定为-1表示要初始化表了
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        @SuppressWarnings("unchecked")
                        Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);  //初始化后，sizeCtl长度为数组长度的3/4
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }

    private final void addCount(long x, int check) {
        CounterCell[] as;
        long b, s;
        // counterCells不为null的时候代表这时候是有冲突的
        // 所以在尝试修改baseCount基础值失败后进入并发计数模式，也就是调用fullAddCount
        if ((as = counterCells) != null || !U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
            CounterCell a;
            long v;
            int m;
            boolean uncontended = true;
            if (as == null || (m = as.length - 1) < 0 || (a = as[ThreadLocalRandom.getProbe() & m]) == null
                    || !(uncontended = U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                fullAddCount(x, uncontended); // 这里跟LongAdder是一样的，可以去看LongAdder源码分析
                return;
            }
            if (check <= 1)
                return;
            s = sumCount();
        }
        if (check >= 0) { // check是桶中的Node数量，这里判断是否要扩容，代码类似helpTransfer
            Node<K, V>[] tab, nt;
            int n, sc;
            while (s >= (long) (sc = sizeCtl) && (tab = table) != null && (n = tab.length) < MAXIMUM_CAPACITY) {
                int rs = resizeStamp(n);
                if (sc < 0) {
                    if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS
                            || (nt = nextTable) == null || transferIndex <= 0)
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
                        transfer(tab, nt);
                } else if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2))
                    transfer(tab, null);
                s = sumCount();
            }
        }
    }

    /**
     * Helps transfer if a resize is in progress.
     */
    final Node<K, V>[] helpTransfer(Node<K, V>[] tab, Node<K, V> f) {
        Node<K, V>[] nextTab;
        int sc;
        if (tab != null && (f instanceof ForwardingNode) && (nextTab = ((ForwardingNode<K, V>) f).nextTable) != null) {
            int rs = resizeStamp(tab.length); // 通过resizeStamp生成唯一戳，来保证这次扩容的唯一性，防止出现扩容重叠的现象
            while (nextTab == nextTable && table == tab && (sc = sizeCtl) < 0) {
                if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS || transferIndex <= 0)
                    break;
                if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                    transfer(tab, nextTab); // 将tab扩容到nextTab
                    break;
                }
            }
            return nextTab;
        }
        return table;
    }

    private final void tryPresize(int size) {
        int c = (size >= (MAXIMUM_CAPACITY >>> 1)) ? MAXIMUM_CAPACITY : tableSizeFor(size + (size >>> 1) + 1);
        int sc;
        while ((sc = sizeCtl) >= 0) {  // 这里一直获取最新得sizeCtl 直到“拿到锁”，compareAndSwapInt替换成功
            Node<K, V>[] tab = table;
            int n;
            if (tab == null || (n = tab.length) == 0) {  // 出现tab为null的情况就是初始化map用的是传入一个Map,这样一上来就得扩容数组，从而tab都是空得
                n = (sc > c) ? sc : c; // sc理论上是小于c的，大于c的情况仅限于上面的情况，sc = Default_Capablity 而size是传入map的size
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                    try {
                        if (table == tab) {
                            @SuppressWarnings("unchecked")
                            Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n];
                            table = nt;
                            sc = n - (n >>> 2); // sizeCtl = table.length * (3/4)
                        }
                    } finally {
                        sizeCtl = sc;
                    }
                }
            } else if (c <= sc || n >= MAXIMUM_CAPACITY)
                break;
            else if (tab == table) {
                int rs = resizeStamp(n); // ???  计算本次扩容的生成戳  rs >>> RESIZE_STAMP_SHIFT 必是负数
                if (sc < 0) { // 如果正在扩容Table的话，则帮助扩容
                    Node<K, V>[] nt;
                    if ((sc >>> 2) != rs || sc == rs + 1 || sc == rs + MAX_RESIZERS  // MAX_RESIZERS是最大扩容的数量
                            || (nt = nextTable) == null || transferIndex <= 0)  // ？？？
                        break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) // 这里的sc是线程数 多线程帮助扩容
                        transfer(tab, nt); // 将第一个参数的table中的元素，移动到第二个元素的table中去，
                } else if (U.compareAndSwapInt(this, SIZECTL, sc, (rs << RESIZE_STAMP_SHIFT) + 2)) // 试着让自己成为第一个执行transfer任务的线程
                    transfer(tab, null); // 当第二个参数为null的时候，会创建一个两倍大小的table
            }
        }
    }

    private final void transfer(Node<K, V>[] tab, Node<K, V>[] nextTab) {
        int n = tab.length, stride;
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE;
        if (nextTab == null) { // 如果new tab为空的话 就初始化一个两倍于原table的数组
            try {
                @SuppressWarnings("unchecked")
                Node<K, V>[] nt = (Node<K, V>[]) new Node<?, ?>[n << 1];
                nextTab = nt;
            } catch (Throwable ex) { //处理内存不足导致的OOM，以及table数组超过最大长度，这两种情况都实际上无法再进行扩容了
                sizeCtl = Integer.MAX_VALUE;
                return;
            }
            nextTable = nextTab;
            transferIndex = n;
        }
        int nextn = nextTab.length;
        // // 转发节点，在旧数组的一个hash桶中所有节点都被迁移完后，放置在这个hash桶中，表明已经迁移完，对它的读操作会转发到新数组
        ForwardingNode<K, V> fwd = new ForwardingNode<K, V>(nextTab);
        boolean advance = true;
        boolean finishing = false; // to ensure sweep before committing nextTab
        for (int i = 0, bound = 0; ; ) {
            Node<K, V> f;
            int fh;
            while (advance) {  // 这里相当于每个线程领取任务
                int nextIndex, nextBound;
                if (--i >= bound || finishing) // 一次transfer任务还没有执行完毕
                    advance = false;
                else if ((nextIndex = transferIndex) <= 0) { // transfer任务已经没有了，表明可以准备退出扩容了
                    i = -1;
                    advance = false;
                } else if (U.compareAndSwapInt(this, TRANSFERINDEX, nextIndex,
                        nextBound = (nextIndex > stride ? nextIndex - stride : 0))) { // // 尝试申请一个transfer任务
                    bound = nextBound; // 申请到任务后标记自己的任务区间 bound = nextIndex - stride
                    i = nextIndex - 1;
                    advance = false;
                }
            }
            if (i < 0 || i >= n || i + n >= nextn) {  // 处理扩容重叠
                int sc;
                if (finishing) {
                    nextTable = null;
                    table = nextTab;
                    sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
                        return;
                    finishing = advance = true;
                    i = n; // recheck before commit
                }
            } else if ((f = tabAt(tab, i)) == null) // 	hash桶本身为null，不用迁移，直接尝试安放一个转发节点
                advance = casTabAt(tab, i, null, fwd);
            else if ((fh = f.hash) == MOVED)
                advance = true; // already processed
            else {
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        Node<K, V> ln, hn;
                        if (fh >= 0) { //链表处理
                            /*
                             * 因为n的值为数组的长度，且是power(2,x)的，所以，在&操作的结果只可能是0或者n
                             * 根据这个规则
                             *         0-->  放在新表的相同位置
                             *         n-->  放在新表的（n+原来位置）
                             *         'rehash（重新散列）'的操作跟hashMap一样
                             */
                            int runBit = fh & n;
                            Node<K, V> lastRun = f;
                            /*
                             * lastRun 表示的是需要复制的最后一个节点
                             * 每当新节点的hash&n -> b 发生变化的时候，就把runBit设置为这个结果b
                             * 这样for循环之后，runBit的值就是最后不变的hash&n的值
                             * 而lastRun的值就是最后一次导致hash&n 发生变化的节点(假设为p节点)
                             * 为什么要这么做呢？因为p节点后面的节点的hash&n 值跟p节点是一样的，
                             * 所以在复制到新的table的时候，它肯定还是跟p节点在同一个位置
                             * 在复制完p节点之后，p节点的next节点还是指向它原来的节点，就不需要进行复制了，自己就被带过去了
                             * 这也就导致了一个问题就是复制后的链表的顺序并不一定是原来的倒序
                             */
                            for (Node<K, V> p = f.next; p != null; p = p.next) {
                                int b = p.hash & n;
                                if (b != runBit) {
                                    runBit = b;
                                    lastRun = p;
                                }
                            }
                            if (runBit == 0) { // (注一) 这里可以认为设置完runBit = 0之后的节点都是不用复制到新数组的
                                ln = lastRun;
                                hn = null;
                            } else { // （注二）这里可以认为设置完runBit != 0之后的节点都是需要复制到新数组的
                                hn = lastRun;
                                ln = null;
                            }
                            for (Node<K, V> p = f; p != lastRun; p = p.next) { // 这里轮询链表节点p，根据(ph & n) == 0判断是否要复制
                                int ph = p.hash;
                                K pk = p.key;
                                V pv = p.val;
                                if ((ph & n) == 0)
                                    ln = new Node<K, V>(ph, pk, pv, ln); // 将不需要复制的节点放在上述(注一) lastRun的前面，如果为空相当于重新构建一个链表
                                else
                                    hn = new Node<K, V>(ph, pk, pv, hn); //将需要复制的节点放在上述(注二) lastRun的前面，如果为空相当于重新构建一个链表
                            }
                            setTabAt(nextTab, i, ln); // 将ln所在的链表放置在原地
                            setTabAt(nextTab, i + n, hn); // 将hn所在的链表放在在原地 + n的位置
                            setTabAt(tab, i, fwd); // 在旧tab的位置 放置 ForwardingNode节点
                            advance = true; //进入下一个循环
                        } else if (f instanceof TreeBin) { // 红黑树处理
                            TreeBin<K, V> t = (TreeBin<K, V>) f;
                            TreeNode<K, V> lo = null, loTail = null; //
                            TreeNode<K, V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            for (Node<K, V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K, V> p = new TreeNode<K, V>(h, e.key, e.val, null, null);
                                if ((h & n) == 0) { // 将不需要复制的节点 整合到 lo ~ loTail上
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                } else { // 将需要复制的节点 整合到 hi ~ hiTail上
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            // 判断新生成的两个红黑树 是否要转成链表
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) : (hc != 0) ? new TreeBin<K, V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) : (lc != 0) ? new TreeBin<K, V>(hi) : t;
                            setTabAt(nextTab, i, ln);
                            setTabAt(nextTab, i + n, hn);
                            setTabAt(tab, i, fwd);
                            advance = true;
                        }
                    }
                }
            }
        }
    }
    /* ---------------- Counter support -------------- */

    /**
     * A padded cell for distributing counts. Adapted from LongAdder and Striped64.
     * See their internal docs for explanation.
     */
    @sun.misc.Contended
    static final class CounterCell {
        volatile long value;

        CounterCell(long x) {
            value = x;
        }
    }

    final long sumCount() {
        CounterCell[] as = counterCells;
        CounterCell a;
        long sum = baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    sum += a.value;
            }
        }
        return sum;
    }

    // See LongAdder version for explanation
    private final void fullAddCount(long x, boolean wasUncontended) {
        int h;
        if ((h = ThreadLocalRandom.getProbe()) == 0) {
            ThreadLocalRandom.localInit(); // force initialization
            h = ThreadLocalRandom.getProbe();
            wasUncontended = true;
        }
        boolean collide = false; // True if last slot nonempty
        for (; ; ) {
            CounterCell[] as;
            CounterCell a;
            int n;
            long v;
            if ((as = counterCells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) { // Try to attach new Cell
                        CounterCell r = new CounterCell(x); // Optimistic create
                        if (cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            boolean created = false;
                            try { // Recheck under lock
                                CounterCell[] rs;
                                int m, j;
                                if ((rs = counterCells) != null && (m = rs.length) > 0 && rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            if (created)
                                break;
                            continue; // Slot is now non-empty
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) // CAS already known to fail
                    wasUncontended = true; // Continue after rehash
                else if (U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))
                    break;
                else if (counterCells != as || n >= NCPU)
                    collide = false; // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                    try {
                        if (counterCells == as) {// Expand table unless stale
                            CounterCell[] rs = new CounterCell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            counterCells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue; // Retry with expanded table
                }
                h = ThreadLocalRandom.advanceProbe(h);
            } else if (cellsBusy == 0 && counterCells == as && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                boolean init = false;
                try { // Initialize table
                    if (counterCells == as) {
                        CounterCell[] rs = new CounterCell[2];
                        rs[h & 1] = new CounterCell(x);
                        counterCells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            } else if (U.compareAndSwapLong(this, BASECOUNT, v = baseCount, v + x))
                break; // Fall back on using base
        }
    }

    /* ---------------- Conversion from/to TreeBins -------------- */

    // table扩容或者链表转红黑树
    // 当数组长度小于64的时候进行扩容
    private final void treeifyBin(Node<K, V>[] tab, int index) {  //TODO
        Node<K, V> b;
        @SuppressWarnings("unused")
        int n, sc;
        if (tab != null) {
            if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
                tryPresize(n << 1);   // 扩容
            else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
                synchronized (b) {
                    if (tabAt(tab, index) == b) {
                        TreeNode<K, V> hd = null, tl = null; // hd 头节点 tl 尾节点
                        for (Node<K, V> e = b; e != null; e = e.next) {
                            TreeNode<K, V> p = new TreeNode<K, V>(e.hash, e.key, e.val, null, null);
                            if ((p.prev = tl) == null)
                                hd = p;
                            else
                                tl.next = p;
                            tl = p;
                        }
                        setTabAt(tab, index, new TreeBin<K, V>(hd)); //将链表转换后的红黑树的头节点放在table的index所在的位置
                    }
                }
            }
        }
    }

    /**
     * Returns a list on non-TreeNodes replacing those in given list.
     */
    static <K, V> Node<K, V> untreeify(Node<K, V> b) {
        Node<K, V> hd = null, tl = null;
        for (Node<K, V> q = b; q != null; q = q.next) {
            Node<K, V> p = new Node<K, V>(q.hash, q.key, q.val, null);
            if (tl == null)
                hd = p;
            else
                tl.next = p;
            tl = p;
        }
        return hd;
    }

    /* ---------------- TreeNodes -------------- */

    /**
     * Nodes for use in TreeBins
     */
    static final class TreeNode<K, V> extends Node<K, V> {
        TreeNode<K, V> parent; // red-black tree links
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        TreeNode<K, V> prev; // needed to unlink next upon deletion
        boolean red;

        TreeNode(int hash, K key, V val, Node<K, V> next, TreeNode<K, V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }

        Node<K, V> find(int h, Object k) {
            return findTreeNode(h, k, null);
        }

        /**
         * Returns the TreeNode (or null if not found) for the given key starting at
         * given root.
         */
        final TreeNode<K, V> findTreeNode(int h, Object k, Class<?> kc) {
            if (k != null) {
                TreeNode<K, V> p = this;
                do {
                    int ph, dir;
                    K pk;
                    TreeNode<K, V> q;
                    TreeNode<K, V> pl = p.left, pr = p.right;
                    if ((ph = p.hash) > h)
                        p = pl;
                    else if (ph < h)
                        p = pr;
                    else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                        return p;
                    else if (pl == null)
                        p = pr;
                    else if (pr == null)
                        p = pl;
                    else if ((kc != null || (kc = comparableClassFor(k)) != null)
                            && (dir = compareComparables(kc, k, pk)) != 0)
                        p = (dir < 0) ? pl : pr;
                    else if ((q = pr.findTreeNode(h, k, kc)) != null)
                        return q;
                    else
                        p = pl;
                } while (p != null);
            }
            return null;
        }
    }

    /* ---------------- TreeBins -------------- */

    /**
     * TreeNodes used at the heads of bins. TreeBins do not hold user keys or
     * values, but instead point to list of TreeNodes and their root. They also
     * maintain a parasitic read-write lock forcing writers (who hold bin lock) to
     * wait for readers (who do not) to complete before tree restructuring
     * operations.
     */
    static final class TreeBin<K, V> extends Node<K, V> {
        TreeNode<K, V> root;
        volatile TreeNode<K, V> first;
        volatile Thread waiter;
        volatile int lockState;

        static final int WRITER = 1; // 写锁
        static final int WAITER = 2; // 等待中
        static final int READER = 4; // 读写锁

        /**
         * Tie-breaking utility for ordering insertions when equal hashCodes and
         * non-comparable. We don't require a total order, just a consistent insertion
         * rule to maintain equivalence across rebalancings. Tie-breaking further than
         * necessary simplifies testing a bit.
         */
        static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null || (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1);
            return d;
        }

        /**
         * Creates bin with initial set of nodes headed by b.
         */
        TreeBin(TreeNode<K, V> b) {
            super(TREEBIN, null, null, null);
            this.first = b;
            TreeNode<K, V> r = null;
            for (TreeNode<K, V> x = b, next; x != null; x = next) {
                next = (TreeNode<K, V>) x.next;
                x.left = x.right = null;
                if (r == null) {
                    x.parent = null;
                    x.red = false;
                    r = x;
                } else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (TreeNode<K, V> p = r; ; ) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null && (kc = comparableClassFor(k)) == null)
                                || (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);
                        TreeNode<K, V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            r = balanceInsertion(r, x);
                            break;
                        }
                    }
                }
            }
            this.root = r;
            assert checkInvariants(root);
        }

        /**
         * Acquires write lock for tree restructuring.
         */
        private final void lockRoot() {
            if (!U.compareAndSwapInt(this, LOCKSTATE, 0, WRITER))
                contendedLock(); // offload to separate method
        }

        /**
         * Releases write lock for tree restructuring.
         */
        private final void unlockRoot() {
            lockState = 0;
        }

        /**
         * Possibly blocks awaiting root lock.
         */
        private final void contendedLock() {
            boolean waiting = false;
            for (int s; ; ) {
                if (((s = lockState) & ~WAITER) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, WRITER)) {
                        if (waiting)
                            waiter = null;
                        return;
                    }
                } else if ((s & WAITER) == 0) {
                    if (U.compareAndSwapInt(this, LOCKSTATE, s, s | WAITER)) {
                        waiting = true;
                        waiter = Thread.currentThread();
                    }
                } else if (waiting)
                    LockSupport.park(this);
            }
        }

        /**
         * Returns matching node or null if none. Tries to search using tree comparisons
         * from root, but continues linear search when lock not available.
         */
        final Node<K, V> find(int h, Object k) {
            if (k != null) {
                for (Node<K, V> e = first; e != null; ) {
                    int s;
                    K ek;
                    if (((s = lockState) & (WAITER | WRITER)) != 0) {
                        if (e.hash == h && ((ek = e.key) == k || (ek != null && k.equals(ek))))
                            return e;
                        e = e.next;
                    } else if (U.compareAndSwapInt(this, LOCKSTATE, s, s + READER)) {
                        TreeNode<K, V> r, p;
                        try {
                            p = ((r = root) == null ? null : r.findTreeNode(h, k, null));
                        } finally {
                            Thread w;
                            if (U.getAndAddInt(this, LOCKSTATE, -READER) == (READER | WAITER) && (w = waiter) != null)
                                LockSupport.unpark(w);
                        }
                        return p;
                    }
                }
            }
            return null;
        }

        /**
         * Finds or adds a node.
         *
         * @return null if added
         */
        final TreeNode<K, V> putTreeVal(int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            for (TreeNode<K, V> p = root; ; ) {
                int dir, ph;
                K pk;
                if (p == null) {
                    first = root = new TreeNode<K, V>(h, k, v, null, null);
                    break;
                } else if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (pk != null && k.equals(pk)))
                    return p;
                else if ((kc == null && (kc = comparableClassFor(k)) == null)
                        || (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {
                        TreeNode<K, V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null && (q = ch.findTreeNode(h, k, kc)) != null)
                                || ((ch = p.right) != null && (q = ch.findTreeNode(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                TreeNode<K, V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    TreeNode<K, V> x, f = first;
                    first = x = new TreeNode<K, V>(h, k, v, f, xp);
                    if (f != null)
                        f.prev = x;
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    if (!xp.red)
                        x.red = true;
                    else {
                        lockRoot();
                        try {
                            root = balanceInsertion(root, x);
                        } finally {
                            unlockRoot();
                        }
                    }
                    break;
                }
            }
            assert checkInvariants(root);
            return null;
        }

        /**
         * Removes the given node, that must be present before this call. This is
         * messier than typical red-black deletion code because we cannot swap the
         * contents of an interior node with a leaf successor that is pinned by "next"
         * pointers that are accessible independently of lock. So instead we swap the
         * tree linkages.
         *
         * @return true if now too small, so should be untreeified
         */
        final boolean removeTreeNode(TreeNode<K, V> p) {
            TreeNode<K, V> next = (TreeNode<K, V>) p.next;
            TreeNode<K, V> pred = p.prev; // unlink traversal pointers
            TreeNode<K, V> r, rl;
            if (pred == null)
                first = next;
            else
                pred.next = next;
            if (next != null)
                next.prev = pred;
            if (first == null) {
                root = null;
                return true;
            }
            if ((r = root) == null || r.right == null || // too small
                    (rl = r.left) == null || rl.left == null)
                return true;
            lockRoot();
            try {
                TreeNode<K, V> replacement;
                TreeNode<K, V> pl = p.left;
                TreeNode<K, V> pr = p.right;
                if (pl != null && pr != null) {
                    TreeNode<K, V> s = pr, sl;
                    while ((sl = s.left) != null) // find successor
                        s = sl;
                    boolean c = s.red;
                    s.red = p.red;
                    p.red = c; // swap colors
                    TreeNode<K, V> sr = s.right;
                    TreeNode<K, V> pp = p.parent;
                    if (s == pr) { // p was s's direct parent
                        p.parent = s;
                        s.right = p;
                    } else {
                        TreeNode<K, V> sp = s.parent;
                        if ((p.parent = sp) != null) {
                            if (s == sp.left)
                                sp.left = p;
                            else
                                sp.right = p;
                        }
                        if ((s.right = pr) != null)
                            pr.parent = s;
                    }
                    p.left = null;
                    if ((p.right = sr) != null)
                        sr.parent = p;
                    if ((s.left = pl) != null)
                        pl.parent = s;
                    if ((s.parent = pp) == null)
                        r = s;
                    else if (p == pp.left)
                        pp.left = s;
                    else
                        pp.right = s;
                    if (sr != null)
                        replacement = sr;
                    else
                        replacement = p;
                } else if (pl != null)
                    replacement = pl;
                else if (pr != null)
                    replacement = pr;
                else
                    replacement = p;
                if (replacement != p) {
                    TreeNode<K, V> pp = replacement.parent = p.parent;
                    if (pp == null)
                        r = replacement;
                    else if (p == pp.left)
                        pp.left = replacement;
                    else
                        pp.right = replacement;
                    p.left = p.right = p.parent = null;
                }

                root = (p.red) ? r : balanceDeletion(r, replacement);

                if (p == replacement) { // detach pointers
                    TreeNode<K, V> pp;
                    if ((pp = p.parent) != null) {
                        if (p == pp.left)
                            pp.left = null;
                        else if (p == pp.right)
                            pp.right = null;
                        p.parent = null;
                    }
                }
            } finally {
                unlockRoot();
            }
            assert checkInvariants(root);
            return false;
        }

        /* ------------------------------------------------------------ */
        // Red-black tree methods, all adapted from CLR

        static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, TreeNode<K, V> p) {
            TreeNode<K, V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, TreeNode<K, V> p) {
            TreeNode<K, V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
            x.red = true;
            for (TreeNode<K, V> xp, xpp, xppl, xppr; ; ) {
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                } else if (!xp.red || (xpp = xp.parent) == null)
                    return root;
                if (xp == (xppl = xpp.left)) {
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.right) {
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                } else {
                    if (xppl != null && xppl.red) {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    } else {
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x) {
            for (TreeNode<K, V> xp, xpl, xpr; ; ) {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                } else if (x.red) {
                    x.red = false;
                    return root;
                } else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        TreeNode<K, V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) && (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        } else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ? null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                } else { // symmetric
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        TreeNode<K, V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) && (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        } else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ? null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Recursive invariant check
         */
        static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
            TreeNode<K, V> tp = t.parent, tl = t.left, tr = t.right, tb = t.prev, tn = (TreeNode<K, V>) t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }

        private static final sun.misc.Unsafe U;
        private static final long LOCKSTATE;

        static {
            try {
                U = sun.misc.Unsafe.getUnsafe();
                Class<?> k = TreeBin.class;
                LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    /* ----------------Table Traversal -------------- */

    /**
     * Records the table, its length, and current traversal index for a traverser
     * that must process a region of a forwarded table before proceeding with
     * current table.
     */
    static final class TableStack<K, V> {
        int length;
        int index;
        Node<K, V>[] tab;
        TableStack<K, V> next;
    }

    /**
     * Encapsulates traversal for methods such as containsValue; also serves as a
     * base class for other iterators and spliterators.
     * <p>
     * Method advance visits once each still-valid node that was reachable upon
     * iterator construction. It might miss some that were added to a bin after the
     * bin was visited, which is OK wrt consistency guarantees. Maintaining this
     * property in the face of possible ongoing resizes requires a fair amount of
     * bookkeeping state that is difficult to optimize away amidst volatile
     * accesses. Even so, traversal maintains reasonable throughput.
     * <p>
     * Normally, iteration proceeds bin-by-bin traversing lists. However, if the
     * table has been resized, then all future steps must traverse both the bin at
     * the current index as well as at (index + baseSize); and so on for further
     * resizings. To paranoically cope with potential sharing by users of iterators
     * across threads, iteration terminates if a bounds checks fails for a table
     * read.
     */
    static class Traverser<K, V> {
        Node<K, V>[] tab; // current table; updated if resized
        Node<K, V> next; // the next entry to use
        TableStack<K, V> stack, spare; // to save/restore on ForwardingNodes
        int index; // index of bin to use next
        int baseIndex; // current index of initial table
        int baseLimit; // index bound for initial table
        final int baseSize; // initial table size

        Traverser(Node<K, V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
        }

        /**
         * Advances if possible, returning next valid node, or null if none.
         */
        final Node<K, V> advance() {
            Node<K, V> e;
            if ((e = next) != null)
                e = e.next;
            for (; ; ) {
                Node<K, V>[] t;
                int i, n; // must use locals in checks
                if (e != null)
                    return next = e;
                if (baseIndex >= baseLimit || (t = tab) == null || (n = t.length) <= (i = index) || i < 0)
                    return next = null;
                if ((e = tabAt(t, i)) != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode<K, V>) e).nextTable;
                        e = null;
                        pushState(t, i, n);
                        continue;
                    } else if (e instanceof TreeBin)
                        e = ((TreeBin<K, V>) e).first;
                    else
                        e = null;
                }
                if (stack != null)
                    recoverState(n);
                else if ((index = i + baseSize) >= n)
                    index = ++baseIndex; // visit upper slots if present
            }
        }

        /**
         * Saves traversal state upon encountering a forwarding node.
         */
        private void pushState(Node<K, V>[] t, int i, int n) {
            TableStack<K, V> s = spare; // reuse if possible
            if (s != null)
                spare = s.next;
            else
                s = new TableStack<K, V>();
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = stack;
            stack = s;
        }

        /**
         * Possibly pops traversal state.
         *
         * @param n length of current table
         */
        private void recoverState(int n) {
            TableStack<K, V> s;
            int len;
            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                n = len;
                index = s.index;
                tab = s.tab;
                s.tab = null;
                TableStack<K, V> next = s.next;
                s.next = spare; // save for reuse
                stack = next;
                spare = s;
            }
            if (s == null && (index += baseSize) >= n)
                index = ++baseIndex;
        }
    }

    /**
     * Base of key, value, and entry Iterators. Adds fields to Traverser to support
     * iterator.remove.
     */
    static class BaseIterator<K, V> extends Traverser<K, V> {
        final ConcurrentHashMapDebug<K, V> map;
        Node<K, V> lastReturned;

        BaseIterator(Node<K, V>[] tab, int size, int index, int limit, ConcurrentHashMapDebug<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            advance();
        }

        public final boolean hasNext() {
            return next != null;
        }

        public final boolean hasMoreElements() {
            return next != null;
        }

        public final void remove() {
            Node<K, V> p;
            if ((p = lastReturned) == null)
                throw new IllegalStateException();
            lastReturned = null;
            map.replaceNode(p.key, null, null);
        }
    }

    static final class KeyIterator<K, V> extends BaseIterator<K, V> implements Iterator<K>, Enumeration<K> {
        KeyIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMapDebug<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final K next() {
            Node<K, V> p;
            if ((p = next) == null)
                throw new NoSuchElementException();
            K k = p.key;
            lastReturned = p;
            advance();
            return k;
        }

        public final K nextElement() {
            return next();
        }
    }

    static final class ValueIterator<K, V> extends BaseIterator<K, V> implements Iterator<V>, Enumeration<V> {
        ValueIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMapDebug<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final V next() {
            Node<K, V> p;
            if ((p = next) == null)
                throw new NoSuchElementException();
            V v = p.val;
            lastReturned = p;
            advance();
            return v;
        }

        public final V nextElement() {
            return next();
        }
    }

    static final class EntryIterator<K, V> extends BaseIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        EntryIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMapDebug<K, V> map) {
            super(tab, index, size, limit, map);
        }

        public final Map.Entry<K, V> next() {
            Node<K, V> p;
            if ((p = next) == null)
                throw new NoSuchElementException();
            K k = p.key;
            V v = p.val;
            lastReturned = p;
            advance();
            return new MapEntry<K, V>(k, v, map);
        }
    }

    /**
     * Exported Entry for EntryIterator
     */
    static final class MapEntry<K, V> implements Map.Entry<K, V> {
        final K key; // non-null
        V val; // non-null
        final ConcurrentHashMapDebug<K, V> map;

        MapEntry(K key, V val, ConcurrentHashMapDebug<K, V> map) {
            this.key = key;
            this.val = val;
            this.map = map;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return val;
        }

        public int hashCode() {
            return key.hashCode() ^ val.hashCode();
        }

        public String toString() {
            return key + "=" + val;
        }

        public boolean equals(Object o) {
            Object k, v;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
                    && (v = e.getValue()) != null && (k == key || k.equals(key)) && (v == val || v.equals(val)));
        }

        /**
         * Sets our entry's value and writes through to the map. The value to return is
         * somewhat arbitrary here. Since we do not necessarily track asynchronous
         * changes, the most recent "previous" value could be different from what we
         * return (or could even have been removed, in which case the put will
         * re-establish). We do not and cannot guarantee more.
         */
        public V setValue(V value) {
            if (value == null)
                throw new NullPointerException();
            V v = val;
            val = value;
            map.put(key, value);
            return v;
        }
    }

    static final class KeySpliterator<K, V> extends Traverser<K, V> implements Spliterator<K> {
        long est; // size estimate

        KeySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        public Spliterator<K> trySplit() {
            int i, f, h;
            return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null
                    : new KeySpliterator<K, V>(tab, baseSize, baseLimit = h, f, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            for (Node<K, V> p; (p = advance()) != null; )
                action.accept(p.key);
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            Node<K, V> p;
            if ((p = advance()) == null)
                return false;
            action.accept(p.key);
            return true;
        }

        public long estimateSize() {
            return est;
        }

        public int characteristics() {
            return Spliterator.DISTINCT | Spliterator.CONCURRENT | Spliterator.NONNULL;
        }
    }

    static final class ValueSpliterator<K, V> extends Traverser<K, V> implements Spliterator<V> {
        long est; // size estimate

        ValueSpliterator(Node<K, V>[] tab, int size, int index, int limit, long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        public Spliterator<V> trySplit() {
            int i, f, h;
            return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null
                    : new ValueSpliterator<K, V>(tab, baseSize, baseLimit = h, f, est >>>= 1);
        }

        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            for (Node<K, V> p; (p = advance()) != null; )
                action.accept(p.val);
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            Node<K, V> p;
            if ((p = advance()) == null)
                return false;
            action.accept(p.val);
            return true;
        }

        public long estimateSize() {
            return est;
        }

        public int characteristics() {
            return Spliterator.CONCURRENT | Spliterator.NONNULL;
        }
    }

    static final class EntrySpliterator<K, V> extends Traverser<K, V> implements Spliterator<Map.Entry<K, V>> {
        final ConcurrentHashMapDebug<K, V> map; // To export MapEntry
        long est; // size estimate

        EntrySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est, ConcurrentHashMapDebug<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            this.est = est;
        }

        public Spliterator<Map.Entry<K, V>> trySplit() {
            int i, f, h;
            return (h = ((i = baseIndex) + (f = baseLimit)) >>> 1) <= i ? null
                    : new EntrySpliterator<K, V>(tab, baseSize, baseLimit = h, f, est >>>= 1, map);
        }

        public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            for (Node<K, V> p; (p = advance()) != null; )
                action.accept(new MapEntry<K, V>(p.key, p.val, map));
        }

        public boolean tryAdvance(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            Node<K, V> p;
            if ((p = advance()) == null)
                return false;
            action.accept(new MapEntry<K, V>(p.key, p.val, map));
            return true;
        }

        public long estimateSize() {
            return est;
        }

        public int characteristics() {
            return Spliterator.DISTINCT | Spliterator.CONCURRENT | Spliterator.NONNULL;
        }
    }

    // Parallel bulk operations

    /**
     * Computes initial batch value for bulk tasks. The returned value is
     * approximately exp2 of the number of times (minus one) to split task by two
     * before executing leaf action. This value is faster to compute and more
     * convenient to use as a guide to splitting than is the depth, since it is used
     * while dividing by two anyway.
     */
    final int batchFor(long b) {
        long n;
        if (b == Long.MAX_VALUE || (n = sumCount()) <= 1L || n < b)
            return 0;
        int sp = ForkJoinPool.getCommonPoolParallelism() << 2; // slack of 4
        return (b <= 0L || (n /= b) >= sp) ? sp : (int) n;
    }

    /**
     * Performs the given action for each (key, value).
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEach(long parallelismThreshold, BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();
        new ForEachMappingTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation of each (key,
     * value).
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case the action is not
     *                             applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEach(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer,
                            Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new ForEachTransformedMappingTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer,
                action).invoke();
    }

    /**
     * Returns a non-null result from applying the given search function on each
     * (key, value), or null if none. Upon success, further element processing is
     * suppressed and the results of any other parallel invocations of the search
     * function are ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param searchFunction       a function returning a non-null result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search function on each
     * (key, value), or null if none
     * @since 1.8
     */
    public <U> U search(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> searchFunction) {
        if (searchFunction == null)
            throw new NullPointerException();
        return new SearchMappingsTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
                new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all (key,
     * value) pairs using the given reducer to combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation of all (key,
     * value) pairs
     * @since 1.8
     */
    public <U> U reduce(long parallelismThreshold, BiFunction<? super K, ? super V, ? extends U> transformer,
                        BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceMappingsTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all (key,
     * value) pairs using the given reducer to combine values, and the given basis
     * as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all (key,
     * value) pairs
     * @since 1.8
     */
    public double reduceToDouble(long parallelismThreshold, ToDoubleBiFunction<? super K, ? super V> transformer,
                                 double basis, DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceMappingsToDoubleTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
                transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all (key,
     * value) pairs using the given reducer to combine values, and the given basis
     * as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all (key,
     * value) pairs
     * @since 1.8
     */
    public long reduceToLong(long parallelismThreshold, ToLongBiFunction<? super K, ? super V> transformer, long basis,
                             LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceMappingsToLongTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
                transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all (key,
     * value) pairs using the given reducer to combine values, and the given basis
     * as an identity value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all (key,
     * value) pairs
     * @since 1.8
     */
    public int reduceToInt(long parallelismThreshold, ToIntBiFunction<? super K, ? super V> transformer, int basis,
                           IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceMappingsToIntTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
                transformer, basis, reducer).invoke();
    }

    /**
     * Performs the given action for each key.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEachKey(long parallelismThreshold, Consumer<? super K> action) {
        if (action == null)
            throw new NullPointerException();
        new ForEachKeyTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation of each key.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case the action is not
     *                             applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEachKey(long parallelismThreshold, Function<? super K, ? extends U> transformer,
                               Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new ForEachTransformedKeyTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer, action)
                .invoke();
    }

    /**
     * Returns a non-null result from applying the given search function on each
     * key, or null if none. Upon success, further element processing is suppressed
     * and the results of any other parallel invocations of the search function are
     * ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param searchFunction       a function returning a non-null result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search function on each
     * key, or null if none
     * @since 1.8
     */
    public <U> U searchKeys(long parallelismThreshold, Function<? super K, ? extends U> searchFunction) {
        if (searchFunction == null)
            throw new NullPointerException();
        return new SearchKeysTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
                new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating all keys using the given reducer to
     * combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating all keys using the given reducer to
     * combine values, or null if none
     * @since 1.8
     */
    public K reduceKeys(long parallelismThreshold, BiFunction<? super K, ? super K, ? extends K> reducer) {
        if (reducer == null)
            throw new NullPointerException();
        return new ReduceKeysTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all keys using
     * the given reducer to combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation of all keys
     * @since 1.8
     */
    public <U> U reduceKeys(long parallelismThreshold, Function<? super K, ? extends U> transformer,
                            BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceKeysTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all keys using
     * the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all keys
     * @since 1.8
     */
    public double reduceKeysToDouble(long parallelismThreshold, ToDoubleFunction<? super K> transformer, double basis,
                                     DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceKeysToDoubleTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all keys using
     * the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all keys
     * @since 1.8
     */
    public long reduceKeysToLong(long parallelismThreshold, ToLongFunction<? super K> transformer, long basis,
                                 LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceKeysToLongTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all keys using
     * the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all keys
     * @since 1.8
     */
    public int reduceKeysToInt(long parallelismThreshold, ToIntFunction<? super K> transformer, int basis,
                               IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceKeysToIntTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                basis, reducer).invoke();
    }

    /**
     * Performs the given action for each value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEachValue(long parallelismThreshold, Consumer<? super V> action) {
        if (action == null)
            throw new NullPointerException();
        new ForEachValueTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation of each value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case the action is not
     *                             applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEachValue(long parallelismThreshold, Function<? super V, ? extends U> transformer,
                                 Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new ForEachTransformedValueTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer, action)
                .invoke();
    }

    /**
     * Returns a non-null result from applying the given search function on each
     * value, or null if none. Upon success, further element processing is
     * suppressed and the results of any other parallel invocations of the search
     * function are ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param searchFunction       a function returning a non-null result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search function on each
     * value, or null if none
     * @since 1.8
     */
    public <U> U searchValues(long parallelismThreshold, Function<? super V, ? extends U> searchFunction) {
        if (searchFunction == null)
            throw new NullPointerException();
        return new SearchValuesTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
                new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating all values using the given reducer to
     * combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating all values
     * @since 1.8
     */
    public V reduceValues(long parallelismThreshold, BiFunction<? super V, ? super V, ? extends V> reducer) {
        if (reducer == null)
            throw new NullPointerException();
        return new ReduceValuesTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all values
     * using the given reducer to combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation of all values
     * @since 1.8
     */
    public <U> U reduceValues(long parallelismThreshold, Function<? super V, ? extends U> transformer,
                              BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceValuesTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all values
     * using the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all values
     * @since 1.8
     */
    public double reduceValuesToDouble(long parallelismThreshold, ToDoubleFunction<? super V> transformer, double basis,
                                       DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceValuesToDoubleTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
                transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all values
     * using the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all values
     * @since 1.8
     */
    public long reduceValuesToLong(long parallelismThreshold, ToLongFunction<? super V> transformer, long basis,
                                   LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceValuesToLongTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all values
     * using the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all values
     * @since 1.8
     */
    public int reduceValuesToInt(long parallelismThreshold, ToIntFunction<? super V> transformer, int basis,
                                 IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceValuesToIntTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                basis, reducer).invoke();
    }

    /**
     * Performs the given action for each entry.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param action               the action
     * @since 1.8
     */
    public void forEachEntry(long parallelismThreshold, Consumer<? super Map.Entry<K, V>> action) {
        if (action == null)
            throw new NullPointerException();
        new ForEachEntryTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, action).invoke();
    }

    /**
     * Performs the given action for each non-null transformation of each entry.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case the action is not
     *                             applied)
     * @param action               the action
     * @param <U>                  the return type of the transformer
     * @since 1.8
     */
    public <U> void forEachEntry(long parallelismThreshold, Function<Map.Entry<K, V>, ? extends U> transformer,
                                 Consumer<? super U> action) {
        if (transformer == null || action == null)
            throw new NullPointerException();
        new ForEachTransformedEntryTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, transformer, action)
                .invoke();
    }

    /**
     * Returns a non-null result from applying the given search function on each
     * entry, or null if none. Upon success, further element processing is
     * suppressed and the results of any other parallel invocations of the search
     * function are ignored.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param searchFunction       a function returning a non-null result on success, else null
     * @param <U>                  the return type of the search function
     * @return a non-null result from applying the given search function on each
     * entry, or null if none
     * @since 1.8
     */
    public <U> U searchEntries(long parallelismThreshold, Function<Map.Entry<K, V>, ? extends U> searchFunction) {
        if (searchFunction == null)
            throw new NullPointerException();
        return new SearchEntriesTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, searchFunction,
                new AtomicReference<U>()).invoke();
    }

    /**
     * Returns the result of accumulating all entries using the given reducer to
     * combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating all entries
     * @since 1.8
     */
    public Map.Entry<K, V> reduceEntries(long parallelismThreshold,
                                         BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
        if (reducer == null)
            throw new NullPointerException();
        return new ReduceEntriesTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all entries
     * using the given reducer to combine values, or null if none.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element, or null if
     *                             there is no transformation (in which case it is not combined)
     * @param reducer              a commutative associative combining function
     * @param <U>                  the return type of the transformer
     * @return the result of accumulating the given transformation of all entries
     * @since 1.8
     */
    public <U> U reduceEntries(long parallelismThreshold, Function<Map.Entry<K, V>, ? extends U> transformer,
                               BiFunction<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceEntriesTask<K, V, U>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all entries
     * using the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all entries
     * @since 1.8
     */
    public double reduceEntriesToDouble(long parallelismThreshold, ToDoubleFunction<Map.Entry<K, V>> transformer,
                                        double basis, DoubleBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceEntriesToDoubleTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
                transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all entries
     * using the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all entries
     * @since 1.8
     */
    public long reduceEntriesToLong(long parallelismThreshold, ToLongFunction<Map.Entry<K, V>> transformer, long basis,
                                    LongBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceEntriesToLongTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null,
                transformer, basis, reducer).invoke();
    }

    /**
     * Returns the result of accumulating the given transformation of all entries
     * using the given reducer to combine values, and the given basis as an identity
     * value.
     *
     * @param parallelismThreshold the (estimated) number of elements needed for this operation to be
     *                             executed in parallel
     * @param transformer          a function returning the transformation for an element
     * @param basis                the identity (initial default value) for the reduction
     * @param reducer              a commutative associative combining function
     * @return the result of accumulating the given transformation of all entries
     * @since 1.8
     */
    public int reduceEntriesToInt(long parallelismThreshold, ToIntFunction<Map.Entry<K, V>> transformer, int basis,
                                  IntBinaryOperator reducer) {
        if (transformer == null || reducer == null)
            throw new NullPointerException();
        return new MapReduceEntriesToIntTask<K, V>(null, batchFor(parallelismThreshold), 0, 0, table, null, transformer,
                basis, reducer).invoke();
    }

    /* ----------------Views -------------- */

    /**
     * Base class for views.
     */
    abstract static class CollectionView<K, V, E> implements Collection<E>, java.io.Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        final ConcurrentHashMapDebug<K, V> map;

        CollectionView(ConcurrentHashMapDebug<K, V> map) {
            this.map = map;
        }

        /**
         * Returns the map backing this view.
         *
         * @return the map backing this view
         */
        public ConcurrentHashMapDebug<K, V> getMap() {
            return map;
        }

        /**
         * Removes all of the elements from this view, by removing all the mappings from
         * the map backing this view.
         */
        public final void clear() {
            map.clear();
        }

        public final int size() {
            return map.size();
        }

        public final boolean isEmpty() {
            return map.isEmpty();
        }

        // implementations below rely on concrete classes supplying these
        // abstract methods

        /**
         * Returns an iterator over the elements in this collection.
         *
         * <p>
         * The returned iterator is <a href="package-summary.html#Weakly"><i>weakly
         * consistent</i></a>.
         *
         * @return an iterator over the elements in this collection
         */
        public abstract Iterator<E> iterator();

        public abstract boolean contains(Object o);

        public abstract boolean remove(Object o);

        private static final String oomeMsg = "Required array size too large";

        public final Object[] toArray() {
            long sz = map.mappingCount();
            if (sz > MAX_ARRAY_SIZE)
                throw new OutOfMemoryError(oomeMsg);
            int n = (int) sz;
            Object[] r = new Object[n];
            int i = 0;
            for (E e : this) {
                if (i == n) {
                    if (n >= MAX_ARRAY_SIZE)
                        throw new OutOfMemoryError(oomeMsg);
                    if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1)
                        n = MAX_ARRAY_SIZE;
                    else
                        n += (n >>> 1) + 1;
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }
            return (i == n) ? r : Arrays.copyOf(r, i);
        }

        @SuppressWarnings("unchecked")
        public final <T> T[] toArray(T[] a) {
            long sz = map.mappingCount();
            if (sz > MAX_ARRAY_SIZE)
                throw new OutOfMemoryError(oomeMsg);
            int m = (int) sz;
            T[] r = (a.length >= m) ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), m);
            int n = r.length;
            int i = 0;
            for (E e : this) {
                if (i == n) {
                    if (n >= MAX_ARRAY_SIZE)
                        throw new OutOfMemoryError(oomeMsg);
                    if (n >= MAX_ARRAY_SIZE - (MAX_ARRAY_SIZE >>> 1) - 1)
                        n = MAX_ARRAY_SIZE;
                    else
                        n += (n >>> 1) + 1;
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = (T) e;
            }
            if (a == r && i < n) {
                r[i] = null; // null-terminate
                return r;
            }
            return (i == n) ? r : Arrays.copyOf(r, i);
        }

        /**
         * Returns a string representation of this collection. The string representation
         * consists of the string representations of the collection's elements in the
         * order they are returned by its iterator, enclosed in square brackets
         * ({@code "[]"}). Adjacent elements are separated by the characters
         * {@code ", "} (comma and space). Elements are converted to strings as by
         * {@link String#valueOf(Object)}.
         *
         * @return a string representation of this collection
         */
        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<E> it = iterator();
            if (it.hasNext()) {
                for (; ; ) {
                    Object e = it.next();
                    sb.append(e == this ? "(this Collection)" : e);
                    if (!it.hasNext())
                        break;
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }

        public final boolean containsAll(Collection<?> c) {
            if (c != this) {
                for (Object e : c) {
                    if (e == null || !contains(e))
                        return false;
                }
            }
            return true;
        }

        public final boolean removeAll(Collection<?> c) {
            if (c == null)
                throw new NullPointerException();
            boolean modified = false;
            for (Iterator<E> it = iterator(); it.hasNext(); ) {
                if (c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        public final boolean retainAll(Collection<?> c) {
            if (c == null)
                throw new NullPointerException();
            boolean modified = false;
            for (Iterator<E> it = iterator(); it.hasNext(); ) {
                if (!c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

    }

    /**
     * A view of a ConcurrentHashMap as a {@link Set} of keys, in which additions
     * may optionally be enabled by mapping to a common value. This class cannot be
     * directly instantiated. See {@link #keySet() keySet()}, {@link #keySet(Object)
     * keySet(V)}, {@link #newKeySet() newKeySet()}, {@link #newKeySet(int)
     * newKeySet(int)}.
     *
     * @since 1.8
     */
    public static class KeySetView<K, V> extends CollectionView<K, V, K> implements Set<K>, java.io.Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;

        KeySetView(ConcurrentHashMapDebug<K, V> map, V value) { // non-public
            super(map);
            this.value = value;
        }

        /**
         * Returns the default mapped value for additions, or {@code null} if additions
         * are not supported.
         *
         * @return the default mapped value for additions, or {@code null} if not
         * supported
         */
        public V getMappedValue() {
            return value;
        }

        /**
         * {@inheritDoc}
         *
         * @throws NullPointerException if the specified key is null
         */
        public boolean contains(Object o) {
            return map.containsKey(o);
        }

        /**
         * Removes the key from this map view, by removing the key (and its
         * corresponding value) from the backing map. This method does nothing if the
         * key is not in the map.
         *
         * @param o the key to be removed from the backing map
         * @return {@code true} if the backing map contained the specified key
         * @throws NullPointerException if the specified key is null
         */
        public boolean remove(Object o) {
            return map.remove(o) != null;
        }

        /**
         * @return an iterator over the keys of the backing map
         */
        public Iterator<K> iterator() {
            Node<K, V>[] t;
            ConcurrentHashMapDebug<K, V> m = map;
            int f = (t = m.table) == null ? 0 : t.length;
            return new KeyIterator<K, V>(t, f, 0, f, m);
        }

        /**
         * Adds the specified key to this set view by mapping the key to the default
         * mapped value in the backing map, if defined.
         *
         * @param e key to be added
         * @return {@code true} if this set changed as a result of the call
         * @throws NullPointerException          if the specified key is null
         * @throws UnsupportedOperationException if no default mapped value for additions was provided
         */
        public boolean add(K e) {
            V v;
            if ((v = value) == null)
                throw new UnsupportedOperationException();
            return map.putVal(e, v, true) == null;
        }

        /**
         * Adds all of the elements in the specified collection to this set, as if by
         * calling {@link #add} on each one.
         *
         * @param c the elements to be inserted into this set
         * @return {@code true} if this set changed as a result of the call
         * @throws NullPointerException          if the collection or any of its elements are {@code null}
         * @throws UnsupportedOperationException if no default mapped value for additions was provided
         */
        public boolean addAll(Collection<? extends K> c) {
            boolean added = false;
            V v;
            if ((v = value) == null)
                throw new UnsupportedOperationException();
            for (K e : c) {
                if (map.putVal(e, v, true) == null)
                    added = true;
            }
            return added;
        }

        public int hashCode() {
            int h = 0;
            for (K e : this)
                h += e.hashCode();
            return h;
        }

        public boolean equals(Object o) {
            Set<?> c;
            return ((o instanceof Set) && ((c = (Set<?>) o) == this || (containsAll(c) && c.containsAll(this))));
        }

        public Spliterator<K> spliterator() {
            Node<K, V>[] t;
            ConcurrentHashMapDebug<K, V> m = map;
            long n = m.sumCount();
            int f = (t = m.table) == null ? 0 : t.length;
            return new KeySpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n);
        }

        public void forEach(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            Node<K, V>[] t;
            if ((t = map.table) != null) {
                Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
                for (Node<K, V> p; (p = it.advance()) != null; )
                    action.accept(p.key);
            }
        }
    }

    /**
     * A view of a ConcurrentHashMap as a {@link Collection} of values, in which
     * additions are disabled. This class cannot be directly instantiated. See
     * {@link #values()}.
     */
    static final class ValuesView<K, V> extends CollectionView<K, V, V> implements Collection<V>, java.io.Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        ValuesView(ConcurrentHashMapDebug<K, V> map) {
            super(map);
        }

        public final boolean contains(Object o) {
            return map.containsValue(o);
        }

        public final boolean remove(Object o) {
            if (o != null) {
                for (Iterator<V> it = iterator(); it.hasNext(); ) {
                    if (o.equals(it.next())) {
                        it.remove();
                        return true;
                    }
                }
            }
            return false;
        }

        public final Iterator<V> iterator() {
            ConcurrentHashMapDebug<K, V> m = map;
            Node<K, V>[] t;
            int f = (t = m.table) == null ? 0 : t.length;
            return new ValueIterator<K, V>(t, f, 0, f, m);
        }

        public final boolean add(V e) {
            throw new UnsupportedOperationException();
        }

        public final boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }

        public Spliterator<V> spliterator() {
            Node<K, V>[] t;
            ConcurrentHashMapDebug<K, V> m = map;
            long n = m.sumCount();
            int f = (t = m.table) == null ? 0 : t.length;
            return new ValueSpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n);
        }

        public void forEach(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            Node<K, V>[] t;
            if ((t = map.table) != null) {
                Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
                for (Node<K, V> p; (p = it.advance()) != null; )
                    action.accept(p.val);
            }
        }
    }

    /**
     * A view of a ConcurrentHashMap as a {@link Set} of (key, value) entries. This
     * class cannot be directly instantiated. See {@link #entrySet()}.
     */
    static final class EntrySetView<K, V> extends CollectionView<K, V, Map.Entry<K, V>>
            implements Set<Map.Entry<K, V>>, java.io.Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        EntrySetView(ConcurrentHashMapDebug<K, V> map) {
            super(map);
        }

        public boolean contains(Object o) {
            Object k, v, r;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
                    && (r = map.get(k)) != null && (v = e.getValue()) != null && (v == r || v.equals(r)));
        }

        public boolean remove(Object o) {
            Object k, v;
            Map.Entry<?, ?> e;
            return ((o instanceof Map.Entry) && (k = (e = (Map.Entry<?, ?>) o).getKey()) != null
                    && (v = e.getValue()) != null && map.remove(k, v));
        }

        /**
         * @return an iterator over the entries of the backing map
         */
        public Iterator<Map.Entry<K, V>> iterator() {
            ConcurrentHashMapDebug<K, V> m = map;
            Node<K, V>[] t;
            int f = (t = m.table) == null ? 0 : t.length;
            return new EntryIterator<K, V>(t, f, 0, f, m);
        }

        public boolean add(Entry<K, V> e) {
            return map.putVal(e.getKey(), e.getValue(), false) == null;
        }

        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            boolean added = false;
            for (Entry<K, V> e : c) {
                if (add(e))
                    added = true;
            }
            return added;
        }

        public final int hashCode() {
            int h = 0;
            Node<K, V>[] t;
            if ((t = map.table) != null) {
                Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
                for (Node<K, V> p; (p = it.advance()) != null; ) {
                    h += p.hashCode();
                }
            }
            return h;
        }

        public final boolean equals(Object o) {
            Set<?> c;
            return ((o instanceof Set) && ((c = (Set<?>) o) == this || (containsAll(c) && c.containsAll(this))));
        }

        public Spliterator<Map.Entry<K, V>> spliterator() {
            Node<K, V>[] t;
            ConcurrentHashMapDebug<K, V> m = map;
            long n = m.sumCount();
            int f = (t = m.table) == null ? 0 : t.length;
            return new EntrySpliterator<K, V>(t, f, 0, f, n < 0L ? 0L : n, m);
        }

        public void forEach(Consumer<? super Map.Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            Node<K, V>[] t;
            if ((t = map.table) != null) {
                Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
                for (Node<K, V> p; (p = it.advance()) != null; )
                    action.accept(new MapEntry<K, V>(p.key, p.val, map));
            }
        }

    }

    // -------------------------------------------------------

    /**
     * Base class for bulk tasks. Repeats some fields and code from class Traverser,
     * because we need to subclass CountedCompleter.
     */
    @SuppressWarnings("serial")
    abstract static class BulkTask<K, V, R> extends CountedCompleter<R> {
        Node<K, V>[] tab; // same as Traverser
        Node<K, V> next;
        TableStack<K, V> stack, spare;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;
        int batch; // split control

        BulkTask(BulkTask<K, V, ?> par, int b, int i, int f, Node<K, V>[] t) {
            super(par);
            this.batch = b;
            this.index = this.baseIndex = i;
            if ((this.tab = t) == null)
                this.baseSize = this.baseLimit = 0;
            else if (par == null)
                this.baseSize = this.baseLimit = t.length;
            else {
                this.baseLimit = f;
                this.baseSize = par.baseSize;
            }
        }

        /**
         * Same as Traverser version
         */
        final Node<K, V> advance() {
            Node<K, V> e;
            if ((e = next) != null)
                e = e.next;
            for (; ; ) {
                Node<K, V>[] t;
                int i, n;
                if (e != null)
                    return next = e;
                if (baseIndex >= baseLimit || (t = tab) == null || (n = t.length) <= (i = index) || i < 0)
                    return next = null;
                if ((e = tabAt(t, i)) != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode<K, V>) e).nextTable;
                        e = null;
                        pushState(t, i, n);
                        continue;
                    } else if (e instanceof TreeBin)
                        e = ((TreeBin<K, V>) e).first;
                    else
                        e = null;
                }
                if (stack != null)
                    recoverState(n);
                else if ((index = i + baseSize) >= n)
                    index = ++baseIndex;
            }
        }

        private void pushState(Node<K, V>[] t, int i, int n) {
            TableStack<K, V> s = spare;
            if (s != null)
                spare = s.next;
            else
                s = new TableStack<K, V>();
            s.tab = t;
            s.length = n;
            s.index = i;
            s.next = stack;
            stack = s;
        }

        private void recoverState(int n) {
            TableStack<K, V> s;
            int len;
            while ((s = stack) != null && (index += (len = s.length)) >= n) {
                n = len;
                index = s.index;
                tab = s.tab;
                s.tab = null;
                TableStack<K, V> next = s.next;
                s.next = spare; // save for reuse
                stack = next;
                spare = s;
            }
            if (s == null && (index += baseSize) >= n)
                index = ++baseIndex;
        }
    }

    /*
     * Task classes. Coded in a regular but ugly format/style to simplify checks
     * that each variant differs in the right way from others. The null screenings
     * exist because compilers cannot tell that we've already null-checked task
     * arguments, so we force simplest hoisted bypass to help avoid convoluted
     * traps.
     */
    @SuppressWarnings("serial")
    static final class ForEachKeyTask<K, V> extends BulkTask<K, V, Void> {
        final Consumer<? super K> action;

        ForEachKeyTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Consumer<? super K> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final Consumer<? super K> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachKeyTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    action.accept(p.key);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachValueTask<K, V> extends BulkTask<K, V, Void> {
        final Consumer<? super V> action;

        ForEachValueTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Consumer<? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final Consumer<? super V> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachValueTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    action.accept(p.val);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachEntryTask<K, V> extends BulkTask<K, V, Void> {
        final Consumer<? super Entry<K, V>> action;

        ForEachEntryTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                         Consumer<? super Entry<K, V>> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final Consumer<? super Entry<K, V>> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachEntryTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    action.accept(p);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachMappingTask<K, V> extends BulkTask<K, V, Void> {
        final BiConsumer<? super K, ? super V> action;

        ForEachMappingTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                           BiConsumer<? super K, ? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        public final void compute() {
            final BiConsumer<? super K, ? super V> action;
            if ((action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachMappingTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    action.accept(p.key, p.val);
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedKeyTask<K, V, U> extends BulkTask<K, V, Void> {
        final Function<? super K, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedKeyTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                  Function<? super K, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final Function<? super K, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null && (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachTransformedKeyTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
                            action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedValueTask<K, V, U> extends BulkTask<K, V, Void> {
        final Function<? super V, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedValueTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                    Function<? super V, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final Function<? super V, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null && (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachTransformedValueTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
                            action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.val)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedEntryTask<K, V, U> extends BulkTask<K, V, Void> {
        final Function<Map.Entry<K, V>, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedEntryTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                    Function<Map.Entry<K, V>, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final Function<Map.Entry<K, V>, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null && (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachTransformedEntryTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
                            action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ForEachTransformedMappingTask<K, V, U> extends BulkTask<K, V, Void> {
        final BiFunction<? super K, ? super V, ? extends U> transformer;
        final Consumer<? super U> action;

        ForEachTransformedMappingTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                      BiFunction<? super K, ? super V, ? extends U> transformer, Consumer<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        public final void compute() {
            final BiFunction<? super K, ? super V, ? extends U> transformer;
            final Consumer<? super U> action;
            if ((transformer = this.transformer) != null && (action = this.action) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    new ForEachTransformedMappingTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, transformer,
                            action).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key, p.val)) != null)
                        action.accept(u);
                }
                propagateCompletion();
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchKeysTask<K, V, U> extends BulkTask<K, V, U> {
        final Function<? super K, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchKeysTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                       Function<? super K, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final Function<? super K, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new SearchKeysTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
                            .fork();
                }
                while (result.get() == null) {
                    U u;
                    Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p.key)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchValuesTask<K, V, U> extends BulkTask<K, V, U> {
        final Function<? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchValuesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                         Function<? super V, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final Function<? super V, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new SearchValuesTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
                            .fork();
                }
                while (result.get() == null) {
                    U u;
                    Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p.val)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchEntriesTask<K, V, U> extends BulkTask<K, V, U> {
        final Function<Entry<K, V>, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchEntriesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                          Function<Entry<K, V>, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final Function<Entry<K, V>, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new SearchEntriesTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
                            .fork();
                }
                while (result.get() == null) {
                    U u;
                    Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        return;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class SearchMappingsTask<K, V, U> extends BulkTask<K, V, U> {
        final BiFunction<? super K, ? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchMappingsTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                           BiFunction<? super K, ? super V, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        public final U getRawResult() {
            return result.get();
        }

        public final void compute() {
            final BiFunction<? super K, ? super V, ? extends U> searchFunction;
            final AtomicReference<U> result;
            if ((searchFunction = this.searchFunction) != null && (result = this.result) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    if (result.get() != null)
                        return;
                    addToPendingCount(1);
                    new SearchMappingsTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, searchFunction, result)
                            .fork();
                }
                while (result.get() == null) {
                    U u;
                    Node<K, V> p;
                    if ((p = advance()) == null) {
                        propagateCompletion();
                        break;
                    }
                    if ((u = searchFunction.apply(p.key, p.val)) != null) {
                        if (result.compareAndSet(null, u))
                            quietlyCompleteRoot();
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ReduceKeysTask<K, V> extends BulkTask<K, V, K> {
        final BiFunction<? super K, ? super K, ? extends K> reducer;
        K result;
        ReduceKeysTask<K, V> rights, nextRight;

        ReduceKeysTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceKeysTask<K, V> nextRight,
                       BiFunction<? super K, ? super K, ? extends K> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        public final K getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<? super K, ? super K, ? extends K> reducer;
            if ((reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new ReduceKeysTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights, reducer))
                            .fork();
                }
                K r = null;
                for (Node<K, V> p; (p = advance()) != null; ) {
                    K u = p.key;
                    r = (r == null) ? u : u == null ? r : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    ReduceKeysTask<K, V> t = (ReduceKeysTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        K tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ReduceValuesTask<K, V> extends BulkTask<K, V, V> {
        final BiFunction<? super V, ? super V, ? extends V> reducer;
        V result;
        ReduceValuesTask<K, V> rights, nextRight;

        ReduceValuesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceValuesTask<K, V> nextRight,
                         BiFunction<? super V, ? super V, ? extends V> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        public final V getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<? super V, ? super V, ? extends V> reducer;
            if ((reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new ReduceValuesTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights, reducer))
                            .fork();
                }
                V r = null;
                for (Node<K, V> p; (p = advance()) != null; ) {
                    V v = p.val;
                    r = (r == null) ? v : reducer.apply(r, v);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    ReduceValuesTask<K, V> t = (ReduceValuesTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        V tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class ReduceEntriesTask<K, V> extends BulkTask<K, V, Map.Entry<K, V>> {
        final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
        Map.Entry<K, V> result;
        ReduceEntriesTask<K, V> rights, nextRight;

        ReduceEntriesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceEntriesTask<K, V> nextRight,
                          BiFunction<Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        public final Map.Entry<K, V> getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
            if ((reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new ReduceEntriesTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights, reducer))
                            .fork();
                }
                Map.Entry<K, V> r = null;
                for (Node<K, V> p; (p = advance()) != null; )
                    r = (r == null) ? p : reducer.apply(r, p);
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    ReduceEntriesTask<K, V> t = (ReduceEntriesTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        Map.Entry<K, V> tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysTask<K, V, U> extends BulkTask<K, V, U> {
        final Function<? super K, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceKeysTask<K, V, U> rights, nextRight;

        MapReduceKeysTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                          MapReduceKeysTask<K, V, U> nextRight, Function<? super K, ? extends U> transformer,
                          BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final Function<? super K, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceKeysTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, reducer)).fork();
                }
                U r = null;
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceKeysTask<K, V, U> t = (MapReduceKeysTask<K, V, U>) c, s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesTask<K, V, U> extends BulkTask<K, V, U> {
        final Function<? super V, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceValuesTask<K, V, U> rights, nextRight;

        MapReduceValuesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                            MapReduceValuesTask<K, V, U> nextRight, Function<? super V, ? extends U> transformer,
                            BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final Function<? super V, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceValuesTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, reducer)).fork();
                }
                U r = null;
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.val)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceValuesTask<K, V, U> t = (MapReduceValuesTask<K, V, U>) c, s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesTask<K, V, U> extends BulkTask<K, V, U> {
        final Function<Map.Entry<K, V>, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceEntriesTask<K, V, U> rights, nextRight;

        MapReduceEntriesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                             MapReduceEntriesTask<K, V, U> nextRight, Function<Map.Entry<K, V>, ? extends U> transformer,
                             BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final Function<Map.Entry<K, V>, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceEntriesTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, reducer)).fork();
                }
                U r = null;
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceEntriesTask<K, V, U> t = (MapReduceEntriesTask<K, V, U>) c, s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsTask<K, V, U> extends BulkTask<K, V, U> {
        final BiFunction<? super K, ? super V, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceMappingsTask<K, V, U> rights, nextRight;

        MapReduceMappingsTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                              MapReduceMappingsTask<K, V, U> nextRight, BiFunction<? super K, ? super V, ? extends U> transformer,
                              BiFunction<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        public final U getRawResult() {
            return result;
        }

        public final void compute() {
            final BiFunction<? super K, ? super V, ? extends U> transformer;
            final BiFunction<? super U, ? super U, ? extends U> reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceMappingsTask<K, V, U>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, reducer)).fork();
                }
                U r = null;
                for (Node<K, V> p; (p = advance()) != null; ) {
                    U u;
                    if ((u = transformer.apply(p.key, p.val)) != null)
                        r = (r == null) ? u : reducer.apply(r, u);
                }
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceMappingsTask<K, V, U> t = (MapReduceMappingsTask<K, V, U>) c, s = t.rights;
                    while (s != null) {
                        U tr, sr;
                        if ((sr = s.result) != null)
                            t.result = (((tr = t.result) == null) ? sr : reducer.apply(tr, sr));
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysToDoubleTask<K, V> extends BulkTask<K, V, Double> {
        final ToDoubleFunction<? super K> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceKeysToDoubleTask<K, V> rights, nextRight;

        MapReduceKeysToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                  MapReduceKeysToDoubleTask<K, V> nextRight, ToDoubleFunction<? super K> transformer, double basis,
                                  DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleFunction<? super K> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceKeysToDoubleTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceKeysToDoubleTask<K, V> t = (MapReduceKeysToDoubleTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesToDoubleTask<K, V> extends BulkTask<K, V, Double> {
        final ToDoubleFunction<? super V> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceValuesToDoubleTask<K, V> rights, nextRight;

        MapReduceValuesToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                    MapReduceValuesToDoubleTask<K, V> nextRight, ToDoubleFunction<? super V> transformer, double basis,
                                    DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleFunction<? super V> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceValuesToDoubleTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceValuesToDoubleTask<K, V> t = (MapReduceValuesToDoubleTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesToDoubleTask<K, V> extends BulkTask<K, V, Double> {
        final ToDoubleFunction<Map.Entry<K, V>> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceEntriesToDoubleTask<K, V> rights, nextRight;

        MapReduceEntriesToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                     MapReduceEntriesToDoubleTask<K, V> nextRight, ToDoubleFunction<Map.Entry<K, V>> transformer,
                                     double basis, DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleFunction<Map.Entry<K, V>> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceEntriesToDoubleTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceEntriesToDoubleTask<K, V> t = (MapReduceEntriesToDoubleTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsToDoubleTask<K, V> extends BulkTask<K, V, Double> {
        final ToDoubleBiFunction<? super K, ? super V> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceMappingsToDoubleTask<K, V> rights, nextRight;

        MapReduceMappingsToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                      MapReduceMappingsToDoubleTask<K, V> nextRight, ToDoubleBiFunction<? super K, ? super V> transformer,
                                      double basis, DoubleBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Double getRawResult() {
            return result;
        }

        public final void compute() {
            final ToDoubleBiFunction<? super K, ? super V> transformer;
            final DoubleBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                double r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceMappingsToDoubleTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsDouble(r, transformer.applyAsDouble(p.key, p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceMappingsToDoubleTask<K, V> t = (MapReduceMappingsToDoubleTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsDouble(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysToLongTask<K, V> extends BulkTask<K, V, Long> {
        final ToLongFunction<? super K> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceKeysToLongTask<K, V> rights, nextRight;

        MapReduceKeysToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                MapReduceKeysToLongTask<K, V> nextRight, ToLongFunction<? super K> transformer, long basis,
                                LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongFunction<? super K> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceKeysToLongTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.key));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceKeysToLongTask<K, V> t = (MapReduceKeysToLongTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesToLongTask<K, V> extends BulkTask<K, V, Long> {
        final ToLongFunction<? super V> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceValuesToLongTask<K, V> rights, nextRight;

        MapReduceValuesToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                  MapReduceValuesToLongTask<K, V> nextRight, ToLongFunction<? super V> transformer, long basis,
                                  LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongFunction<? super V> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceValuesToLongTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceValuesToLongTask<K, V> t = (MapReduceValuesToLongTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesToLongTask<K, V> extends BulkTask<K, V, Long> {
        final ToLongFunction<Map.Entry<K, V>> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceEntriesToLongTask<K, V> rights, nextRight;

        MapReduceEntriesToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                   MapReduceEntriesToLongTask<K, V> nextRight, ToLongFunction<Map.Entry<K, V>> transformer, long basis,
                                   LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongFunction<Map.Entry<K, V>> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceEntriesToLongTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceEntriesToLongTask<K, V> t = (MapReduceEntriesToLongTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsToLongTask<K, V> extends BulkTask<K, V, Long> {
        final ToLongBiFunction<? super K, ? super V> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceMappingsToLongTask<K, V> rights, nextRight;

        MapReduceMappingsToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                    MapReduceMappingsToLongTask<K, V> nextRight, ToLongBiFunction<? super K, ? super V> transformer,
                                    long basis, LongBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Long getRawResult() {
            return result;
        }

        public final void compute() {
            final ToLongBiFunction<? super K, ? super V> transformer;
            final LongBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                long r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceMappingsToLongTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsLong(r, transformer.applyAsLong(p.key, p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceMappingsToLongTask<K, V> t = (MapReduceMappingsToLongTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsLong(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceKeysToIntTask<K, V> extends BulkTask<K, V, Integer> {
        final ToIntFunction<? super K> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceKeysToIntTask<K, V> rights, nextRight;

        MapReduceKeysToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                               MapReduceKeysToIntTask<K, V> nextRight, ToIntFunction<? super K> transformer, int basis,
                               IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntFunction<? super K> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceKeysToIntTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.key));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceKeysToIntTask<K, V> t = (MapReduceKeysToIntTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceValuesToIntTask<K, V> extends BulkTask<K, V, Integer> {
        final ToIntFunction<? super V> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceValuesToIntTask<K, V> rights, nextRight;

        MapReduceValuesToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                 MapReduceValuesToIntTask<K, V> nextRight, ToIntFunction<? super V> transformer, int basis,
                                 IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntFunction<? super V> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceValuesToIntTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceValuesToIntTask<K, V> t = (MapReduceValuesToIntTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceEntriesToIntTask<K, V> extends BulkTask<K, V, Integer> {
        final ToIntFunction<Map.Entry<K, V>> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceEntriesToIntTask<K, V> rights, nextRight;

        MapReduceEntriesToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                  MapReduceEntriesToIntTask<K, V> nextRight, ToIntFunction<Map.Entry<K, V>> transformer, int basis,
                                  IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntFunction<Map.Entry<K, V>> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceEntriesToIntTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceEntriesToIntTask<K, V> t = (MapReduceEntriesToIntTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    static final class MapReduceMappingsToIntTask<K, V> extends BulkTask<K, V, Integer> {
        final ToIntBiFunction<? super K, ? super V> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceMappingsToIntTask<K, V> rights, nextRight;

        MapReduceMappingsToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t,
                                   MapReduceMappingsToIntTask<K, V> nextRight, ToIntBiFunction<? super K, ? super V> transformer,
                                   int basis, IntBinaryOperator reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        public final Integer getRawResult() {
            return result;
        }

        public final void compute() {
            final ToIntBiFunction<? super K, ? super V> transformer;
            final IntBinaryOperator reducer;
            if ((transformer = this.transformer) != null && (reducer = this.reducer) != null) {
                int r = this.basis;
                for (int i = baseIndex, f, h; batch > 0 && (h = ((f = baseLimit) + i) >>> 1) > i; ) {
                    addToPendingCount(1);
                    (rights = new MapReduceMappingsToIntTask<K, V>(this, batch >>>= 1, baseLimit = h, f, tab, rights,
                            transformer, r, reducer)).fork();
                }
                for (Node<K, V> p; (p = advance()) != null; )
                    r = reducer.applyAsInt(r, transformer.applyAsInt(p.key, p.val));
                result = r;
                CountedCompleter<?> c;
                for (c = firstComplete(); c != null; c = c.nextComplete()) {
                    @SuppressWarnings("unchecked")
                    MapReduceMappingsToIntTask<K, V> t = (MapReduceMappingsToIntTask<K, V>) c, s = t.rights;
                    while (s != null) {
                        t.result = reducer.applyAsInt(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;

    static {
        try {
            // U = sun.misc.Unsafe.getUnsafe();
            U = getUnsafe();
            Class<?> k = ConcurrentHashMapDebug.class;
            SIZECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
            Class<?> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset(ck.getDeclaredField("value"));
            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    static Unsafe getUnsafe() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        return unsafe;
    }
}
