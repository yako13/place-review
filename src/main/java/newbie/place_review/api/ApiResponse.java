package newbie.place_review.api;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

@Getter
public class ApiResponse<T> {

    private final HttpStatus httpStatus;

    private final String message;

    private final T data;

    private ApiResponse(String message, HttpStatus httpStatus, T data) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
    }

    private ApiResponse(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.data = null;
    }

    public static <T> ApiResponse<T> of(String message, HttpStatus httpStatus, T data) {
        Assert.notNull(data, "The data argument cannot be null");

        return new ApiResponse<>(message, httpStatus, data);
    }

    public static ApiResponse<Void> of(String message, HttpStatus httpStatus) {
        return new ApiResponse<>(message, httpStatus);
    }
}
