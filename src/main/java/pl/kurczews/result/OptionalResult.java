package pl.kurczews.result;

import java.util.Optional;

public interface OptionalResult<E extends Exception, T> extends Mapper<E, T> {
    Optional<T> unwrapOpt() throws E;
}
