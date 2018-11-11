package pl.kurczews.result;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<E extends Exception, T> {

    private final E exception;
    private final T value;

    private Result(E exception) {
        this.exception = Objects.requireNonNull(exception);
        this.value = null;
    }

    private Result(T value) {
        this.value = Objects.requireNonNull(value);
        this.exception = null;
    }

    public static <E extends Exception, T> Result<E, T> success(T value) {
        return new Result<>(value);
    }

    public static <E extends Exception, T> Result<E, T> failure(E exception) {
        return new Result<>(exception);
    }

    public static <E extends Exception, T> Result<E, T> of(CheckedSupplier<E, T> supplier) {
        try {
            return Result.success(supplier.get());
        } catch (Exception ex) {
            return Result.failure(castException(ex));
        }
    }

    public <U> Result<E, U> map(Function<T, U> mapper) {
        return (value != null) ? Result.success(mapper.apply(value)) : Result.failure(exception);
    }

    public <U> Result<E, U> flatMap(Function<T, Result<E, U>> mapper) {
        return (value != null) ? mapper.apply(value) : Result.failure(exception);
    }

    public Result<E, T> peek(Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public T unwrap() throws E {
        if (exception != null) {
            throw exception;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> E castException(Exception ex) {
        if (ex instanceof RuntimeException) throw (RuntimeException) ex;
        return (E) ex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?, ?> result = (Result<?, ?>) o;
        return Objects.equals(exception, result.exception) &&
                Objects.equals(value, result.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exception, value);
    }
}