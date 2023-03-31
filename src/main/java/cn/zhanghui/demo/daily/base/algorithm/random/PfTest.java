package cn.zhanghui.demo.daily.base.algorithm.random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ZhangHui
 * @version 1.0
 * @className PfTest
 * @description HyperLogLog算法的测试实验
 * 使用概率学解决基数统计问题
 * @date 2020/8/19
 */
public class PfTest {

    static class BitKeeper {
        private int maxBit;

        public void random() {
            long value = ThreadLocalRandom.current().nextLong(2L << 32);
            int bit = lowZeros(value);

            if (bit > maxBit) {
                this.maxBit = bit;
            }
        }

        private int lowZeros(long value) {
            int i = 0;
            for (; i < 32; i++) {
                if (value >> i << i != value) {
                    break;
                }
            }
            return i - 1;
        }
    }

    static class Experiment {
        private int n;
        private int k;
        private BitKeeper[] keepers;

        public Experiment(int n) {
            this(n, 1024);
        }

        public Experiment(int n, int k) {
            this.n = n;
            this.k = k;
            this.keepers = new BitKeeper[k];
            for (int i = 0; i < k; i++) {
                keepers[i] = new BitKeeper();
            }
        }

        public void work() {
            for (int i = 0; i < n; i++) {
                long m = ThreadLocalRandom.current().nextLong(1L << 32);
                int ran_index = (int) (((m & 0xfff0000) >> 16) % keepers.length);
                BitKeeper keeper = keepers[ran_index];
                keeper.random();
            }
        }

        public double estimate() {
            double submitsInverse = 0.0;
            for (BitKeeper keeper : keepers) {
                submitsInverse += 1.0 / (float) keeper.maxBit;
            }
            double avgBits = (float) keepers.length / submitsInverse;
            return Math.pow(2, avgBits) * this.k;
        }

//        public void debug() {
//            System.out.printf("%d %.2f %d\n", this.n, Math.log(this.n) / Math.log(2), this.keeper.maxBit);
//        }
    }

    public static void main(String[] args) {
        for (int i = 1000; i < 10000; i += 100) {
            Experiment exp = new Experiment(i);
            exp.work();
            double est = exp.estimate();
            System.out.printf("%d %.2f %.2f\n", i, est, Math.abs(est - i) / i);
        }
    }
}
