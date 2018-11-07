package pl.kurczews.result;

@FunctionalInterface
public interface CheckedSupplier<E extends Exception, T> {
    T get() throws E;
}
