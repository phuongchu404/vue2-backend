package vn.mk.eid.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import vn.mk.eid.common.data.ServiceResult;
import vn.mk.eid.web.exception.BadRequestException;
import vn.mk.eid.web.exception.FileUploadException;
import vn.mk.eid.web.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ServiceResult handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ServiceResult.fail(String.valueOf(HttpStatus.NOT_FOUND), ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ServiceResult handleBadRequestException(BadRequestException ex) {
        log.error("Bad request: {}", ex.getMessage());
        return ServiceResult.fail(String.valueOf(HttpStatus.BAD_REQUEST), ex.getMessage());
    }

    @ExceptionHandler(FileUploadException.class)
    public ServiceResult handleFileUploadException(FileUploadException ex) {
        log.error("File upload error: {}", ex.getMessage());
        return ServiceResult.fail(String.valueOf(HttpStatus.BAD_REQUEST), ex.getMessage());

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ServiceResult handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("Validation failed: {}", errors);
        return ServiceResult.fail(String.valueOf(HttpStatus.BAD_REQUEST), String.valueOf(errors));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ServiceResult handleMaxSizeException(MaxUploadSizeExceededException ex) {
        log.error("File too large: {}", ex.getMessage());
        return ServiceResult.fail(String.valueOf(HttpStatus.PAYLOAD_TOO_LARGE), "File size exceeds maximum allowed size");

    }

    @ExceptionHandler(Exception.class)
    public ServiceResult handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        return ServiceResult.fail(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR), "An unexpected error occurred");
    }
}
