package vk.haveplace.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import vk.haveplace.exceptions.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleRegularEventBusy(RegularEventBusy b) {
        log.error(b.getMessage(), b);
        return new ResponseEntity<>(new ExceptionResponse("RegularEventBusy", b.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleWrongClient(WrongClient w) {
        log.error(w.getMessage(), w);
        return new ResponseEntity<>(new ExceptionResponse("WrongClient", w.getMessage()),
                HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleBookingUpdateError(BookingUpdateError e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ExceptionResponse("BookingUpdateError", e.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException o) {
        log.error(o.getMessage(), o);
        return new ResponseEntity<>(new ExceptionResponse("ObjectOptimisticLockingFailureException", o.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(DataIntegrityViolationException d) {
        log.error(d.getMessage(), d);
        UniqueConstraintException uCE = new UniqueConstraintException(d.getMessage()
                .substring(d.getMessage().indexOf("("), d.getMessage().indexOf(")\"")+1));
        return new ResponseEntity<>(new ExceptionResponse(uCE.getClass().getName(), uCE.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException n) {
        return ResponseEntity.notFound().build();
    }


    @ExceptionHandler
    public ResponseEntity<UnknownException> handleUnknownException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new UnknownException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
