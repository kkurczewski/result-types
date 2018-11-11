package pl.kurczews.result;

import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class ResultTest {

    private final IOException cause = new IOException();
    private final Result<IOException, String> failedResult = Result.failure(cause);
    private final Result<IOException, String> successfulResult = Result.success("success");

    @Test
    public void unwrap_should_return_value_when_result_succeed() throws IOException {
        assertEquals("success", successfulResult.unwrap());
    }

    @Test
    public void unwrap_should_throw_defined_exception_when_result_failed() {
        assertEquals(cause, catchThrowable(failedResult::unwrap));
    }

    @Test
    public void map_should_return_mapped_result_when_result_succeed() {
        assertEquals(Result.success(7), successfulResult.map(String::length));
    }

    @Test
    public void map_should_preserve_original_exception_when_result_failed() {
        assertEquals(Result.failure(cause), failedResult.map(String::length));
    }

    @Test
    public void flat_map_should_return_mapped_result_when_result_succeed() {
        assertEquals(Result.success(7), successfulResult.flatMap(v -> Result.success(v.length())));
    }

    @Test
    public void flat_map_should_preserve_original_exception_when_result_failed() {
        assertEquals(Result.failure(cause), failedResult.flatMap(v -> Result.success(v.length())));
    }

    @Test
    public void peek_should_run_consumer_function_when_result_succeed() {
        Consumer<String> consumer = getConsumerMock();

        assertEquals(successfulResult, successfulResult.peek(consumer));
        verify(consumer).accept("success");
    }

    @Test
    public void peek_should_ignore_consumer_function_when_result_failed() {
        Consumer<String> consumer = getConsumerMock();

        assertEquals(failedResult, failedResult.peek(consumer));
        verifyZeroInteractions(consumer);
    }

    @Test
    public void result_of_succeed_operation_should_return_succeed_result() {
        assertEquals(Result.success("success"), Result.of(() -> "success"));
    }

    @Test
    public void result_of_failed_operation_should_return_failed_result() {
        assertEquals(Result.failure(cause), Result.of(() -> { throw cause; }));
    }

    @Test
    public void result_of_runtime_exception_operation_should_throw_original_exception() {
        IllegalArgumentException runtimeException = new IllegalArgumentException();
        assertEquals(runtimeException, catchThrowable(() -> Result.of(() -> { throw runtimeException; })));
    }

    @Test
    public void equals_hash_code() {
        Result<Exception, String> same = Result.success("same");
        Result<Exception, String> alsoSame = Result.success("same");
        Result<Exception, String> notSame = Result.success("not-same");

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