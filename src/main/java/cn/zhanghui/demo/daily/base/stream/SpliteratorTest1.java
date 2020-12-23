package cn.zhanghui.demo.daily.base.stream;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @ClassName: SpliteratorTest1.java
 * @Description: 这是一个对于Spliterator接口的测试
 * 试用String的subString对arr进行分割
 * @author: ZhangHui
 * @date: 2019年7月18日 上午9:17:18
 */
public class SpliteratorTest1 {

    public static void main(String[] args) {
        String str = "asda1e23ew41 1 sd65$r@4#5";
        // 非并行的方式执行
        Stream<Character> stream = IntStream.range(0, str.length()).mapToObj(str::charAt);
        System.out.println(getCount(stream));

        // 并行的方式执行
        Spliterator<Character> spliterator = new NumSpliterator(str, 0, true);
        Stream<Character> parallelStream = StreamSupport.stream(spliterator, true);
        System.out.println(getCount(parallelStream));
    }

    public static int getCount(Stream<Character> stream) {
        NumCounter numCounter = stream.reduce(new NumCounter(0, 0, false), NumCounter::accumator, NumCounter::combine);
        return numCounter.getSum();
    }
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
    private String str;
    private int currentChar = 0;
    private boolean canSplit = false;

    public NumSpliterator(String str, int currentChar, boolean canSplit) {
        this.str = str;
        this.currentChar = currentChar;
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
        if ("".equals(str)) {
            return false;
        }
        action.accept(str.charAt(currentChar++));
        return currentChar < str.length();
    }

    // 利用String的subString进行分离 返回可继续分割的剩下部分
    // 将分割出来的部分交由forEachRemaining遍历
    @Override
    public Spliterator<Character> trySplit() {
        int i = currentChar;
        for (; canSplit && i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                String str1 = str;
                this.str = str1.substring(currentChar, i);
                canSplit = false;
                if (i + 1 < str1.length()) {
                    return new NumSpliterator(str1.substring(i + 1, str1.length()), 0, true);
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
        return str.length() - currentChar;
    }

    // Spliterator的特性
    @Override
    public int characteristics() {
        return Spliterator.SIZED | Spliterator.CONCURRENT | Spliterator.IMMUTABLE
                | Spliterator.ORDERED | Spliterator.SUBSIZED;
    }

}