package vk.haveplace.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vk.haveplace.exceptions.AdminNotFound;
import vk.haveplace.exceptions.BookingNotFound;

@ControllerAdvice
@Slf4j
public class BadRequestHandler {
    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException h) {
        return new ResponseEntity<>(new ExceptionResponse(h.getClass().getName(), h.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException h) {
        return new ResponseEntity<>(new ExceptionResponse("HttpMessageNotReadableException", h.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException m) {
        return new ResponseEntity<>(new ExceptionResponse("MethodArgumentNotValidException", m.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleBookingNotFound(BookingNotFound b) {
        log.error(b.getMessage(), b);
        return new ResponseEntity<>(new ExceptionResponse("BookingNotFound", b.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleMissingPathVariableException(MissingPathVariableException m) {
        return new ResponseEntity<>(new ExceptionResponse("MissingPathVariableException", m.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleAdminNotFound(AdminNotFound a) {
        return new ResponseEntity<>(new ExceptionResponse("AdminNotFound", a.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
