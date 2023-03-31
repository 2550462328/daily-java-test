package cn.zhanghui.demo.daily.base.algorithm.filter;

import com.google.common.hash.Funnels;

import java.util.BitSet;

/**
 * @author ZhangHui
 * @version 1.0
 * @className BloomFilter
 * @description 这是布隆过滤器的测试
 * 布隆过滤器可以装载2^n的数据，n是布隆过滤器位数组的长度
 * 布隆过滤器说有的 不一定有（数据越大，概率越高）；布隆过滤器说没有的肯定没有
 * 当然有现成的轮子，基于google的guava中也是有直接可以使用的布隆过滤器，在测试的时候会用到，遗憾的是只能用于单线程（没做线程安全）
 * @date 2020/7/8
 */
public class BloomFilter<T> {

    private static int DEFAULT_SIZE = 2 << 24;

    private BitSet bitSet;

    private int seeds[] = {10, 20, 4, 32, 13, 42, 12};

    private SimpleHash[] hashes = new SimpleHash[seeds.length];

    public BloomFilter() {
        this(DEFAULT_SIZE);
    }

    public BloomFilter(int capablity) {
        bitSet = new BitSet(capablity);
        for (int i = 0; i < hashes.length; i++) {
            hashes[i] = new SimpleHash(capablity, seeds[i]);
        }
    }

    public void addElement(T value) {
        for (SimpleHash simpleHash : hashes) {
            bitSet.set(simpleHash.hash(value), true);
        }
    }

    public boolean contain(T value) {
        for (SimpleHash simpleHash : hashes) {
            if (!bitSet.get(simpleHash.hash(value))) {
                return false;
            }
        }
        return true;
    }

    class SimpleHash<T> {

        private int cap;

        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        public int hash(T value) {
            int h;
            return value == null ? 0 : Math.abs(seed * (cap - 1) & ((h = value.hashCode()) ^ h >>> 16));
        }
    }

    public static void main(String[] args) {

//      基于自定义布隆过滤器实现测试
        BloomFilter<Integer> bloomFilter = new BloomFilter<>(2);

        bloomFilter.addElement(2);
        bloomFilter.addElement(3);

        System.out.println(bloomFilter.contain(2));
        System.out.println(bloomFilter.contain(3));
        System.out.println(bloomFilter.contain(4));
        System.out.println(bloomFilter.contain(5));

        BloomFilter<String> stringBloomFilter = new BloomFilter<>();

        stringBloomFilter.addElement("www.baidu.com");
        stringBloomFilter.addElement("www.google.com");

        System.out.println(stringBloomFilter.contain("www.baidu.com"));
        System.out.println(stringBloomFilter.contain("www.xinlan.com"));

        // 基于guava的布隆过滤器的测试
        // 最大容量1500，最大容忍出错率0.01
        com.google.common.hash.BloomFilter<Integer> googleBloomFilter = com.google.common.hash.BloomFilter.create(Funnels.integerFunnel(), 1500, 0.01);
        googleBloomFilter.put(1);
        googleBloomFilter.put(2);

        System.out.println(googleBloomFilter.mightContain(1));
        System.out.println(googleBloomFilter.mightContain(10));
    }
}
