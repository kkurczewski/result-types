package pl.kurczews.result;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Mapper<E extends Exception, T> {
    <U> Result<E, U> map(Function<T, U> mapper);

    <U> Result<E, U> flatMap(Function<T, Result<E, U>> mapper);

    Result<E, T> peek(Consumer<T> consumer);
}
