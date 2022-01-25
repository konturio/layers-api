package io.kontur.layers.controller.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        List<Error.FieldErr<String>> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(Error.fieldError(fieldName, Error.error(errorMessage)));
        });
        return new ResponseEntity<>(Error.objectError(null, errors), BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowable(Throwable ex, WebRequest request) {
        LOG.error(ex.getMessage(), ex);
        return new ResponseEntity<>(Error.error("internal server error"), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String msg = "invalid field value";
        if (ex.getCause() instanceof NumberFormatException) {
            msg = "invalid numeric value";
        }
        return new ResponseEntity<>(Error.objectError(null, Error.fieldError(ex.getName(), Error.error(msg))), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        String msg = "invalid field value";
        String fieldName = "";
        if (ex.getCause() instanceof InvalidFormatException) {
            fieldName = getInvalidFormatExceptionFieldName((InvalidFormatException) ex.getCause());
        }
        return new ResponseEntity<>(Error.objectError(null, Error.fieldError(fieldName, Error.error(msg))), BAD_REQUEST);
    }

    private String getInvalidFormatExceptionFieldName(InvalidFormatException ex) {
        for (JsonMappingException.Reference r : ex.getPath()) {
            return r.getFieldName();
        }
        return "";
    }

    @ExceptionHandler(WebApplicationException.class)
    public ResponseEntity<Object> handleWebApplicationException(WebApplicationException ex, WebRequest request) {
        if (ex.getStatus().equals(INTERNAL_SERVER_ERROR) && ex.getCause() != null) {
            LOG.error(ex.getMessage(), ex);
        }
        return new ResponseEntity<>(ex.getErr(), ex.getStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        return new ResponseEntity<>(Error.error(ex.getMessage()), UNAUTHORIZED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        var errors = new ArrayList<Error.FieldErr<String>>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            String prop = StringUtils.substringAfterLast(v.getPropertyPath().toString(), ".");
            errors.add(Error.fieldError(prop, Error.error(v.getMessage())));
        }
        return new ResponseEntity<>(Error.objectError(null, errors), BAD_REQUEST);
    }
}
