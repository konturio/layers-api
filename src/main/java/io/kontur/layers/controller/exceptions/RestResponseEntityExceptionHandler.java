package io.kontur.layers.controller.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<Err> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(Err.objectError(null, Err.fieldError(fieldName, Err.error(errorMessage))));
        });
        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler(WebApplicationException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(WebApplicationException ex, WebRequest request) {
        if (ex.getStatus().equals(INTERNAL_SERVER_ERROR) && ex.getCause() != null) {
            LOG.error(ex.getMessage(), ex);
        }
        return new ResponseEntity<>(ex.getErr(), ex.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(ConstraintViolationException ex, WebRequest request) {
        var errors = new ArrayList<Err.FieldErr<String>>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            String prop = StringUtils.substringAfterLast(v.getPropertyPath().toString(), ".");
            errors.add(Err.fieldError(prop, Err.error(v.getMessage())));
        }
        return new ResponseEntity<>(Err.objectError(null, errors), BAD_REQUEST);
    }
}
