package pl.kurczews.result;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ResultTest {

    @Test
    public void should_map_wrapped_value() throws IOException {
        int value = successfulResult()
                .map(String::length)
                .unwrap();

        assertEquals(7, value);
    }

    @Test
    public void should_flat_map_wrapped_value() throws IOException {

        int value = successfulResult()
                .flatMap(v -> Result.success(v.length()))
                .unwrap();

        assertEquals(7, value);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void should_peek_wrapped_value() {
        Consumer<String> consumer = mock(Consumer.class);

        Result<IOException, String> result = spy(successfulResult());

        assertEquals(result, result.peek(consumer));
        verify(consumer).accept("success");
    }

    @Test
    public void should_unwrap_as_optional() throws IOException {
        Optional<String> value = successfulResult().unwrapOpt();

        assertEquals(Optional.of("success"), value);
    }

    @Test
    public void when_empty_result_unwrapped_as_optional_return_empty_optional() throws IOException {
        Optional<String> value = emptyResult().unwrapOpt();

        assertEquals(Optional.empty(), value);
    }

    @Test
    public void when_empty_result_unwrapped_throw_no_such_element() {
        assertThatThrownBy(emptyResult()::unwrap).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void when_failed_result_unwrapped_throw_defined_exception() {
        assertThatThrownBy(failedResult()::unwrap).isInstanceOf(IOException.class);
    }

    @Test
    public void when_failed_result_unwrapped_as_optional_throw_defined_exception() {
        assertThatThrownBy(failedResult()::unwrapOpt).isInstanceOf(IOException.class);
    }

    @Test
    public void should_return_successful_result() throws IOException {
        List<String> result = Result
                .of(() -> readFile("./src/test/resources/ResultTest.txt"))
                .unwrap();

        assertThat(result).contains("passed");
    }

    @Test
    public void should_return_failed_result() {
        Result<IOException, List<String>> result = Result.of(() -> readFile("not-exists"));

        assertThatThrownBy(result::unwrap).isInstanceOf(IOException.class);
    }

    @Test
    public void should_forward_runtime_exception_when_unwrapped() {
        String message = "some-runtime-exception";

        Result<RuntimeException, Object> result = Result.of(() -> {
            throw new IllegalArgumentException(message);
        });

        assertThatThrownBy(result::unwrap)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message)
                .hasNoCause();
    }

    private Result<IOException, String> successfulResult() {
        return Result.success("success");
    }

    private Result<IOException, String> failedResult() {
        return Result.failure(new IOException("failure"));
    }

    private Result<IOException, String> emptyResult() {
        return Result.empty();
    }

    private List<String> readFile(String path) throws IOException {
        return Files.readAllLines(Paths.get(path));
    }
}