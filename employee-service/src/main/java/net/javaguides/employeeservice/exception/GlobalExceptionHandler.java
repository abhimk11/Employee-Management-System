package net.javaguides.employeeservice.exception;

import net.javaguides.employeeservice.payload.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiException> handleResourceNotFoundException(ResourceNotFoundException ex){
        ApiException apiException = ApiException.builder().message(ex.getMessage())
                .success(true)
                .status(HttpStatus.NOT_FOUND).build();

        return new ResponseEntity<>(apiException,HttpStatus.NOT_FOUND);
    }
}
