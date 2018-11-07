package pl.kurczews.result;

public interface MandatoryResult<E extends Exception, T> extends Mapper<E, T> {
    T unwrap() throws E;
}
