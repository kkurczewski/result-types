package pl.kurczews.result;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class OptionalResultTest {

    private final IOException cause = new IOException();
    private final OptionalResult<IOException, String> failedResult = OptionalResult.failure(cause);
    private final OptionalResult<IOException, String> successfulResult = OptionalResult.success("success");
    private final OptionalResult<IOException, String> emptyResult = OptionalResult.empty();

    @Test
    public void unwrap_should_return_optional_when_result_succeed() throws IOException {
        assertEquals(Optional.of("success"), successfulResult.unwrapOpt());
    }

    @Test
    public void unwrap_should_return_empty_optional_when_result_is_empty() throws IOException {
        assertEquals(Optional.empty(), emptyResult.unwrapOpt());
    }

    @Test
    public void unwrap_should_throw_defined_exception_when_result_failed() {
        assertEquals(cause, catchThrowable(failedResult::unwrapOpt));
    }

    @Test
    public void map_should_return_mapped_result_when_result_succeed() {
        assertEquals(OptionalResult.success(7), successfulResult.map(String::length));
    }

    @Test
    public void map_should_return_empty_result_when_result_is_empty() {
        assertEquals(OptionalResult.empty(), emptyResult.map(String::length));
    }

    @Test
    public void map_should_preserve_original_exception_when_result_failed() {
        assertEquals(OptionalResult.failure(cause), failedResult.map(String::length));
    }

    @Test
    public void flat_map_should_return_mapped_result_when_result_succeed() {
        assertEquals(OptionalResult.success(7), successfulResult.flatMap(v -> OptionalResult.success(v.length())));
    }

    @Test
    public void flat_map_should_return_empty_result_when_result_is_empty() {
        assertEquals(OptionalResult.empty(), emptyResult.flatMap(v -> OptionalResult.success(v.length())));
    }

    @Test
    public void flat_map_should_preserve_original_exception_when_result_failed() {
        assertEquals(OptionalResult.failure(cause), failedResult.flatMap(v -> OptionalResult.success(v.length())));
    }

    @Test
    public void peek_should_run_consumer_function_when_result_succeed() {
        Consumer<String> consumer = getConsumerMock();

        assertEquals(successfulResult, successfulResult.peek(consumer));
        verify(consumer).accept("success");
    }

    @Test
    public void peek_should_ignore_consumer_function_when_result_is_empty() {
        Consumer<String> consumer = getConsumerMock();

        assertEquals(emptyResult, emptyResult.peek(consumer));
        verifyZeroInteractions(consumer);
    }

    @Test
    public void peek_should_ignore_consumer_function_when_result_failed() {
        Consumer<String> consumer = getConsumerMock();

        assertEquals(failedResult, failedResult.peek(consumer));
        verifyZeroInteractions(consumer);
    }

    @Test
    public void result_of_succeed_operation_should_return_succeed_result() {
        assertEquals(OptionalResult.success("success"), OptionalResult.of(() -> Optional.of("success")));
    }

    @Test
    public void result_of_empty_operation_should_return_empty_result() {
        assertEquals(OptionalResult.empty(), OptionalResult.of(Optional::empty));
    }

    @Test
    public void result_of_failed_operation_should_return_failed_result() {
        assertEquals(OptionalResult.failure(cause), OptionalResult.of(() -> { throw cause; }));
    }

    @Test
    public void result_of_runtime_exception_operation_should_throw_original_exception() {
        IllegalArgumentException runtimeCause = new IllegalArgumentException();
        assertEquals(runtimeCause, catchThrowable(() -> OptionalResult.of(() -> { throw runtimeCause; })));
    }

    @Test
    public void equals_hash_code() {
        OptionalResult<Exception, String> same = OptionalResult.success("same");
        OptionalResult<Exception, String> alsoSame = OptionalResult.success("same");
        OptionalResult<Exception, String> notSame = OptionalResult.success("not-same");

        assertEquals(same, alsoSame);
        assertEquals(same.hashCode(), alsoSame.hashCode());

        assertNotEquals(notSame, same);
        assertNotEquals(notSame, null);
        assertNotEquals(notSame.hashCode(), same.hashCode());
    }

    @SuppressWarnings("unchecked")
    private Consumer<String> getConsumerMock() {
        return mock(Consumer.class);
    }
}