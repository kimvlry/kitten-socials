package ru.kimvlry.atmSimulator.operationResults;
import java.util.Optional;

public class Result<T> {
    private final boolean isSuccess;
    private final Exception exception;
    private final Optional<T> value;

    public Result(boolean isSuccess, T value, Exception exception) {
        this.isSuccess = isSuccess;
        this.exception = exception;
        this.value = Optional.ofNullable(value);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public Exception getException() {
        return exception;
    }

    public Optional<T> getValue() {
        return value;
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(true, value, null);
    }

    public static <T> Result<T> failure(Exception exception) {
        return new Result<>(false, null, exception);
    }
}
