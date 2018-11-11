package pl.kurczews.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class OptionalResult<E extends Exception, T> {

    private final E exception;
    private final T value;

    private OptionalResult() {
        this.exception = null;
        this.value = null;
    }

    private OptionalResult(T value) {
        this.value = Objects.requireNonNull(value);
        this.exception = null;
    }

    private OptionalResult(E exception) {
        this.exception = Objects.requireNonNull(exception);
        this.value = null;
    }

    public static <E extends Exception, T> OptionalResult<E, T> success(T value) {
        return new OptionalResult<>(value);
    }

    public static <E extends Exception, T> OptionalResult<E, T> failure(E exception) {
        return new OptionalResult<>(exception);
    }

    public static <E extends Exception, T> OptionalResult<E, T> empty() {
        return new OptionalResult<>();
    }

    public static <E extends Exception, T> OptionalResult<E, T> of(CheckedSupplier<E, Optional<T>> supplier) {
        try {
            return supplier
                    .get()
                    .map(OptionalResult::<E, T>success)
                    .orElseGet(OptionalResult::empty);
        } catch (Exception ex) {
            return OptionalResult.failure(castException(ex));
        }
    }

    public <U> OptionalResult<E, U> map(Function<T, U> mapper) {
        OptionalResult<E, U> result;

        if (value != null) result = OptionalResult.success(mapper.apply(value));
        else if (exception != null) result = OptionalResult.failure(exception);
        else result = OptionalResult.empty();

        return result;
    }

    public <U> OptionalResult<E, U> flatMap(Function<T, OptionalResult<E, U>> mapper) {
        OptionalResult<E, U> result;

        if (value != null) result = mapper.apply(value);
        else if (exception != null) result = OptionalResult.failure(exception);
        else result = OptionalResult.empty();

        return result;
    }

    public OptionalResult<E, T> peek(Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public Optional<T> unwrapOpt() throws E {
        if (exception != null) {
            throw exception;
        }
        return (value != null) ? Optional.of(value) : Optional.empty();
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
        OptionalResult<?, ?> that = (OptionalResult<?, ?>) o;
        return Objects.equals(exception, that.exception) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exception, value);
    }
}
