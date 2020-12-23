package cn.zhanghui.demo.daily.base.collection.tree;

import java.util.*;

/**
 * @author ZhangHui
 * @version 1.0
 * @className BTree
 * @description BTree的java实现
 * @date 2020/9/14
 */
public class BTree<K, V> {

    /**
     * 内部类，B树中节点中的元素。K：键类型，V：值类型，可以是指向数据的索引，也可以是实体数据
     *
     * @author ZhangHui
     * @date 2020/9/14
     */
    private class Entry<K, V> {
        private K key;
        private V value;

        public void setKey(K key) {
            this.key = key;
        }

        public K getKey() {
            return this.key;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public V getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            return "key: " + this.key + " , ";
        }
    }

    /**
     * @Author ZhangHui
     * @Date 2020/9/14 14:12
     * @Description 内部类，封装搜索结果
     */
    private class SearchResult<V> {
        private boolean isExist;
        private V value;
        private int index;

        //构造方法，将查询结果封装入对象
        public SearchResult(boolean isExist, int index, V value) {
            this.isExist = isExist;
            this.index = index;
            this.value = value;
        }

        public boolean isExist() {
            return isExist;
        }

        public V getValue() {
            return value;
        }

        public int getIndex() {
            return index;
        }
    }

    /**
     * @Author ZhangHui
     * @Date 2020/9/14 14:28
     * @Description 树的节点
     */
    public class Node<K, V> {
        //节点内的项
        private List<Entry<K, V>> entrys;
        //节点的孩子节点们
        private List<Node<K, V>> sons;
        //是否是叶子节点
        private boolean isLeaf;
        //键值比较函数对象，如果采用倒序或者其它排序方式，传入该对象
        private Comparator<K> kComparator;

        //比较两个key，如果没有传入自定义排序方式则采用默认的升序
        private int compare(K key1, K key2) {
            return this.kComparator == null ? ((Comparable<K>) key2).compareTo(key1) : kComparator.compare(key1, key2);
        }

        //普通构造函数
        Node() {
            this.entrys = new LinkedList<Entry<K, V>>();
            this.sons = new LinkedList<Node<K, V>>();
            this.isLeaf = false;
        }

        //自定义K排序方式的构造函数
        Node(Comparator<K> kComparator) {
            this();
            this.kComparator = kComparator;
        }

        public void setIsLeaf(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }

        public boolean getIsLeaf() {
            return this.isLeaf;
        }

        //返回本节点的项数
        public int nodeSize() {
            return this.entrys.size();
        }

        /**
         * @Author ZhangHui
         * @Date 2020/9/14 15:19
         * @Param key:待查找元素的key值
         * @Return 查找结果封装入 SearchResult
         * @Exception
         * @Description 在本节点内查找元素, 本质就是一个有序数组的二分查找
         */
        public SearchResult<V> search(K key) {
            int begin = 0;
            int end = this.nodeSize() - 1;
//            if (end == 0) {
//                return new SearchResult<V>(false, 0, null);
//            }
            int mid = (begin + end) / 2;
            boolean isExist = false;
            int index = 0;
            V value = null;
            //二分查找
            while (begin < end) {
                mid = (begin + end) / 2;
                Entry midEntry = this.entrys.get(mid);
                int compareRe = compare((K) midEntry.getKey(), key);
                //找到了
                if (compareRe == 0) {
                    break;
                } else {
                    if (compareRe > 0) {
                        //在中点右边
                        begin = mid + 1;
                    } else {
                        end = mid - 1;
                    }
                }
            }
            //二分查找结束，判断结果;三个元素以上才是正经二分，只有两个或一个元素属于边界条件要着重考虑
            if (begin < end) {
                //找到了
                isExist = true;
                index = mid;
                value = this.entrys.get(mid).getValue();
            } else if (begin == end) {
                K midKey = this.entrys.get(begin).getKey();
                int comRe = compare(midKey, key);
                if (comRe == 0) {
                    isExist = true;
                    index = begin;
                    value = this.entrys.get(mid).getValue();
                } else if (comRe > 0) {
                    isExist = false;
                    index = begin + 1;
                    value = null;
                } else {
                    isExist = false;
                    index = begin;
                    value = null;
                }
            } else {
                isExist = false;
                index = begin;
                value = null;
            }
            return new SearchResult<V>(isExist, index, value);
        }

