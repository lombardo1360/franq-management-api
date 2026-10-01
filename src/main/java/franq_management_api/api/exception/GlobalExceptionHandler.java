package franq_management_api.api.exception;

import franq_management_api.domain.exception.BranchNotFoundException;
import franq_management_api.domain.exception.FranchiseNotFoundException;
import franq_management_api.domain.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            WebExchangeBindException exception) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error ->  error.getDefaultMessage())
                .orElse("Invalid request");

        return new ErrorResponse(
                400,
                message
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedError(Exception exception) {

        return new ErrorResponse(
                500,
                "Internal server error"
        );
    }

    @ExceptionHandler(FranchiseNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFound(
            FranchiseNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                404,
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(BranchNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFound(
            BranchNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                404,
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleClientNotFound(
            ProductNotFoundException exception) {

        ErrorResponse response = new ErrorResponse(
                404,
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
}
