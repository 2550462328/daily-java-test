package cn.zhanghui.demo.daily.base.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @ClassName: SpliteratorTest2.java
 * @Description: 该类的功能描述
 * @author: ZhangHui
 * @date: 2019年7月18日 下午3:11:34
 */
public class SpliteratorTest2 {
    public static void main(String[] args) {
        String str = "asda1e23ew41 1 sd65$r@4#5";
        // 非并行方式处理
        Stream<Character> stream = IntStream.range(0, str.length()).mapToObj(str::charAt);
        System.out.println(getCount(stream));
        // 并行方式处理
        Spliterator<Character> spliterator = new SpliteratorTest2().new NumSpliterator(str.toCharArray(), 0,
                str.length(), true);
        Stream<Character> paralleStream = StreamSupport.stream(spliterator, true);
//		paralleStream.forEach(System.out::print);  // 这里会输出
//		paralleStream.forEachOrdered(System.out::print); //这里会报错
//		paralleStream.sorted().forEachOrdered(System.out::print); // 这里会报NullPointerException
        System.out.println(getCount(paralleStream));
    }

    public static int getCount(Stream<Character> stream) {
        NumCounter numCounter = stream.reduce(new SpliteratorTest2().new NumCounter(0, 0, false), NumCounter::accumator,
                NumCounter::combine);
        return numCounter.getSum();
    }

    class NumCounter {
        // 假设当前遍历到了第i个数字 num就是第i-1的数字
        private int num;
        // 假设可汇总的数字个数为n sum就是前n-1个值得综合
        private int sum;
        // 前一个数是否为完整数字 如果前一个是字符 则是true 如果前一个是数字 则是false
        private boolean isWholeNum;

        public NumCounter(int num, int sum, boolean isWholeNum) {
            super();
            this.num = num;
            this.sum = sum;
            this.isWholeNum = isWholeNum;
        }

        // 对传入的字符进行计算
        public NumCounter accumator(char ch) {
            if (Character.isDigit(ch)) {
                return isWholeNum ? new NumCounter(Integer.valueOf("" + ch), sum, false)
                        : new NumCounter(Integer.valueOf("" + num + ch), sum, false);
            } else {
                return new NumCounter(0, sum + num, true);
            }
        }

        // 对两个NumCounter进行结合
        public NumCounter combine(NumCounter numCounter) {
            if (numCounter != null) {
                return new NumCounter(0, this.getSum() + numCounter.getSum(), numCounter.isWholeNum);
            } else {
                return new NumCounter(0, this.getSum(), this.isWholeNum);
            }
        }

        // 获取当前总数
        public int getSum() {
            return sum + num;
        }
    }

    // 自定义Spliterator 对字符串进行分割
    class NumSpliterator implements Spliterator<Character> {
        private char[] str;
        private int currentChar = 0;
        private int end = Integer.MAX_VALUE;
        private boolean canSplit = false;

        public NumSpliterator(char[] str, int currentChar, int end, boolean canSplit) {
            super();
            this.str = str;
            this.currentChar = currentChar;
            this.end = end;
            this.canSplit = canSplit;
        }

        // 不断的遍历元素 直到返回false 即没有可遍历元素
        @Override
        public void forEachRemaining(Consumer<? super Character> action) {
            do {
            } while (tryAdvance(action));
        }

        // 遍历一个元素 如果还有元素 返回true 如果没有 返回false
        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            if (str.length == 0) {
                return false;
            }
            action.accept(str[currentChar++]);
            return currentChar < end;
        }

        // 通过对数组分割的方式
        // 将分割出来的部分交由forEachRemaining遍历
        @Override
        public Spliterator<Character> trySplit() {
            int i = currentChar;
            for (; canSplit && i <= end; ++i) {
                if (!Character.isDigit(str[i])) {
                    int tempEnd = end;
                    end = i;
                    canSplit = false;
                    if (i + 1 <= tempEnd) {
                        return new NumSpliterator(str, i + 1, tempEnd, true);
                    } else {
                        return null;
                    }
                }
            }
            canSplit = false;
            return null;
        }

        // 返回剩下可遍历的元素数量
        @Override
        public long estimateSize() {
            return end - currentChar;
        }

        // Spliterator的特性
        @Override
        public int characteristics() {
            return Spliterator.SIZED | Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.IMMUTABLE
                    | Spliterator.ORDERED | Spliterator.SUBSIZED;
        }

    }

}