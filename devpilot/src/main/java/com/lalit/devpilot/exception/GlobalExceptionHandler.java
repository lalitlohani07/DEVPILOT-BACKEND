package com.lalit.devpilot.exception;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.lalit.devpilot.dto.ErrorResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;
import org.springframework.web.bind.MethodArgumentNotValidException;


@RestControllerAdvice
public class GlobalExceptionHandler {


@ExceptionHandler({
        IncidentNotFoundException.class,
        UserNotFoundException.class
})

//----------------------------------------------------------
public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
    ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            List.of()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
}

//------------------------------------------------------------
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleBadRequest(
        IllegalArgumentException exception) {

    ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            exception.getMessage(),
            List.of()
    );

    return ResponseEntity.badRequest().body(body);
}

//------------------------------------------------------------
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException exception) {

    List<String> fieldErrors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();

    ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation failed",
            fieldErrors
    );

    return ResponseEntity.badRequest().body(body);
}

//------------------------------------------------------------
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {

    ErrorResponse body = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            List.of()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
}

}
