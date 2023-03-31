package cn.zhanghui.demo.daily.jdk8_newProp.lambda_test;

import java.util.function.Consumer;

@FunctionalInterface
public interface ConsumerCheck<T, E extends Throwable> {
    void accept(T t) throws E;

    static <T, E extends Throwable> Consumer<T> uncheck(ConsumerCheck<T, E> c) {
        return t -> {
            try {
                c.accept(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
