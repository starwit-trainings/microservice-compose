package de.starwit.errorhandling;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleException(Exception ex) {
        LOG.error(ex.getClass() + " " + ex.getMessage(), ex.fillInStackTrace());
        return ResponseEntity.internalServerError().body("Internal Server Error");
    }
    
    @ExceptionHandler(value = { NoSuchElementException.class })
    public ResponseEntity<Object> handleException(NoSuchElementException ex) {
        LOG.info("Entity not found Exception: {}", ex.getMessage());
        return new ResponseEntity<>("Entity not found" + ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
