package net.junhabaek.authserver.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e){
        ErrorStatus errorStatus = ErrorStatus.NOT_CLASSIFIED_BUSINESS_ERROR;

        final ErrorResponse response = ErrorResponse.of(errorStatus);
        return new ResponseEntity<>(response, errorStatus.getHttpStatus());
    }
}
