package khozhaev.postalitemstracker.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiException {

    private final HttpStatus httpStatus;
    private final String message;
    private final LocalDateTime time = LocalDateTime.now();

    public ApiException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
