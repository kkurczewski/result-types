package pl.kurczews.result;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Result<E extends Exception, T> {

    private final E exception;
    private final T value;

    private Result(E exception, T value) {
        this.exception = exception;
        this.value = value;
    }

    public static <E extends Exception, T> Result<E, T> success(T value) {
        return new Result<>(null, value);
    }

    public static <E extends Exception, T> Result<E, T> failure(E exception) {
        return new Result<>(exception, null);
    }

    public static <E extends Exception, T> Result<E, T> empty() {
        return new Result<>(null, null);
    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception, T> Result<E, T> of(CheckedSupplier<E, T> supplier) {
        try {
            return Result.success(supplier.get());
        } catch (Exception ex) {
            return Result.failure((E) ex);
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

        if (value != null) {
            return value;
        } else {
            throw new NoSuchElementException();
        }
    }

    public Optional<T> unwrapOpt() throws E {
        if (exception != null) {
            throw exception;
        }

        return (value != null) ? Optional.of(value) : Optional.empty();
    }
}