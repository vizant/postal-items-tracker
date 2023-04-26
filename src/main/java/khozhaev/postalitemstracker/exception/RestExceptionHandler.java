package khozhaev.postalitemstracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<ApiException> handleNotFoundExceptions(EntityNotFoundException exception) {
        log.error(exception.getMessage(), exception);

        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(
                httpStatus,
                exception.getMessage()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<ApiException> handleExceptions(RuntimeException exception) {
        log.error(exception.getMessage(), exception);

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException apiException = new ApiException(
                httpStatus,
                exception.getMessage()
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }
}
