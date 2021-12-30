package io.kontur.layers.controller.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Error {
    private String msg;
    private Map<String, Error> fieldErrors;

    public static Error error(String message) {
        final Error errorResponse = new Error();
        errorResponse.setMsg(message);
        return errorResponse;
    }

    public static Error errorFmt(String message, Object... stringFormatParams) {
        return error(String.format(message, stringFormatParams));
    }

    public static Error objectError(String message, List<FieldErr<String>> fieldErrors) {
        return objectError(message, fieldErrors.toArray(new FieldErr[fieldErrors.size()]));
    }

    public static Error objectError(String message, FieldErr<String>... fieldErrors) {
        final Error err = new Error();
        err.setMsg(message);
        if (fieldErrors.length > 0) {
            if (err.getFieldErrors() == null) {
                err.setFieldErrors(new HashMap<>());
            }
            for (FieldErr<String> fieldError : fieldErrors) {
                err.getFieldErrors().put(fieldError.getKey(), fieldError.getValue());
            }
        }
        return err;
    }

    public static <T> FieldErr<T> fieldError(T name, Error value) {
        return new FieldErr<>(name, value);
    }

    public static class FieldErr<T> {
        private T key;
        private Error value;

        public FieldErr(final T key, final Error value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return key;
        }

        public Error getValue() {
            return value;
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public Map<String, Error> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(final Map<String, Error> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