        //删除给定索引位置的项
        public Entry<K, V> removeEntry(int index) {
            Entry<K, V> re = this.entrys.get(index);
            this.entrys.remove(index);
            return re;
        }

        //得到index处的项
        public Entry<K, V> entryAt(int index) {
            return this.entrys.get(index);
        }

        //将新项插入指定位置
        private void insertEntry(Entry<K, V> entry, int index) {
            this.entrys.add(index, entry);
        }

        //节点内插入项
        private boolean insertEntry(Entry<K, V> entry) {
            SearchResult<V> result = search(entry.getKey());
            if (result.isExist()) {
                return false;
            } else {
                insertEntry(entry, result.getIndex());
                return true;
            }
        }

        //更新项，如果项存在，更新其值并返回原值，否则直接插入
        public V putEntry(Entry<K, V> entry) {
            SearchResult<V> re = search(entry.getKey());
            if (re.isExist) {
                Entry oldEntry = this.entrys.get(re.getIndex());
                V oldValue = (V) oldEntry.getValue();
                oldEntry.setValue(entry.getValue());
                return oldValue;
            } else {
                insertEntry(entry);
                return null;
            }
        }

        //获得指定索引的子节点
        public Node childAt(int index) {
            return this.sons.get(index);
        }

        //删除给定索引的子节点
        public void removeChild(int index) {
            this.sons.remove(index);
        }

        //将新的子节点插入到指定位置
        public void insertChild(Node<K, V> child, int index) {
            this.sons.add(index, child);
        }
    }

    //度数T，不传入则默认为 2-3 树
    private Integer DEFAULT_T = 2;
    //根节点
    private Node<K, V> root;

    private int t = DEFAULT_T;

    //非根节点的最小项数，体现的是除了根节点，其余节点都是分裂而来的！
    private int nodeMinSize = t - 1;
    //节点的最大项数
    private int nodeMaxSize = 2 * t - 1;
    //比较函数对象
    private Comparator<K> kComparator;

    //构造一棵自然排序的B树
    BTree() {
        Node<K, V> root = new Node<K, V>();
        this.root = root;
        root.setIsLeaf(true);
    }

    //构造一棵度为 t 的B树
    BTree(int t) {
        this();
        this.t = t;
        nodeMinSize = t - 1;
        nodeMaxSize = 2 * t - 1;
    }

    //构造一棵按给定排序方式排序，且度为 t 的B树
    BTree(Comparator<K> com, int t) {
        this(t);
        this.kComparator = com;
    }

    //在以root为根的树内搜索key项
    private V search(Node<K, V> root, K key) {
        SearchResult<V> re = root.search(key);
        if (re.isExist) {
            return re.value;
        } else {
            //回归条件
            if (root.isLeaf) {
                return null;
            }
            int index = re.index;
            //递归搜索子节点
            return (V) search(root.childAt(index), key);
        }
    }

    public V search(K key) {
        return search(this.root, key);
    }

    /**
     * @Author ZhangHui
     * @Date 2020/9/14 20:49
     * @Param 分裂满子结点，fatherNode：待分裂节点的父节点，splitNode:待分裂节点，index:待分裂节点在父节点中的索引
     * @Return
     * @Exception
     * @Description 满子节点的分裂过程：从中间节点断开，后半部分形成新结点插入父节点。若分裂节点不是叶子节点，将子节点一并分裂到新节点
     */
    private void splitNode(Node<K, V> fatherNode, Node<K, V> splitNode, int index) {
        //分裂产生的新节点
        Node<K, V> newNode = new Node<K, V>(this.kComparator);
        //如果原节点为叶子节点，那么新节点也是
        newNode.setIsLeaf(splitNode.isLeaf);
        //将 t到2*t-2 项迁移到新节点
        for (int i = t; i < this.nodeMaxSize; i++) {
            newNode.entrys.add(splitNode.entrys.get(i));
        }
        //中间节点向上融合到父节点的 index+1
        Entry<K, V> midEntry = splitNode.entrys.get(t - 1);
        for (int i = this.nodeMaxSize - 1; i >= t - 1; i--) {
            //删除原节点中已迁移的项,删除时注意从尾部向前删除
            splitNode.entrys.remove(i);
        }
        //如果分裂的节点不是叶子节点，子节点一并跟随分裂
        if (!splitNode.getIsLeaf()) {
            for (int i = t; i < this.nodeMaxSize + 1; i++) {
                newNode.sons.add(splitNode.sons.get(i));
            }
            //删除时注意从尾部向前删除
            for (int i = this.nodeMaxSize; i >= t; i--) {
                splitNode.sons.remove(i);
            }
        }
        //父节点插入分裂的中间元素，分裂出的新节点加入父节点的 sons
        fatherNode.insertEntry(midEntry);
        fatherNode.insertChild(newNode, index + 1);
    }

