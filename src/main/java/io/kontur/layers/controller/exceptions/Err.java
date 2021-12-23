package io.kontur.layers.controller.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Err {
    private String msg;
    private Map<String, Err> fieldErrors;
    private Map<Integer, Err> indexErrors;

    public static Err error(String message) {
        final Err errorResponse = new Err();
        errorResponse.setMsg(message);
        return errorResponse;
    }

    public static Err errorFmt(String message, Object... stringFormatParams) {
        return error(String.format(message, stringFormatParams));
    }

    public static Err objectError(String message, List<FieldErr<String>> fieldErrors) {
        return objectError(message, fieldErrors.toArray(new FieldErr[fieldErrors.size()]));
    }

    public static Err objectError(String message, FieldErr<String>... fieldErrors) {
        final Err err = new Err();
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

    public static Err arrayError(String message, FieldErr<Integer>... fieldErrors) {
        final Err err = new Err();
        err.setMsg(message);
        if (fieldErrors.length > 0) {
            if (err.getIndexErrors() == null) {
                err.setIndexErrors(new HashMap<>());
            }
            for (FieldErr<Integer> fieldError : fieldErrors) {
                err.getIndexErrors().put(fieldError.getKey(), fieldError.getValue());
            }
        }
        return err;
    }

    public static <T> FieldErr<T> fieldError(T name, Err value) {
        return new FieldErr<>(name, value);
    }

    public static class FieldErr<T> {
        private T key;
        private Err value;

        public FieldErr(final T key, final Err value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return key;
        }

        public Err getValue() {
            return value;
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public Map<String, Err> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(final Map<String, Err> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Map<Integer, Err> getIndexErrors() {
        return indexErrors;
    }

    public void setIndexErrors(final Map<Integer, Err> indexErrors) {
        this.indexErrors = indexErrors;
    }
}
