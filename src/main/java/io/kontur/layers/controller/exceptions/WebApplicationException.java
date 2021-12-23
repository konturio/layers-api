package io.kontur.layers.controller.exceptions;

import org.springframework.http.HttpStatus;

public class WebApplicationException extends RuntimeException {
    private HttpStatus status;
    private Err err;

    public WebApplicationException(final HttpStatus status, final Err err, final Throwable cause) {
        super(err.getMsg(), cause);
        this.status = status;
        this.err = err;
    }

    public WebApplicationException(final HttpStatus status, final Err err) {
        this(status, err, null);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Err getErr() {
        return err;
    }
}
