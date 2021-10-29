package net.junhabaek.authserver.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class BasicExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        ErrorStatus errorStatus = ErrorStatus.METHOD_NOT_ALLOWED;

        final ErrorResponse response = ErrorResponse.of(errorStatus);
        return new ResponseEntity<>(response, errorStatus.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorStatus errorStatus = ErrorStatus.INVALID_INPUT_VALUE;

        final ErrorResponse response = ErrorResponse.of(errorStatus, e.getBindingResult());
        return new ResponseEntity<>(response, errorStatus.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        ErrorStatus errorStatus = ErrorStatus.INVALID_INPUT_VALUE;

        final ErrorResponse response = ErrorResponse.of(errorStatus, e.getBindingResult());
        return new ResponseEntity<>(response, errorStatus.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorStatus errorStatus = ErrorStatus.INVALID_TYPE;

        final String mismatchedValue = e.getValue() != null ? e.getValue().toString() : "";

        final List<FieldError> errors = List.of(new FieldError(e.getName(), mismatchedValue, e.getErrorCode()));

        final ErrorResponse response = ErrorResponse.of(errorStatus, errors);
        return new ResponseEntity<>(response, errorStatus.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorStatus errorStatus = ErrorStatus.ACCESS_DENIED;

        final ErrorResponse response = ErrorResponse.of(errorStatus);
        return new ResponseEntity<>(response, errorStatus.getHttpStatus());
    }

    // handle rest of exceptions
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorStatus errorStatus = ErrorStatus.INTERNAL_SERVER_ERROR;

        final ErrorResponse response = ErrorResponse.of(errorStatus);
        return new ResponseEntity<>(response, errorStatus.getHttpStatus());
    }
}