    /**
     * @Author ZhangHui
     * @Date 2020/9/14 23:53
     * @Param root：当前节点，entry:待插入元素
     * @Return
     * @Exception
     * @Description 插入一个非满节点：一路向下寻找插入位置。
     * 在寻找的路径上，如果碰到大小为2t-1的节点，分裂并向上融合。
     * 每次插入都从叶子节点插入，通过分裂将插入动作向上反馈，直到融合到根节点，只有由根节点的分裂
     * 才能增加整棵树的高度，从而维持树的平衡。
     * 树在一开始就是平衡的（只有根），整棵树的高度增加必须由根节点的分裂引发，从而高度增加后还是平衡的
     * 因为没次检查子节点前如果子节点满了会先分裂，所以除根节点外，其余节点被其子节点向上融合均不会导致节点满
     * 仅插入一个元素的情况下，每个节点最多经历一次子节点的分裂
     */
    private boolean insertNotFull(Node<K, V> root, Entry<K, V> entry) {
        if (root.getIsLeaf()) {
            //到达叶子节点，直接插入
            return root.insertEntry(entry);
        }
        SearchResult<V> re = root.search(entry.getKey());
        if (re.isExist) {
            //已存在key，直接返回
            return false;
        }
        int index = re.getIndex();
        Node<K, V> searchChild = root.childAt(index);
        //待查询子节点已满，分裂后再判断该搜索哪个子节点
        if (searchChild.nodeSize() == 2 * t - 1) {
            splitNode(root, searchChild, index);
            if (root.compare(root.entryAt(index).getKey(), entry.getKey()) > 0) {
                searchChild = root.childAt(index + 1);
            }
        }
        return insertNotFull(searchChild, entry);
    }

    //插入一个新节点
    public boolean insertNode(Entry<K, V> entry) {
        //根节点满了，先分裂根节点
        if (root.nodeSize() == 2 * t - 1) {
            Node<K, V> newRoot = new Node<K, V>();
            newRoot.setIsLeaf(false);
            newRoot.insertChild(root, 0);
            splitNode(newRoot, root, 0);
            this.root = newRoot;
        }
        return insertNotFull(root, entry);
    }

    /**
     * @Author ZhangHui
     * @Date 2020/2/24 14:00
     * @Param
     * @Return
     * @Exception
     * @Description 如果Key已存在，更新value，否则直接插入entry
     */
    private V putNotFull(Node<K, V> root, Entry<K, V> entry) {
        assert root.nodeSize() < nodeMaxSize;
        if (root.isLeaf) {
            return root.putEntry(entry);
        }
        SearchResult<V> re = root.search(entry.getKey());
        if (re.isExist) {
            //如果存在，则更新
            root.entryAt(re.index).setValue(entry.getValue());
            return re.value;
        }
        //如果不存在，继续向下搜素，先判断子节点是否需要分裂
        Node<K, V> searchChild = root.childAt(re.index);
        if (searchChild.nodeSize() == 2 * t - 1) {
            splitNode(root, searchChild, re.index);
            if (root.compare(entry.getKey(), root.entryAt(re.index).getKey()) > 0) {
                searchChild = root.childAt(re.index + 1);
            }
        }
        return putNotFull(searchChild, entry);
    }

    // 如果树中已存在 key 则更新并返回原 value，否则插入并返回null
    public V put(Entry<K, V> entry) {
        //如果根节点已满，先分裂根节点
        if (this.root.nodeSize() == nodeMaxSize) {
            Node<K, V> newRoot = new Node<K, V>(kComparator);
            newRoot.setIsLeaf(false);
            newRoot.insertChild(root, 0);
            splitNode(newRoot, root, 0);
            this.root = newRoot;
        }
        return putNotFull(root, entry);
    }

