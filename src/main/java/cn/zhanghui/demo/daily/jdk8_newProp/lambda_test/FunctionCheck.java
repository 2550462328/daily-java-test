package cn.zhanghui.demo.daily.jdk8_newProp.lambda_test;

import java.util.function.Function;

@FunctionalInterface
public interface FunctionCheck<T, R, E extends Throwable> {
    R apply(T t) throws E;

    static <T, R, E extends Throwable> Function<T, R>  unchecked(FunctionCheck<T, R, E> f){
        return t -> {
            try {
                return f.apply(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

}
