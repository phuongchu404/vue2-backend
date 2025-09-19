package vn.mk.eid.web.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vn.mk.eid.web.dto.report.ErrorResponse;
import vn.mk.eid.web.exception.ETLException;
import vn.mk.eid.web.exception.ReportGenerationException;

@ControllerAdvice
public class ReportingExceptionHandler {
    @ExceptionHandler(ReportGenerationException.class)
    public ResponseEntity<ErrorResponse> handleReportGenerationException(ReportGenerationException e) {
        ErrorResponse error = new ErrorResponse("REPORT_GENERATION_FAILED", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ETLException.class)
    public ResponseEntity<ErrorResponse> handleETLException(ETLException e) {
        ErrorResponse error = new ErrorResponse("ETL_FAILED", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse error = new ErrorResponse("INVALID_PARAMETER", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