    private Entry<K, V> delete(Node<K, V> root, Entry<K, V> entry) {
        SearchResult<V> re = root.search(entry.getKey());
        if (re.isExist()) {
            //回归条件，如果是叶子节点中的元素，直接删除
            if (root.getIsLeaf()) {
                return root.removeEntry(re.getIndex());
            }
            //如果不是叶子节点，判断应将待删除节点交换到左子节点还是右子节点
            Node<K, V> leftChild = root.childAt(re.getIndex());
            //如果左子节点包含多于 t-1 个项，转移到左子节点删除
            if (leftChild.nodeSize() >= t) {
                //删除过程为，将待删除项与其左子节点最后一项互换，并递归互换下去，直到将待删除节点换到叶子节点后删除
                root.removeEntry(re.getIndex());
                root.insertEntry(leftChild.entryAt(leftChild.nodeSize() - 1), re.getIndex());
                leftChild.removeEntry(leftChild.nodeSize() - 1);
                leftChild.insertEntry(entry);
                return delete(leftChild, entry);
            }
            //左子节点不可删除项，则同样逻辑检查右子节点
            Node<K, V> rightChild = root.childAt(re.getIndex() + 1);
            if (rightChild.nodeSize() >= t) {
                root.removeEntry(re.getIndex());
                root.insertEntry(rightChild.entryAt(0), re.getIndex());
                rightChild.removeEntry(0);
                rightChild.insertEntry(entry);
                return delete(rightChild, entry);
            }
            //如果左右子节点均不能删除项，将左右子节点合并，并将删除项放到新节点的合并连接处
            Entry<K, V> deletedEntry = root.removeEntry(re.getIndex());
            leftChild.insertEntry(deletedEntry);
            root.removeChild(re.getIndex() + 1);
            //左右子节点合并
            for (int i = 0; i < rightChild.nodeSize(); i++) {
                leftChild.insertEntry(rightChild.entryAt(i));
            }
            //右子节点存在子节点，则子节点也合并入左子节点子节点集合
            if (!rightChild.getIsLeaf()) {
                for (int i = 0; i < rightChild.sons.size(); i++) {
                    leftChild.insertChild(rightChild.childAt(i), leftChild.sons.size());
                }
            }
            //合并后继续向左递归
            return delete(leftChild, entry);
        } else {//删除节点不在本节点
            //回归条件，搜索到叶节点依然没找到，待删除节点不在树中
            if (root.getIsLeaf()) {
                for (int i = 0; i < root.nodeSize(); i++) {
                    System.out.print("++++++++++++++++++++");
                    System.out.print(root.entryAt(i).getKey() + "，");
                    System.out.print("++++++++++++++++++++");
                }
                throw new RuntimeException(entry.key + " is not in this tree!");
            }
            Node<K, V> searchChild = root.childAt(re.index);
            //子节点可删除项，递归删除
            if (searchChild.nodeSize() >= t) {
                return delete(searchChild, entry);
            }
            //待旋转节点，子节点项数小于等于 t-1 ，不能删除项，准备左旋或右旋为其补充项数
            Node<K, V> siblingNode = null;
            int siblingIndex = -1;
            //存在右兄弟
            if (re.getIndex() < root.nodeSize() - 1) {
                Node<K, V> rightBrother = root.childAt(re.getIndex() + 1);
                if (rightBrother.nodeSize() >= t) {
                    siblingNode = rightBrother;
                    siblingIndex = re.getIndex() + 1;
                }
            }
            //不存在右兄弟则尝试左兄嘚
            if (siblingNode == null) {
                if (re.getIndex() > 0) {
                    //尝试左兄弟节点
                    Node<K, V> leftBrothr = root.childAt(re.getIndex() - 1);
                    if (leftBrothr.nodeSize() >= t) {
                        siblingNode = leftBrothr;
                        siblingIndex = re.getIndex() - 1;
                    }
                }
            }
            //至少有一个兄弟可以匀出项来
            if (siblingNode != null) {
                //是左兄嘚
                if (siblingIndex < re.getIndex()) {
                    //左节点最后一项右旋
                    searchChild.insertEntry(root.entryAt(siblingIndex), 0);
                    root.removeEntry(siblingIndex);
                    root.insertEntry(siblingNode.entryAt(siblingNode.nodeSize() - 1), siblingIndex);
                    siblingNode.removeEntry(siblingNode.nodeSize() - 1);
                    //子节点跟着右旋
                    if (!siblingNode.getIsLeaf()) {
                        searchChild.insertChild(siblingNode.childAt(siblingNode.sons.size() - 1), 0);
                        siblingNode.removeChild(siblingNode.sons.size() - 1);
                    }
                } else {
                    //是右兄嘚
                    searchChild.insertEntry(root.entryAt(re.getIndex()), searchChild.nodeSize() - 1);
                    root.removeEntry(re.getIndex());
                    root.insertEntry(siblingNode.entryAt(0), re.getIndex());
                    siblingNode.removeEntry(0);
                    if (!siblingNode.getIsLeaf()) {
                        searchChild.insertChild(siblingNode.childAt(0), searchChild.sons.size());
                        siblingNode.removeChild(0);
                    }
                }
                return delete(searchChild, entry);
            }
            //左右兄嘚都匀不出项来，直接由左右兄嘚节点与父项合并为一个节点
            if (re.getIndex() <= root.nodeSize() - 1) {
                Node<K, V> rightSon = root.childAt(re.getIndex() + 1);
                searchChild.insertEntry(root.entryAt(re.getIndex()), searchChild.nodeSize());
                root.removeEntry(re.getIndex());
                root.removeChild(re.getIndex() + 1);
                for (int i = 0; i < rightSon.nodeSize(); i++) {
                    searchChild.insertEntry(rightSon.entryAt(i));
                }
                if (!rightSon.getIsLeaf()) {
                    for (int j = 0; j < rightSon.sons.size(); j++) {
                        searchChild.insertChild(rightSon.childAt(j), searchChild.sons.size());
                    }
                }
                if (root == this.root) {
                    this.root = searchChild;
                }
            } else {
                //没有右兄弟，试试左兄嘚
                Node<K, V> leftSon = root.childAt(re.getIndex() - 1);
                searchChild.insertEntry(root.entryAt(re.getIndex() - 1), 0);
                root.removeChild(re.getIndex() - 1);
                root.removeEntry(re.getIndex() - 1);
                for (int i = 0; i < leftSon.nodeSize(); i++) {
                    searchChild.insertEntry(leftSon.entryAt(i));
                }
                if (!leftSon.getIsLeaf()) {
                    for (int i = leftSon.sons.size() - 1; i >= 0; i--) {
                        searchChild.insertChild(leftSon.childAt(i), 0);
                    }
                }
                if (root == this.root) {
                    this.root = searchChild;
                }
            }
//            if (root == this.root && root.nodeSize() == 0) {
//                root = searchChild;
//            }
            return delete(searchChild, entry);
        }
    }

    public Entry<K, V> delete(K key) {
        Entry<K, V> en = new Entry<K, V>();
        en.setKey(key);
        return delete(root, en);
    }

    /**
     * @Author ZhangHui
     * @Date 2020/2/25 14:18
     * @Description 借助队列打印B树
     */
    public void output() {
        Queue<Node<K, V>> queue = new LinkedList<Node<K, V>>();
        queue.offer(this.root);
        while (!queue.isEmpty()) {
            Node<K, V> node = queue.poll();
            for (int i = 0; i < node.nodeSize(); ++i) {
                System.out.print(node.entryAt(i) + " ");
            }
            System.out.println();
            if (!node.getIsLeaf()) {
                for (int i = 0; i <= node.sons.size() - 1; ++i) {
                    queue.offer(node.childAt(i));
                }
            }
        }
    }

    public static void main(String[] args) {
        Random random = new Random();
        BTree<Integer, Integer> btree = new BTree<Integer, Integer>(3);
        List<Integer> save = new ArrayList<Integer>(30);
//        save.add(8290);
//        save.add(7887);
//        save.add(9460);
//        save.add(9928);
//        save.add(6127);
//        save.add(5891);
//        save.add(1592);
//        save.add(14);
//        save.add(8681);
//        save.add(4843);
//        save.add(1051);

        for (int i = 0; i < 20; ++i) {
            int r = random.nextInt(10000);
            save.add(r);
            System.out.print(r + "  ");
            BTree.Entry en = btree.new Entry<Integer, Integer>();
            en.setKey(r);
            en.setValue(r);
//            BTree.Entry en = btree.new Entry<Integer, Integer>();
//            en.setKey(save.get(i));
            btree.insertNode(en);
        }

        System.out.println("----------------------");
        btree.output();
        System.out.println("----------------------");
        btree.delete(save.get(0));
        btree.output();
    }

}
